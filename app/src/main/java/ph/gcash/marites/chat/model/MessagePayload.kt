package ph.gcash.marites.chat.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class MessagePayload(
    val message: String = "",
    val timestamp: String = "",
    val userUID: String = "",
    val imageUrl: String = "") {
}