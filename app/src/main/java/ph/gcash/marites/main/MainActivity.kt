package ph.gcash.marites.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import ph.gcash.marites.R
import ph.gcash.marites.databinding.ActivityMainBinding
import ph.gcash.marites.main.ui.ContactsFragment
import ph.gcash.marites.main.ui.InfoFragment
import ph.gcash.marites.main.ui.ProfileFragment
import ph.gcash.marites.main.ui.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(ContactsFragment())
        binding.botnavMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                R.id.item_contacts -> {
                    loadFragment(ContactsFragment())
                    true
                }
                R.id.item_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.item_info -> {
                    loadFragment(InfoFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fl_main, fragment).addToBackStack(null).commit()
    }
}