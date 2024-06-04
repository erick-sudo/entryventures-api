package com.entryventures.extensions

import java.text.NumberFormat
import java.util.*

val CURRENCY_FORMATTER_INSTANCE: NumberFormat by lazy {
    NumberFormat.getCurrencyInstance()
}

fun Double.toCurrency(currencyCode: String = "USD"): String {
    return CURRENCY_FORMATTER_INSTANCE.apply { currency = Currency.getInstance(currencyCode)  }.format(this)
}