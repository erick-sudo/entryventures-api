package com.entryventures.extensions

import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

fun Date.yyyymmdd(): String {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return String.format("%4d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE))
}

fun LocalDate.asDate(): Date = Date.from(this.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
fun Date.toLocalDate(): LocalDate {
    val calendar = Calendar.getInstance().also { it.time = this }
    return LocalDate.of(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}