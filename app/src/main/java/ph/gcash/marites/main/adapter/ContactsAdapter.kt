package ph.gcash.marites.main.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import ph.gcash.marites.R
import ph.gcash.marites.chat.ChatActivity
import ph.gcash.marites.chat.adapter.loadIntoPicasso
import ph.gcash.marites.databinding.ItemContactBinding
import ph.gcash.marites.models.User

class ContactsAdapter(private val userList : ArrayList<User>, private val context: Context)
    : RecyclerView.Adapter<ContactsAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemContactBinding = ItemContactBinding
            .inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        return MyViewHolder(itemContactBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = userList[position]
        holder.bindItem(currentitem)
    }

    fun Task<Uri>.loadIntoPicasso(imageView: ShapeableImageView) {
        addOnSuccessListener {
            Picasso.get().load(it).resize(300, 300).centerInside().into(imageView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class MyViewHolder(private val itemContactBinding: ItemContactBinding)
        : RecyclerView.ViewHolder(itemContactBinding.root){

        fun bindItem(currentItem: User) {
            val fullName : TextView = itemContactBinding.fullNameItem
            val userEmail : TextView =  itemContactBinding.userEmailItem
            val contactsPhoto : ShapeableImageView = itemContactBinding.contactImage

            fullName.text = currentItem.name
            userEmail.text = currentItem.email

            val userId = currentItem.userUID
            val imageURL = FirebaseStorage
                .getInstance()
                .getReference("users/$userId")
                .downloadUrl
            imageURL.loadIntoPicasso(contactsPhoto)

            itemView.setOnClickListener{
                val goToChatActivity = Intent(
                    context,
                    ChatActivity::class.java)
                goToChatActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                val bundle = Bundle()
                bundle.putSerializable("ChatUser",currentItem)
                goToChatActivity.putExtras(bundle)

                context.startActivity(goToChatActivity)
            }
        }

    }


}