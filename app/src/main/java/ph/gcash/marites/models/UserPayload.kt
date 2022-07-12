package ph.gcash.marites.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserPayload (
    val name : String? = "",
    val userUID : String? = "",
    val email : String? = ""
    )