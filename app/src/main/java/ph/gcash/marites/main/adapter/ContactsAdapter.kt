package ph.gcash.marites.main.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import ph.gcash.marites.models.User

class ContactsAdapter(private val userList : ArrayList<User>, private val context: Context): RecyclerView.Adapter<ContactsAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.contact_item,parent,false)

        return MyViewHolder(itemView , context)
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

    override fun getItemCount(): Int {

        return userList.size
    }

    class MyViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView){

        fun bindItem(currentitem: User) {
            val fullName : TextView = itemView.findViewById(R.id.fullName_item)
            val userEmail : TextView =  itemView.findViewById(R.id.userEmail_item)
            val contactsProfile : ShapeableImageView = itemView.findViewById(R.id.contact_image)

            fullName.text = currentitem.name
            userEmail.text = currentitem.email

            val userId = currentitem.userUID
            val imageURL = FirebaseStorage.getInstance().reference.child("users/$userId").downloadUrl
            imageURL.loadIntoPicasso(contactsProfile)

            itemView.setOnClickListener{
                val goToChatActivity = Intent(
                    context,
                    ChatActivity::class.java)
                goToChatActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                val bundle = Bundle()
                bundle.putSerializable("ChatUser",currentitem)
                goToChatActivity.putExtras(bundle)

                context.startActivity(goToChatActivity)
            }
        }

    }


}