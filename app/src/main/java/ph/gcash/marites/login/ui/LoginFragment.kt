package ph.gcash.marites.login.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ph.gcash.marites.ContactsActivity
import ph.gcash.marites.R
import ph.gcash.marites.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            val goToContactsActivity = Intent(
                this.requireActivity().applicationContext,
                ContactsActivity::class.java)
            goToContactsActivity.putExtra("email", binding.tieEmail.text.toString())
            startActivity(goToContactsActivity)
            this.requireActivity().finish()
        }
    }
}