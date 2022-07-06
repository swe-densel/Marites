package ph.gcash.marites

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class User (val name : String , val userUID : String){

}