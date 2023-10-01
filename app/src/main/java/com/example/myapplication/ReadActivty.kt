package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import pl.droidsonroids.gif.GifImageView

class ReadActivty : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<ModelStory, Holder>
    var linearLayoutManager: LinearLayoutManager? = null
    var texts = listOf<TextView>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_activty)
        textPhotos()
        linearLayoutManager = LinearLayoutManager(MainActivity())
        linearLayoutManager!!.reverseLayout = true
        linearLayoutManager!!.stackFromEnd = true
        recyclerView = findViewById(R.id.recycler_view)
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
            materialRead = findViewById(R.id.materialToolbar),
            nothing = null,
            nothing1 = null
        )

        texts = texts + findViewById<TextView>(R.id.read_title)
        val materialToolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
        setSupportActionBar(materialToolbar)


        FirebaseDatabase.getInstance().reference.child("UserNotes")
            .child(intent.extras!!.getString("author").toString())
            .child(intent.extras!!.getString("data").toString().replace("/", ""))
            .child(intent.extras!!.getString("title").toString()).child("data").get()
            .addOnSuccessListener {
                if (it.value.toString() != "null") {
                    val title = (it.value.toString().dropLast(5))
                    materialToolbar.title = title
                }

                materialToolbar.setOnMenuItemClickListener { menuItem ->
                    if (menuItem.itemId == R.id.edit) {
                        FirebaseDatabase.getInstance().reference.child("UserNotes")
                            .child(intent.extras!!.getString("author").toString())
                            .child(intent.extras!!.getString("data").toString().replace("/", ""))
                            .child(intent.extras!!.getString("title").toString())
                            .child("partners").get().addOnSuccessListener { users ->
                                var uids =
                                    listOf<String>(intent.extras!!.getString("author").toString())
                                for (user in users.children) {
                                    uids = uids + user.key!!.toString()
                                }
                                if (FirebaseAuth.getInstance().currentUser!!.uid in uids) {
                                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                                        .child(intent.extras!!.getString("author").toString())
                                        .child(
                                            intent.extras!!.getString("data").toString()
                                                .replace("/", "")
                                        )
                                        .child(intent.extras!!.getString("title").toString()).get()
                                        .addOnSuccessListener {
                                            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child(
                                                    intent.extras!!.getString("title").toString()
                                                )
                                                .setValue(it.value)
                                        }
                                    val intent1 = Intent(this, WriteNote::class.java)
                                    intent1.putExtra(
                                        "author",
                                        intent.extras!!.getString("author").toString()
                                    )
                                    intent1.putExtra(
                                        "titleKey",
                                        intent.extras!!.getString("title").toString()
                                    )
                                    intent1.putExtra(
                                        "newTitle",
                                        intent.extras!!.getString("newTitle").toString()
                                    )
                                    intent1.putExtra(
                                        "data",
                                        intent.extras!!.getString("data").toString()
                                    )

                                    startActivity(intent1)
                                    finish()

                                } else {
                                    Toast.makeText(
                                        this,
                                        "Вы не являетесь партнёром автора",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }

                    }

                    if (menuItem.itemId == R.id.ideas) {
                        if (findViewById<MaterialCardView>(R.id.materialCardView2).visibility != View.VISIBLE) {
                            findViewById<MaterialCardView>(R.id.materialCardView2).visibility =
                                View.VISIBLE
                            findViewById<RecyclerView>(R.id.recycler_view).visibility = View.GONE
                        } else {

                            findViewById<MaterialCardView>(R.id.materialCardView2).visibility =
                                View.GONE
                            findViewById<RecyclerView>(R.id.recycler_view).visibility = View.VISIBLE
                            clear()


                        }
                    }
                    if (menuItem.itemId == R.id.favourite) {
                        FirebaseDatabase.getInstance().reference.child("UserNotes")
                            .child(intent.extras!!.getString("author").toString())
                            .child(intent.extras!!.getString("data").toString().replace("/", ""))
                            .child(intent.extras!!.getString("title").toString())
                            .get().addOnSuccessListener {
                                FirebaseDatabase.getInstance().reference.child("UserNotes")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child(
                                        intent.extras!!.getString("data").toString()
                                            .replace("/", "")
                                    )
                                    .child(intent.extras!!.getString("title").toString())
                                    .setValue(it.value)
                            }
                        Toast.makeText(this, "Теперь вы следите за записью", Toast.LENGTH_SHORT)
                            .show()
                    }
                    true
                }
                findViewById<TextView>(R.id.read_title).setText(
                    intent.extras!!.getString("newTitle").toString()
                )
                addStickers()

                showIdeas()
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
                    materialRead = findViewById(R.id.materialToolbar),
                    nothing = null,
                    nothing1 = null
                )
            }
    }

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
            materialRead = findViewById(R.id.materialToolbar),
            nothing = null,
            nothing1 = null
        )
    }

    private fun showIdeas() {
        val options = FirebaseRecyclerOptions.Builder<ModelStory>()
            .setQuery(
                FirebaseDatabase.getInstance().reference.child("Ideas")
                    .child(intent.extras!!.getString("author").toString())
                    .child(intent.extras!!.getString("title").toString()),
                ModelStory::class.java
            ).build()

        firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<ModelStory, Holder>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
                    val itemView = layoutInflater.inflate(R.layout.item_pod, parent, false)
                    val viewHolder = Holder(itemView, ModelStory())
                    return viewHolder
                }

                override fun onBindViewHolder(
                    holder: Holder,
                    position: Int,
                    model: ModelStory
                ) {
                    holder.setDetails(
                        applicationContext,
                        model.accountImage,
                        model.title,
                        model.firstText,
                        model.firstImage,
                        model.author,
                        model.accountImageType,
                        model.oldTitle,
                        model.authorIdea,
                        model.newTitle,
                        model.authorImage,
                        model.key,
                        null,
                        null
                    )
                    holder.itemView.setOnLongClickListener {

                        val builder1: AlertDialog.Builder =
                            AlertDialog.Builder(this@ReadActivty)
                        builder1.setMessage("Вы уверены, что хотите удалить предложение?")
                        builder1.setCancelable(false)

                        builder1.setPositiveButton(
                            "Оставить предложение"
                        ) { dialog, id ->
                            dialog.cancel()
                        }

                        builder1.setNegativeButton(
                            "Удалить предложение"
                        ) { dialog, id ->
                            FirebaseDatabase.getInstance().reference.child("UserNotes")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(model.title)
                                .child("partners").child(model.authorIdea).removeValue()
                            dialog.cancel()
                        }

                        val alert11 = builder1.create()
                        alert11.show()
                        true
                    }

                    holder.itemView.setOnClickListener {

                        val intent = Intent(this@ReadActivty, ReadIdeas::class.java)
                        intent.putExtra("author", model.author)
                        intent.putExtra("authorIdea", model.authorIdea)
                        intent.putExtra("title", model.title)
                        intent.putExtra("newTitle", model.newTitle)
                        intent.putExtra("data", intent.extras!!.getString("data").toString())
                        startActivity(intent)
                    }
                }

            }
        recyclerView.adapter = firebaseRecyclerAdapter
        recyclerView.layoutManager = linearLayoutManager
        firebaseRecyclerAdapter.startListening()

    }

    var stickersList = listOf<GifImageView>()
    fun addStickers() {
        FirebaseDatabase.getInstance().getReference().child("UserNotes")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(intent.extras!!.getString("data").toString().replace("/", ""))
            .child(intent.extras!!.getString("title").toString()).child("stickers").get()
            .addOnSuccessListener { stickerCount ->
                for (sticker in stickerCount.children) {
                    if (sticker.child("top").value.toString() != "null") {

                        val gif = GifImageView(this)
                        Glide.with(applicationContext)
                            .load(sticker.child("url").value.toString())
                            .into(gif)


                        FirebaseDatabase.getInstance().getReference()
                            .child("UserNotes")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(intent.extras!!.getString("data").toString().replace("/", ""))
                            .child(intent.extras!!.getString("title").toString())
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

    fun clear() {

        for (sticker in stickersList) {
            findViewById<RelativeLayout>(R.id.constraint).removeView(sticker)
        }

    }

    fun textPhotos() {

        val title = intent.getExtras()!!.getString("title").toString()
        val scrollLinear = findViewById<LinearLayout>(R.id.scroll_linear)
        val firstText = TextView(this)
        firstText.setTextSize("20".toFloat())
        val editTextParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        texts = texts + firstText

        val images = FirebaseDatabase.getInstance().reference.child("UserNotes")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(intent.extras!!.getString("data").toString().replace("/", ""))
            .child(title).child("images")

        FirebaseDatabase.getInstance().reference.child("UserNotes")
            .child(FirebaseAuth.getInstance().currentUser!!.uid.toString())
            .child(intent.extras!!.getString("data").toString().replace("/", ""))
            .child(title).child("firstText").get().addOnSuccessListener {
                if (decrypt(it.value.toString()) != "null") {
                    val newLinearLayout = LinearLayout(this)
                    newLinearLayout.setOrientation(LinearLayout.HORIZONTAL)
                    firstText.setText(decrypt(it.value.toString()))
                    texts = texts + firstText
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
                texts = texts + editText + nextText

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
                    val constraintLayout =
                        findViewById<ConstraintLayout>(R.id.constraint)
                    constraintLayout.addView(bigImage, bigParams)
                    FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                        .child(intent.extras!!.getString("author").toString())
                        .child(intent.extras!!.getString("data").toString().replace("/", ""))
                        .child(intent.extras!!.getString("title").toString())
                        .child("images").child(image.key.toString()).child("url")
                        .get()
                        .addOnSuccessListener {
                            Glide.with(applicationContext).load(it.value.toString())

                                .into(bigImage)
                        }
                    bigImage.setOnClickListener {

                        constraintLayout.removeView(bigImage)

                    }
                }

                FirebaseDatabase.getInstance().reference.child(
                    "UserNotes"
                )
                    .child(intent.extras!!.getString("author").toString())
                    .child(intent.extras!!.getString("data").toString().replace("/", ""))
                    .child(title.toString())
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
        menuInflater.inflate(R.menu.menu_read, menu)
        return super.onCreateOptionsMenu(menu)
    }

}

