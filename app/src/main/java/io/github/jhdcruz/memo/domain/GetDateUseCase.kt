package io.github.jhdcruz.memo.domain

import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

fun LocalDateTime.toTimestamp(): Timestamp {
    // Convert LocalDateTime to Instant
    val instant = this.atZone(ZoneId.systemDefault()).toInstant()

    // Convert Instant to java.util.Date
    val date = Date.from(instant)

    // Convert Date to Timestamp
    return Timestamp(date)
}

fun Timestamp.toLocalDateTime(): LocalDateTime {
    // Convert Timestamp to Date
    val date = toDate()

    // Convert Date to LocalDateTime
    return date.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}

fun createTimestamp(millis: Long, hour: Int, minute: Int): Timestamp {
    // Convert system milliseconds to LocalDateTime
    var localDateTime =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()

    // Adjust the hour and minute of LocalDateTime
    localDateTime = localDateTime.withHour(hour).withMinute(minute)

    // Convert LocalDateTime to Instant
    val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()

    // Convert Date to Timestamp
    return Timestamp(Date.from(instant))
}
