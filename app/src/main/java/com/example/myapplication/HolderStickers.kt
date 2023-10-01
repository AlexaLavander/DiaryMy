package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import pl.droidsonroids.gif.GifImageView

class HolderStickers(itemView: View, model: Sticker) : RecyclerView.ViewHolder(itemView) {

    @SuppressLint("ClickableViewAccessibility")
    fun setDetails(
        main: Activity,
        imageUri: String,
        titleNote: String,
        title: String,
        author: String,
        status: String,
        key: String,
        titleKey: String,
        firstTitle: String,
        data: String

    ) {
        val stickerGif = itemView.findViewById<GifImageView>(R.id.image)



        itemView.findViewById<TextView>(R.id.titleSticker).setText(title)
        itemView.setOnClickListener {

            val titleStickerInput = itemView.findViewById<TextInputEditText>(R.id.titleStickerInput)
            titleStickerInput.visibility = View.GONE
            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(titleKey).get().addOnSuccessListener {


                    FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(titleKey).child("stickers").get().addOnSuccessListener { sticker ->
                            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(titleKey).child("stickers")
                                .child(key)
                                .child("key").setValue(key)
                            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(titleKey).child("stickers")
                                .child(key)
                                .child("url").setValue(imageUri)
                            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(titleKey).child("stickers")
                                .child(key)
                                .child("top").setValue("1")
                            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(titleKey).child("stickers")
                                .child(key)
                                .child("left").setValue("1").addOnSuccessListener {


                                    val gif = GifImageView(main)
                                    Glide.with(main).load(imageUri).into(gif)
                                    gif.x = 0.toFloat()
                                    gif.y = 0.toFloat()

                                    val dialog_layout =
                                        (main as Activity).layoutInflater.inflate(
                                            R.layout.inside_outside,
                                            null
                                        )
                                    val alertDialog = AlertDialog.Builder(main)
                                    alertDialog.setView(dialog_layout)
                                    val show = alertDialog.show()
                                    alertDialog.setCancelable(false)
                                    changeTheme(
                                        constraintLayout = null,
                                        flb = null,
                                        text = null,
                                        image = null,
                                        gif = null,
                                        circle = null,
                                        cardView = null,
                                        materialToolbar = null,
                                        btn = null,
                                        context = main,
                                        navigation = null,
                                        materialWriteRead = null,
                                        materialRead = null,
                                        nothing = null,
                                        nothing1 = null
                                    )
                                    dialog_layout.findViewById<ImageView>(R.id.outside)
                                        .setOnClickListener {
                                            FirebaseDatabase.getInstance().getReference()
                                                .child("UserNotesSketches")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child(titleKey)
                                                .child("stickers")
                                                .child(key)
                                                .child("side").setValue("outside")
                                                .addOnSuccessListener {
                                                    val scroll_linear =
                                                        main.findViewById<RelativeLayout>(R.id.constraint)
                                                    val gifParams =
                                                        RelativeLayout.LayoutParams(400, 400)
                                                    scroll_linear.addView(gif, gifParams)
                                                }

                                            show.dismiss()

                                        }
                                    dialog_layout.findViewById<ImageView>(R.id.inside)
                                        .setOnClickListener {
                                            FirebaseDatabase.getInstance().getReference()
                                                .child("UserNotesSketches")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child(titleKey)
                                                .child("stickers")
                                                .child(key)
                                                .child("side").setValue("inside")
                                                .addOnSuccessListener {
                                                    val gifParams =
                                                        RelativeLayout.LayoutParams(400, 400)
                                                    val text =
                                                        main.findViewById<RelativeLayout>(R.id.stickersSide)
                                                    text.addView(gif, gifParams)


                                                }
                                            show.dismiss()
                                        }


                                    gif.setOnClickListener {
                                        val alertBuilder =
                                            androidx.appcompat.app.AlertDialog.Builder(main)
                                        alertBuilder.setMessage("Вы хотите удалить  или передвинуть стикер?")
                                        alertBuilder.setPositiveButton("Удалить") { dialog, id ->

                                            FirebaseDatabase.getInstance().getReference()
                                                .child("UserNotesSketches")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child(titleKey).child("stickers")
                                                .child(key).removeValue()
                                            if (sticker.child("side").value.toString() == "inside") {
                                                val text =
                                                    main.findViewById<RelativeLayout>(R.id.stickersSide)
                                                text.removeView(gif)
                                            } else {
                                                val text =
                                                    main.findViewById<RelativeLayout>(R.id.constraint)
                                                text.removeView(gif)
                                            }
                                            dialog.cancel()


                                        }
                                        alertBuilder.setNegativeButton("Передвинуть") { dialog, id ->
                                            var yDelta = 0
                                            var xDelta = 0
                                            val btn =
                                                main.findViewById<FloatingActionButton>(R.id.doneTouch)
                                            btn.visibility = View.VISIBLE
                                            gif.setOnTouchListener(object : View.OnTouchListener {
                                                override fun onTouch(
                                                    v: View,
                                                    event: MotionEvent
                                                ): Boolean {
                                                    val x = event.rawX.toInt()
                                                    val y = event.rawY.toInt()
                                                    when (event.action and MotionEvent.ACTION_MASK) {
                                                        MotionEvent.ACTION_DOWN -> {
                                                            var lParams =
                                                                v.layoutParams as RelativeLayout.LayoutParams


                                                            xDelta = x - lParams.leftMargin
                                                            yDelta = y - lParams.topMargin
                                                        }
                                                        MotionEvent.ACTION_POINTER_DOWN -> {
                                                        }
                                                        MotionEvent.ACTION_POINTER_UP -> {
                                                        }
                                                        MotionEvent.ACTION_MOVE -> {
                                                            val layoutParams =
                                                                v.layoutParams as RelativeLayout.LayoutParams
                                                            layoutParams.leftMargin = x - xDelta
                                                            layoutParams.topMargin = y - yDelta
                                                            layoutParams.rightMargin = 0
                                                            layoutParams.bottomMargin = 0
                                                            v.layoutParams = layoutParams
                                                            FirebaseDatabase.getInstance()
                                                                .getReference()
                                                                .child("UserNotesSketches")
                                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                .child(titleKey)
                                                                .child("stickers")
                                                                .child(key).child("left")
                                                                .setValue(x - xDelta)
                                                            FirebaseDatabase.getInstance()
                                                                .getReference()
                                                                .child("UserNotesSketches")
                                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                .child(titleKey)
                                                                .child("stickers")
                                                                .child(key).child("top")
                                                                .setValue(y - yDelta)
                                                        }
                                                    }
                                                    main.findViewById<RelativeLayout>(R.id.stickersSide)
                                                        .invalidate()
                                                    main.findViewById<RelativeLayout>(R.id.constraint)
                                                        .invalidate()
                                                    return true
                                                }
                                            })
                                            btn.setOnClickListener {
                                                gif.setOnTouchListener(null)
                                                btn.visibility = View.GONE
                                            }
                                            dialog.cancel()
                                        }
                                        val alert = alertBuilder.create()
                                        alert.show()
                                    }


                                }
                        }


                }

        }
        Glide.with(main).load(imageUri)
            .apply(RequestOptions.overrideOf(120, 120))
            .into(stickerGif)
        val authorSticker = itemView.findViewById<TextView>(R.id.autor)
        authorSticker.setText(author)
        val titleSticker = itemView.findViewById<TextView>(R.id.titleSticker)
        titleSticker.setText(title)
        val titleStickerInput = itemView.findViewById<TextInputEditText>(R.id.titleStickerInput)
        titleStickerInput.setText(title)

        titleSticker.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("username").get().addOnSuccessListener {
                    if (it.value.toString() == author) {
                        titleSticker.visibility = View.GONE
                        titleStickerInput.visibility = View.VISIBLE
                        titleStickerInput.setImeOptions(EditorInfo.IME_ACTION_DONE)
                        titleStickerInput.setOnEditorActionListener { v, action, smth ->
                            if (action == EditorInfo.IME_ACTION_DONE) {
                                FirebaseDatabase.getInstance().getReference().child("Stickers")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child(key).get().addOnSuccessListener {
                                        if (titleStickerInput.text.toString() != "") {
                                            FirebaseDatabase.getInstance()
                                                .getReference()
                                                .child("Stickers")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child(key)
                                                .child("title")
                                                .setValue(titleStickerInput.text.toString())

                                            titleSticker.setText(titleStickerInput.text.toString())
                                            titleStickerInput.visibility = View.GONE
                                            titleSticker.visibility = View.VISIBLE
                                        } else {
                                            Toast.makeText(
                                                main,
                                                "Введите название",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                            }
                            false
                        }
                    }
                }
        }




        itemView.setOnLongClickListener {
            val dialog_layout =
                (main).layoutInflater.inflate(R.layout.delete, null)
            dialog_layout.findViewById<TextView>(R.id.message)
                .setText("Вы хотите удалить стикер из вашего списка?")
            dialog_layout.findViewById<TextView>(R.id.yes).setText("Да")
            dialog_layout.findViewById<TextView>(R.id.no).setText("Нет")
            val alert = AlertDialog.Builder(main)
            alert.setView(dialog_layout)
            val show = alert.show()
            dialog_layout.findViewById<TextView>(R.id.yes).setOnClickListener {
                FirebaseDatabase.getInstance().reference.child("Stickers")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid).child(key)
                    .removeValue()
                show.dismiss()

            }

            dialog_layout.findViewById<TextView>(R.id.no).setOnClickListener {
                show.dismiss()

            }

            changeTheme(
                null,
                null,
                listOf(
                    dialog_layout.findViewById(R.id.no),
                    dialog_layout.findViewById(R.id.yes),
                    dialog_layout.findViewById(R.id.message),

                    ),
                null,
                null,
                null,
                null,
                null,
                null,
                main,
                null,
                null,
                null,
                null,
                null
            )

            true
        }

        changeTheme(
            null,
            null,
            listOf(itemView.findViewById(R.id.titleSticker), itemView.findViewById(R.id.autor)),
            listOf(itemView.findViewById(R.id.my)),
            null,
            null,
            null,
            null,
            null,
            main,
            null,
            null,
            null,
            listOf(itemView.findViewById(R.id.titleStickerInput)),
            null
        )


    }
}

