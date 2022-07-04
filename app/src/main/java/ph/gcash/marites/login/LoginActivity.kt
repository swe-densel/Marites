package ph.gcash.marites.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import ph.gcash.marites.R
import ph.gcash.marites.databinding.ActivityLoginBinding
import ph.gcash.marites.login.adapter.LoginAdapter
import ph.gcash.marites.login.ui.LoginFragment
import ph.gcash.marites.login.ui.RegistrationFragment

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val loginFragment = LoginFragment()
    private val registrationFragment = RegistrationFragment()
    private lateinit var loginAdapter: LoginAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFragments()
    }

    private fun initFragments() {
        loginAdapter = LoginAdapter(supportFragmentManager)
        loginAdapter.add(loginFragment)
        loginAdapter.add(registrationFragment)
        binding.vpLogin.adapter = loginAdapter
    }
}