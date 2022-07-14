package ph.gcash.marites.main.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import ph.gcash.marites.R
import ph.gcash.marites.databinding.FragmentContactsBinding
import ph.gcash.marites.login.LoginActivity
import ph.gcash.marites.main.adapter.ContactsAdapter
import ph.gcash.marites.models.User
import ph.gcash.marites.utilities.UserPreference
import ph.gcash.marites.utilities.UserPreference.uid

class ContactsFragment : Fragment() {
    private lateinit var binding: FragmentContactsBinding
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>

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

        getDataFromDatabase()
    }

    private fun getDataFromDatabase() {
        val userId = UserPreference
            .getUserPreference(
                this.requireActivity().applicationContext,
                getString(R.string.app_id))
            .uid

        val usersReference = Firebase.database.getReference("Contacts").child(userId!!)

        usersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        userArrayList.add(user!!)
                    }

                    userRecyclerView.adapter = ContactsAdapter(
                        userArrayList,
                        this@ContactsFragment.requireActivity().applicationContext
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ContactsFragment.requireActivity().applicationContext,
                    "ERROR: ${error.toException()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}