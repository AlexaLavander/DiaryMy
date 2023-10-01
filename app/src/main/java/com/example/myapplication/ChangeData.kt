package com.example.myapplication

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import pl.droidsonroids.gif.GifImageView

class ChangeData : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_data)

        changeTheme(
            findViewById(R.id.constraint),
            null,
            listOf(
                findViewById(R.id.title),
                findViewById(R.id.mUsername),
                findViewById(R.id.status)
            ),
            listOf(findViewById(R.id.image)),
            findViewById(R.id.gif),
            null,
            null,
            null,
            listOf(findViewById(R.id.changePassword)),
            this,
            null,
            null,
            null,
            null,
            findViewById(R.id.progress_bar)
        )


        val photo = findViewById<CircleImageView>(R.id.image)
        val gif = findViewById<GifImageView>(R.id.gif)

        findViewById<TextInputEditText>(R.id.changeName).setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("username")
                    .setValue(findViewById<TextInputEditText>(R.id.changeName).text.toString())
                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("username").get().addOnSuccessListener {
                        findViewById<TextView>(R.id.mUsername).setText(it.value.toString())
                    }
            }
            true
        }


        findViewById<TextInputEditText>(R.id.changeStatus).setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("status")
                    .setValue(findViewById<TextInputEditText>(R.id.changeStatus).text.toString())
                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("status").get().addOnSuccessListener {
                        findViewById<TextView>(R.id.status).setText(it.value.toString())
                    }
            }
            true
        }


        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("email").get().addOnSuccessListener {
                findViewById<TextInputEditText>(R.id.email).setText(it.value.toString())
                findViewById<TextInputEditText>(R.id.email).setTextColor(R.color.grey)
            }

        findViewById<Button>(R.id.changePassword).setOnClickListener {
            Toast.makeText(
                this,
                "На вашу почта отправлена ссылка для смены пароля",
                Toast.LENGTH_LONG
            ).show()
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("email").get().addOnSuccessListener {
                    Firebase.auth.sendPasswordResetEmail(it.value.toString())
                    findViewById<Button>(R.id.changePassword).setText("Ссылка отпралена")

                }

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

        gif.setOnClickListener {
            imageTouched()
        }

        photo.setOnClickListener {
            imageTouched()
        }
    }

    override fun onStop() {
        super.onStop()
    }

    fun imageTouched() {
        val alertDialogBuilder =
            (this as Activity).layoutInflater.inflate(
                R.layout.delete,
                null
            )
        val alert = AlertDialog.Builder(this)
        alert.setView(alertDialogBuilder)
        val show = alert.show()

        alertDialogBuilder.findViewById<TextView>(R.id.message)
            .setText("Вы хотите открыть или сменить фото?")
        alertDialogBuilder.findViewById<TextView>(R.id.yes).setText("Посмотреть фото")
        alertDialogBuilder.findViewById<TextView>(R.id.no).setText("Сменить фото профиля")

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        changeTheme(
            findViewById(R.id.constraint),
            null,
            listOf(
                alertDialogBuilder.findViewById<TextView>(R.id.message),
                alertDialogBuilder.findViewById<TextView>(R.id.yes),
                alertDialogBuilder.findViewById<TextView>(R.id.no)

            ),
            null,
            null,
            null,
            null,
            null,
            null,
            this,
            null,
            null,
            null,
            null,
            null
        )
        Handler(Looper.getMainLooper()).postDelayed({

            alertDialogBuilder.findViewById<TextView>(R.id.yes).setOnClickListener {
                val imageView = ImageView(this)

                FirebaseDatabase.getInstance().reference.child("Users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("accountImage").get().addOnSuccessListener {
                        Glide.with(applicationContext)
                            .load(it.value.toString()).into(imageView)
                    }

                findViewById<ConstraintLayout>(R.id.constraint)
                    .addView(imageView, (ConstraintLayout.LayoutParams.MATCH_PARENT))
                imageView.setOnClickListener {
                    findViewById<ConstraintLayout>(R.id.constraint).removeView(imageView)
                }
                show.dismiss()
            }

            alertDialogBuilder.findViewById<TextView>(R.id.no).setOnClickListener {

                val dialog_layout2 =
                    (this as Activity).layoutInflater.inflate(
                        R.layout.gif_or_image,
                        null
                    )
                changeTheme(
                    findViewById(R.id.constraint),
                    null,
                    null,
                    listOf(
                        dialog_layout2.findViewById(R.id.image),
                        dialog_layout2.findViewById(R.id.gif)
                    ),
                    null,
                    null,
                    null,
                    null,
                    null,
                    this,
                    null,
                    null,
                    null,
                    null,
                    null
                )
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )

                Handler(Looper.getMainLooper()).postDelayed({

                    val alertDialog2 = AlertDialog.Builder(this)
                    alertDialog2.setView(dialog_layout2)
                    alertDialog2.setCancelable(true)
                    val show2 = alertDialog2.show()
                    dialog_layout2.findViewById<ImageView>(R.id.gif).setOnClickListener {
                        openFileChooser("image/gif")
                        show2.cancel()

                    }
                    dialog_layout2.findViewById<ImageView>(R.id.image).setOnClickListener {
                        openFileChooser("image/jpeg")
                        show2.cancel()
                    }
                    dialog_layout2.findViewById<ImageView>(R.id.done).setOnClickListener {
                        dialog_layout2.findViewById<ImageView>(R.id.done)
                            .setImageResource(R.drawable.done)
                    }

                }, 500)
                show.dismiss()

            }

        }, 3000)


    }

    val REQUEST_SELECT_IMAGE_IN_ALBUM = 1

    private fun openFileChooser(s: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = s
        if (intent.resolveActivity(packageManager) != null) {
            if (s == "image/jpeg") {
                startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
            } else {
                startActivityForResult(intent, 100)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == RESULT_OK && data != null) {
            val path = FirebaseStorage.getInstance().reference.child("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
            path.putFile(data.data!!).addOnSuccessListener {
                path.downloadUrl.addOnCompleteListener { uri ->
                    if (uri.isSuccessful) {
                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child("accountImage").setValue(
                                uri.result.toString()
                            )

                        val photo = findViewById<ImageView>(R.id.image)
                        val gif = findViewById<GifImageView>(R.id.gif)
                        gif.visibility = View.GONE
                        photo.visibility = View.VISIBLE
                        Glide.with(applicationContext).load(uri.result.toString())
                            .apply(RequestOptions.overrideOf(100, 100))
                            .circleCrop().into(photo)
                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child("accountImageType").setValue("image")


                        FirebaseDatabase.getInstance().reference.child("Images")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .get().addOnSuccessListener {
                                if (it.value.toString() != "null") {
                                    var listNumbers = listOf<Int>()
                                    var linearNumbers = listOf<Int>()

                                    for (linear in it.children) {
                                        linearNumbers =
                                            linearNumbers + linear.key.toString().toInt()

                                    }
                                    FirebaseDatabase.getInstance().reference.child("Images")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child(linearNumbers.max().toString())
                                        .get().addOnSuccessListener {
                                            if (it.children.count() < 3) {
                                                FirebaseDatabase.getInstance().reference.child("Images")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child(linearNumbers.max().toString())
                                                    .child("image" + (it.children.count() + 1).toString())
                                                    .setValue(uri.result.toString())
                                            } else {
                                                val linear = linearNumbers.max()
                                                FirebaseDatabase.getInstance().reference.child("Images")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child((linear + 1).toString())
                                                    .child("image" + (1).toString())
                                                    .setValue(uri.result.toString())
                                            }
                                        }

                                } else {
                                    FirebaseDatabase.getInstance().reference.child("Images")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child((1).toString())
                                        .child("image" + (1).toString())
                                        .setValue(uri.result.toString())
                                }
                            }

                    }
                }
            }
        }
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val path = FirebaseStorage.getInstance().reference.child("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)

            path.putFile(data.data!!).addOnSuccessListener {
                path.downloadUrl.addOnCompleteListener { uri ->
                    if (uri.isSuccessful) {
                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child("accountImage").setValue(
                                uri.result.toString()
                            )
                        val photo = findViewById<ImageView>(R.id.image)
                        val gif = findViewById<GifImageView>(R.id.gif)
                        gif.visibility = View.VISIBLE
                        photo.visibility = View.GONE

                        Glide.with(applicationContext).load(uri.result.toString())
                            .apply(RequestOptions.overrideOf(100, 100))
                            .circleCrop().into(gif)
                        FirebaseDatabase.getInstance().reference.child("Users")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child("accountImageType").setValue("gif")

                        FirebaseDatabase.getInstance().reference.child("Images")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .get().addOnSuccessListener {
                                if (it.value.toString() != "null") {
                                    var listNumbers = listOf<Int>()
                                    var linearNumbers = listOf<Int>()

                                    for (linear in it.children) {
                                        linearNumbers =
                                            linearNumbers + linear.key.toString().toInt()

                                    }
                                    FirebaseDatabase.getInstance().reference.child("Images")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child(linearNumbers.max().toString())
                                        .get().addOnSuccessListener {
                                            if (it.children.count() < 3) {
                                                FirebaseDatabase.getInstance().reference.child("Images")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child(linearNumbers.max().toString())
                                                    .child("image" + (it.children.count() + 1).toString())
                                                    .setValue(uri.result.toString())
                                            } else {
                                                val linear = linearNumbers.max()
                                                FirebaseDatabase.getInstance().reference.child("Images")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child((linear + 1).toString())
                                                    .child("image" + (1).toString())
                                                    .setValue(uri.result.toString())
                                            }
                                        }

                                }
                            }
                    }
                }
            }
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler(Looper.getMainLooper()).postDelayed({
            findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
        }, 3000)



        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {

        super.onDestroy()

    }
}