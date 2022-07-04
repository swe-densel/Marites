package ph.gcash.marites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ph.gcash.marites.databinding.ActivityContactsBinding
import ph.gcash.marites.databinding.ActivityLoginBinding

class ContactsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactsBinding
    private val logger = "ContactsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbApp)

        val email = intent.getStringExtra("email")
        Log.d(logger, "Email: $email")
    }
}