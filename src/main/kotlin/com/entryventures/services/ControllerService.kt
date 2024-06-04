package com.entryventures.services

import com.entryventures.apis.equity.JengaTransactions
import com.entryventures.apis.infobid.InfoBidRequests
import com.entryventures.apis.infobid.email.EmailEnvelope
import com.entryventures.apis.infobid.email.EmailPayload
import com.entryventures.apis.infobid.sms.SMSs
import com.entryventures.apis.infobid.sms.SmsDestination
import com.entryventures.apis.infobid.sms.SmsMessage
import com.entryventures.apis.mpesa.MpesaTransactions
import com.entryventures.apis.mpesa.express.AsyncExpressBody
import com.entryventures.apis.mpesa.express.SyncMpesaExpressResponse
import com.entryventures.trusted.mpesa.B2CResult
import com.entryventures.dtos.MailDto
import com.entryventures.dtos.MessageDto
import com.entryventures.exceptions.EntryVenturesException
import com.entryventures.extensions.*
import com.entryventures.models.LoanStatus
import com.entryventures.models.dto.*
import com.entryventures.models.jpa.*
import com.entryventures.queuing.EntryVenturesMessageProducerService
import com.entryventures.repository.jpa.*
import com.entryventures.repository.mongo.B2CResultRepository
import com.entryventures.repository.mongo.ExpressResultRespository
import com.entryventures.security.JwtService
import com.entryventures.security.PasswordService
import com.entryventures.trusted.jpa.LoanDisbursementSchedule
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.util.*

private val LOAN_UPDATE_FIELDS = listOf("amount", "status")
private val CLIENT_UPDATE_FIELDS = listOf("name", "email", "address")
private val VAULT_UPDATE_FIELDS = listOf("name", "key")
private val LOAN_COLLECTION_UPDATE_FIELDS = listOf("amount", "collection_date", "payment_method")

@Service
class ControllerService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val jengaTransactions: JengaTransactions,
    private val infoBidRequests: InfoBidRequests,
    private val mpesaTransactions: MpesaTransactions,
    private val loanRepository: LoanRepository,
    private val clientRepository: ClientRepository,
    private val vaultRepository: VaultRepository,
    private val loanOfficerRepository: LoanOfficerRepository,
    private val loanCollectionRepository: LoanCollectionRepository,
    private val loanDisbursementScheduleRepository: LoanDisbursementScheduleRepository,
    private val b2CResultRepository: B2CResultRepository,
    private val entryVenturesMessageProducerService: EntryVenturesMessageProducerService,
    private val syncMpesaExpressResponseRepository: SyncMpesaExpressResponseRepository,
    private val expreResultRepository: ExpressResultRespository
) {

    fun getCurrentUserInformation(): Authenticatable {
        val authentication = SecurityContextHolder.getContext().authentication

        return Crud.find { userRepository.findByUsernameOrEmail(authentication.principal.toString()) }
    }

    fun getSchedules(): List<LoanDisbursementScheduleOut> = loanDisbursementScheduleRepository.findMostRecentlyModified(PageRequest.of(0, 10)).map { it.out() }

    fun mpesaExpressCallback(asyncExpressBody: AsyncExpressBody) {
        try {
            entryVenturesMessageProducerService
                .sendToQueue(
                    entryVenturesMessageProducerService.mpesaExpressCallBackQueue.queueName,
                    asyncExpressBody
                )
        } catch (ex: Exception) {
            // Failed to send to queue
            // Handle JMSException
            println("Exception: ${ex.message}")
        } finally {
            expreResultRepository.save(asyncExpressBody)
        }
    }

    fun sendB2CCallBackResultToQueue(b2cResult: B2CResult) {
        try {
            entryVenturesMessageProducerService
                .sendToQueue(
                    entryVenturesMessageProducerService.b2cCallBackQueue.queueName,
                    b2cResult
                )
        } catch (ex: Exception) {
            // Failed to send to queue
            // Handle JMSException
            println("Exception: ${ex.message}")
        } finally {
            b2CResultRepository.save(b2cResult)
        }
    }

    suspend fun triggerLoanCollection(trigger: CollectionTrigger): Map<String, String> {

        val loan = Crud.find { loanRepository.findById(trigger.loanId) }

        // Check if amount is less or equal to maximum payable amount
        if(trigger.amount > loan.balance()) {
            throw EntryVenturesException(HttpStatus.UNPROCESSABLE_ENTITY) { "Please pay an amount within your total balance, ${loan.balance().toDouble().toCurrency("KES")}" }
        }

        // Validate amount
        if(trigger.amount < 5) {
            throw EntryVenturesException(HttpStatus.UNPROCESSABLE_ENTITY) { "Invalid amount" }
        }
        // Validate phone number
        if(!("${trigger.phone}".matches("254[0-9]{9}\$".toRegex()))) {
            throw EntryVenturesException(HttpStatus.UNPROCESSABLE_ENTITY, errorDescription = {
                val errors = mutableListOf("Required format 254XXXXXXXXX")
                errors
            }) { "Invalid phone number" }
        }

//        return mpesaTransactions.mpesaExpress(
//            amount = trigger.amount,
//            phone = trigger.phone,
//            accountReference = "EVentures REF:${loan.id.uppercase()}",
//            transactionDescription = trigger.transactionDescription
//        )?
        return SyncMpesaExpressResponse(
            merchantRequestId = "29115-34620561-1",
            checkoutRequestId = "ws_CO_191220191020363925",
            responseCode = "0",
            responseDescription = "Success. Request accepted for processing",
            customerMessage = "Success. Request accepted for processing",
            reference = loan.id
        ).let {

            syncMpesaExpressResponseRepository.save(it.apply {
                // Tag checkout request id with the loan's id
                reference = loan.id
            })

            mapOf(
                "message" to it.customerMessage
            )
        }?: mapOf(
            "error" to "Failed to initiate payment"
        )
    }

    fun saveUserWithPassword(user: User, password: String): User  {
        val user1 = userRepository.findByUserName(user.userName)
        if(user1 == null) {
            user.passwordDigest = PasswordService.encryptPassword(password)
            userRepository.save(user)
        }
        return user
    }

    fun getAccessToken(credentials: AccessTokenRequest): Map<String, Any> {

        if(credentials.grantType == "admin_credentials" || credentials.grantType == "client_credentials") {
            val user = Crud.find {
                userRepository.findByUsernameOrEmail(identifier = credentials.clientId)
            }

            if(user.authenticate(credentials.clientSecret)) {
                val payload = mapOf(
                    "id" to user.id,
                    "grant_type" to "admin_credentials",
                    "username" to user.userName,
                    "email" to user.email,
                    "roles" to user.getRoles().map { mapOf("name" to it.name, "description" to it.description ) },
                    "groups" to user.getGroups().map { mapOf("name" to it.name, "description" to it.description ) }
                )

                val token = jwtService.generateJwtToken(payload)

                return mapOf(
                    "access_token" to token,
                    "expires_in" to jwtService.jwtExpirationMs,
                    "realm" to "Bearer"
                )
            }
        } else {
            throw EntryVenturesException(HttpStatus.UNAUTHORIZED) {
                "Invalid grant type"
            }
        }

        throw EntryVenturesException(HttpStatus.UNAUTHORIZED) {
            "Invalid email or password"
        }
    }

    fun getLoans(pageNumber: Int, pageSize: Int): List<Loan> {
        return Crud.paginate(
            pageNumber = pageNumber,
            pageSize = pageSize,
            count = { loanRepository.count() },
            execute = { pageable ->
                loanRepository.findAll(pageable).toList()
            }
        )
    }

    fun countLoans(): Map<String, Long> = mapOf("count" to loanRepository.count())

    fun countLoanCollectionsByLoanId(loanId: String): Map<String, Long> = mapOf("count" to loanCollectionRepository.countLoanCollectionsByLoanId(loanId))

    fun loanTallyByStatus(): Map<String, Long> = LoanStatus.entries.fold(mutableMapOf()) { acc, curr ->
        acc += (curr.name to loanRepository.countByStatus(curr))
        acc
    }

    fun loansByStatus(
        loanStatus: LoanStatus,
        pageNumber: Int,
        pageSize: Int
    ): List<Loan> {
        return loanRepository.findLoanByStatus(loanStatus, PageRequest.of(if(pageNumber > 0) pageNumber -1 else 1, pageSize))
    }

    fun createLoan(
        loanDto: LoanDto
    ): Loan {
        val loan = Loan(
            loanDto,
            clientRepository,
            loanOfficerRepository,
            vaultRepository
        )
        loanRepository.save(loan)
        return loan
    }

    fun createLoans(
        loanDtos: List<LoanDto>
    ): List<Loan> {

        val loans = Loan.initializeMultipleLoans(
            loanDtos,
            clientRepository,
            loanOfficerRepository,
            vaultRepository
        )

        loanRepository.saveAll(loans)
        return loans
    }

    fun updateLoan(
        loanId: String,
        payload: Map<String, String>
    ): Loan {
        val loan = Crud.find {
            loanRepository.findById(loanId)
        }

        payload.keys.filter { LOAN_UPDATE_FIELDS.contains(it) }.forEach { key ->
            when(key) {
                "amount" -> loan.apply {
                    payload["amount"]?.let { amountString ->
                        try {
                            amount = amountString.toFloat()
                        } catch (exc: NumberFormatException) {
                            throw EntryVenturesException(HttpStatus.UNPROCESSABLE_ENTITY) {
                                "Invalid loan amount"
                            }
                        }
                    }
                }
                "status" -> loan.apply {
                    payload["status"]?.let {  statusString ->
                        status = statusString.toLoanStatus()
                    }
                }
            }
        }

        loanRepository.save(loan)
        return loan
    }

    fun deleteLoan(
        loanId: String
    ) {
        Crud.find {
            loanRepository.findById(loanId)
        }.apply {
            loanRepository.deleteById(id)
        }
    }

    fun showLoan(
        loanId: String
    ): Loan {
        return Crud.find {
            loanRepository.findById(loanId)
        }
    }

    fun getLoanRepayments(
        loanId: String,
        pageNumber: Int,
        pageSize: Int
    ): List<LoanCollection> {
        val loan = Crud.find {
            loanRepository.findById(loanId)
        }

        return loanCollectionRepository.findLoanCollectionsByLoan(loan.id, PageRequest.of(pageNumber - 1, pageSize))
    }

    fun approveLoan(loanId: String): Map<String, String> {
        Crud.find {
            loanRepository.findById(loanId)
        }.apply {
            if(loanDisbursementSchedule == null) {
                // Not yet scheduled for disbursement
                loanDisbursementSchedule = LoanDisbursementSchedule().let { lds ->
                    lds.loan = this
                    loanDisbursementScheduleRepository.save(lds)

                    try {
                        entryVenturesMessageProducerService.sendToQueue(
                            entryVenturesMessageProducerService.disbursementSchedulesQueue.queueName,
                            lds
                        )
                    } catch (ex: Exception) {
                        println("Error Sending LDS to queue: ${ex.message}")
                    }

                    this.status = LoanStatus.Approved
                    loanRepository.save(this)
                    lds
                }
            } else {
                throw EntryVenturesException(HttpStatus.CONFLICT) {
                    loanDisbursementSchedule?.let {
                        if(it.processed) if (status == LoanStatus.Closed) "Loan completed and closed" else "Loan already disbursed" else "Loan is awaiting disbursement"
                    } ?: "Pending approval"
                }
            }
        }

        return mapOf("message" to "Loan approved successfully")
    }

    fun getLoanCollections(): List<LoanCollection> {
        return loanCollectionRepository.findAll().toList()
    }

    fun createLoanCollection(
        loanCollectionDto: LoanCollectionDto
    ): LoanCollection {
        val loanCollection = LoanCollection(loanCollectionDto, loanRepository)
        loanCollectionRepository.save(loanCollection)
        return loanCollection
    }

    fun updateLoanCollection(
        loanCollectionID: String,
        payload: Map<String, String>
    ): LoanCollection {
        return Crud.find {
            loanCollectionRepository.findById(loanCollectionID)
        }.apply {
            payload.keys
                .filter { LOAN_COLLECTION_UPDATE_FIELDS.contains(it) }
                .forEach { key ->
                    when(key) {
                        "collection_date" -> {
                            payload["collection_date"]?.let { date ->
                                collectionDate = date.toDate()
                            }
                        }
                        "amount" -> {
                            payload["amount"]?.let { amountString ->
                                try {
                                    amount = amountString.toFloat()
                                } catch (exc: NumberFormatException) {
                                    throw EntryVenturesException(HttpStatus.UNPROCESSABLE_ENTITY) {
                                        "Invalid amount"
                                    }
                                }
                            }
                        }
                        "payment_method" -> {
                            payload["payment_method"]?.let {  paymentMethodString ->
                                paymentMethod = paymentMethodString.toPaymentMethod()
                            }
                        }
                    }
                }
        }
    }

    fun showLoanCollection(
        loanCollectionID: String
    ): LoanCollection {
        return Crud.find {
            loanCollectionRepository.findById(loanCollectionID)
        }
    }

    fun deleteLoanCollection(
        collectionId: String
    ) {
        Crud.find {
            loanCollectionRepository.findById(collectionId)
        }.apply {
            loanCollectionRepository.deleteById(id)
        }
    }

    fun countLoanOfficers(): Map<String, Long> = mapOf("count" to loanOfficerRepository.count())

    fun getLoanOfficers(pageNumber: Int, pageSize: Int): List<LoanOfficer> {
        return Crud.paginate(
            pageNumber = pageNumber,
            pageSize = pageSize,
            count = { loanOfficerRepository.count() },
            execute = { pageable ->
                loanOfficerRepository.findAll(pageable).toList()
            }
        )
    }

    fun searchLoanOfficers(query: String): List<LoanOfficerOut> {
        return loanOfficerRepository.searchLoanOfficers(query).map { it.out() }
    }

//    fun createLoanOfficer(
//        client: Client,
//        password: String
//    ): Client {
//        return Crud.create { clientRepository.save(client.apply { passwordDigest = PasswordService.encryptPassword(password) }) }
//    }
//
//    fun createLoanOfficers(
//        clients: List<Client>
//    ): List<Client> {
//        return clientRepository.saveAll(Client.initializeClients(clients, clientRepository).map { it.apply { passwordDigest = PasswordService.encryptPassword("password") } })
//    }

    fun showLoanOfficer(
        loanOfficerID: String
    ): LoanOfficerOut {
        return Crud.find {
            loanOfficerRepository.findById(loanOfficerID)
        }.out()
    }

    fun countClients(): Map<String, Long> = mapOf("count" to clientRepository.count())

    fun getClients(pageNumber: Int, pageSize: Int): List<Client> {
        return Crud.paginate(
            pageNumber = pageNumber,
            pageSize = pageSize,
            count = { clientRepository.count() },
            execute = { pageable ->
                clientRepository.findAll(pageable).toList()
            }
        )
    }

    fun searchClients(query: String): List<ClientOut> {
        return clientRepository.searchClients(query).map { it.out() }
    }

//    fun createClient(
//        client: Client,
//        password: String
//    ): Client {
//        return Crud.create { clientRepository.save(client.apply { passwordDigest = PasswordService.encryptPassword(password) }) }
//    }
//
//    fun createClients(
//        clients: List<Client>
//    ): List<Client> {
//        return clientRepository.saveAll(Client.initializeClients(clients, clientRepository).map { it.apply { passwordDigest = PasswordService.encryptPassword("password") } })
//    }

    fun showClient(
        clientId: String
    ): Client {
        return Crud.find {
            clientRepository.findById(clientId)
        }
    }

//    fun updateClient(
//        clientId: String,
//        payload: Map<String, String>
//    ): Client {
//        val client = Crud.find {
//            clientRepository.findById(clientId)
//        }
//
//        val errors = mutableMapOf<String, String>()
//
//        payload.keys.filter { CLIENT_UPDATE_FIELDS.contains(it) }.forEach { key ->
//            try {
//                when(key) {
//                    "name" -> payload["name"]?.let { name ->
//                        if(StringUtils.hasText(name)) {
//                            client.name = name
//                        } else {
//                            errors["name"] = "name cannot be blank"
//                        }
//                    }
//                    "email" -> payload["email"]?.let { email ->
//                        if(email.isValidEmail()) {
//                            client.email = email
//                        } else {
//                            errors["name"] = "invalid email address"
//                        }
//                    }
//                    "address" -> payload["address"]?.let { address ->
//                        if(StringUtils.hasText(address)) {
//                            client.address = address
//                        } else {
//                            errors["name"] = "address cannot be blank"
//                        }
//                    }
//                }
//            } catch (ex: IllegalArgumentException) {
//                // errors
//
//            }
//        }
//
//        if(errors.isNotEmpty()) {
//            throw EntryVenturesException(
//                serverStatus = HttpStatus.UNPROCESSABLE_ENTITY,
//                errorDescription = { errors },
//                errorMessage = { "Error processing client details" }
//            )
//        }
//
//        clientRepository.save(client)
//        return client
//    }

    fun deleteClient(
        clientID: String
    ) {
        Crud.find {
            clientRepository.findById(clientID)
        }.apply {
            clientRepository.deleteById(id)
        }
    }

    fun getVaults(): List<Vault> {
        return vaultRepository.findAll().toList()
    }

    fun searchVaults(query: String): List<VaultOut> {
        return vaultRepository.searchVaults(query).map { it.out() }
    }

    fun createVault(
        vault: Vault
    ): Vault {
        return Crud.create { vaultRepository.save(vault) }
    }

    fun createVaults(
        vaults: List<Vault>
    ): List<Vault> {
        return vaultRepository.saveAll(vaults)
    }

    fun showVault(
        vaultId: String
    ): Vault {
        return Crud.find {
            vaultRepository.findById(vaultId)
        }
    }

    fun updateVault(
        vaultId: String,
        payload: Map<String, String>
    ): Vault {
        val vault = Crud.find {
            vaultRepository.findById(vaultId)
        }

        val errors = mutableMapOf<String, String>()

        payload.keys.filter { VAULT_UPDATE_FIELDS.contains(it) }.forEach { f ->
            try {
                when(f) {
                    "name" -> payload["name"]?.let { name ->
                        if(StringUtils.hasText(name)) {
                            vault.name = name
                        } else {
                            errors["name"] = "name cannot be blank"
                        }
                    }
                    "key" -> payload["key"]?.let { key ->
                        if(StringUtils.hasText(key)) {
                            vault.key = key
                        } else {
                            errors["key"] = "key cannot be blank"
                        }
                    }
                }
            } catch (ex: IllegalArgumentException) {
                // errors

            }
        }

        if(errors.isNotEmpty()) {
            throw EntryVenturesException(
                serverStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                errorDescription = { errors },
                errorMessage = { "Error processing vault details" }
            )
        }

        vaultRepository.save(vault)

        return vault
    }

    fun deleteVault(
        vaultID: String
    ) {
        Crud.find {
            vaultRepository.findById(vaultID)
        }.apply {
            vaultRepository.deleteById(id)
        }
    }

    fun response(
        httpStatus: HttpStatus = HttpStatus.OK,
        response: Any
    ): ResponseEntity<*> {
        return ResponseEntity.status(httpStatus).body(response)
    }

    fun entryVenturesExceptionResponse(
        entryVenturesException: EntryVenturesException
    ) : ResponseEntity<*> {
        return ResponseEntity.status(entryVenturesException.serverStatus).body(mapOf("error" to entryVenturesException.message))
    }

    suspend fun sendSms(message: MessageDto): Map<String, Any> {
        val payload = SMSs(
            bulkId = UUID.randomUUID().toString(),
            messages = listOf(
                SmsMessage(
                    from = "Entry Ventures",
                    text = message.text,
                    destinations = message.phones.map { SmsDestination(
                        messageId = UUID.randomUUID().toString(),
                        to = "$it"
                    ) }
                )
            )
        )
        return infoBidRequests.sendSms(
            payload
        )?.let { successRes ->
            mapOf(
                "messages" to successRes.messages.map {
                    mapOf(
                        "messageId" to it.messageId,
                        "status" to it.status.description
                    )
                }
            )
        } ?: mapOf("message" to "An error occurred while sending message")
    }

    suspend fun sendMail(mailDto: MailDto): Map<String, Any> {
        val payload = EmailPayload(
            envelope = EmailEnvelope(
                from = mailDto.from,
                to = mailDto.to
            ),
            header = mapOf(
                "from" to mailDto.from,
                "to" to mailDto.to,
                "subject" to mailDto.subject
            ),
            body = mailDto.text
        )
        return infoBidRequests.sendMail(
            payload
        )?.let { successRes ->
            mapOf(
                "messages" to successRes.messages.map {
                    mapOf(
                        "messageId" to it.messageId,
                        "status" to it.status.description
                    )
                }
            )
        } ?: mapOf("message" to "An error occurred while sending mail")
    }

    fun getUsers(): List<UserDto> = userRepository.findAll().map { it.userDto() }

    fun showUser(userId: String): User = Crud.find { userRepository.findById(userId) }

    fun createUser(user: User): User = userRepository.save(user)

    fun updateUser(userId: String, payload: Map<String, String>): Any {
        TODO("Not yet implemented")
    }

    fun deleteUser(userId: String): Any {
        TODO("Not yet implemented")
    }
}