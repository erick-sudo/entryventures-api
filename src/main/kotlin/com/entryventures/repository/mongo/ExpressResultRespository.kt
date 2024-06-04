package com.entryventures.repository.mongo

import com.entryventures.apis.mpesa.express.AsyncExpressBody
import org.springframework.data.mongodb.repository.MongoRepository

interface ExpressResultRespository: MongoRepository<AsyncExpressBody, String>