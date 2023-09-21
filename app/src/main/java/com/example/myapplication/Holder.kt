package com.example.myapplication

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import pl.droidsonroids.gif.GifImageView


open class Holder(itemView: View, ModelStory: ModelStory) : RecyclerView.ViewHolder(itemView) {

    fun setDetails(
        applicationContext: Context,
        accountImage: String,
        title: String,
        text: String,
        first_image: String
    ) {
        itemView.findViewById<TextView>(R.id.storyTitle).setText(title)
        itemView.findViewById<TextView>(R.id.storyText).setText(text)
        val accountPhoto = itemView.findViewById<GifImageView>(R.id.mAccount)
        var images = listOf<String>()
        Glide.with(applicationContext).load(accountImage)
            .placeholder(R.drawable.baseline_account_circle_24).circleCrop().into(accountPhoto)
        FirebaseDatabase.getInstance().reference.child("UserNotes")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child(title)
            .child("images").get().addOnSuccessListener {
                for (image in it.children){
                    images = images + image.key.toString()
                }
                images.sorted()
                if (it.value.toString() != "null") {
                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).child(title)
                        .child("images").child(images[0]).child("url").get().addOnSuccessListener {
                            val first_image = itemView.findViewById<CircleImageView>(R.id.first_photo)
                            Glide.with(applicationContext).load(it.value.toString()).into(first_image)
                        }

                }


            }
    }

}
