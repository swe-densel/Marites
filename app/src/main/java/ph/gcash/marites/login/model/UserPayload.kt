package ph.gcash.marites.login.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserPayload (
    val name : String? = "",
    val userUID : String? = "",
    val email : String? = ""
    )