package ph.gcash.marites.login.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil.bind
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import ph.gcash.marites.ContactsActivity
import ph.gcash.marites.MainActivity
import ph.gcash.marites.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth


//    override fun onStart() {
//        super.onStart()
//
//        if(firebaseAuth.currentUser != null){
//            val intent = Intent(this.requireActivity().applicationContext,
//            MainActivity::class.java)
//            startActivity(intent)
//        }
//    }

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
//        binding.textView.setOnClickListener {
//            val intent = Intent(this.requireActivity().applicationContext,
//                LoginFragment::class.java)
//            startActivity(intent)
//        }

        binding.btnLogin.setOnClickListener {
            val email = binding.tieEmail.text.toString()
            val pass = binding.tiePassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val goToContactsActivity = Intent(
                            this.requireActivity().applicationContext,
                            ContactsActivity::class.java)
                        goToContactsActivity.putExtra("email", binding.tieEmail.text.toString())
                        startActivity(goToContactsActivity)
                        this.requireActivity().finish()
                    } else {
                        Toast.makeText(this.requireActivity().applicationContext,"Wrong Credentials", Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(this.requireActivity().applicationContext, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }

    }
}