package ph.gcash.marites.utilities

import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

interface StringGenerator {
    fun generateString() : String
}

object UUIDGenerator: StringGenerator {
    override fun generateString(): String {
        return UUID.randomUUID().toString()
    }
}

object EpochGenerator: StringGenerator {
    override fun generateString(): String {
        return Instant.now().toEpochMilli().toString()
    }
}

object TimestampGenerator: StringGenerator {
    override fun generateString(): String {
        return LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm"))
    }
}
