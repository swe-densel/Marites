package ph.gcash.marites.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import ph.gcash.marites.R
import ph.gcash.marites.databinding.ActivityMainBinding
import ph.gcash.marites.main.ui.AboutFragment
import ph.gcash.marites.main.ui.ContactsFragment
import ph.gcash.marites.main.ui.ProfileFragment
import ph.gcash.marites.main.ui.SearchFragment

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(ContactsFragment())
        binding.botnavMain.setOnItemSelectedListener(this)
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction
            .replace(R.id.fl_main, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_profile -> {
                loadFragment(ProfileFragment())
                return true
            }
            R.id.item_contacts -> {
                loadFragment(ContactsFragment())
                return true
            }
            R.id.item_search -> {
                loadFragment(SearchFragment())
                return true
            }
            R.id.item_info -> {
                loadFragment(AboutFragment())
                return true
            }
            else -> {
                return false
            }
        }
    }
}