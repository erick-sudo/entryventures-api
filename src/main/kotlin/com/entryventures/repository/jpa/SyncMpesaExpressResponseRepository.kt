package com.entryventures.repository.jpa

import com.entryventures.apis.mpesa.express.SyncMpesaExpressResponse
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface SyncMpesaExpressResponseRepository: JpaRepository<SyncMpesaExpressResponse, String> {

    fun findByCheckoutRequestId(checkoutRequestId: String): Optional<SyncMpesaExpressResponse>
}