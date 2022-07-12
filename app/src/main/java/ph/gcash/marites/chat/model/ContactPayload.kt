package ph.gcash.marites.chat.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ContactPayload(
    val email: String = "",
    val name: String = "",
    val roomID: String = "",
    val userUID: String = "") {
}