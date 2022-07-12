package ph.gcash.marites.main.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import ph.gcash.marites.R
import ph.gcash.marites.databinding.FragmentProfileBinding
import ph.gcash.marites.login.LoginActivity
import ph.gcash.marites.utilities.UserPreference
import ph.gcash.marites.utilities.UserPreference.user


class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        val userPref = UserPreference.getUserPreference(
            this.requireActivity().applicationContext,
            getString(R.string.app_id)
        )
        val user = userPref.user

        fetchUserData(user.userUID)


        binding.UUIDItem.text = user.userUID
        binding.userEmailItem.text = user.email
        binding.fullNameItem.text = user.name
        binding.btnLogout.setOnClickListener {
            signOutUser()
        }

    }

    private fun fetchUserData(userUID: String) {
        val imageURL =
            FirebaseStorage.getInstance().reference.child("users/$userUID").downloadUrl
        imageURL.loadIntoPicasso(binding.profileImage)
    }


    fun Task<Uri>.loadIntoPicasso(imageView: ShapeableImageView) {
        addOnSuccessListener {
            Picasso.get().load(it).resize(300, 300).centerInside().into(imageView)
        }
    }

    private fun signOutUser() {
        UserPreference.clearPrefs(this.requireActivity().applicationContext, getString(R.string.app_id))
        firebaseAuth.signOut()

        val goToLoginActivity = Intent(this.requireActivity().applicationContext, LoginActivity::class.java)
        startActivity(goToLoginActivity)
        this.requireActivity().finish()
    }
}


