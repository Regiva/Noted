package com.noted.features.note.utils

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun LocalDateTime.toEpochSec() = this.toEpochSecond(OffsetDateTime.now().offset)

fun localDateTimeOfEpochSec(
    seconds: Long?,
    nanoOfSecond: Int = 0,
    offset: ZoneOffset = OffsetDateTime.now().offset,
): LocalDateTime {
    return if (seconds == null) {
        LocalDateTime.now()
    } else {
        LocalDateTime.ofEpochSecond(
            seconds,
            nanoOfSecond,
            offset,
        )
    }
}