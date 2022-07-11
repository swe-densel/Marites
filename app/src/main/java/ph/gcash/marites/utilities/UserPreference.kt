package ph.gcash.marites.utilities

import android.content.Context
import android.content.SharedPreferences
import ph.gcash.marites.login.model.User

object UserPreference {
    val userUID = "USER_UID"
    val userName = "USER_NAME"
    val userEmail = "USER_EMAIL"
    val userContact = "HAS_CONTACT"

    fun getUserPreference(context: Context, prefkey: String):
            SharedPreferences = context
        .getSharedPreferences(prefkey, Context.MODE_PRIVATE)

    private inline fun SharedPreferences.editor(
        operation: (SharedPreferences.Editor) -> Unit
    ) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    fun clearPrefs(context: Context, prefkey: String){
        val editor: SharedPreferences.Editor = getUserPreference(context, prefkey).edit()
        editor.clear()
        editor.apply()
    }

    var SharedPreferences.user : User
        get() = User(name!!, email!!, uid!!)
        set(value) {
            editor {
                it.putString(userUID, value.userUID)
                it.putString(userName, value.name)
                it.putString(userEmail, value.email)
            }
        }

    var SharedPreferences.uid
    get() = getString(userUID, "")
    set(value) {
        editor {
            it.putString(userUID, value)
        }
    }

    var SharedPreferences.name
        get() = getString(userName, "")
        set(value) {
            editor {
                it.putString(userName, value)
            }
        }

    var SharedPreferences.email
        get() = getString(userEmail, "")
        set(value) {
            editor {
                it.putString(userEmail, value)
            }
        }

    var SharedPreferences.contact
        get() = getBoolean(userContact, false)
        set(value) {
            editor {
                it.putBoolean(userContact, value)
            }
        }

}