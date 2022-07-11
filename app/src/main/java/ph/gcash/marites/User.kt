package ph.gcash.marites

import android.provider.ContactsContract
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class User (var name : String? ="" , var userUID : String? ="", var email: String? =""){

}