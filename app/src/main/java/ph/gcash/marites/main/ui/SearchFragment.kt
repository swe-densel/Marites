package ph.gcash.marites.main.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import ph.gcash.marites.databinding.FragmentSearchBinding
import ph.gcash.marites.login.adapter.ContactsAdapter
import ph.gcash.marites.login.model.User

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var userReference : DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>
    private val logger = "ContactsActivity"
    private lateinit var storageReference: FirebaseStorage
    private lateinit var searchView : SearchView

    private lateinit var selectedImq: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        userRecyclerView = binding.rvContacts
        userRecyclerView.layoutManager =
            LinearLayoutManager(this.requireActivity().applicationContext)
        userRecyclerView.setHasFixedSize(true)
        userArrayList = ArrayList<User>()
        searchView = binding.searchView


        userRecyclerView.adapter = ContactsAdapter(userArrayList,this@SearchFragment.requireActivity().applicationContext)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                getDataFromDatabase()

                return true
            }


            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        val clearButton : ImageView = searchView.findViewById(androidx.appcompat.R.id.search_close_btn)
        clearButton.setOnClickListener {
            if (searchView.getQuery().length == 0) {
                searchView.setIconified(true);
            } else {

                userArrayList.clear()
                searchView.setQuery("", false);
            }
        };

    }


    private fun getDataFromDatabase() {
        val usersReference = Firebase.database.getReference("Users")
        usersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                userArrayList.clear()

                for (snap in snapshot.children){
                    if (snap.child("email").value==searchView.query.toString())
                    {
                        val user = snap.getValue(User::class.java)
                        userArrayList.add(user!!)
                    }
                }
                userRecyclerView.adapter!!.notifyDataSetChanged()
                Log.d("SearchFragment",userArrayList.toString())


//                if (snapshot.exists()){
//
//                    for (userSnapshot in snapshot.children){
//                        val user = userSnapshot.getValue(User::class.java)
//                        userArrayList.add(user!!)
//                    }
//
//                    userRecyclerView.adapter = ContactsAdapter(userArrayList,this@SearchFragment.requireActivity().applicationContext)
//                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}