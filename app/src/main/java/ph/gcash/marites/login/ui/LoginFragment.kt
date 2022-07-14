package ph.gcash.marites.login.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ph.gcash.marites.R
import ph.gcash.marites.databinding.FragmentLoginBinding
import ph.gcash.marites.models.User
import ph.gcash.marites.models.UserPayload
import ph.gcash.marites.main.MainActivity
import ph.gcash.marites.utilities.UserPreference
import ph.gcash.marites.utilities.UserPreference.user

class LoginFragment() : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firesbaseDatabase: FirebaseDatabase

    override fun onStart() {
        super.onStart()
        checkIfUserLoggedIn()
    }

    private fun checkIfUserLoggedIn() {
        if (firebaseAuth.currentUser != null) {
            moveToMainActivity()
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
        initFirebase()

        binding.btnLogin.setOnClickListener {
            val email = binding.tieEmail.text.toString()
            val password = binding.tiePassword.text.toString()

            val loginUser = User()
            loginUser.email = email
            loginUser.password = password

            val areFieldsValid = checkLoginFields(loginUser)
            if (areFieldsValid) {
                signInUser(loginUser)
            }
        }

    }

    private fun initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
        firesbaseDatabase = FirebaseDatabase.getInstance()
    }

    private fun signInUser(loginUser: User) {
        firebaseAuth
            .signInWithEmailAndPassword(loginUser.email, loginUser.password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showProgressBar()
                    getUserDataFromDatabase(firebaseAuth.currentUser!!.uid)

                } else {
                    hideProgressBar()
                    Toast.makeText(
                        this.requireActivity().applicationContext,
                        "Wrong Credentials",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun showProgressBar() {
        binding.pbLoginProgress.visibility = ProgressBar.VISIBLE
        binding.tvLoginMessage.text = getString(R.string.please_wait)
    }

    private fun hideProgressBar() {
        binding.pbLoginProgress.visibility = ProgressBar.GONE
        binding.tvLoginMessage.text = getString(R.string.register_now)
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
        val usersReference = firesbaseDatabase.getReference("Users/$userUID")
        usersReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userPayload = snapshot.getValue(UserPayload::class.java)

                userPayload?.let {
                    saveUserToPref(it)
                    moveToMainActivity()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@LoginFragment.requireActivity().applicationContext,
                    "ERROR: ${error.toException()}",
                    Toast.LENGTH_SHORT
                ).show()
                hideProgressBar()
            }
        })
    }

    private fun moveToMainActivity() {
        val goToMainActivity = Intent(
            this@LoginFragment.requireActivity().applicationContext,
            MainActivity::class.java
        )
        startActivity(goToMainActivity)
        this@LoginFragment.requireActivity().finish()
    }

    private fun saveUserToPref(loginUser: UserPayload) {
        val userPref = UserPreference.getUserPreference(
            this.requireActivity().applicationContext,
            getString(R.string.app_id)
        )
        userPref.user = User(name = loginUser.name!!,
                        email = loginUser.email!!,
                        userUID = loginUser.userUID!!)

    }
}