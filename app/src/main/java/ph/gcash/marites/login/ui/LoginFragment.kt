package ph.gcash.marites.login.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import ph.gcash.marites.ContactsActivity
import ph.gcash.marites.databinding.FragmentLoginBinding
import ph.gcash.marites.login.model.User
import ph.gcash.marites.login.model.UserPayload
import ph.gcash.marites.utilities.UserPreference
import ph.gcash.marites.utilities.UserPreference.email
import ph.gcash.marites.utilities.UserPreference.name
import ph.gcash.marites.utilities.UserPreference.uid

class LoginFragment(val prefKey: String) : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val intent = Intent(
                this.requireActivity().applicationContext,
                ContactsActivity::class.java
            )
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.tieEmail.text.toString()
            val password = binding.tiePassword.text.toString()

            val loginUser = User(email, password)
            val areFieldsValid = checkLoginFields(loginUser)

            if (areFieldsValid) {
                signInUser(loginUser)
            }
        }

    }

    private fun signInUser(loginUser: User) {
        firebaseAuth
            .signInWithEmailAndPassword(loginUser.email, loginUser.password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    getUserDataFromDatabase(firebaseAuth.currentUser!!.uid)
                    val goToContactsActivity = Intent(
                        this.requireActivity().applicationContext,
                        ContactsActivity::class.java)
                    startActivity(goToContactsActivity)
                    this.requireActivity().finish()

                } else {
                    Toast.makeText(
                        this.requireActivity().applicationContext,
                        "Wrong Credentials",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun checkLoginFields(loginUser: User): Boolean {
        if (loginUser.email.isEmpty()) {
            Toast.makeText(
                this.requireActivity().applicationContext,
                "Please enter your email address.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (loginUser.password.isEmpty()) {
            Toast.makeText(
                this.requireActivity().applicationContext,
                "Please enter your password.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    private fun getUserDataFromDatabase(userUID : String) {
        val usersReference = Firebase.database.getReference("Users").child(userUID)
        usersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userPayload = snapshot.getValue(UserPayload::class.java)
                userPayload?.let { saveUserToPref(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@LoginFragment.requireActivity().applicationContext,
                    "ERROR: ${error.toException()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun saveUserToPref(loginUser: UserPayload) {
        val userPref = UserPreference.getUserPreference(
            this.requireActivity().applicationContext, prefKey
        )
        userPref.name = loginUser.name
        userPref.uid = loginUser.userUID
        userPref.email = loginUser.email
    }
}