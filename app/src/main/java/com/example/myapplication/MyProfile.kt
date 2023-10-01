package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import pl.droidsonroids.gif.GifImageView

class MyProfile : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        val photo = findViewById<CircleImageView>(R.id.image)
        val gif = findViewById<GifImageView>(R.id.gif)

        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("theme").get().addOnSuccessListener {
                val background = findViewById<ConstraintLayout>(R.id.constraint)
                val title = findViewById<TextView>(R.id.text)
                val username = findViewById<TextView>(R.id.mUsername)
                val status = findViewById<TextView>(R.id.status)
                val gif = findViewById<GifImageView>(R.id.gif)
                val iicon = findViewById<ImageView>(R.id.iicon)
                val ticon = findViewById<ImageView>(R.id.ticon)
                val licon = findViewById<ImageView>(R.id.licon)
                val ficon = findViewById<ImageView>(R.id.ficon)
                val exit = findViewById<ImageView>(R.id.exit)
                val idColor: Int =
                    getResources().getIdentifier(it.value.toString(), "color", packageName)
                val idBackground: Int =
                    getResources().getIdentifier(it.value.toString(), "drawable", packageName)

                background.setBackground(getDrawable(idBackground))

                exit.imageTintList =
                    ResourcesCompat.getColorStateList(getResources(), idColor, getTheme())
                iicon.imageTintList =
                    ResourcesCompat.getColorStateList(getResources(), idColor, getTheme())
                licon.imageTintList =
                    ResourcesCompat.getColorStateList(getResources(), idColor, getTheme())
                ticon.imageTintList =
                    ResourcesCompat.getColorStateList(getResources(), idColor, getTheme())
                ficon.imageTintList =
                    ResourcesCompat.getColorStateList(getResources(), idColor, getTheme())
                gif.imageTintList =
                    ResourcesCompat.getColorStateList(getResources(), idColor, getTheme())
                title.setTextColor(
                    ResourcesCompat.getColorStateList(
                        getResources(),
                        idColor,
                        getTheme()
                    )
                )
                username.setTextColor(
                    ResourcesCompat.getColorStateList(
                        getResources(),
                        idColor,
                        getTheme()
                    )
                )
                status.setTextColor(
                    ResourcesCompat.getColorStateList(
                        getResources(),
                        idColor,
                        getTheme()
                    )
                )


            }

        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("status").get().addOnSuccessListener {
                findViewById<TextView>(R.id.status).setText(it.value.toString())
            }

        findViewById<LinearLayout>(R.id.images).setOnClickListener {
            startActivity(Intent(this, Images::class.java))
        }

        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("status").get()
            .addOnSuccessListener {
                if (it.value.toString() != "null") {
                    findViewById<TextView>(R.id.status).setText(it.value.toString())
                } else {
                    findViewById<TextView>(R.id.status).setText(" ")

                }
            }
        findViewById<LinearLayout>(R.id.lock).setOnClickListener {
            startActivity(Intent(this, ChangeData::class.java))

        }
        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("accountImageType").get().addOnSuccessListener {
                if (it.value.toString() == "image") {
                    gif.visibility = View.GONE
                    photo.visibility = View.VISIBLE

                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("accountImage")
                        .get().addOnSuccessListener {
                            Glide.with(applicationContext).load(it.value.toString())
                                .apply(RequestOptions.overrideOf(100, 100))
                                .into(photo)
                        }
                }
                if (it.value.toString() == "gif") {
                    gif.visibility = View.VISIBLE
                    photo.visibility = View.GONE

                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("accountImage")
                        .get().addOnSuccessListener {
                            Glide.with(applicationContext).load(it.value.toString())
                                .apply(RequestOptions.overrideOf(100, 100))
                                .circleCrop().into(gif)
                        }
                }
            }

        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("username").get().addOnSuccessListener {
                findViewById<TextView>(R.id.mUsername).setText(it.value.toString())
            }

        findViewById<LinearLayout>(R.id.friends).setOnClickListener {
            startActivity(Intent(this, Friends::class.java))
        }


        findViewById<ImageView>(R.id.exit).setOnClickListener {
            val dialog_layout =
                (this as Activity).layoutInflater.inflate(R.layout.alert_exit, null)


            val dialog_layout2 =
                (this as Activity).layoutInflater.inflate(R.layout.come_back, null)

            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setView(dialog_layout)
            val show = alertDialog.show()
            alertDialog.setCancelable(false)
            dialog_layout.findViewById<Button>(R.id.yes).setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                val alertDialog2 = AlertDialog.Builder(this)
                alertDialog2.setView(dialog_layout2)
                alertDialog2.setCancelable(false)
                val show2 = alertDialog2.show()
                dialog_layout2.findViewById<Button>(R.id.ok).setOnClickListener {
                    show2.dismiss()
                    show.dismiss()
                    MainActivity().finish()
                    finish()
                }

            }
            dialog_layout.findViewById<Button>(R.id.no).setOnClickListener {
                show.dismiss()
            }
            changeTheme(
                null,
                null,
                listOf(dialog_layout.findViewById(R.id.text), dialog_layout2.findViewById(R.id.byeText)),
                null,
                null,
                null,
                null,
                null,
                listOf(dialog_layout.findViewById(R.id.yes), dialog_layout.findViewById(R.id.no), dialog_layout2.findViewById(R.id.ok)),
                this,
                null,
                null,
                null,
                null,
                null
            )

        }
        findViewById<LinearLayout>(R.id.theme).setOnClickListener{
            changeThemeF()
        }

    }

    fun changeThemeF() {
        Log.i("theme", "changed")
        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("theme").get().addOnSuccessListener {
                if (it.value.toString() == "purple") {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("theme").setValue("yellow")
                } else if (it.value.toString() == "yellow") {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("theme").setValue("red")
                } else if (it.value.toString() == "red") {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("theme").setValue("gold")
                } else if (it.value.toString() == "gold") {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("theme").setValue("pink")
                } else if (it.value.toString() == "pink") {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("theme").setValue("blue")
                } else if (it.value.toString() == "blue") {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("theme").setValue("grey")
                } else if (it.value.toString() == "grey") {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("theme").setValue("purple_grey")
                } else if (it.value.toString() == "purple_grey") {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("theme").setValue("green")
                } else if (it.value.toString() == "green") {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("theme").setValue("purple")
                }
            }.addOnSuccessListener {


                val background = findViewById<ConstraintLayout>(R.id.constraint)
                val title = findViewById<TextView>(R.id.text)
                val username = findViewById<TextView>(R.id.mUsername)
                val status = findViewById<TextView>(R.id.status)
                val gif = findViewById<GifImageView>(R.id.gif)
                val iicon = findViewById<ImageView>(R.id.iicon)
                val ticon = findViewById<ImageView>(R.id.ticon)
                val licon = findViewById<ImageView>(R.id.licon)
                val ficon = findViewById<ImageView>(R.id.ficon)
                val exit = findViewById<ImageView>(R.id.exit)
                changeTheme(
                    background, null, listOf(title, username, status),
                    listOf(iicon, licon, ticon, ficon, exit), gif, null, null,
                    null, null, this, null, null,
                    null,
                    null,
                    null
                )
            }

    }


}

@SuppressLint("NewApi")
fun changeTheme(
    constraintLayout: ConstraintLayout?,
    flb: List<FloatingActionButton>?,
    text: List<TextView?>?,
    image: List<ImageView?>?,
    gif: GifImageView?,
    circle: List<ImageView?>?,
    cardView: List<MaterialCardView?>?,
    materialToolbar: MaterialToolbar?,
    btn: List<Button?>?,
    context: Context,
    navigation: BottomNavigationView?,
    materialWriteRead: MaterialToolbar?,
    materialRead: MaterialToolbar?,
    nothing: List<TextInputEditText?>?,
    nothing1: ProgressBar?
) {


    FirebaseDatabase.getInstance().reference.child("Users")
        .child(FirebaseAuth.getInstance().currentUser!!.uid)
        .child("theme").get().addOnSuccessListener {
            val idColor: Int =
                context.getResources()
                    .getIdentifier(it.value.toString(), "color", context.packageName)
            val idBackground: Int =
                context.getResources()
                    .getIdentifier(it.value.toString(), "drawable", context.packageName)

            nothing1?.indeterminateTintList = ResourcesCompat.getColorStateList(context.resources, idColor, context.theme)

            constraintLayout?.setBackground(context.getDrawable(idBackground))


            if (text != null) {
                for (textView in text) {
                    textView?.setTextColor(
                        ResourcesCompat.getColorStateList(
                            context.getResources(),
                            idColor,
                            context.getTheme()
                        )
                    )
                }
            }
            if (cardView != null) {
                for (cardView in cardView) {
                    cardView?.strokeColor =
                        context.getColor(idColor)
                }
            }
            if (image != null) {
                for (image in image) {
                    image?.imageTintList =
                        ResourcesCompat.getColorStateList(
                            context.getResources(),
                            idColor,
                            context.getTheme()
                        )
                }
            }
            if (circle != null) {
                for (circle in circle) {
                    circle?.imageTintList =
                        ResourcesCompat.getColorStateList(
                            context.getResources(),
                            idColor,
                            context.getTheme()
                        )
                }
            }
            if (gif != null) {
                gif?.imageTintList =
                    ResourcesCompat.getColorStateList(
                        context.getResources(),
                        idColor,
                        context.getTheme())

            }

            if (btn != null) {
                for (btn in btn) {
                    btn?.backgroundTintList=(ResourcesCompat.getColorStateList(context.resources, idColor, context.theme))
                }
            }

            if (flb != null) {
                for (flb in flb) {
                    flb?.backgroundTintList = ResourcesCompat.getColorStateList(context.resources, idColor, context.theme)
                }
            }

            if (navigation != null) {
                navigation.itemIconTintList =
                    ResourcesCompat.getColorStateList(context.resources, idColor, context.theme)

            }

            if (nothing != null){
                for (input in nothing){
                    input?.setTextColor(ResourcesCompat.getColorStateList(
                        context.getResources(),
                        idColor,
                        context.getTheme()
                    ))
                }
            }

            materialToolbar?.setTitleTextColor(context.getResources().getColor(R.color.white))
            materialToolbar?.setNavigationIconTint(context.getResources().getColor(R.color.white))

            materialRead?.setTitleTextColor(context.getResources().getColor(idColor))
            materialWriteRead?.setTitleTextColor(context.getResources().getColor(idColor))
            materialRead?.menu?.getItem(0)?.iconTintList =
                ResourcesCompat.getColorStateList(context.resources, idColor, context.theme)
            materialRead?.menu?.getItem(1)?.iconTintList =
                ResourcesCompat.getColorStateList(context.resources, idColor, context.theme)
            materialRead?.menu?.getItem(2)?.iconTintList =
                ResourcesCompat.getColorStateList(context.resources, idColor, context.theme)

            materialWriteRead?.menu?.getItem(0)?.iconTintList =
                ResourcesCompat.getColorStateList(context.resources, idColor, context.theme)
            materialWriteRead?.menu?.getItem(1)?.iconTintList =
                ResourcesCompat.getColorStateList(context.resources, idColor, context.theme)
        }
}
