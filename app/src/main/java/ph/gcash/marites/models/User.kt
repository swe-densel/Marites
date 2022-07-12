package ph.gcash.marites.models

import android.graphics.drawable.Drawable
import java.io.Serializable

class User () : Serializable {
    var name : String = ""
    var email : String = ""
    var password : String = ""
    var confirmPassword : String = ""
    var userUID : String = ""
    var image : Drawable? = null



    constructor(name: String, email: String) : this() {
        this.name = name
        this.email = email
    }

    constructor(name: String, email: String, userUID: String) : this() {
        this.name = name
        this.email = email
        this.userUID = userUID
    }

    constructor(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        image: Drawable?
    ) : this() {

        this.name = name
        this.email = email
        this.password = password
        this.confirmPassword = confirmPassword
        this.image = image
    }

}