# Entry Ventures

## Resources
1. User
2. Loan
3. Client
4. LoanCollection


JengaSendToMobileRequestPayload(
source = MoveMoneySource(
countryCode = transferCurrencyCode,
name = "Security Test",
accountNumber = sourceAccountNumber
),
destination = MoveMoneyDestination(
type = "mobile",
countryCode = transferCurrencyCode,
name = "Francisca Orwa",
mobileNumber = "254792753471",
walletName = "Mpesa"
),
transfer = MoneyTransfer(
type = "MobileWallet",
amount = transferAmount,
currencyCode = "${transferCurrencyCode}S",
reference = transferReference,
date = "2024-01-31",
description = "Entry Ventures Sandbox testing",
callbackUrl = "http://ec2-13-53-125-78.eu-north-1.compute.amazonaws.com:3000/callbacks"
)
)


PurchaseAirtimeRequestPayload(
customer = Customer(
countryCode = customerCountryCode,
mobileNumber = customerMobileNumber
),
airtime = Airtime(
amount = airtimeAmount,
reference = airtimeReference,
telco = airtimeTelco
)
)

FullStatementRequestPayload(
countryCode = countryCode,
accountNumber = accountNumber,
fromDate = fromDate,
toDate = toDate
)

