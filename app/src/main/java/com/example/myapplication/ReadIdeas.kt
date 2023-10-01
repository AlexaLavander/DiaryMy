package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import pl.droidsonroids.gif.GifImageView

class ReadIdeas : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_ideas)
        textPhotos()
        texts = texts + findViewById<TextView>(R.id.read_title)

        val materialToolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
        setSupportActionBar(materialToolbar)

        FirebaseDatabase.getInstance().reference.child("UserNotes")
            .child(intent.extras!!.getString("author").toString())
            .child(intent.extras!!.getString("data").toString().replace("/", ""))
            .child(intent.extras!!.getString("title").toString()).child("data").get()
            .addOnSuccessListener {
                materialToolbar.title = it.value.toString()
            }
        materialToolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.done) {
                if ((intent.extras!!.getString("author")
                        .toString()) == FirebaseAuth.getInstance().currentUser!!.uid
                ) {

                    FirebaseDatabase.getInstance().reference.child("Ideas")
                        .child((intent.extras!!.getString("author").toString()))
                        .child(intent.extras!!.getString("title").toString())
                        .child(intent.extras!!.getString("authorIdea").toString()).get()
                        .addOnSuccessListener { data ->
                            FirebaseDatabase.getInstance().reference.child("Ideas")
                                .child((intent.extras!!.getString("author").toString()))
                                .child(intent.extras!!.getString("title").toString())
                                .child(intent.extras!!.getString("authorIdea").toString())
                                .removeValue()
                            FirebaseDatabase.getInstance().reference.child("UserNotes")
                                .child((intent.extras!!.getString("author").toString()))
                                .child(intent.extras!!.getString("title").toString())
                                .setValue(data.value)
                            FirebaseDatabase.getInstance().reference.child("UserNotes")
                                .child((intent.extras!!.getString("author").toString()))
                                .child(intent.extras!!.getString("title").toString())
                                .child("authorIdea").removeValue().addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        FirebaseDatabase.getInstance().reference.child("UserNotes")
                                            .child((intent.extras!!.getString("author").toString()))
                                            .child(intent.extras!!.getString("data").toString().replace("/", ""))
                                            .child(intent.extras!!.getString("title").toString())
                                            .get().addOnSuccessListener { newData ->
                                                FirebaseDatabase.getInstance().reference.child("UserNotes")
                                                    .get().addOnSuccessListener {
                                                        if (it.value.toString().contains(
                                                                intent.extras!!.getString("title")
                                                                    .toString()
                                                            )
                                                        ) {
                                                            for (user in it.children) {
                                                                FirebaseDatabase.getInstance().reference.child(
                                                                    "UserNotes"
                                                                ).child(user.key.toString())
                                                                    .child(intent.extras!!.getString("data").toString().replace("/", ""))
                                                                    .child(
                                                                        intent.extras!!.getString(
                                                                            "title"
                                                                        ).toString()
                                                                    )
                                                                    .setValue(newData.value)
                                                            }
                                                        }
                                                    }
                                            }
                                    }
                                }

                            finish()
                        }
                } else {
                    Toast.makeText(this, "у вас недостаточно прав", Toast.LENGTH_LONG).show()
                }

            }
            if (menuItem.itemId == R.id.go_back) {
                finish()
            }

            true
        }
        findViewById<TextView>(R.id.read_title).setText(
            intent.extras!!.getString("newTitle").toString()
        )
        addStickers()

    }

    var texts = listOf<TextView>()

    override fun onStart() {
        super.onStart()
        changeTheme(
            constraintLayout = findViewById(R.id.constraint),
            flb = null,
            text = texts,
            image = null,
            gif = null,
            circle = null,
            cardView = null,
            materialToolbar = null,
            btn = null,
            context = this,
            navigation = null,
            materialWriteRead = findViewById(R.id.materialToolbar),
            materialRead = null,
            nothing = null,
            nothing1 = null
        )
    }

    fun addStickers() {
        FirebaseDatabase.getInstance().getReference().child("Ideas")
            .child(intent.extras!!.getString("author").toString())
            .child(intent.extras!!.getString("title").toString())
            .child(intent.extras!!.getString("authorIdea").toString())
            .child("stickers").get().addOnSuccessListener {
                for (sticker in it.children) {

                    if (sticker.child("top").value.toString() != "null") {

                        val gif = GifImageView(this)
                        Glide.with(applicationContext)
                            .load(sticker.child("url").value.toString())
                            .into(gif)


                        FirebaseDatabase.getInstance().getReference()
                            .child("Ideas")
                            .child(intent.extras!!.getString("author").toString())
                            .child(intent.extras!!.getString("title").toString())
                            .child(intent.extras!!.getString("authorIdea").toString())
                            .child("stickers")
                            .child(sticker.key.toString()).get().addOnSuccessListener {
                                if (sticker.child("side").value.toString() == "inside") {
                                    val gifParams = RelativeLayout.LayoutParams(400, 400)
                                    val text = findViewById<RelativeLayout>(R.id.stickersSide)
                                    text.addView(gif, gifParams)
                                } else {
                                    val gifParams = ConstraintLayout.LayoutParams(400, 400)
                                    val text = findViewById<RelativeLayout>(R.id.constraint1)
                                    text.addView(gif, gifParams)
                                }
                                val layoutParams =
                                    gif.layoutParams as RelativeLayout.LayoutParams?
                                layoutParams?.leftMargin =
                                    it.child("left").value.toString().toInt()
                                layoutParams?.topMargin =
                                    it.child("top").value.toString().toInt()
                                layoutParams?.rightMargin = 0
                                layoutParams?.bottomMargin = 0
                                gif.layoutParams = layoutParams
                                findViewById<RelativeLayout>(R.id.stickersSide).invalidate()
                                findViewById<RelativeLayout>(R.id.constraint1).invalidate()


                            }
                    }
                }
            }
    }

    fun textPhotos() {

        Log.i("idea", intent.extras!!.getString("authorIdea").toString())
        val scrollLinear = findViewById<LinearLayout>(R.id.scroll_linear)
        val firstText = TextView(this)
        firstText.setTextSize("20".toFloat())
        val editTextParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        val images = FirebaseDatabase.getInstance().getReference()
            .child("Ideas")
            .child(intent.extras!!.getString("author").toString())
            .child(intent.extras!!.getString("title").toString())
            .child(intent.extras!!.getString("authorIdea").toString()).child("images")

        FirebaseDatabase.getInstance().getReference()
            .child("Ideas")
            .child(intent.extras!!.getString("author").toString())
            .child(intent.extras!!.getString("title").toString())
            .child(intent.extras!!.getString("authorIdea").toString()).child("firstText").get()
            .addOnSuccessListener {
                if (decrypt(it.value.toString()) != "null") {
                    val newLinearLayout = LinearLayout(this)
                    newLinearLayout.setOrientation(LinearLayout.HORIZONTAL)
                    firstText.setText(decrypt(it.value.toString()))
                    newLinearLayout.addView(firstText, editTextParams)
                    scrollLinear.addView(newLinearLayout)
                }
            }
        images.get().addOnSuccessListener {
            for (image in it.children) {

                val newLinearLayout = LinearLayout(this)
                newLinearLayout.setOrientation(LinearLayout.HORIZONTAL)
                val newImage = ImageView(this)
                val editText = TextView(this)
                val nextText = TextView(this)


                editText.setTextSize("20".toFloat())
                nextText.setTextSize("20".toFloat())
                editTextParams.marginStart = 10


                editText.layoutParams = editTextParams
                nextText.layoutParams = editTextParams

                texts = texts + editText + nextText + firstText

                changeTheme(
                    constraintLayout = null,
                    flb = null,
                    text = texts,
                    image = null,
                    gif = null,
                    circle = null,
                    cardView = null,
                    materialToolbar = null,
                    btn = null,
                    context = this,
                    navigation = null,
                    materialWriteRead = null,
                    materialRead = null,
                    nothing = null,
                    nothing1 = null
                )

                editText.setText(decrypt(image.child("text").value.toString()))
                nextText.setText(decrypt(image.child("nextText").value.toString()))

                Glide.with(applicationContext)
                    .load(image.child("url").value.toString())
                    .apply(RequestOptions.overrideOf(400))
                    .into(newImage)

                newLinearLayout.setOnClickListener {
                    val bigImage = ImageView(this)
                    val bigParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    )
                    val constraintLayout = findViewById<ConstraintLayout>(R.id.constraint)
                    constraintLayout.addView(bigImage, bigParams)
                    FirebaseDatabase.getInstance().getReference()
                        .child("Ideas")
                        .child(intent.extras!!.getString("author").toString())
                        .child(intent.extras!!.getString("title").toString())
                        .child(intent.extras!!.getString("authorIdea").toString())
                        .child("images").child(image.key.toString()).child("url").get()
                        .addOnSuccessListener {
                            Glide.with(applicationContext).load(it.value.toString())

                                .into(bigImage)
                        }
                    bigImage.setOnClickListener {

                        constraintLayout.removeView(bigImage)

                    }
                }

                FirebaseDatabase.getInstance().getReference()
                    .child("Ideas")
                    .child(intent.extras!!.getString("author").toString())
                    .child(intent.extras!!.getString("title").toString())
                    .child(intent.extras!!.getString("authorIdea").toString())
                    .child("images").child(image.key.toString())
                    .child("direction").get().addOnSuccessListener {
                        if (it.value.toString() == "RTL") {
                            newLinearLayout.layoutDirection =
                                View.LAYOUT_DIRECTION_RTL
                            editText.layoutDirection = View.LAYOUT_DIRECTION_LTR
                            newLinearLayout.addView(newImage)
                            newLinearLayout.addView(editText)

                            scrollLinear.addView(newLinearLayout)
                            scrollLinear.addView(nextText)
                        } else {
                            newLinearLayout.layoutDirection =
                                View.LAYOUT_DIRECTION_LTR
                            editText.layoutDirection = View.LAYOUT_DIRECTION_LTR

                            newLinearLayout.addView(newImage)
                            newLinearLayout.addView(editText)

                            scrollLinear.addView(newLinearLayout)
                            scrollLinear.addView(nextText)
                        }
                    }


            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_write, menu)
        return super.onCreateOptionsMenu(menu)
    }

}