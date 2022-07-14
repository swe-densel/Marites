package ph.gcash.marites.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import ph.gcash.marites.R
import ph.gcash.marites.chat.adapter.FirebaseMessageAdapter
import ph.gcash.marites.models.ContactPayload
import ph.gcash.marites.models.MessagePayload
import ph.gcash.marites.databinding.ActivityChatBinding
import ph.gcash.marites.models.User
import ph.gcash.marites.utilities.EpochGenerator
import ph.gcash.marites.utilities.TimestampGenerator
import ph.gcash.marites.utilities.UUIDGenerator
import ph.gcash.marites.utilities.UserPreference
import ph.gcash.marites.utilities.UserPreference.user
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

        initFirebase()
        initUsers()
        bindViews()
    }

    private fun bindViews() {
        binding.tvChatName.text = userToChat.name
        binding.ivBack.setOnClickListener(this)
        binding.ivSend.setOnClickListener(this)
        binding.ivUploadImg.setOnClickListener(this)
    }

    private fun initUsers() {
        //get current user of the app
        currentUser = getCurrentUser()

        //user to chat object (this is provided by SearchFragment)
        userToChat = intent.extras?.getSerializable("ChatUser") as User

        //check if contact exists in database
        checkExistingContact()
    }

    private fun getCurrentUser(): User {
        return UserPreference
            .getUserPreference(this, getString(R.string.app_id))
            .user
    }

    private fun initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
    }

    /**
     checkExistingContact() - checks if the user to chat has an entry under the
        current user's contacts in the Realtime Database.

        IF EXISTING: gets the roomID value to be use for referencing the Chatrooms node in the
            Realtime Database

     **/
    private fun checkExistingContact() {
        val contactPath = "Contacts/${currentUser.userUID}/${userToChat.userUID}"

        firebaseDatabase
            .getReference(contactPath)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        hasContact = true
                        roomID = snapshot.child("roomID").getValue(String::class.java)!!
                        getChatroomMessages()
                    } else {
                        hasContact = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(logger, "ERROR: ${error.toException()}")
                }
            })
    }

    private fun getChatroomMessages() {
        setMessageReference()
        initRVAdapter().startListening()
    }

    private fun setMessageReference() {
        val roomPath = "Chatrooms/$roomID"
        messageReference = firebaseDatabase.getReference(roomPath)
    }

    private fun initRVAdapter(): FirebaseMessageAdapter {
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
        return messageAdapter
    }

    private fun saveMessageToDatabase(
        messagePayload: MessagePayload
    ) {
        messageReference
            .child(EpochGenerator.generateString())
            .setValue(messagePayload)
    }

    private fun saveImageToStorage(imgData: Uri) {
        val timestampEpoch = Instant.now().toEpochMilli().toString()
        val directory = "images/$timestampEpoch.jpg"

        if (!hasContact) {
            createContactEntry()
        }

        firebaseStorage
            .getReference(directory)
            .putFile(imgData)
            .addOnSuccessListener {
                val messageToSend = MessagePayload(
                    timestamp = TimestampGenerator.generateString(),
                    userUID = currentUser.userUID,
                    imageUrl = directory
                )
                saveMessageToDatabase(messageToSend)
            }

    }

    private fun createContactEntry() {
        roomID = UUIDGenerator.generateString()
        setMessageReference()
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

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.iv_send -> {
                if (!hasContact) {
                    createContactEntry()
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

            R.id.iv_back -> { finish() }

            R.id.iv_uploadImg -> {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                photoResultLaunch.launch(intent)
            }
        }
    }

    override fun finish() {
        messageAdapter.stopListening()
        super.finish()
    }
}