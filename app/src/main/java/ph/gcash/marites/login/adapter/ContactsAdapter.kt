package ph.gcash.marites.login.adapter

import android.net.Uri
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
import ph.gcash.marites.User

class ContactsAdapter(private val userList : ArrayList<User>): RecyclerView.Adapter<ContactsAdapter.myViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.contact_item,parent,false)

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentitem = userList[position]
        val userId = currentitem.userUID
        val imageURL = FirebaseStorage.getInstance().reference.child("users/$userId").downloadUrl

        imageURL.loadIntoPicasso(holder.contactsProfile)


        holder.fullName.text = currentitem.name
        holder.userEmail.text = currentitem.email
    }

    fun Task<Uri>.loadIntoPicasso(imageView: ShapeableImageView) {
        addOnSuccessListener {
            Picasso.get().load(it).resize(300, 300).centerInside().into(imageView)
        }
    }

    override fun getItemCount(): Int {

        return userList.size
    }

    class myViewHolder(itemView: View ) : RecyclerView.ViewHolder(itemView){

        val fullName : TextView = itemView.findViewById(R.id.fullName_item)
        val userEmail : TextView =  itemView.findViewById(R.id.userEmail_item)
        val contactsProfile : ShapeableImageView = itemView.findViewById(R.id.contact_image)

    }
}