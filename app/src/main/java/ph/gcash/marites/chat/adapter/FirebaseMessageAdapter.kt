package ph.gcash.marites.chat.adapter

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.database.Query
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import ph.gcash.marites.R
import ph.gcash.marites.chat.model.MessagePayload
import ph.gcash.marites.databinding.ItemMessageBinding
import java.util.*

class FirebaseMessageAdapter(private val context: Context,
                             private val myUID: String,
                             query: Query)
    : FirebaseRecyclerAdapter<
        MessagePayload,
        FirebaseMessageAdapter.MessageViewHolder>(setRecyclerOptions(query)){

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int)
    : MessageViewHolder {
        val itemBinding = ItemMessageBinding
            .inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MessageViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder,
                                  position: Int,
                                  model: MessagePayload) {
        holder.bindItems(model)
    }

    inner class MessageViewHolder(private val itemMessageBinding: ItemMessageBinding)
        : RecyclerView.ViewHolder(itemMessageBinding.root){

        fun bindItems(data: MessagePayload){
            if (data.userUID == myUID){
                itemMessageBinding.llOuter.gravity = Gravity.END
                itemMessageBinding.llMessageBox
                    .setBackgroundResource(
                        R.drawable.sh_right_message_background
                    )

            } else {
                itemMessageBinding.llOuter.gravity = Gravity.START
                itemMessageBinding.llMessageBox
                    .setBackgroundResource(
                        R.drawable.sh_left_message_background
                    )
            }

            if (data.message != "") {
                val newTextView = TextView(context)
                newTextView.text = data.message
                newTextView.textSize = 21f
                newTextView.setTextColor(Color.BLACK)
                newTextView.setOnLongClickListener(object : View.OnLongClickListener {
                    override fun onLongClick(v: View?): Boolean {
                        speakOut(data.message)
                        return true
                    }

                })


                itemMessageBinding.flMessageContent.addView(newTextView)

            }



            if (data.imageUrl != "") {
                val newImageView = ImageView(context)
                val imageURL = FirebaseStorage
                    .getInstance()
                    .getReference(data.imageUrl)
                    .downloadUrl
                imageURL.loadIntoPicasso(newImageView)
                itemMessageBinding.flMessageContent.addView(newImageView)
            }

            itemMessageBinding.tvTimestamp.text = data.timestamp
        }
    }

    private val textToSpeechEngine: TextToSpeech by lazy {
        // Pass in context and the listener.
        TextToSpeech(context,
            TextToSpeech.OnInitListener { status ->
                // set our locale only if init was success.
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale.CANADA
                }
            })
    }

    private fun speakOut(data: String) {
        textToSpeechEngine.speak(
            data,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "tts1"
        )
    }

}

fun setRecyclerOptions(query : Query)
    : FirebaseRecyclerOptions<MessagePayload> {
    return FirebaseRecyclerOptions.Builder<MessagePayload>()
        .setQuery(query, MessagePayload::class.java)
        .build()
}

fun Task<Uri>.loadIntoPicasso(imageView: ImageView){
    addOnSuccessListener {
        Picasso.get().load(it).resize(600, 600).centerInside().into(imageView)
    }
}


