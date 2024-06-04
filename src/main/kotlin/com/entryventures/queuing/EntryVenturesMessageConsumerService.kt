package com.entryventures.queuing

import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
class EntryVenturesMessageConsumerService {

    @JmsListener(destination = "receive-money-channel")
    fun receiveMoneyChannel() {
        TODO("Not yet implemented")
    }
}