package ph.gcash.marites.login.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import ph.gcash.marites.ContactsActivity
import ph.gcash.marites.User
import ph.gcash.marites.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var selectedImq: Uri


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()


//        binding.textView.setOnClickListener {
//            val intent = Intent(this.requireActivity().applicationContext, LoginFragment::class.java)
//            startActivity(intent)
//        }


        binding.btnRegister.setOnClickListener {
            val email = binding.tieEmail.text.toString()
            val pass = binding.tiePassword.text.toString()
            val fullName = binding.tieName.text.toString()
            val confirm = binding.tieConfirm.text.toString()
            val image = binding.profileImage.drawable


            if (email.isNotEmpty() && pass.isNotEmpty() && fullName.isNotEmpty()) {
                if (pass == confirm) {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener {
                            if (it.isSuccessful && image != null) {


                                val intent = Intent(
                                    this.requireActivity().applicationContext,
                                    ContactsActivity::class.java
                                )
                                startActivity(intent)

                                uploadImageToFirebaseStorage(firebaseAuth.currentUser!!.uid)

                                writeDataBase(fullName,firebaseAuth.currentUser!!.uid , "anystring")




                            } else {
                                Toast.makeText(
                                    this.requireActivity().applicationContext,
                                    "Password must contain atleast 8 characters and Photo is Required",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                } else {
                    Toast.makeText(
                        this.requireActivity().applicationContext,
                        "Password is not matching",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this.requireActivity().applicationContext,
                    "Empty Fields Are not Allowed !!",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }

        binding.profileImage.setOnClickListener {
            Toast.makeText(
                this.requireActivity().applicationContext,
                "Try to Show photo selector",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {

            if (data.data != null) {
                selectedImq = data.data!!

                binding.profileImage.setImageURI(selectedImq)
            }
        }


    }

    private fun uploadImageToFirebaseStorage(uid: String) {
//               val filename = UUID.randomUUID().toString()
        val filename = uid
        val ref = FirebaseStorage.getInstance().getReference("users/$filename")

        ref.putFile(selectedImq)

    }

    // function to write user database

    private fun writeDataBase(name : String , userUID : String , email: String) {
        val database = Firebase.database
        val userRef = database.getReference("Users")
        val user = User(name, userUID , email)
        userRef.child(userUID).setValue(user)
    }
}





