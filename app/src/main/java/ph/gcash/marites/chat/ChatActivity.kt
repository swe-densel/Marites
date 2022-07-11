package ph.gcash.marites.chat

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import ph.gcash.marites.R
import ph.gcash.marites.chat.adapter.FirebaseMessageAdapter
import ph.gcash.marites.chat.model.MessagePayload
import ph.gcash.marites.databinding.ActivityChatBinding
import ph.gcash.marites.login.model.User
import ph.gcash.marites.utilities.UserPreference
import ph.gcash.marites.utilities.UserPreference.uid
import ph.gcash.marites.utilities.UserPreference.user
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var messageReference: DatabaseReference

    private val logger = "ChatActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbContact)

        val roomID = "some_room_id"

        val currentUser = getCurrentUser()
        Log.d(logger, "${currentUser.userUID}, ${currentUser.name}, ${currentUser.email}")


//        firebaseDatabase = FirebaseDatabase.getInstance()
//        messageReference = firebaseDatabase.getReference("Chatrooms").child(roomID)
//
//        getDataFromDatabase(roomID, userPref.uid!!)
//
//        binding.ivSend.setOnClickListener {
//            val messageToSend = MessagePayload(
//                message = binding.etMessage.text.toString(),
//                timestamp = LocalDateTime
//                    .now()
//                    .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm")),
//                userUID = userPref.uid!!
//            )
//            saveMessageToDatabase(messageToSend)
//            binding.etMessage.text.clear()
//        }
//
//        val photoResultLauncher = photoResultLaunch()
//        binding.ivUploadImg.setOnClickListener {
//            val intent = Intent()
//            intent.action = Intent.ACTION_GET_CONTENT
//            intent.type = "image/*"
//            photoResultLauncher.launch(intent)
//        }
    }

    private fun getCurrentUser(): User {
        val userPref = UserPreference
            .getUserPreference(this, getString(R.string.app_id))

        userPref.user = User(
            "First Last",
            "firstLast@email.com",
        "some_random_id")

        return userPref.user
    }

    private fun getDataFromDatabase(
        roomID: String,
        userUID: String
    ) {
        val messageAdapter = FirebaseMessageAdapter(
            this,
            userUID,
            messageReference.limitToLast(50)
        )

        binding.rvMessages.layoutManager = LinearLayoutManager(
            applicationContext,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvMessages.adapter = messageAdapter
        binding.rvMessages.setHasFixedSize(true)

        messageAdapter.startListening()
    }

    private fun saveMessageToDatabase(
        messagePayload: MessagePayload
    ) {
        messageReference
            .child(Instant.now().toEpochMilli().toString())
            .setValue(messagePayload)
    }

//    private fun photoResultLaunch() = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val data: Intent? = result.data
//
//            if (data != null && data.data != null) {
//                saveImageToStorage(data.data!!)
//            }
//        }
//    }

//    private fun saveImageToStorage(imgData: Uri) {
//        val timestampEpoch = Instant.now().toEpochMilli().toString()
//        val directory = "images/$timestampEpoch/.jpg"
//
//        val ref = FirebaseStorage.getInstance().getReference(directory)
//        ref.putFile(imgData)
//
//        val messageToSend = MessagePayload(
//            timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm")),
//            userUID = getPreference().uid!!,
//            imageUrl = directory
//        )
//        saveMessageToDatabase(messageToSend)
//    }
}