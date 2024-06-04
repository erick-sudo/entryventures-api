package com.entryventures.repository.mongo

import com.entryventures.apis.infobid.sms.SMSs
import org.springframework.data.mongodb.repository.MongoRepository

interface SMSsRepository: MongoRepository<SMSs, String> {
}