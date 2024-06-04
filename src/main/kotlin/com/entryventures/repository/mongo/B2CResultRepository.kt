package com.entryventures.repository.mongo

import com.entryventures.trusted.mpesa.B2CResult
import jakarta.transaction.Transactional
import org.springframework.data.mongodb.repository.MongoRepository

interface B2CResultRepository: MongoRepository<B2CResult, String> {

    @Transactional
    fun findByProcessed(processed: Boolean): List<B2CResult>
}