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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import ph.gcash.marites.R
import ph.gcash.marites.chat.adapter.FirebaseMessageAdapter
import ph.gcash.marites.chat.model.ContactPayload
import ph.gcash.marites.chat.model.MessagePayload
import ph.gcash.marites.databinding.ActivityChatBinding
import ph.gcash.marites.login.model.User
import ph.gcash.marites.utilities.UserPreference
import ph.gcash.marites.utilities.UserPreference.contact
import ph.gcash.marites.utilities.UserPreference.uid
import ph.gcash.marites.utilities.UserPreference.user
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var messageReference: DatabaseReference

    private lateinit var currentUser: User
    private lateinit var userToChat: User
    private lateinit var roomID: String
    private var hasContact: Boolean = false

    private lateinit var messageAdapter: FirebaseMessageAdapter

    private val logger = "ChatActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get current user of the app
        currentUser = getCurrentUser()
        Log.d(logger, "${currentUser.userUID}, ${currentUser.name}, ${currentUser.email}")

        //user to chat object
        //ONLY USED FOR TESTING
        userToChat = User("Second User", "secondUser@email.com", "another_random_id2")
        roomID = ""
        //ONLY USED FOR TESTING

        binding.tvChatName.text = userToChat.name
        binding.ivBack.setOnClickListener { finish() }

        firebaseDatabase = FirebaseDatabase.getInstance()

        //check if contact exists in database
        firebaseDatabase
            .getReference("Contacts/${currentUser.userUID}/${userToChat.userUID}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        hasContact = true
                        roomID = snapshot.child("roomID").getValue(String::class.java)!!
                        getDataFromDatabase()
                    } else {
                        hasContact = false
                    }
                    Log.d(logger, "hasContact = $hasContact")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        binding.ivSend.setOnClickListener {
            if (!hasContact) {
                roomID = UUID.randomUUID().toString()
                messageReference = firebaseDatabase.getReference("Chatrooms").child(roomID)
                firebaseDatabase
                    .getReference("Contacts/${currentUser.userUID}/${userToChat.userUID}")
                    .setValue(ContactPayload(
                        userToChat.email,
                        userToChat.name,
                        roomID,
                        userToChat.userUID
                    ))
                firebaseDatabase
                    .getReference("Contacts/${userToChat.userUID}/${currentUser.userUID}")
                    .setValue(ContactPayload(
                        currentUser.email,
                        currentUser.name,
                        roomID,
                        currentUser.userUID
                    ))
            }

            val messageToSend = MessagePayload(
                message = binding.etMessage.text.toString(),
                timestamp = LocalDateTime
                    .now()
                    .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm")),
                userUID = currentUser.userUID
            )
            saveMessageToDatabase(messageToSend)
            binding.etMessage.text.clear()

        }

        val photoResultLauncher = photoResultLaunch()
        binding.ivUploadImg.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            photoResultLauncher.launch(intent)
        }
    }

    private fun getCurrentUser(): User {
        val userPref = getCurrentPreference()

        //ONLY USED FOR TESTING
        userPref.user = User(
            "First Last",
            "firstLast@email.com",
        "some_random_id")
        //ONLY USED FOR TESTING

        return userPref.user
    }

    private fun getCurrentPreference() = UserPreference
        .getUserPreference(this, getString(R.string.app_id))

    private fun getDataFromDatabase() {
        messageReference = firebaseDatabase.getReference("Chatrooms").child(roomID)
        messageAdapter = FirebaseMessageAdapter(
            this,
            currentUser.userUID,
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

    private fun photoResultLaunch() = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            if (data != null && data.data != null) {
                saveImageToStorage(data.data!!)
            }
        }
    }

    private fun saveImageToStorage(imgData: Uri) {
        val timestampEpoch = Instant.now().toEpochMilli().toString()
        val directory = "images/$timestampEpoch.jpg"

        if (!hasContact) {
            roomID = UUID.randomUUID().toString()
            messageReference = firebaseDatabase.getReference("Chatrooms").child(roomID)
            firebaseDatabase
                .getReference("Contacts/${currentUser.userUID}/${userToChat.userUID}")
                .setValue(ContactPayload(
                    userToChat.email,
                    userToChat.name,
                    roomID,
                    userToChat.userUID
                ))
            firebaseDatabase
                .getReference("Contacts/${userToChat.userUID}/${currentUser.userUID}")
                .setValue(ContactPayload(
                    currentUser.email,
                    currentUser.name,
                    roomID,
                    currentUser.userUID
                ))
        }

        val ref = FirebaseStorage.getInstance()
            .getReference(directory)
            .putFile(imgData)
            .addOnSuccessListener {
                val messageToSend = MessagePayload(
                    timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm")),
                    userUID = currentUser.userUID,
                    imageUrl = directory
                )
                saveMessageToDatabase(messageToSend)
            }

    }
}