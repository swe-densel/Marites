package ph.gcash.marites.main.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import ph.gcash.marites.R
import ph.gcash.marites.databinding.FragmentContactsBinding
import ph.gcash.marites.login.LoginActivity
import ph.gcash.marites.login.adapter.ContactsAdapter
import ph.gcash.marites.login.model.User
import ph.gcash.marites.utilities.UserPreference
import ph.gcash.marites.utilities.UserPreference.user

class ContactsFragment : Fragment() {
    private lateinit var binding: FragmentContactsBinding
    private lateinit var userReference : DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>
    private val logger = "ContactsActivity"
    private lateinit var storageReference: FirebaseStorage

    private lateinit var selectedImq: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userRecyclerView = binding.rvContacts
        userRecyclerView.layoutManager = LinearLayoutManager(this.requireActivity().applicationContext)
        userRecyclerView.setHasFixedSize(true)
        userArrayList = ArrayList<User>()

        fetchUserData()
        getDataFromDatabase()
    }


    private fun fetchUserData() {
        val user = UserPreference.getUserPreference(this.requireActivity().applicationContext, getString(R.string.app_id)).user
        val imageURL = FirebaseStorage.getInstance().reference.child("users/${user.userUID}").downloadUrl
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

                    userRecyclerView.adapter = ContactsAdapter(userArrayList,this@ContactsFragment.requireActivity().applicationContext)
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun signOutUser() {
        UserPreference.clearPrefs(this.requireActivity().applicationContext, getString(R.string.app_id))
        FirebaseAuth.getInstance().signOut()

        val goToLoginActivity = Intent(this.requireActivity().applicationContext, LoginActivity::class.java)
        startActivity(goToLoginActivity)
        this.requireActivity().finish()
    }

}