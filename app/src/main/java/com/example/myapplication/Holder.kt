package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import pl.droidsonroids.gif.GifImageView


open class Holder(itemView: View, ModelStory: ModelStory) : RecyclerView.ViewHolder(itemView) {

    @SuppressLint("SetTextI18n")
    fun setDetails(
        applicationContext: Context,
        accountImage: String,
        title: String,
        firstText: String,
        firstImage: String,
        author: String,
        accountImageType: String,
        old_title: String,
        ideaAuthor: String,
        newTitle: String,
        authorImage: Any?,
        key: String,
        search: TextInputEditText?,
        pathS: DatabaseReference?,
    ) {
        itemView.findViewById<TextView>(R.id.storyTitle).setText(newTitle)


        var count = 0

        FirebaseDatabase.getInstance().reference.child("UserNotes")
            .child(author).child(title).child("partners").get().addOnSuccessListener {
                for (partners in it.children) {
                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                        .child(author).child(title).child("partners")
                        .child(partners.key.toString()).child("accountImage").get()
                        .addOnSuccessListener {
                            if (count == 0) {
                                count++
                                Glide.with(applicationContext).load(it.value.toString())
                                    .circleCrop()
                                    .into(itemView.findViewById<GifImageView>(R.id.person1))
                            } else if (count == 1) {
                                count++
                                Glide.with(applicationContext).load(it.value.toString())
                                    .circleCrop()
                                    .into(itemView.findViewById<GifImageView>(R.id.person2))

                            } else if (count == 2) {
                                count++
                                Glide.with(applicationContext).load(it.value.toString())
                                    .circleCrop()
                                    .into(itemView.findViewById<GifImageView>(R.id.person3))

                            }

                        }
                }
            }


        var images = listOf<String>()
        FirebaseDatabase.getInstance().reference.child("UserNotes")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child(title)
            .child("images").get().addOnSuccessListener {
                for (image in it.children) {
                    images = images + image.key.toString()
                }
                images.sorted()
                if (it.value.toString() != "null") {
                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).child(title)
                        .child("images").child(images[0]).child("url").get().addOnSuccessListener {
                            val first_image =
                                itemView.findViewById<GifImageView>(R.id.first_photo)
                            Glide.with(applicationContext).load(it.value.toString()).circleCrop()
                                .into(first_image)
                        }

                }


            }
        val photo = itemView.findViewById<CircleImageView>(R.id.image)
        val gif = itemView.findViewById<GifImageView>(R.id.gif)

        photo.visibility = View.VISIBLE
        gif.visibility = View.VISIBLE
        if (ideaAuthor == "") {

            FirebaseDatabase.getInstance().reference.child("Users").child(author)
                .child("accountImage").get().addOnSuccessListener {
                    FirebaseDatabase.getInstance().reference.child("Users").child(author)
                        .child("accountImageType").get().addOnSuccessListener { type ->
                            if (type.value.toString() == "image") {
                                gif.visibility = View.GONE
                                photo.visibility = View.VISIBLE


                                Glide.with(applicationContext).load(it.value.toString())
                                    .apply(RequestOptions.overrideOf(100, 100))
                                    .into(photo)

                            }
                            if (type.value.toString() == "gif") {
                                gif.visibility = View.VISIBLE
                                photo.visibility = View.GONE

                                Glide.with(applicationContext).load(it.value.toString())
                                    .apply(RequestOptions.overrideOf(100, 100))
                                    .circleCrop().into(gif)

                            }
                        }

                }
        }

        if (ideaAuthor != "" && author != ideaAuthor) {
            FirebaseDatabase.getInstance().reference.child("Users").child(ideaAuthor)
                .child("accountImage").get().addOnSuccessListener {
                    FirebaseDatabase.getInstance().reference.child("Users").child(ideaAuthor)
                        .child("accountImageType").get().addOnSuccessListener { type ->
                            if (type.value.toString() == "image") {
                                gif.visibility = View.GONE
                                photo.visibility = View.VISIBLE


                                Glide.with(applicationContext).load(it.value.toString())
                                    .apply(RequestOptions.overrideOf(100, 100))
                                    .into(photo)

                            }
                            if (type.value.toString() == "gif") {
                                gif.visibility = View.VISIBLE
                                photo.visibility = View.GONE

                                Glide.with(applicationContext).load(it.value.toString())
                                    .apply(RequestOptions.overrideOf(100, 100))
                                    .circleCrop().into(gif)

                            }
                        }

                }
        }
        search?.setOnEditorActionListener { v, actionID, event ->
            if (actionID == EditorInfo.IME_ACTION_DONE) {
                pathS?.get()?.addOnSuccessListener {

                    pathS?.child(title)?.child("images")?.get()
                        ?.addOnSuccessListener {
                            var wholeText = ""

                            for (image in it.children) {
                                wholeText =
                                    wholeText + decrypt(image.child("text").value.toString()).toString() + decrypt(
                                        image.child("nextText").value.toString()
                                    )

                            }
                            pathS?.child(title)!!.child("firstText").get().addOnSuccessListener {
                                wholeText += decrypt(it.value.toString()).toString()
                            }
                            if (v.text.toString() in newTitle || v.text.toString() in wholeText) {
                                val params = itemView.layoutParams
                                params.width = LayoutParams.MATCH_PARENT
                                params.height = LayoutParams.WRAP_CONTENT

                                itemView.visibility = View.VISIBLE
                                itemView.layoutParams = params
                            } else {
                                val params = itemView.layoutParams
                                params.width = 0
                                params.height = 0
                                itemView.visibility = View.GONE
                                itemView.layoutParams = params

                            }

                        }
                }
            }
            true
        }

        changeTheme(
            null,
            null,
            listOf(itemView.findViewById(R.id.storyTitle), itemView.findViewById(R.id.storyText)),
            null,
            itemView.findViewById(R.id.gif),
            listOf(itemView.findViewById(R.id.image)),
            null,
            null,
            null,
            applicationContext,
            null,
            null,
            null,
            null,
            null
        )
        changeTheme(
            null,
            null,
            null,
            null,
            itemView.findViewById(R.id.person2),
            null,
            null,
            null,
            null,
            applicationContext,
            null,
            null,
            null,
            null,
            null
        )
        changeTheme(
            null,
            null,
            null,
            null,
            itemView.findViewById(R.id.person3),
            null,
            null,
            null,
            null,
            applicationContext,
            null,
            null,
            null,
            null,
            null
        )
        changeTheme(
            null,
            null,
            null,
            null,
            itemView.findViewById(R.id.person1),
            null,
            null,
            null,
            null,
            applicationContext,
            null,
            null,
            null,
            null,
            null
        )
        changeTheme(
            null,
            null,
            null,
            null,
            itemView.findViewById(R.id.first_photo),
            null,
            listOf( itemView.findViewById<MaterialCardView>(R.id.card_view)),
            null,
            null,
            applicationContext,
            null,
            null,
            null,
            null,
            null
        )

    }

}

open class HolderPublic(itemView: View, ModelStory: ModelStory) :
    RecyclerView.ViewHolder(itemView) {

    @SuppressLint("SetTextI18n")
    fun setDetails(
        applicationContext: Context,
        accountImage: String,
        title: String,
        firstText: String,
        firstImage: String,
        author: String,
        accountImageType: String,
        old_title: String,
        ideaAuthor: String,
        newTitle: String?,
        authorImage: Any?,
        search: TextInputEditText,
        pathPublic: DatabaseReference?,
    ) {
        search?.setOnEditorActionListener { v, actionID, event ->
            if (actionID == EditorInfo.IME_ACTION_DONE) {


                if ((v.text.toString()) in newTitle!! == true) {
                    val params = itemView.layoutParams
                    params.width = LayoutParams.MATCH_PARENT
                    params.height = LayoutParams.WRAP_CONTENT

                    itemView.visibility = View.VISIBLE
                    itemView.layoutParams = params
                } else {
                    val params = itemView.layoutParams
                    params.width = 0
                    params.height = 0
                    itemView.visibility = View.GONE
                    itemView.layoutParams = params

                }


            }
            true
        }

        itemView.findViewById<TextView>(R.id.storyTitle).setText(newTitle)

        var count = 0

        FirebaseDatabase.getInstance().reference.child("UserNotes")
            .child(author).child(title).child("partners").get().addOnSuccessListener {
                for (partners in it.children) {
                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                        .child(author).child(title).child("partners")
                        .child(partners.key.toString()).child("accountImage").get()
                        .addOnSuccessListener {
                            if (count == 0) {
                                count++
                                Glide.with(applicationContext).load(it.value.toString())
                                    .circleCrop()
                                    .into(itemView.findViewById<GifImageView>(R.id.person1))
                            } else if (count == 1) {
                                count++
                                Glide.with(applicationContext).load(it.value.toString())
                                    .circleCrop()
                                    .into(itemView.findViewById<GifImageView>(R.id.person2))

                            } else if (count == 2) {
                                count++
                                Glide.with(applicationContext).load(it.value.toString())
                                    .circleCrop()
                                    .into(itemView.findViewById<GifImageView>(R.id.person3))

                            }

                        }
                }
            }


        var images = listOf<String>()
        FirebaseDatabase.getInstance().reference.child("UserNotes")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child(title)
            .child("images").get().addOnSuccessListener {
                for (image in it.children) {
                    images = images + image.key.toString()
                }
                images.sorted()
                if (it.value.toString() != "null") {
                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).child(title)
                        .child("images").child(images[0]).child("url").get().addOnSuccessListener {
                            val first_image =
                                itemView.findViewById<GifImageView>(R.id.first_photo)
                            Glide.with(applicationContext).load(it.value.toString()).circleCrop()
                                .into(first_image)
                        }

                }


            }
        val photo = itemView.findViewById<CircleImageView>(R.id.image)
        val gif = itemView.findViewById<GifImageView>(R.id.gif)

        photo.visibility = View.VISIBLE
        gif.visibility = View.VISIBLE
        if (ideaAuthor == "") {

            FirebaseDatabase.getInstance().reference.child("Users").child(author)
                .child("accountImage").get().addOnSuccessListener {
                    FirebaseDatabase.getInstance().reference.child("Users").child(author)
                        .child("accountImageType").get().addOnSuccessListener { type ->
                            if (type.value.toString() == "image") {
                                gif.visibility = View.GONE
                                photo.visibility = View.VISIBLE


                                Glide.with(applicationContext).load(it.value.toString())
                                    .apply(RequestOptions.overrideOf(100, 100))
                                    .into(photo)

                            }
                            if (type.value.toString() == "gif") {
                                gif.visibility = View.VISIBLE
                                photo.visibility = View.GONE

                                Glide.with(applicationContext).load(it.value.toString())
                                    .apply(RequestOptions.overrideOf(100, 100))
                                    .circleCrop().into(gif)

                            }
                        }

                }
        }

        if (ideaAuthor != "" && author != ideaAuthor) {
            FirebaseDatabase.getInstance().reference.child("Users").child(ideaAuthor)
                .child("accountImage").get().addOnSuccessListener {
                    FirebaseDatabase.getInstance().reference.child("Users").child(ideaAuthor)
                        .child("accountImageType").get().addOnSuccessListener { type ->
                            if (type.value.toString() == "image") {
                                gif.visibility = View.GONE
                                photo.visibility = View.VISIBLE


                                Glide.with(applicationContext).load(it.value.toString())
                                    .apply(RequestOptions.overrideOf(100, 100))
                                    .into(photo)

                            }
                            if (type.value.toString() == "gif") {
                                gif.visibility = View.VISIBLE
                                photo.visibility = View.GONE

                                Glide.with(applicationContext).load(it.value.toString())
                                    .apply(RequestOptions.overrideOf(100, 100))
                                    .circleCrop().into(gif)

                            }
                        }

                }
        }

        changeTheme(
            null,
            null,
            listOf(itemView.findViewById(R.id.storyTitle), itemView.findViewById(R.id.storyText)),
            null,
            itemView.findViewById(R.id.gif),
            listOf(itemView.findViewById(R.id.image)),
            null,
            null,
            null,
            applicationContext,
            null,
            null,
            null,
            null,
            null
        )




    }

}


open class ImagesHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setDetails(
        image1: String,
        image2: String,
        image3: String,
        activity: Activity
    ) {
        Glide.with(activity).load(image1).into(itemView.findViewById(R.id.image1))
        Glide.with(activity).load(image2).into(itemView.findViewById(R.id.image2))
        Glide.with(activity).load(image3).into(itemView.findViewById(R.id.image3))

        itemView.findViewById<CardView>(R.id.image3Card).setOnClickListener {
            val bigImage = GifImageView(activity)
            activity.findViewById<ConstraintLayout>(R.id.constraint).addView(
                bigImage,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            Glide.with(activity).load(image3).into(bigImage)

            bigImage.setOnClickListener {
                activity.findViewById<ConstraintLayout>(R.id.constraint).removeView(bigImage)
            }
        }
        itemView.findViewById<CardView>(R.id.image2Card).setOnClickListener {
            val bigImage = GifImageView(activity)
            activity.findViewById<ConstraintLayout>(R.id.constraint).addView(
                bigImage,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            Glide.with(activity).load(image2).into(bigImage)

            bigImage.setOnClickListener {
                activity.findViewById<ConstraintLayout>(R.id.constraint).removeView(bigImage)
            }
        }
        itemView.findViewById<CardView>(R.id.image1Card).setOnClickListener {
            val bigImage = GifImageView(activity)
            activity.findViewById<ConstraintLayout>(R.id.constraint).addView(
                bigImage,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            Glide.with(activity).load(image1).into(bigImage)

            bigImage.setOnClickListener {
                activity.findViewById<ConstraintLayout>(R.id.constraint).removeView(bigImage)
            }
        }


    }
}


