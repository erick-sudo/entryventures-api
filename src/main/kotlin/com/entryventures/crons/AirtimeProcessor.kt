package com.entryventures.crons

import com.entryventures.apis.Apis
import org.springframework.stereotype.Component

@Component
class AirtimeProcessor(
    private val apis: Apis
)