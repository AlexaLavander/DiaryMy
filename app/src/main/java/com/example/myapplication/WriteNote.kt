package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class WriteNote : AppCompatActivity() {


    var editTexts: MutableList<TextInputEditText> = mutableListOf<TextInputEditText>()
    var nextEditTexts: MutableList<TextInputEditText> = mutableListOf<TextInputEditText>()

    lateinit var firstText: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_note)


        firstText = TextInputEditText(this)

        val titleExtra = intent.getExtras()!!.getString("title")

        findViewById<TextInputEditText>(R.id.write_title).setText(titleExtra)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        var bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        bottomAppBar.backgroundTint = getColorStateList(R.color.white)
        var navigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationView.background = null
        navigationView.menu.getItem(2).isEnabled = false

        navigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            when (it.itemId) {
                R.id.favourite -> {

                    true
                }
                R.id.stickers -> {
                    true
                }

                R.id.choose_theme -> {
                    true
                }

                R.id.add_person -> {
                    true
                }

                else -> {
                    true
                }
            }

        })

        val addPhotoBtn = findViewById<FloatingActionButton>(R.id.add)

        addPhotoBtn.setOnClickListener {
            openFileChooser()
        }


        val scrollLinear = findViewById<LinearLayout>(R.id.scroll_linear)
        addPhotos(scrollLinear)
    }

    override fun onStop() {
        val title = findViewById<TextInputEditText>(R.id.write_title)
        val titleExtra = intent.extras!!.getString("title")
        val oldPhotoCount = FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(titleExtra.toString())
            .child("images")

        if (firstText.text.toString() != " ") {
            FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child(titleExtra.toString())
                .child("firstText").setValue(firstText.text.toString())
        } else {
            FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child(titleExtra.toString())
                .child("firstText").setValue("")

        }


        oldPhotoCount.get().addOnSuccessListener {
            var imageKey: MutableList<String> = mutableListOf<String>()
            for (image in it.children) {
                imageKey.add(image.key.toString())
            }
            for (key in imageKey) {
                for (edit in editTexts) {
                    if (imageKey.indexOf(key) == editTexts.indexOf(edit)) {
                        oldPhotoCount.child(key.toString())
                            .child("text")
                            .setValue(edit.text.toString())
                    }

                }
                for (edit in nextEditTexts) {
                    if (imageKey.indexOf(key) == nextEditTexts.indexOf(edit)) {
                        oldPhotoCount.child(key.toString())
                            .child("nextText")
                            .setValue(edit.text.toString())
                    }

                }
            }
        }.addOnCompleteListener {
            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child(titleExtra.toString())
                .get()
                .addOnSuccessListener {


                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(title.text.toString()).setValue(it.value)
                        .addOnCompleteListener {

                        }

                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(title.text.toString()).child("title").setValue(title.text.toString())
                    FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).child(titleExtra.toString())
                        .removeValue()

                }
        }


        super.onStop()
    }


    private fun addPhotos(scrollLinear: LinearLayout) {
        editTexts.clear()
        nextEditTexts.clear()
        val title = intent.getExtras()!!.getString("title")
        scrollLinear.removeAllViews()
        if (firstText.text.toString() != "") {
            FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child(title.toString())
                .child("firstText").setValue(firstText.text.toString())
        }

        val oldPhotoCount =
            FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child(title.toString())
                .child("images")

        oldPhotoCount.get().addOnSuccessListener {
            val imageKey: MutableList<String> = mutableListOf<String>()
            for (image in it.children) {
                imageKey.add(image.key.toString())
            }
            for (key in imageKey) {
                for (edit in editTexts) {
                    if (imageKey.indexOf(key) == editTexts.indexOf(edit)) {
                        oldPhotoCount.child(key.toString())
                            .child("text")
                            .setValue(edit.text.toString())
                    }

                }
            }
            for (key in imageKey) {
                for (edit in nextEditTexts) {
                    if (imageKey.indexOf(key) == editTexts.indexOf(edit)) {
                        oldPhotoCount.child(key.toString())
                            .child("nextText")
                            .setValue(edit.text.toString())
                    }

                }
            }
        }



        FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(title.toString()).child("images").get().addOnSuccessListener {
                if (it.value.toString() == "null") {
                    scrollLinear.addView(firstText)
                    FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(title.toString())
                        .child("firstText").get().addOnSuccessListener {
                            if (it.value.toString() == "null") {
                                firstText.setText(" ")
                            } else {
                                firstText.setText(it.value.toString())
                            }
                        }
                } else {
                    FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(title.toString())
                        .child("firstText").get().addOnSuccessListener {
                            if (it.value.toString() == "null") {
                                firstText.setText(" ")
                            } else {
                                firstText.setText(it.value.toString())
                            }
                        }

                    val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
                    progressBar.visibility = View.VISIBLE
                    scrollLinear.addView(firstText)


                    FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(title.toString())
                        .child("images").get().addOnSuccessListener { imageResult ->
                            for (image in imageResult.children) {

                                var newLinearLayout = LinearLayout(this)
                                newLinearLayout.setOrientation(LinearLayout.HORIZONTAL)
                                val newImage = ImageView(this)
                                val editText = TextInputEditText(this)
                                val nextText = TextInputEditText(this)

                                val editTextParams = RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                                )



                                editTextParams.marginEnd = 10

                                FirebaseDatabase.getInstance().getReference()
                                val newImageParams = RelativeLayout.LayoutParams(600, 600)
                                newImage.layoutParams = newImageParams
                                editText.layoutParams = editTextParams
                                nextText.layoutParams = editTextParams

                                nextEditTexts.add(nextText)
                                editTexts.add(editText)

                                editText.setText(image.child("text").value.toString())
                                nextText.setText(image.child("nextText").value.toString())

                                FirebaseDatabase.getInstance().reference.child(
                                    "UserNotesSketches"
                                )
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child(title.toString())
                                    .child("images").child(image.key.toString())
                                    .child("direction").get().addOnSuccessListener {
                                        if (it.value.toString() != "null") {
                                            newLinearLayout.layoutDirection =
                                                View.LAYOUT_DIRECTION_RTL
                                            editText.layoutDirection = View.LAYOUT_DIRECTION_LTR
                                        } else {
                                            newLinearLayout.layoutDirection =
                                                View.LAYOUT_DIRECTION_LTR
                                            editText.layoutDirection = View.LAYOUT_DIRECTION_LTR

                                        }
                                    }


                                Glide.with(applicationContext)
                                    .load(image.child("url").value.toString())
                                    .apply(RequestOptions.overrideOf(700, 550))
                                    .into(newImage)
                                newLinearLayout.addView(newImage)
                                newLinearLayout.addView(editText)

                                scrollLinear.addView(newLinearLayout)
                                scrollLinear.addView(nextText)

                                newLinearLayout.setOnClickListener {
                                    addPhotos(scrollLinear)
                                    val bigImage = ImageView(this)
                                    val bigParams = RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.MATCH_PARENT,
                                        RelativeLayout.LayoutParams.MATCH_PARENT
                                    )
                                    val constraintLayout =
                                        findViewById<ConstraintLayout>(R.id.constraint)
                                    constraintLayout.addView(bigImage, bigParams)
                                    Glide.with(applicationContext)
                                        .load(image.child("url").value.toString())
                                        .into(bigImage)
                                    bigImage.setOnClickListener {

                                        constraintLayout.removeView(bigImage)
                                        addPhotos(scrollLinear)

                                    }
                                }
                                var imagesKey = listOf<String>()

                                newLinearLayout.setOnLongClickListener {

                                    val builer = AlertDialog.Builder(this)
                                    builer.setPositiveButton("Удалить фото") { dialog, id ->
                                        for (imageRes in imageResult.children) {
                                            if (!imagesKey.contains(imageRes.key.toString())) {
                                                imagesKey =
                                                    imagesKey + imageRes.key!!.toString()
                                            }
                                        }
                                        imagesKey.sorted()
                                        for (key in imagesKey) {
                                            if (image.key.toString() == key) {
                                                if (imagesKey.indexOf(key) == 0) {
                                                    FirebaseDatabase.getInstance().reference.child(
                                                        "UserNotesSketches"
                                                    )
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .child(title.toString())
                                                        .child("firstText")
                                                        .get().addOnSuccessListener {

                                                            FirebaseDatabase.getInstance().reference.child(
                                                                "UserNotesSketches"
                                                            )
                                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                .child(title.toString())
                                                                .child("firstText")
                                                                .get().addOnSuccessListener {
                                                                    var oldText =
                                                                        it.value.toString()
                                                                    if (it.value.toString() == "null") {
                                                                        oldText = " "
                                                                    }
                                                                    FirebaseDatabase.getInstance().reference.child(
                                                                        "UserNotesSketches"
                                                                    )
                                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                        .child(title.toString())
                                                                        .child("firstText")
                                                                        .setValue(oldText + editText.text.toString() + nextText.text.toString())
                                                                }
                                                            FirebaseDatabase.getInstance().reference.child(
                                                                "UserNotesSketches"
                                                            )
                                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                .child(title.toString())
                                                                .child("images")
                                                                .child(image.key.toString())
                                                                .removeValue()
                                                                .addOnCompleteListener {
                                                                    editTexts.remove(editText)
                                                                    nextEditTexts.remove(
                                                                        nextText
                                                                    )
                                                                    addPhotos(scrollLinear)
                                                                    dialog.cancel()
                                                                }

                                                        }
                                                } else {
                                                    val minus = (key.toInt()) - imagesKey.get(
                                                        imagesKey.indexOf(key) - 1
                                                    ).toInt()
                                                    val higherImage = key.toInt() - minus
                                                    FirebaseDatabase.getInstance().reference.child(
                                                        "UserNotesSketches"
                                                    )
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .child(title.toString()).child("images")
                                                        .child(higherImage.toString())
                                                        .child("nextText").get()
                                                        .addOnSuccessListener {
                                                            var oldText = it.value.toString()
                                                            if (oldText == "null") {
                                                                oldText = " "
                                                            }
                                                            FirebaseDatabase.getInstance().reference.child(
                                                                "UserNotesSketches"
                                                            )
                                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                .child(title.toString())
                                                                .child("images")
                                                                .child(higherImage.toString())
                                                                .child("nextText")
                                                                .setValue(oldText + (editText.text.toString()) + (nextText.text.toString()))
                                                                .addOnSuccessListener {
                                                                    nextEditTexts.remove(nextText)
                                                                    editTexts.remove(editText)

                                                                    oldPhotoCount.get()
                                                                        .addOnSuccessListener {
                                                                            val imageKey: MutableList<String> =
                                                                                mutableListOf<String>()
                                                                            for (imageRest in it.children) {
                                                                                imageKey.add(
                                                                                    imageRest.key.toString()
                                                                                )
                                                                            }
                                                                            imageKey.remove(image.key.toString())
                                                                            for (key in imageKey) {
                                                                                for (edit in editTexts) {
                                                                                    if (imageKey.indexOf(
                                                                                            key
                                                                                        ) == editTexts.indexOf(
                                                                                            edit
                                                                                        )
                                                                                    ) {
                                                                                        oldPhotoCount.child(
                                                                                            key.toString()
                                                                                        )
                                                                                            .child("text")
                                                                                            .setValue(
                                                                                                edit.text.toString()
                                                                                            )
                                                                                    }

                                                                                }
                                                                            }
                                                                            for (key in imageKey) {
                                                                                for (edit in nextEditTexts) {
                                                                                    if (imageKey.indexOf(
                                                                                            key
                                                                                        ) == editTexts.indexOf(
                                                                                            edit
                                                                                        )
                                                                                    ) {
                                                                                        oldPhotoCount.child(
                                                                                            key.toString()
                                                                                        )
                                                                                            .child("nextText")
                                                                                            .setValue(
                                                                                                edit.text.toString()
                                                                                            )
                                                                                    }

                                                                                }
                                                                            }
                                                                        }
                                                                    FirebaseDatabase.getInstance().reference.child(
                                                                        "UserNotesSketches"
                                                                    )
                                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                        .child(title.toString())
                                                                        .child("images")
                                                                        .child(key)
                                                                        .removeValue()
                                                                    addPhotos(scrollLinear)
                                                                    dialog.cancel()
                                                                }
                                                        }
                                                }
                                            }
                                        }
                                    }
                                    builer.setNegativeButton("В другую сторону") { dialog, id ->

                                        val oldPhotoCount =
                                            FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid).child(title.toString())
                                                .child("images")

                                        oldPhotoCount.get().addOnSuccessListener {
                                            val imageKey: MutableList<String> = mutableListOf<String>()
                                            for (image in it.children) {
                                                imageKey.add(image.key.toString())
                                            }
                                            for (key in imageKey) {
                                                for (edit in editTexts) {
                                                    if (imageKey.indexOf(key) == editTexts.indexOf(edit)) {
                                                        oldPhotoCount.child(key.toString())
                                                            .child("text")
                                                            .setValue(edit.text.toString())
                                                    }

                                                }
                                                for (edit in nextEditTexts) {
                                                    if (imageKey.indexOf(key) == nextEditTexts.indexOf(edit)) {
                                                        oldPhotoCount.child(key.toString())
                                                            .child("nextText")
                                                            .setValue(edit.text.toString())
                                                    }

                                                }
                                            }
                                        }

                                        FirebaseDatabase.getInstance().reference.child(
                                            "UserNotesSketches"
                                        )
                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                            .child(title.toString())
                                            .child("images")
                                            .child(image.key.toString())
                                            .child("direction").get().addOnSuccessListener {
                                                if (it.value.toString() == "null") {
                                                    newLinearLayout.layoutDirection =
                                                        View.LAYOUT_DIRECTION_RTL
                                                    editText.textDirection =
                                                        View.LAYOUT_DIRECTION_LTR
                                                    FirebaseDatabase.getInstance().reference.child(
                                                        "UserNotesSketches"
                                                    )
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .child(title.toString())
                                                        .child("images").child(image.key.toString())
                                                        .child("direction").setValue("RTL")

                                                    addPhotos(scrollLinear)

                                                } else {
                                                    newLinearLayout.layoutDirection =
                                                        View.LAYOUT_DIRECTION_LTR
                                                    editText.textDirection =
                                                        View.LAYOUT_DIRECTION_LTR
                                                    FirebaseDatabase.getInstance().reference.child(
                                                        "UserNotesSketches"
                                                    )
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .child(title.toString())
                                                        .child("images").child(image.key.toString())
                                                        .child("direction").setValue("null")
                                                    addPhotos(scrollLinear)
                                                }
                                                dialog.cancel()
                                            }

                                    }
                                    val alert = builer.create()
                                    alert.show()
                                    true
                                }


                            }
                        }
                    progressBar.visibility = View.GONE
                }
            }
    }


    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    val CAMERA_REQUEST_CODE = 0

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == RESULT_OK && data != null) {

            val titleExtra = intent.getExtras()!!.getString("title")
            val title = intent.getExtras()!!.getString("title")
            val oldPhotoCount =
                FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(title.toString())
                    .child("images")

            oldPhotoCount.get().addOnSuccessListener { oldPhotos ->
                val firebaseStorageReferencePath =
                    FirebaseStorage.getInstance().getReference("UserNotesSketches")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(titleExtra.toString())
                        .child((oldPhotos.children.count().toInt() + 1).toString())
                        .child("url")
                firebaseStorageReferencePath.putFile(data.data!!).addOnCompleteListener {
                    if (it.isSuccessful) {
                        firebaseStorageReferencePath.downloadUrl.addOnCompleteListener {
                            if (it.isSuccessful) {
                                val photoUrl = it.result.toString()
                                oldPhotoCount.child(
                                    (oldPhotos.children.count().toString()
                                        .toInt() + 1).toString()
                                ).child("url").setValue(photoUrl)
                                oldPhotoCount.child(
                                    (oldPhotos.children.count().toString()
                                        .toInt() + 1).toString()
                                ).child("text").setValue("")
                                oldPhotoCount.child(
                                    (oldPhotos.children.count().toString()
                                        .toInt() + 1).toString()
                                ).child("nextText").setValue("")

                                FirebaseDatabase.getInstance().getReference()
                                    .child("UserNotesSketches")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child(titleExtra.toString()).child("firstText")
                                    .setValue(firstText.text.toString())
                                oldPhotoCount.get().addOnSuccessListener {
                                    for (image in it.children) {
                                        for (edit in editTexts) {
                                            if ((image.key!!.toInt()).toString() == (editTexts.indexOf(
                                                    edit
                                                )
                                                    .toInt() + 1).toString()
                                            ) {
                                                oldPhotoCount.child(image.key.toString())
                                                    .child("text")
                                                    .setValue(edit.text.toString())
                                            }

                                        }
                                        for (edit in nextEditTexts) {
                                            if ((image.key!!.toInt()).toString() == (nextEditTexts.indexOf(
                                                    edit
                                                )
                                                    .toInt() + 1).toString()
                                            ) {
                                                oldPhotoCount.child(image.key.toString())
                                                    .child("nextText")
                                                    .setValue(edit.text.toString())
                                            }

                                        }
                                    }
                                    addPhotos(findViewById(R.id.scroll_linear))

                                }
                            }
                        }
                    }
                }
            }
        }


    }


}

private fun LinearLayout.setOnLongClickListener(longClickListener: () -> Task<DataSnapshot>) {

}


const val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
const val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
const val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX

fun encrypt(strToEncrypt: String): String? {
    try {
        val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec = PBEKeySpec(
            secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256
        )
        val tmp = factory.generateSecret(spec)
        val secretKey = SecretKeySpec(tmp.encoded, "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(
            Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec
        )
        return Base64.encodeToString(
            cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8)), Base64.DEFAULT
        )
    } catch (e: Exception) {
        println("Error while encrypting: $e")
    }
    return null
}

fun decrypt(strToDecrypt: String): String? {
    try {

        val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec = PBEKeySpec(
            secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256
        )
        val tmp = factory.generateSecret(spec);
        val secretKey = SecretKeySpec(tmp.encoded, "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(
            Cipher.DECRYPT_MODE, secretKey, ivParameterSpec
        );
        return String(
            cipher.doFinal(
                Base64.decode(
                    strToDecrypt, Base64.DEFAULT
                )
            )
        )
    } catch (e: Exception) {
        println("Error while decrypting: $e");
    }
    return null
}


