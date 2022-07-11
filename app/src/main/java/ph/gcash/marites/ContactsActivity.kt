package ph.gcash.marites

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import ph.gcash.marites.databinding.ActivityContactsBinding
import ph.gcash.marites.login.LoginActivity
import ph.gcash.marites.login.adapter.ContactsAdapter
import ph.gcash.marites.login.model.User

class ContactsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContactsBinding
    private lateinit var userReference : DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>
    private val logger = "ContactsActivity"
    private lateinit var storageReference: FirebaseStorage

    private lateinit var selectedImq: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbApp)
        userRecyclerView = findViewById(R.id.rv_contacts)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)
        userArrayList = ArrayList<User>()



        fetchData()



        getDataFromDatabase()
    }


    private fun fetchData() {
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val imageURL = FirebaseStorage.getInstance().reference.child("users/$userID").downloadUrl

        imageURL.loadIntoPicasso(binding.profilePicture)



        }

    fun Task<Uri>.loadIntoPicasso(imageView: ShapeableImageView) {
        addOnSuccessListener {
            Picasso.get().load(it).resize(300, 300).centerInside().into(imageView)
        }
    }

    private fun getDataFromDatabase() {
        val usersReference = Firebase.database.getReference("Users")
        usersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (userSnapshot in snapshot.children){
                        val user = userSnapshot.getValue(User::class.java)
                        userArrayList.add(user!!)
                    }

                    userRecyclerView.adapter = ContactsAdapter(userArrayList)
                }

//                val value = snapshot.value
//                Log.d(logger, "DATA: $value")

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


