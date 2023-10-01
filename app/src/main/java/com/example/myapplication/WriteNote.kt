package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import pl.droidsonroids.gif.GifImageView
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class WriteNote : AppCompatActivity() {


    var editTexts: MutableList<TextInputEditText> = mutableListOf<TextInputEditText>()
    var nextEditTexts: MutableList<TextInputEditText> = mutableListOf<TextInputEditText>()
    lateinit var recyclerView: RecyclerView
    lateinit var firebaseAdapter: FirebaseRecyclerAdapter<Sticker, HolderStickers>
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var firstText: TextInputEditText

    var wholeText = listOf<TextInputEditText>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_note)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler(Looper.getMainLooper()).postDelayed({
            findViewById<ConstraintLayout>(R.id.screen).visibility = View.GONE
            addGifs()
        }, 2000)


        addPhotos(findViewById(R.id.scroll_linear))



        FirebaseDatabase.getInstance().reference.child("UserNotesFavourite")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(intent.extras!!.getString("titleKey").toString()).get().addOnSuccessListener {
                if (it.value.toString() != "null") {
                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(intent.extras!!.getString("titleKey").toString()).get()
                        .addOnSuccessListener {
                            FirebaseDatabase.getInstance().reference.child("UserNotesFavourite")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(intent.extras!!.getString("titleKey").toString())
                                .setValue(it.value)
                        }
                }
            }
        FirebaseDatabase.getInstance().reference.child("NotSearchedStickers")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
                for (sticker in it.children) {
                    FirebaseDatabase.getInstance().reference.child("NotSearchedStickers")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(sticker.key.toString()).get().addOnSuccessListener {
                            if (it.value.toString() != "null") FirebaseDatabase.getInstance().reference.child(
                                "Stickers"
                            ).child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(sticker.key.toString()).setValue(it.value)
                                .addOnSuccessListener {
                                    FirebaseDatabase.getInstance().reference.child(
                                        "NotSearchedStickers"
                                    ).child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child(sticker.key.toString()).removeValue()
                                }
                        }
                }
            }

        val search = findViewById<TextInputEditText>(R.id.stickerSearch)
        search.imeOptions = EditorInfo.IME_ACTION_DONE
        search.setOnEditorActionListener { v, actionID, event ->
            if (actionID == EditorInfo.IME_ACTION_DONE) {
                val text = search.text.toString()
                FirebaseDatabase.getInstance().reference.child("Stickers")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid).get()
                    .addOnSuccessListener {
                        for (sticker in it.children) {
                            var boolean = true
                            for (letter in text) {
                                if (sticker.key.toString().contains(text.toString())) {
                                    boolean = false
                                }
                            }
                            if (text.replace(" ", "") == "") {
                                FirebaseDatabase.getInstance().reference.child("NotSearchedStickers")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid).get()
                                    .addOnSuccessListener {
                                        for (sticker in it.children) {
                                            FirebaseDatabase.getInstance().reference.child("NotSearchedStickers")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child(sticker.key.toString()).get()
                                                .addOnSuccessListener {
                                                    if (it.value.toString() != "null") FirebaseDatabase.getInstance().reference.child(
                                                        "Stickers"
                                                    )
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .child(sticker.key.toString())
                                                        .setValue(it.value).addOnSuccessListener {
                                                            FirebaseDatabase.getInstance().reference.child(
                                                                "NotSearchedStickers"
                                                            )
                                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                .child(sticker.key.toString())
                                                                .removeValue()
                                                        }
                                                }
                                        }
                                    }
                            } else if (!boolean) {
                                FirebaseDatabase.getInstance().reference.child("NotSearchedStickers")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child(sticker.key.toString()).get().addOnSuccessListener {
                                        if (it.value.toString() != "null") FirebaseDatabase.getInstance().reference.child(
                                            "Stickers"
                                        ).child(FirebaseAuth.getInstance().currentUser!!.uid)
                                            .child(sticker.key.toString()).setValue(it.value)
                                            .addOnSuccessListener {
                                                FirebaseDatabase.getInstance().reference.child(
                                                    "NotSearchedStickers"
                                                )
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child(sticker.key.toString()).removeValue()
                                            }
                                    }

                            } else {
                                FirebaseDatabase.getInstance().reference.child("Stickers")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child(sticker.key.toString()).get().addOnSuccessListener {
                                        if (it.value.toString() != "null") FirebaseDatabase.getInstance().reference.child(
                                            "NotSearchedStickers"
                                        ).child(FirebaseAuth.getInstance().currentUser!!.uid)
                                            .child(sticker.key.toString()).setValue(it.value)
                                            .addOnSuccessListener {
                                                FirebaseDatabase.getInstance().reference.child(
                                                    "Stickers"
                                                )
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child(sticker.key.toString()).removeValue()
                                            }
                                    }

                            }

                        }
                    }

            }

            true
        }

        val materialToolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
        setSupportActionBar(materialToolbar)
        materialToolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.done) {
                val title = findViewById<TextInputEditText>(R.id.read_title)
                if (title.text.toString().replace(" ", "") == "") {
                    title.setText(intent.extras!!.getString("title").toString())
                    done()
                } else {
                    done()
                }
                finish()
            }
            if (menuItem.itemId == R.id.go_back) {
                done()
            }
            true
        }
        FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
            .child(intent.extras!!.getString("author").toString())
            .child(intent.extras!!.getString("titleKey").toString()).child("data").get()
            .addOnSuccessListener {
                materialToolbar.title = it.value.toString()
            }

        FirebaseDatabase.getInstance().reference.child("UserNotes")
            .child(intent.extras!!.getString("author").toString())
            .child(intent.extras!!.getString("data").toString().replace("/", ""))
            .child(intent.extras!!.getString("titleKey").toString()).child("data").get()
            .addOnSuccessListener {
                if (it.value.toString() != "null") {
                    materialToolbar.title = it.value.toString()
                }
            }


        FirebaseDatabase.getInstance().reference.child("UserNotes")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(intent.extras!!.getString("titleKey").toString()).child("stickers").get()
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(intent.extras!!.getString("titleKey").toString()).child("stickers")
                    .setValue(it.value)

            }


        firstText = TextInputEditText(this)

        if (intent.extras!!.getString("newTitle").toString() != "null") {
            findViewById<TextInputEditText>(R.id.read_title).setText(
                intent.extras!!.getString("newTitle").toString()
            )
        } else {
            findViewById<TextInputEditText>(R.id.read_title).setText(
                intent.extras!!.getString("title").toString()
            )
        }
        wholeText = wholeText + findViewById<TextInputEditText>(R.id.read_title)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        var bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        bottomAppBar.backgroundTint = getColorStateList(R.color.white)
        var navigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationView.background = null
        navigationView.menu.getItem(2).isEnabled = false
        val card = findViewById<MaterialCardView>(R.id.stickers)
        val add = findViewById<FloatingActionButton>(R.id.addSticker)
        add.visibility = View.GONE
        card.visibility = View.GONE
        navigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            when (it.itemId) {
                R.id.favourite -> {
                    if (FirebaseAuth.getInstance().currentUser!!.uid == intent.extras!!.getString("author")) {
                        FirebaseDatabase.getInstance().reference.child("UserNotesFavourite")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(intent.extras!!.getString("titleKey").toString()).get()
                            .addOnSuccessListener {
                                if (it.value.toString() == "null") {
                                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child(intent.extras!!.getString("titleKey").toString())
                                        .get().addOnSuccessListener {
                                            FirebaseDatabase.getInstance().reference.child("UserNotesFavourite")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child(
                                                    intent.extras!!.getString("titleKey").toString()
                                                ).setValue(it.value)
                                            Toast.makeText(
                                                this,
                                                "Запись добавлена в список любимых",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                } else {
                                    FirebaseDatabase.getInstance().reference.child("UserNotesFavourite")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child(intent.extras!!.getString("titleKey").toString())
                                        .removeValue()
                                    Toast.makeText(
                                        this, "Запись больше не в списке любимых", Toast.LENGTH_LONG
                                    ).show()

                                }
                            }
                    } else {
                        Toast.makeText(this, "Вы не являетесь автором записи", Toast.LENGTH_LONG)
                            .show()
                    }
                    true
                }
                R.id.stickers -> {

                    if (card.visibility == View.GONE) {
                        add.visibility = View.VISIBLE
                        card.visibility = View.VISIBLE
                        recyclerView = findViewById(R.id.recyler)
                        linearLayoutManager = LinearLayoutManager(this)
                        linearLayoutManager!!.stackFromEnd = true
                        linearLayoutManager!!.reverseLayout = true
                        showStickersStore(intent.extras!!.getString("newTitle").toString())
                        add.setOnClickListener {
                            openFileChooserSticker()
                        }
                    } else {
                        add.visibility = View.GONE
                        card.visibility = View.GONE
                    }
                    true
                }

                R.id.choose_theme -> {
                    if (intent.extras!!.getString("author")
                            .toString() == FirebaseAuth.getInstance().currentUser!!.uid
                    ) {
                        FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                            .child(intent.extras!!.getString("author").toString())
                            .child(intent.extras!!.getString("titleKey").toString()).get()
                            .addOnSuccessListener {
                                FirebaseDatabase.getInstance().reference.child("Public").get()
                                    .addOnSuccessListener { public ->

                                        if (it.child("public").value.toString() == "off" || it.child(
                                                "public"
                                            ).value.toString() == "null"
                                        ) {
                                            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child(
                                                    intent.extras!!.getString("titleKey").toString()
                                                ).child("public").setValue("on")

                                            FirebaseDatabase.getInstance().reference.child("Public")
                                                .child(
                                                    intent.extras!!.getString("titleKey").toString()
                                                ).setValue(it.value)

                                            Toast.makeText(
                                                this,
                                                "Ваша запись теперь публична",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                                .child(
                                                    intent.extras!!.getString("author").toString()
                                                ).child(
                                                    intent.extras!!.getString("titleKey").toString()
                                                ).child("public").setValue("off")
                                            FirebaseDatabase.getInstance().reference.child("Public")
                                                .child(
                                                    intent.extras!!.getString("titleKey").toString()
                                                ).removeValue()
                                            Toast.makeText(
                                                this,
                                                "Ваша запись больше не публична",
                                                Toast.LENGTH_LONG
                                            ).show()

                                        }
                                    }


                            }
                    } else {
                        Toast.makeText(this, "У вас недостаточно прав", Toast.LENGTH_LONG).show()
                    }
                    true
                }

                R.id.add_person -> {
                    val intent1 = Intent(this, AddPerson::class.java)
                    intent1.putExtra(
                        "newTitle", findViewById<TextInputEditText>(R.id.read_title).text.toString()
                    )
                    intent1.putExtra("titleKey", intent.extras!!.getString("titleKey").toString())
                    startActivity(intent1)

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


    }


    @SuppressLint("ClickableViewAccessibility")
    private fun addGifs() {

        FirebaseDatabase.getInstance().getReference().child("UserNotes")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(intent.extras!!.getString("titleKey").toString()).child("stickers").get()
            .addOnSuccessListener { stickerCount ->
                for (sticker in stickerCount.children) {
                    if (sticker.child("x").value.toString() != "null") {

                        val gif = GifImageView(this)
                        Glide.with(applicationContext).load(sticker.child("url").value.toString())
                            .into(gif)
                        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
                        progressBar.visibility = View.VISIBLE

                        FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(intent.extras!!.getString("titleKey").toString())
                            .child("stickers").child(sticker.key.toString()).child("layout").get()
                            .addOnSuccessListener {}
                        FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(intent.extras!!.getString("titleKey").toString())
                            .child("stickers").child(sticker.key.toString()).get()
                            .addOnSuccessListener {
                                if (sticker.child("side").value.toString() == "inside") {
                                    val gifParams = RelativeLayout.LayoutParams(400, 400)
                                    val text = findViewById<RelativeLayout>(R.id.stickersSide)
                                    text.addView(gif, gifParams)
                                } else {
                                    val gifParams = ConstraintLayout.LayoutParams(400, 400)
                                    val text = findViewById<RelativeLayout>(R.id.constraint)
                                    text.addView(gif, gifParams)
                                }
                                val layoutParams = gif.layoutParams as RelativeLayout.LayoutParams?
                                layoutParams?.leftMargin = it.child("left").value.toString().toInt()
                                layoutParams?.topMargin = it.child("top").value.toString().toInt()
                                layoutParams?.rightMargin = 0
                                layoutParams?.bottomMargin = 0
                                gif.layoutParams = layoutParams
                                findViewById<RelativeLayout>(R.id.stickersSide).invalidate()
                                findViewById<RelativeLayout>(R.id.constraint).invalidate()


                            }



                        gif.setOnClickListener {
                            val alertBuilder = AlertDialog.Builder(this)
                            alertBuilder.setMessage("Вы хотите удалить  или передвинуть стикер?")
                            alertBuilder.setPositiveButton("Удалить") { dialog, id ->
                                FirebaseDatabase.getInstance().getReference()
                                    .child("UserNotesSketches")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child(intent.extras!!.getString("titleKey").toString())
                                    .child("stickers").child(sticker.key.toString()).removeValue()
                                if (sticker.child("side").value.toString() == "inside") {
                                    val text = findViewById<RelativeLayout>(R.id.stickersSide)
                                    text.removeView(gif)
                                } else {
                                    val text = findViewById<RelativeLayout>(R.id.constraint)
                                    text.removeView(gif)
                                }
                                dialog.cancel()


                            }
                            alertBuilder.setNegativeButton("Передвинуть") { dialog, id ->
                                val btn = findViewById<FloatingActionButton>(R.id.doneTouch)
                                btn.visibility = View.VISIBLE
                                gif.setOnTouchListener(object : OnTouchListener {
                                    override fun onTouch(v: View, event: MotionEvent): Boolean {
                                        val x = event.rawX.toInt()
                                        val y = event.rawY.toInt()
                                        when (event.action and MotionEvent.ACTION_MASK) {
                                            MotionEvent.ACTION_DOWN -> {
                                                val lParams =
                                                    v.layoutParams as RelativeLayout.LayoutParams
                                                xDelta = x - lParams.leftMargin
                                                yDelta = y - lParams.topMargin
                                            }
                                            MotionEvent.ACTION_UP -> Toast.makeText(
                                                this@WriteNote,
                                                "Image is on new Location!",
                                                Toast.LENGTH_SHORT
                                            ).show()
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
                                                FirebaseDatabase.getInstance().getReference()
                                                    .child("UserNotesSketches")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child(
                                                        intent.extras!!.getString("titleKey")
                                                            .toString()
                                                    ).child("stickers")
                                                    .child(sticker.key.toString()).child("left")
                                                    .setValue(x - xDelta)
                                                FirebaseDatabase.getInstance().getReference()
                                                    .child("UserNotesSketches")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child(
                                                        intent.extras!!.getString("titleKey")
                                                            .toString()
                                                    ).child("stickers")
                                                    .child(sticker.key.toString()).child("top")
                                                    .setValue(y - yDelta)
                                                gif.layoutParams = layoutParams
                                            }

                                        }
                                        findViewById<RelativeLayout>(R.id.stickersSide).invalidate()
                                        findViewById<RelativeLayout>(R.id.constraint).invalidate()
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




                        progressBar.visibility = View.GONE
                    }
                }
            }
    }

    private var xDelta = 0
    private var yDelta = 0

    private fun openFileChooserSticker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 5)

    }

    private fun showStickersStore(firstTitle: String) {
        val stickersPath = FirebaseDatabase.getInstance().getReference().child("Stickers")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        val options =
            FirebaseRecyclerOptions.Builder<Sticker>().setQuery(stickersPath, Sticker::class.java)
                .build()
        firebaseAdapter = object : FirebaseRecyclerAdapter<Sticker, HolderStickers>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderStickers {

                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pod_sticker, parent, false)
                val viewHolder = HolderStickers(itemView, Sticker())
                return viewHolder

            }

            override fun onBindViewHolder(
                holder: HolderStickers, position: Int, model: Sticker
            ) {

                holder.setDetails(
                    this@WriteNote as Activity,
                    model.imageUri,
                    findViewById<TextInputEditText>(R.id.read_title).text.toString(),
                    model.title,
                    model.author,
                    model.status,
                    model.key,
                    intent.extras!!.getString("titleKey").toString(),
                    firstTitle,
                    intent.extras!!.getString("data").toString().replace("/", "")

                )

            }

        }
        recyclerView.adapter = firebaseAdapter
        recyclerView.layoutManager = linearLayoutManager
        firebaseAdapter.startListening()
        firebaseAdapter.notifyDataSetChanged()

    }


    private fun done() {
        val titleInput = findViewById<TextInputEditText>(R.id.read_title)
        val titleExtra = intent.extras!!.getString("titleKey")

        findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({

            findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
            finish()

        }, 4000)

        val oldPhotoCount = FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child(titleExtra.toString())
            .child("images")

        if (firstText.text.toString() != " ") {
            FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child(titleExtra.toString())
                .child("firstText").setValue(encrypt(firstText.text.toString()))
        } else {
            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).child(titleExtra.toString())
                .child("firstText").setValue(encrypt(""))

        }.addOnCompleteListener {


            oldPhotoCount.get().addOnSuccessListener {
                var imageKey: List<String> = listOf<String>()
                for (image in it.children) {
                    imageKey = imageKey + image.key.toString()
                }
                for (key in imageKey) {
                    for (edit in editTexts) {
                        if (imageKey.indexOf(key) == editTexts.indexOf(edit)) {
                            oldPhotoCount.child(key.toString()).child("text")
                                .setValue(encrypt(edit.text.toString()))
                        }

                    }
                    for (edit in nextEditTexts) {
                        if (imageKey.indexOf(key) == nextEditTexts.indexOf(edit)) {
                            oldPhotoCount.child(key.toString()).child("nextText")
                                .setValue(encrypt(edit.text.toString()))
                        }

                    }
                }
            }.addOnCompleteListener {
                FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(intent.extras!!.getString("titleKey").toString()).child("newTitle")
                    .setValue(titleInput.text.toString()).addOnSuccessListener {
                        FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(intent.extras!!.getString("titleKey").toString()).get()
                            .addOnSuccessListener { data ->

                                if (FirebaseAuth.getInstance().currentUser!!.uid != intent.extras!!.getString(
                                        "author"
                                    ).toString()
                                ) {
                                    FirebaseDatabase.getInstance().reference.child("Ideas")
                                        .child(intent.extras!!.getString("author").toString())
                                        .child(intent.extras!!.getString("titleKey").toString())
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child("newTitle").setValue(titleInput.text.toString())

                                    FirebaseDatabase.getInstance().reference.child("Ideas")
                                        .child(intent.extras!!.getString("author").toString())
                                        .child(intent.extras!!.getString("titleKey").toString())
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .setValue(data.value)

                                    FirebaseDatabase.getInstance().reference.child("Ideas")
                                        .child(intent.extras!!.getString("author").toString())
                                        .child(intent.extras!!.getString("titleKey").toString())
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child("authorIdea")
                                        .setValue(FirebaseAuth.getInstance().currentUser!!.uid)

                                    FirebaseDatabase.getInstance().reference.child("Users")
                                        .child(intent.extras!!.getString("author").toString())
                                        .child("token").get().addOnSuccessListener { token ->
                                            FirebaseDatabase.getInstance().reference.child("Users")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child("username").get()
                                                .addOnSuccessListener { username ->
                                                    sendNotificationIdeas(
                                                        FirebaseAuth.getInstance().currentUser!!.uid,
                                                        null,
                                                        username.value.toString(),
                                                        token.value.toString(),
                                                        this@WriteNote,
                                                        intent.extras!!.getString("newTitle")
                                                            .toString()
                                                    )
                                                }
                                        }


                                } else {

                                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid).child(
                                            intent.extras!!.getString("data").toString()
                                                .replace("/", "")
                                        ).child(intent.extras!!.getString("titleKey").toString())
                                        .child("data").get().addOnSuccessListener { date ->

                                            FirebaseDatabase.getInstance().reference.child("UserNotes")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child(
                                                    intent.extras!!.getString("data").toString()
                                                        .replace("/", "")
                                                ).child(
                                                    intent.extras!!.getString("titleKey").toString()
                                                ).setValue(data.value)
                                            FirebaseDatabase.getInstance().reference.child("UserNotes")
                                                .get().addOnSuccessListener {
                                                    for (user in it.children) {
                                                        if (user.child(
                                                                intent.extras!!.getString("data")
                                                                    .toString().replace("/", "")
                                                            ).child(
                                                                    intent.extras!!.getString("titleKey")
                                                                        .toString()
                                                                ).value.toString() != "null"
                                                        ) {
                                                            FirebaseDatabase.getInstance().reference.child(
                                                                "UserNotes"
                                                            ).child(user.key.toString()).child(
                                                                    intent.extras!!.getString("data")
                                                                        .toString().replace("/", "")
                                                                ).child(
                                                                    intent.extras!!.getString("titleKey")
                                                                        .toString()
                                                                ).setValue(data.value)
                                                        }
                                                    }
                                                }
                                            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child(
                                                    intent.extras!!.getString("titleKey").toString()
                                                ).child("public").get().addOnSuccessListener {
                                                    if (it.value.toString() == "on") {
                                                        FirebaseDatabase.getInstance().reference.child(
                                                            "Public"
                                                        ).child(
                                                                intent.extras!!.getString("data")
                                                                    .toString().replace("/", "")
                                                            ).child(
                                                                intent.extras!!.getString("titleKey")
                                                                    .toString()
                                                            ).setValue(data.value)
                                                    }
                                                }
                                        }
                                    FirebaseDatabase.getInstance().reference.child("UserNotesFavourite")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid).child(
                                            intent.extras!!.getString("data").toString()
                                                .replace("/", "")
                                        ).child(intent.extras!!.getString("titleKey").toString())
                                        .get().addOnSuccessListener {
                                            if (it.value.toString() != "null") {
                                                FirebaseDatabase.getInstance().reference.child("UserNotesFavourite")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child(
                                                        intent.extras!!.getString("data").toString()
                                                            .replace("/", "")
                                                    ).child(
                                                        intent.extras!!.getString("titleKey")
                                                            .toString()
                                                    ).setValue(data.value)
                                            }
                                        }
                                }
                                FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child(intent.extras!!.getString("titleKey").toString())
                                    .removeValue()
                            }


                    }
            }

        }
    }

    fun sendNotificationIdeas(
        uid: String,
        title: String?,
        username: String,
        token: String,
        activity: Activity,
        newTitle: String
    ) {
        val notificationSender = FcmNotificationSender(
            token,
            "Партнёр",
            "$username предложил изменения к заметке $newTitle",
            activity.applicationContext,
            activity
        )

        notificationSender.SendNotifications()

    }

    fun sendNotificationIdeasAuthor(
        uid: String,
        title: String?,
        username: String,
        token: String,
        activity: Activity,
        newTitle: String
    ) {
        val notificationSender = FcmNotificationSender(
            token,
            "Новые изменения",
            "$username обновил заметку $newTitle",
            activity.applicationContext,
            activity
        )

        notificationSender.SendNotifications()

    }


    fun addPhotos(scrollLinear: LinearLayout) {
        val oldPhotoCount = FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(intent.extras!!.getString("titleKey").toString()).child("images")
        var imagesKey = listOf<String>()

        FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(intent.extras!!.getString("titleKey").toString()).child("images").get()
            .addOnSuccessListener { imageResult ->

                for (imageRes in imageResult.children) {
                    if (!imagesKey.contains(imageRes.key.toString())) {
                        imagesKey = imagesKey + imageRes.key!!.toString()
                    }
                }
                imagesKey.sorted()
            }
        oldPhotoCount.get().addOnSuccessListener {
                var imageKey: List<String> = listOf<String>()
                for (imageRest in it.children) {
                    imageKey = imagesKey + (imageRest.key.toString())
                }

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
                            ).child(
                                "text"
                            ).setValue(
                                encrypt(
                                    edit.text.toString()
                                )
                            )

                        }

                    }

                    for (edit in nextEditTexts) {
                        if (imageKey.indexOf(
                                key
                            ) == editTexts.indexOf(
                                edit
                            )
                        ) {
                            oldPhotoCount.child(
                                key.toString()
                            ).child(
                                "nextText"
                            ).setValue(
                                encrypt(
                                    edit.text.toString()
                                )
                            )

                        }

                    }
                }
            }
        val editTextParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )

        FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(intent.extras!!.getString("titleKey").toString()).child("firstText").get()
            .addOnSuccessListener {
                if (it.value.toString() != "null") {
                    firstText.setText(decrypt(it.value.toString()))
                    scrollLinear.addView(firstText)
                } else {
                    firstText.setText(" ")
                    scrollLinear.addView(firstText)
                }

            }


        val title = intent.getExtras()!!.getString("titleKey")
        scrollLinear.removeAllViews()






        nextEditTexts.clear()
        editTexts.clear()



        FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child(title.toString())
            .child("images").get().addOnSuccessListener { imageResult ->
                var number = 0
                for (image in imageResult.children) {

                    var newLinearLayout = LinearLayout(this)
                    newLinearLayout.setOrientation(LinearLayout.HORIZONTAL)
                    val newImage = GifImageView(this)
                    val imageNumber = number++
                    val editText = TextInputEditText(this)
                    val nextText = TextInputEditText(this)

                    val editTextParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                    )
                    wholeText = wholeText + editText + nextText + firstText

                    editTextParams.marginEnd = 10

                    FirebaseDatabase.getInstance().getReference()
                    editText.layoutParams = editTextParams
                    nextText.layoutParams = editTextParams

                    nextEditTexts.add(nextText)
                    editTexts.add(editText)

                    editText.setText(decrypt(image.child("text").value.toString()))
                    nextText.setText(decrypt(image.child("nextText").value.toString()))

                    FirebaseDatabase.getInstance().reference.child(
                        "UserNotesSketches"
                    ).child(FirebaseAuth.getInstance().currentUser!!.uid).child(title.toString())
                        .child("images").child(image.key.toString()).child("direction").get()
                        .addOnSuccessListener {
                            if (it.value.toString() != "null") {
                                newLinearLayout.layoutDirection = View.LAYOUT_DIRECTION_RTL
                                editText.layoutDirection = View.LAYOUT_DIRECTION_LTR
                                newLinearLayout.addView(newImage)
                                newLinearLayout.addView(editText)

                                scrollLinear.addView(newLinearLayout)
                                scrollLinear.addView(nextText)
                            } else {
                                newLinearLayout.layoutDirection = View.LAYOUT_DIRECTION_LTR
                                editText.layoutDirection = View.LAYOUT_DIRECTION_LTR
                                newLinearLayout.addView(newImage)
                                newLinearLayout.addView(editText)

                                scrollLinear.addView(newLinearLayout)
                                scrollLinear.addView(nextText)

                            }
                        }

                    FirebaseDatabase.getInstance().reference.child(
                        "UserNotesSketches"
                    ).child(FirebaseAuth.getInstance().currentUser!!.uid).child(title.toString())
                        .child("images").child(image.key.toString()).child("url").get()
                        .addOnSuccessListener {
                            Glide.with(applicationContext).load(it.value.toString())
                                .apply(RequestOptions.overrideOf(400)).into(newImage)

                        }




                    newLinearLayout.setOnClickListener {
                        Log.i("check", imageNumber.toString())
                        val bigImage = ImageView(this)
                        val bigParams = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT
                        )
                        val constraintLayout = findViewById<RelativeLayout>(R.id.constraint)
                        constraintLayout.addView(bigImage, bigParams)
                        FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(intent.extras!!.getString("titleKey").toString()).child("images")
                            .child(image.key.toString()).child("url").get().addOnSuccessListener {
                                Glide.with(applicationContext).load(it.value.toString())

                                    .into(bigImage)
                            }
                        bigImage.setOnClickListener {

                            constraintLayout.removeView(bigImage)

                        }
                    }

                    newLinearLayout.setOnLongClickListener {
                        FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(intent.extras!!.getString("titleKey").toString())
                            .child("firstText").setValue(encrypt(firstText.text.toString()))
                        oldPhotoCount.get().addOnSuccessListener {
                                var imageKey: List<String> = listOf<String>()
                                for (imageRest in it.children) {
                                    imageKey = imagesKey + (imageRest.key.toString())
                                }

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
                                            ).child(
                                                "text"
                                            ).setValue(
                                                encrypt(
                                                    edit.text.toString()
                                                )
                                            )

                                        }

                                    }

                                    for (edit in nextEditTexts) {
                                        if (imageKey.indexOf(
                                                key
                                            ) == editTexts.indexOf(
                                                edit
                                            )
                                        ) {
                                            oldPhotoCount.child(
                                                key.toString()
                                            ).child(
                                                "nextText"
                                            ).setValue(
                                                encrypt(
                                                    edit.text.toString()
                                                )
                                            )

                                        }

                                    }
                                }
                            }

                        var imagesKey = listOf<String>()
                        val builer = AlertDialog.Builder(this)
                        builer.setPositiveButton("Удалить фото") { dialog, id ->
                            FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(intent.extras!!.getString("titleKey").toString())
                                .child("images").get().addOnSuccessListener { imageResult ->

                                    for (imageRes in imageResult.children) {
                                        if (!imagesKey.contains(imageRes.key.toString())) {
                                            imagesKey = imagesKey + imageRes.key!!.toString()
                                        }
                                    }
                                    imagesKey.sorted()
                                    for (key in imagesKey) {
                                        if (image.key.toString() == key) {
                                            if (imagesKey.indexOf(key) == 0) {
                                                Log.i("check", "check")
                                                FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child(
                                                        intent.extras!!.getString("titleKey")
                                                            .toString()
                                                    ).child("images").child(image.key.toString())
                                                    .get().addOnSuccessListener {
                                                        val edit =
                                                            decrypt(it.child("text").value.toString())
                                                        val nextEdit =
                                                            decrypt(it.child("nextText").value.toString())

                                                        FirebaseDatabase.getInstance().reference.child(
                                                                "UserNotesSketches"
                                                            )
                                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                            .child(
                                                                intent.extras!!.getString("titleKey")
                                                                    .toString()
                                                            ).child("firstText")
                                                            .setValue(encrypt(firstText.text.toString() + edit + nextEdit))
                                                        FirebaseDatabase.getInstance().reference.child(
                                                                "UserNotesSketches"
                                                            )
                                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                            .child(
                                                                intent.extras!!.getString("titleKey")
                                                                    .toString()
                                                            ).child("images")
                                                            .child(image.key.toString())
                                                            .removeValue()
                                                        addPhotos(scrollLinear)
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
                                                    .child(
                                                        intent.extras!!.getString("titleKey")
                                                            .toString()
                                                    ).child("images").child(higherImage.toString())
                                                    .child("nextText").get().addOnSuccessListener {
                                                        var oldText = decrypt(it.value.toString())

                                                        FirebaseDatabase.getInstance().reference.child(
                                                            "UserNotesSketches"
                                                        )
                                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                            .child(
                                                                intent.extras!!.getString(
                                                                    "titleKey"
                                                                ).toString()
                                                            ).child("images")
                                                            .child(higherImage.toString())
                                                            .child("nextText")
                                                            .setValue(encrypt(oldText + (editText.text.toString()) + (nextText.text.toString())))
                                                            .addOnSuccessListener {
                                                                nextEditTexts.remove(
                                                                    nextText
                                                                )
                                                                editTexts.remove(
                                                                    editText
                                                                )

                                                                oldPhotoCount.get()
                                                                    .addOnSuccessListener {
                                                                        var imageKey: List<String> =
                                                                            listOf<String>()
                                                                        for (imageRest in it.children) {
                                                                            imageKey =
                                                                                imagesKey + (imageRest.key.toString())
                                                                        }
                                                                        imageKey =
                                                                            imageKey - (image.key.toString())
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
                                                                                    ).child(
                                                                                        "text"
                                                                                    ).setValue(
                                                                                        encrypt(
                                                                                            edit.text.toString()
                                                                                        )
                                                                                    )

                                                                                }

                                                                            }

                                                                            for (edit in nextEditTexts) {
                                                                                if (imageKey.indexOf(
                                                                                        key
                                                                                    ) == editTexts.indexOf(
                                                                                        edit
                                                                                    )
                                                                                ) {
                                                                                    oldPhotoCount.child(
                                                                                        key.toString()
                                                                                    ).child(
                                                                                        "nextText"
                                                                                    ).setValue(
                                                                                        encrypt(
                                                                                            edit.text.toString()
                                                                                        )
                                                                                    )

                                                                                }

                                                                            }
                                                                        }
                                                                    }
                                                                FirebaseDatabase.getInstance().reference.child(
                                                                    "UserNotesSketches"
                                                                )
                                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                                    .child(
                                                                        intent.extras!!.getString(
                                                                            "titleKey"
                                                                        ).toString()
                                                                    ).child("images").child(key)
                                                                    .removeValue()
                                                                addPhotos(scrollLinear)
                                                                dialog.cancel()
                                                            }
                                                    }
                                            }
                                        }
                                    }
                                }
                        }
                        builer.setNegativeButton("В другую сторону") { dialog, id ->

                            val oldPhotoCount = FirebaseDatabase.getInstance().getReference()
                                .child("UserNotesSketches")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid).child(
                                    intent.extras!!.getString("titleKey").toString()
                                ).child("images")

                            oldPhotoCount.get().addOnSuccessListener {
                                val imageKey: MutableList<String> = mutableListOf<String>()
                                for (image in it.children) {
                                    imageKey.add(image.key.toString())
                                }
                                for (key in imageKey) {
                                    for (edit in editTexts) {
                                        if (imageKey.indexOf(key) == editTexts.indexOf(
                                                edit
                                            )
                                        ) {
                                            oldPhotoCount.child(key.toString()).child("text")
                                                .setValue(encrypt(edit.text.toString()))
                                        }

                                    }
                                    for (edit in nextEditTexts) {
                                        if (imageKey.indexOf(key) == nextEditTexts.indexOf(
                                                edit
                                            )
                                        ) {
                                            oldPhotoCount.child(key.toString()).child("nextText")
                                                .setValue(encrypt(edit.text.toString()))
                                        }

                                    }
                                }
                            }

                            FirebaseDatabase.getInstance().reference.child(
                                "UserNotesSketches"
                            ).child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(intent.extras!!.getString("titleKey").toString())
                                .child("images").child(image.key.toString()).child("direction")
                                .get().addOnSuccessListener {
                                    if (it.value.toString() == "null") {
                                        newLinearLayout.layoutDirection = View.LAYOUT_DIRECTION_RTL
                                        editText.textDirection = View.LAYOUT_DIRECTION_LTR
                                        FirebaseDatabase.getInstance().reference.child(
                                            "UserNotesSketches"
                                        ).child(FirebaseAuth.getInstance().currentUser!!.uid).child(
                                                intent.extras!!.getString("title").toString()
                                            ).child("images").child(image.key.toString())
                                            .child("direction").setValue("RTL")


                                        turn(
                                            newLinearLayout, editText, image, scrollLinear
                                        )
                                    } else {
                                        newLinearLayout.layoutDirection = View.LAYOUT_DIRECTION_LTR
                                        editText.textDirection = View.LAYOUT_DIRECTION_LTR
                                        FirebaseDatabase.getInstance().reference.child(
                                            "UserNotesSketches"
                                        ).child(FirebaseAuth.getInstance().currentUser!!.uid).child(
                                                intent.extras!!.getString("titleKey").toString()
                                            ).child("images").child(image.key.toString())
                                            .child("direction").setValue("null")

                                        turn(
                                            newLinearLayout, editText, image, scrollLinear
                                        )
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

    }

    override fun onStart() {
        super.onStart()
        changeTheme(
            constraintLayout = findViewById(R.id.background),
            flb = listOf(
                findViewById<FloatingActionButton>(R.id.add),
                findViewById(R.id.addSticker),
                findViewById<FloatingActionButton>(R.id.doneTouch)
            ),
            text = wholeText,
            image = null,
            gif = null,
            circle = null,
            cardView = null,
            materialToolbar = null,
            btn = null,
            context = this,
            navigation = findViewById(R.id.bottomNavigationView),
            materialWriteRead = findViewById(R.id.materialToolbar),
            materialRead = null,
            nothing = null,
            nothing1 = findViewById<ProgressBar>(R.id.progress_bar)
        )
    }

    @SuppressLint("SetTextI18n")
    fun addNewPhoto(scrollLinear: LinearLayout, i: Int) {
        findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE


        findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
        val oldPhotoCount = FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(intent.extras!!.getString("titleKey").toString()).child("images")
        oldPhotoCount.get().addOnSuccessListener {
            var imageKey: List<String> = listOf<String>()
            for (image in it.children) {
                imageKey = imageKey + (image.key.toString())
            }
            for (key in imageKey) {
                for (edit in editTexts) {
                    if (imageKey.indexOf(key) == editTexts.indexOf(edit)) {
                        oldPhotoCount.child(key.toString()).child("text")
                            .setValue(encrypt(edit.text.toString()))
                    }

                }

                for (edit in nextEditTexts) {
                    if (imageKey.indexOf(key) == editTexts.indexOf(edit)) {
                        oldPhotoCount.child(key.toString()).child("nextText")
                            .setValue(encrypt(edit.text.toString()))
                    }

                }

            }
        }


        var imagesKeys = listOf<String>()
        var images = listOf<DataSnapshot>()
        oldPhotoCount.get().addOnSuccessListener {
            for (image in it.children) {
                imagesKeys = imagesKeys + (image.key.toString())
                images = images + (image)
                imagesKeys.sorted()
            }
            val newLinearLayout = LinearLayout(this)
            newLinearLayout.setOrientation(LinearLayout.HORIZONTAL)
            val newImage = GifImageView(this)
            val editText = TextInputEditText(this)
            val nextText = TextInputEditText(this)

            nextEditTexts.add(nextText)
            editTexts.add(editText)

            wholeText = wholeText + nextText + editText
            oldPhotoCount.get().addOnSuccessListener {
                var imageKey: List<String> = listOf<String>()
                for (image in it.children) {
                    imageKey = imageKey + (image.key.toString())
                }
                for (key in imageKey) {
                    for (edit in editTexts) {
                        if (imageKey.indexOf(key) == editTexts.indexOf(edit)) {
                            oldPhotoCount.child(key.toString()).child("text")
                                .setValue(encrypt(edit.text.toString()))
                        }

                    }

                    for (edit in nextEditTexts) {
                        if (imageKey.indexOf(key) == editTexts.indexOf(edit)) {
                            oldPhotoCount.child(key.toString()).child("nextText")
                                .setValue(encrypt(edit.text.toString()))
                        }

                    }

                }
            }

            val editTextParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT,
            )



            editTextParams.marginEnd = 10

            FirebaseDatabase.getInstance().getReference()
            editText.layoutParams = editTextParams
            nextText.layoutParams = editTextParams




            oldPhotoCount.get().addOnSuccessListener {

                if (it.value.toString() != "null") {


                    FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child(intent.extras!!.getString("titleKey").toString()).child("images")
                        .child(i.toString()).get().addOnSuccessListener {
                            Glide.with(applicationContext).load(it.child("url").value.toString())
                                .apply(RequestOptions.overrideOf(400)).into(newImage)
                            editText.setText(decrypt(it.child("text").value.toString()))
                            nextText.setText(decrypt(it.child("nextText").value.toString()))

                        }
                    newLinearLayout.addView(newImage)
                    newLinearLayout.addView(editText)

                    nextText.setText(" ")
                    editText.setText(" ")
                    scrollLinear.addView(newLinearLayout)
                    scrollLinear.addView(nextText)

                    newLinearLayout.setOnClickListener {
                        val bigImage = ImageView(this)
                        val bigParams = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT
                        )
                        val constraintLayout = findViewById<RelativeLayout>(R.id.constraint)
                        constraintLayout.addView(bigImage, bigParams)
                        FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(intent.extras!!.getString("titleKey").toString()).child("images")
                            .child(i.toString()).child("url").get().addOnSuccessListener {
                                Glide.with(applicationContext).load(it.value.toString())
                                    .apply(RequestOptions.overrideOf(400)).into(bigImage)
                            }
                        bigImage.setOnClickListener {

                            constraintLayout.removeView(bigImage)

                        }
                    }

                    newLinearLayout.setOnLongClickListener {
                        FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(intent.extras!!.getString("titleKey").toString())
                            .child("firstText").setValue(encrypt(firstText.text.toString()))

                        var imagesKey = listOf<String>()
                        val builer = AlertDialog.Builder(this)
                        builer.setPositiveButton("Удалить фото") { dialog, id ->

                            FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(intent.extras!!.getString("titleKey").toString())
                                .child("images").get().addOnSuccessListener { imageResult ->

                                    for (imageRes in imageResult.children) {
                                        if (!imagesKey.contains(imageRes.key.toString())) {
                                            imagesKey = imagesKey + imageRes.key!!.toString()
                                        }

                                    }
                                    FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child(intent.extras!!.getString("titleKey").toString())
                                        .child("images").child(i.toString()).get()
                                        .addOnSuccessListener { image ->
                                            if (imagesKey[0] != i.toString()) {
                                                FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child(
                                                        intent.extras!!.getString("titleKey")
                                                            .toString()
                                                    ).child("images").child((i - 1).toString())
                                                    .get().addOnSuccessListener {

                                                        FirebaseDatabase.getInstance().reference.child(
                                                            "UserNotesSketches"
                                                        )
                                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                            .child(
                                                                intent.extras!!.getString("titleKey")
                                                                    .toString()
                                                            ).child("images")
                                                            .child((i - 1).toString())
                                                            .child("nextText")
                                                            .setValue(encrypt(decrypt(it.value.toString()) + (editText.text.toString()) + (nextText.text.toString())))

                                                        FirebaseDatabase.getInstance().reference.child(
                                                            "UserNotesSketches"
                                                        )
                                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                            .child(
                                                                intent.extras!!.getString("titleKey")
                                                                    .toString()
                                                            ).child("images").child(i.toString())
                                                            .removeValue()
                                                        editTexts.remove(editText)
                                                        nextEditTexts.remove(nextText)
                                                        nextEditTexts[nextEditTexts.size - 1].setText(
                                                            decrypt(
                                                                (it.value.toString()) + encrypt(
                                                                    editText.text.toString()
                                                                ) + encrypt(nextText.text.toString())
                                                            )
                                                        )

                                                        scrollLinear.removeView(newLinearLayout)
                                                        scrollLinear.removeView(nextText)
                                                        addPhotos(scrollLinear)
                                                        dialog.dismiss()
                                                    }
                                            } else {
                                                FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child(
                                                        intent.extras!!.getString("titleKey")
                                                            .toString()
                                                    ).child("firstText").get()
                                                    .addOnSuccessListener {
                                                        FirebaseDatabase.getInstance().reference.child(
                                                            "UserNotesSketches"
                                                        )
                                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                            .child(
                                                                intent.extras!!.getString("titleKey")
                                                                    .toString()
                                                            ).child("firstText")
                                                            .setValue(encrypt(decrypt(it.value.toString()) + (editText.text.toString()) + (nextText.text.toString())))

                                                        FirebaseDatabase.getInstance().reference.child(
                                                            "UserNotesSketches"
                                                        )
                                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                            .child(
                                                                intent.extras!!.getString("titleKey")
                                                                    .toString()
                                                            ).child("images").child(i.toString())
                                                            .removeValue()

                                                        firstText.setText(decrypt(it.value.toString()) + (editText.text.toString()) + (nextText.text.toString()))
                                                        scrollLinear.removeView(newLinearLayout)
                                                        scrollLinear.removeView(nextText)
                                                        editTexts.remove(editText)
                                                        nextEditTexts.remove(nextText)
                                                        addPhotos(scrollLinear)
                                                        dialog.dismiss()
                                                    }
                                            }
                                        }

                                }
                        }
                        builer.setNegativeButton("В другую сторону") { dialog, id ->

                            val oldPhotoCount = FirebaseDatabase.getInstance().getReference()
                                .child("UserNotesSketches")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid).child(
                                    intent.extras!!.getString("titleKey").toString()
                                ).child("images")
                            oldPhotoCount.get().addOnSuccessListener {
                                var imageKey: List<String> = listOf<String>()
                                for (image in it.children) {
                                    imageKey = imageKey + (image.key.toString())
                                }
                                for (key in imageKey) {
                                    for (edit in editTexts) {
                                        if (imageKey.indexOf(key) == editTexts.indexOf(edit)) {
                                            oldPhotoCount.child(key.toString()).child("text")
                                                .setValue(encrypt(edit.text.toString()))
                                        }

                                    }

                                    for (edit in nextEditTexts) {
                                        if (imageKey.indexOf(key) == editTexts.indexOf(edit)) {
                                            oldPhotoCount.child(key.toString()).child("nextText")
                                                .setValue(encrypt(edit.text.toString()))
                                        }

                                    }

                                }
                            }
                            oldPhotoCount.get().addOnSuccessListener {
                                val imageKey: MutableList<String> = mutableListOf<String>()
                                for (image in it.children) {
                                    imageKey.add(image.key.toString())
                                }
                                for (key in imageKey) {
                                    for (edit in editTexts) {
                                        if (imageKey.indexOf(key) == editTexts.indexOf(
                                                edit
                                            )
                                        ) {
                                            oldPhotoCount.child(key.toString()).child("text")
                                                .setValue(encrypt(edit.text.toString()))
                                        }

                                    }
                                    for (edit in nextEditTexts) {
                                        if (imageKey.indexOf(key) == nextEditTexts.indexOf(
                                                edit
                                            )
                                        ) {
                                            oldPhotoCount.child(key.toString()).child("nextText")
                                                .setValue(encrypt(edit.text.toString()))
                                        }

                                    }
                                }
                            }

                            FirebaseDatabase.getInstance().reference.child(
                                "UserNotesSketches"
                            ).child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(intent.extras!!.getString("titleKey").toString())
                                .child("images").child(i.toString()).child("direction").get()
                                .addOnSuccessListener {
                                    if (it.value.toString() == "null") {
                                        newLinearLayout.layoutDirection = View.LAYOUT_DIRECTION_RTL
                                        editText.textDirection = View.LAYOUT_DIRECTION_LTR
                                        FirebaseDatabase.getInstance().reference.child(
                                            "UserNotesSketches"
                                        ).child(FirebaseAuth.getInstance().currentUser!!.uid).child(
                                                intent.extras!!.getString("titleKey").toString()
                                            ).child("images").child(i.toString()).child("direction")
                                            .setValue("RTL")

                                        FirebaseDatabase.getInstance().getReference()
                                            .child("UserNotesSketches")
                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                            .child(
                                                intent.extras!!.getString("titleKey").toString()
                                                    .toString()
                                            ).child("images").child(i.toString()).get()
                                            .addOnSuccessListener {
                                                turn(
                                                    newLinearLayout, editText, it, scrollLinear
                                                )
                                            }

                                    } else {
                                        newLinearLayout.layoutDirection = View.LAYOUT_DIRECTION_LTR
                                        editText.textDirection = View.LAYOUT_DIRECTION_LTR
                                        FirebaseDatabase.getInstance().reference.child(
                                            "UserNotesSketches"
                                        ).child(FirebaseAuth.getInstance().currentUser!!.uid).child(
                                                intent.extras!!.getString("titleKey").toString()
                                            ).child("images").child(i.toString()).child("direction")
                                            .setValue("null")

                                        FirebaseDatabase.getInstance().getReference()
                                            .child("UserNotesSketches")
                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                            .child(intent.extras!!.getString("titleKey").toString())
                                            .child("images").child(i.toString()).get()
                                            .addOnSuccessListener {
                                                turn(
                                                    newLinearLayout, editText, it, scrollLinear
                                                )
                                            }
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
            findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE

        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun turn(
        newLinearLayout: LinearLayout,
        editText: TextInputEditText,
        image: DataSnapshot,
        scrollLinear: LinearLayout
    ) {

        FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(intent.extras!!.getString("titleKey").toString()).child("firstText")
            .setValue(encrypt(firstText.text.toString()))


        FirebaseDatabase.getInstance().reference.child(
            "UserNotesSketches"
        ).child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(intent.extras!!.getString("titleKey").toString()).child("images")
            .child(image.key.toString()).child("direction").get().addOnSuccessListener {
                if (it.value.toString() != "null") {
                    newLinearLayout.layoutDirection = View.LAYOUT_DIRECTION_RTL
                    editText.layoutDirection = View.LAYOUT_DIRECTION_LTR
                    addPhotos(scrollLinear)

                } else {
                    newLinearLayout.layoutDirection = View.LAYOUT_DIRECTION_LTR
                    editText.layoutDirection = View.LAYOUT_DIRECTION_LTR
                    addPhotos(scrollLinear)
                }
            }

    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)

    }

    val REQUEST_SELECT_IMAGE_IN_ALBUM = 1

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler(Looper.getMainLooper()).postDelayed({
            findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
        }, 3000)

        val titleExtra = intent.getExtras()!!.getString("title")
        val title = intent.getExtras()!!.getString("titleKey")
        val oldPhotoCount = FirebaseDatabase.getInstance().getReference().child("UserNotesSketches")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child(title.toString())
            .child("images")

        oldPhotoCount.get().addOnSuccessListener { oldPhotos ->
            val firebaseStorageReferencePath =
                FirebaseStorage.getInstance().reference.child("Users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
            if (REQUEST_SELECT_IMAGE_IN_ALBUM == requestCode && resultCode == RESULT_OK && data != null) firebaseStorageReferencePath.putFile(
                data.data!!
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    firebaseStorageReferencePath.downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val photoUrl = it.result.toString()

                            FirebaseDatabase.getInstance().reference.child("Images")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid).get()
                                .addOnSuccessListener {
                                    if (it.value.toString() != "null") {
                                        var listNumbers = listOf<Int>()
                                        var linearNumbers = listOf<Int>()

                                        for (linear in it.children) {
                                            linearNumbers =
                                                linearNumbers + linear.key.toString().toInt()

                                        }

                                        FirebaseDatabase.getInstance().reference.child("Images")
                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                            .child(linearNumbers.max().toString()).get()
                                            .addOnSuccessListener {
                                                if (it.children.count() < 3) {
                                                    FirebaseDatabase.getInstance().reference.child("Images")
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .child(linearNumbers.max().toString())
                                                        .child("image" + (it.children.count() + 1).toString())
                                                        .setValue(photoUrl)
                                                } else {
                                                    val linear = linearNumbers.max()
                                                    FirebaseDatabase.getInstance().reference.child("Images")
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .child((linear + 1).toString())
                                                        .child("image" + (1).toString())
                                                        .setValue(photoUrl)
                                                }
                                            }

                                    } else {
                                        FirebaseDatabase.getInstance().reference.child("Images")
                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                            .child(1.toString()).child("image" + (1).toString())
                                            .setValue(photoUrl)
                                    }
                                }
                            oldPhotoCount.get().addOnSuccessListener {
                                Log.i("heck", (it.children.count() + 1).toString())
                                oldPhotoCount.child(
                                    (it.children.count() + 1).toString()
                                ).child("url").setValue(photoUrl)
                                oldPhotoCount.child(
                                    (it.children.count() + 1).toString()
                                ).child("text").setValue(encrypt(""))
                                oldPhotoCount.child(
                                    (it.children.count() + 1).toString()
                                ).child("nextText").setValue(encrypt(""))

                            }
                            oldPhotoCount.get().addOnSuccessListener {
                                for (image in it.children) {
                                    for (edit in editTexts) {
                                        if ((image.key!!.toInt()).toString() == (editTexts.indexOf(
                                                edit
                                            ).toInt() + 1).toString()
                                        ) {
                                            oldPhotoCount.child(image.key.toString()).child("text")
                                                .setValue(encrypt(edit.text.toString()))
                                        }

                                    }
                                    for (edit in nextEditTexts) {
                                        if ((image.key!!.toInt()).toString() == (nextEditTexts.indexOf(
                                                edit
                                            ).toInt() + 1).toString()
                                        ) {
                                            oldPhotoCount.child(image.key.toString())
                                                .child("nextText")
                                                .setValue(encrypt((edit.text.toString())))
                                                .addOnSuccessListener {}
                                        }

                                    }

                                }
                                addNewPhoto(
                                    findViewById(R.id.scroll_linear), it.children.count() + 1
                                )
                            }
                        }
                    }
                }
            }
        }
        if (5 == requestCode && resultCode == RESULT_OK && data != null) {
            val oldSticker = FirebaseDatabase.getInstance().getReference().child("Stickers")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
            val key = FirebaseDatabase.getInstance().getReference().child("Stickers")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).push().key.toString()
            oldSticker.get().addOnSuccessListener { sticker ->
                val firebaseStorageReferencePath =
                    FirebaseStorage.getInstance().getReference("Stickers")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).child(key)
                firebaseStorageReferencePath.putFile(data.data!!).addOnCompleteListener {
                    firebaseStorageReferencePath.downloadUrl.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val photoUrl = it.result.toString()
                            oldSticker.get().addOnSuccessListener {

                                oldSticker.child(key).child("title")
                                    .setValue((sticker.children.count() + 1).toString())

                                oldSticker.child(key).child("imageUri").setValue(photoUrl)

                                FirebaseDatabase.getInstance().reference.child("Users")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child("username").get().addOnSuccessListener {
                                        oldSticker.child(key).child("author")
                                            .setValue(it.value.toString())
                                    }
                                oldSticker.child(key).child("status").setValue("simple")
                                oldSticker.child(key).child("key").setValue(key)
                            }
                        }
                    }
                }
            }
        }


    }

    override fun onBackPressed() {
        done()
        finish()
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_write, menu)
        return super.onCreateOptionsMenu(menu)


    }
}

data class Sticker(
    val imageUri: String = "",
    val type: String = "",
    val title: String = "",
    val author: String = "",
    val status: String = "",
    val key: String = ""
) {

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


