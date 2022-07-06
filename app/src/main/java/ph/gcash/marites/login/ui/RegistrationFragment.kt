package ph.gcash.marites.login.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil.setContentView
import com.google.firebase.auth.FirebaseAuth
import ph.gcash.marites.ContactsActivity
import ph.gcash.marites.R
import ph.gcash.marites.databinding.FragmentLoginBinding
import ph.gcash.marites.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var firebaseAuth: FirebaseAuth

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

        binding.btnRegister.setOnClickListener {
            val email = binding.tieEmail.text.toString()
            val pass = binding.tiePassword.text.toString()
            val FullName = binding.tieName.text.toString()
            val confirm = binding.tieConfirm.text.toString()


            if (email.isNotEmpty() && pass.isNotEmpty()  && FullName.isNotEmpty() ) {
                if (pass == confirm) {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this.requireActivity().applicationContext, ContactsActivity::class.java)
                            startActivity(intent)

                        } else {
                            Toast.makeText(this.requireActivity().applicationContext,"Password must contain atleast 8 characters", Toast.LENGTH_SHORT).show()

                        }
                    }
                } else {
                    Toast.makeText(this.requireActivity().applicationContext, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this.requireActivity().applicationContext, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }

}