package ph.gcash.marites.login.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ph.gcash.marites.ContactsActivity
import ph.gcash.marites.login.model.User
import ph.gcash.marites.databinding.FragmentRegistrationBinding
import ph.gcash.marites.login.model.UserPayload
import ph.gcash.marites.utilities.UserPreference
import ph.gcash.marites.utilities.UserPreference.user

class RegistrationFragment(val prefKey: String) : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var selectedImq: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFirebaseInstances()

        val photoResultLauncher = photoResultLaunch()

        binding.btnRegister.setOnClickListener {
            val email = binding.tieEmail.text.toString()
            val password = binding.tiePassword.text.toString()
            val name = binding.tieName.text.toString()
            val confirmPassword = binding.tieConfirm.text.toString()
            val image = binding.profileImage.drawable

            val newUser = User(name, email, password, confirmPassword, image)
            val areInputsValid = checkInputs(newUser)

            if (areInputsValid) {
                registerUser(newUser)
            }
        }

        binding.profileImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            photoResultLauncher.launch(intent)
        }
    }

    private fun photoResultLaunch() = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            if (data != null && data.data != null) {
                selectedImq = data.data!!
                binding.profileImage.setImageURI(selectedImq)
            }
        }
    }

    private fun initFirebaseInstances() {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
    }

    private fun registerUser(newUser: User) {
        firebaseAuth
            .createUserWithEmailAndPassword(newUser.email, newUser.password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    newUser.userUID = firebaseAuth.currentUser!!.uid
                    uploadUserImage(newUser.userUID)
                    saveUserToDatabase(newUser)
                    saveUserToPref(newUser)

                    val intent = Intent(
                        this.requireActivity().applicationContext,
                        ContactsActivity::class.java
                    )
                    startActivity(intent)
                    this.requireActivity().finish()

                } else {
                    Toast.makeText(
                        this.requireActivity().applicationContext,
                        "Registration Failed: ${it.exception}",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

    private fun saveUserToPref(newUser: User) {
        val userPref = UserPreference.getUserPreference(
            this.requireActivity().applicationContext, prefKey
        )
        userPref.user = newUser
    }

    private fun checkInputs(newUser : User): Boolean {
        if (newUser.email.isEmpty()) {
            Toast.makeText(
                this.requireActivity().applicationContext,
                "Please enter your email address.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (newUser.password.isEmpty()) {
            Toast.makeText(
                this.requireActivity().applicationContext,
                "Please enter a password that must contain at least 8 characters.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (newUser.name.isEmpty()) {
            Toast.makeText(
                this.requireActivity().applicationContext,
                "Please enter your name.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (newUser.password != newUser.confirmPassword) {
            Toast.makeText(
                this.requireActivity().applicationContext,
                "Passwords do not match.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (newUser.image == null) {
            Toast.makeText(
                this.requireActivity().applicationContext,
                "Please upload a photo of yourself.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    private fun uploadUserImage(uid: String) {
        val ref = FirebaseStorage.getInstance().getReference("users/$uid")
        ref.putFile(selectedImq)
    }

    private fun saveUserToDatabase(newUser: User) {
        val userRef = firebaseDatabase.getReference("Users")
        val createUserRequest = UserPayload(
            newUser.name,
            newUser.userUID,
            newUser.email
        )
        userRef.child(newUser.userUID).setValue(createUserRequest)
    }
}





