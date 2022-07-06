package ph.gcash.marites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import ph.gcash.marites.databinding.ActivityContactsBinding
import ph.gcash.marites.databinding.ActivityLoginBinding
import ph.gcash.marites.login.LoginActivity

class ContactsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactsBinding
    private val logger = "ContactsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbApp)

        getDataFromDatabase()
    }

    private fun getDataFromDatabase() {
        val usersReference = Firebase.database.getReference("Users")
        usersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.value
                Log.d(logger, "DATA: $value")

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_actionbar, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val logoutItem = menu?.findItem(R.id.action_logout)


        val searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                TODO("Not yet implemented")
            }


        })
        logoutItem!!.setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                FirebaseAuth.getInstance().signOut()

                val goToLoginActivity = Intent(this@ContactsActivity,LoginActivity::class.java)
                startActivity(goToLoginActivity)
                finish()
                return true
            }
        })


        return super.onCreateOptionsMenu(menu)
    }


}


