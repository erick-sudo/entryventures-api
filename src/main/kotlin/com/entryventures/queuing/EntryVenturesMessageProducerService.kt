package com.entryventures.queuing

import jakarta.jms.Queue
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component


@Component
class EntryVenturesMessageProducerService(
    val jmsTemplate: JmsTemplate
) {

    val b2cCallBackQueue
        get() = Queue {
            "successful-mpesa-b2c-callbacks"
        }

    val mpesaExpressCallBackQueue
        get() = Queue {
            "successful-mpesa-express-callbacks"
        }

    val disbursementSchedulesQueue
        get() = Queue {
            "disbursement-schedules"
        }

    fun <T: Any> sendToQueue(queue: String, message: T) {
        jmsTemplate.convertAndSend(queue, message)
    }
}