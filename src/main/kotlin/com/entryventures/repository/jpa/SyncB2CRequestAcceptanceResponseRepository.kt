package com.entryventures.repository.jpa

import com.entryventures.apis.mpesa.b2c.SyncB2CRequestAcceptanceResponse
import org.springframework.data.jpa.repository.JpaRepository

interface SyncB2CRequestAcceptanceResponseRepository: JpaRepository<SyncB2CRequestAcceptanceResponse, String> {
}