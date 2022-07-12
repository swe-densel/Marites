package ph.gcash.marites.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import ph.gcash.marites.R
import ph.gcash.marites.chat.adapter.FirebaseMessageAdapter
import ph.gcash.marites.chat.model.ContactPayload
import ph.gcash.marites.chat.model.MessagePayload
import ph.gcash.marites.databinding.ActivityChatBinding
import ph.gcash.marites.login.model.User
import ph.gcash.marites.utilities.UserPreference
import ph.gcash.marites.utilities.UserPreference.user
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChatActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityChatBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseStorage: FirebaseStorage
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

        //user to chat object (this is provided by SearchFragment)
        //ONLY USED FOR TESTING
        userToChat = User("Second User", "secondUser@email.com", "another_random_id2")
        roomID = ""
        //ONLY USED FOR TESTING

        binding.tvChatName.text = userToChat.name


        initFirebase()

        //check if contact exists in database
        checkExistingContact()

        binding.ivBack.setOnClickListener(this)
        binding.ivSend.setOnClickListener(this)
        binding.ivUploadImg.setOnClickListener(this)
    }

    private fun checkExistingContact() {
        firebaseDatabase
            .getReference("Contacts/${currentUser.userUID}/${userToChat.userUID}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        hasContact = true
                        roomID = snapshot.child("roomID").getValue(String::class.java)!!
                        getDataFromDatabase()
                    } else {
                        hasContact = false
                    }
                    Log.d(logger, "hasContact = $hasContact")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(logger, "ERROR: ${error.toException()}")
                }

            })
    }

    private fun initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
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

    private val photoResultLaunch = registerForActivityResult(
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

        createContactEntry()

        firebaseStorage
            .getReference(directory)
            .putFile(imgData)
            .addOnSuccessListener {
                val messageToSend = MessagePayload(
                    timestamp = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm")),
                    userUID = currentUser.userUID,
                    imageUrl = directory
                )
                saveMessageToDatabase(messageToSend)
            }

    }

    private fun createContactEntry() {
        if (!hasContact) {
            roomID = UUID.randomUUID().toString()
            messageReference = firebaseDatabase.getReference("Chatrooms").child(roomID)
            firebaseDatabase
                .getReference("Contacts/${currentUser.userUID}/${userToChat.userUID}")
                .setValue(
                    ContactPayload(
                        userToChat.email,
                        userToChat.name,
                        roomID,
                        userToChat.userUID
                    )
                )
            firebaseDatabase
                .getReference("Contacts/${userToChat.userUID}/${currentUser.userUID}")
                .setValue(
                    ContactPayload(
                        currentUser.email,
                        currentUser.name,
                        roomID,
                        currentUser.userUID
                    )
                )
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.iv_send -> {
                createContactEntry()

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

            R.id.iv_back -> { finish() }

            R.id.iv_uploadImg -> {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                photoResultLaunch.launch(intent)
            }
        }
    }
}