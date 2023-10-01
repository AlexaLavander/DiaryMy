package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {


    private lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<ModelStory, Holder>
    val mAuth = FirebaseAuth.getInstance()


    lateinit var recyclerViewP: RecyclerView
    lateinit var linearLayoutManagerP: LinearLayoutManager
    lateinit var firebaseAdapterP: FirebaseRecyclerAdapter<Person, HolderNotification>

    lateinit var recyclerViewF: RecyclerView
    lateinit var linearLayoutManagerF: LinearLayoutManager
    lateinit var firebaseAdapterF: FirebaseRecyclerAdapter<Person, HolderNotification>

    lateinit var recyclerViewPublic: RecyclerView
    lateinit var linearLayoutManagerPublic: LinearLayoutManager
    lateinit var firebaseAdapterPublic: FirebaseRecyclerAdapter<ModelStory, HolderPublic>
    lateinit var search: TextInputEditText

    lateinit var recyclerViewDatas: RecyclerView
    lateinit var firebaseRecyclerAdapterDatas: FirebaseRecyclerAdapter<ModelDate, DataHolder>
    var linearLayoutManagerDatas: LinearLayoutManager? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (mAuth.currentUser == null) {
            startActivity(Intent(this, Registration::class.java))
            finish()
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

            Handler(Looper.getMainLooper()).postDelayed({
                findViewById<ConstraintLayout>(R.id.screen).visibility = View.GONE

            }, 3000)
            search = findViewById<TextInputEditText>(R.id.search)
            val pathS = FirebaseDatabase.getInstance().reference.child("DatasNote")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
            val value = FirebaseDatabase.getInstance().reference.child("UserNotes")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
            linearLayoutManagerDatas = LinearLayoutManager(MainActivity())
            linearLayoutManagerDatas!!.reverseLayout = true
            linearLayoutManagerDatas!!.stackFromEnd = true
            recyclerViewDatas = findViewById(R.id.recycler_view)
            showDatas(search, pathS, value)

            val firebaseDatabase = FirebaseDatabase.getInstance()
            val firebaseDatabaseReferenceStories = firebaseDatabase.getReference("UserNotes")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)

            val options = FirebaseRecyclerOptions.Builder<ModelStory>()
                .setQuery(firebaseDatabaseReferenceStories, ModelStory::class.java).build()

            FirebaseDatabase.getInstance().reference.child("Notifications")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
                    if (it.value.toString() != "null") {
                        findViewById<MaterialToolbar>(R.id.materialToolbar)
                            .menu.getItem(0).setIcon(R.drawable.notif_active)
                    } else {
                        findViewById<MaterialToolbar>(R.id.materialToolbar)
                            .menu.getItem(0).setIcon(R.drawable.notif)

                    }
                }

            FirebaseDatabase.getInstance().reference.child("Notifications")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).addChildEventListener(object :
                    ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        findViewById<MaterialToolbar>(R.id.materialToolbar)
                            .menu.getItem(0).setIcon(R.drawable.notif_active)
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        findViewById<MaterialToolbar>(R.id.materialToolbar)
                            .menu.getItem(0).setIcon(R.drawable.notif_active)
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        findViewById<MaterialToolbar>(R.id.materialToolbar)
                            .menu.getItem(0).setIcon(R.drawable.notif)
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

            FirebaseDatabase.getInstance().reference.child("UserNotes")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).addChildEventListener(object :
                    ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            FirebaseDatabase.getInstance().reference.child("Stickers")
                .child("Default").get().addOnSuccessListener {
                    for (sticker in it.children) {
                        FirebaseDatabase.getInstance().reference.child("Stickers")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(it.value)

                    }
                }



            FirebaseDatabase.getInstance().reference.child("Stickers")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
                    if (it.value.toString() == "null") {
                        FirebaseDatabase.getInstance().reference.child("Stickers")
                            .child("Default").get().addOnSuccessListener {
                                FirebaseDatabase.getInstance().reference.child("Stickers")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .setValue(it.value)
                            }
                    }
                }


            FirebaseDatabase.getInstance().reference.child("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .get().addOnSuccessListener { data ->
                    FirebaseDatabase.getInstance().reference.child("Friends")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .get().addOnSuccessListener {
                            for (me in it.children) {
                                FirebaseDatabase.getInstance().reference.child("Friends")
                                    .child(me.key.toString())
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .setValue(data.value)

                            }
                        }

                }


            recyclerViewP = findViewById(R.id.recycler_requests)
            linearLayoutManagerP = LinearLayoutManager(this)
            linearLayoutManagerP.reverseLayout = true
            linearLayoutManagerP.stackFromEnd = true

            recyclerViewF = findViewById(R.id.recycler_view_favourite)
            linearLayoutManagerF = LinearLayoutManager(this)
            linearLayoutManagerF.reverseLayout = true
            linearLayoutManagerF.stackFromEnd = true






            FirebaseDatabase.getInstance().reference.child("UserNotes").get().addOnSuccessListener {

                for (id in it.children) {
                    for (value in id.children) {
                        if ((value.value)!!::class.java.typeName.toString() == "java.lang.String") FirebaseDatabase.getInstance().reference.child(
                            "UserNotes"
                        ).child(FirebaseAuth.getInstance().currentUser!!.uid).child("title")
                            .removeValue()
                        FirebaseDatabase.getInstance().reference.child("UserNotes")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("firstText")
                            .removeValue()
                        FirebaseDatabase.getInstance().reference.child("UserNotes")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child("accountImage")
                            .removeValue()
                    }
                }

            }




            if (!hasPermissions(this, Manifest.permission.POST_NOTIFICATIONS.toString())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1234
                    )
                }
            }

            var bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
            bottomAppBar.backgroundTint = getColorStateList(R.color.white)
            var navigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

            navigationView.background = null
            navigationView.menu.getItem(2).isEnabled = false



            navigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
                when (it.itemId) {
                    R.id.love -> {
                        val value = FirebaseDatabase.getInstance().reference.child("UserNotesFavourite")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        showDatas(search, pathS, value)
                        recyclerViewF.visibility = View.VISIBLE
                        navigationView.getMenu().getItem(3).setIcon(R.drawable.baseline_home_24)


                        true
                    }
                    R.id.mAccount -> {
                        startActivity(Intent(this, MyProfile::class.java))

                        true
                    }
                    R.id.home -> {
                        navigationView.getMenu().getItem(3).setIcon(R.drawable.baseline_public_24)

                        if (recyclerViewF.visibility == View.VISIBLE) {
                            val value = FirebaseDatabase.getInstance().reference.child("UserNotes")
                            showDatas(search, pathS, value)
                        } else if (recyclerViewDatas.visibility == View.VISIBLE) {
                            recyclerViewPublic = findViewById(R.id.recycler_view_public)
                            val value = FirebaseDatabase.getInstance().reference.child("Public")

                            showDatas(search, pathS, value)
                        } else {
                            recyclerViewDatas.visibility = View.VISIBLE


                        }
                        search.visibility = View.GONE
                        true

                    }
                    R.id.search -> {
                        if (search.visibility == View.VISIBLE) {
                            search.visibility = View.GONE
                        } else {
                            search.visibility = View.VISIBLE

                            val pathPublic =
                                FirebaseDatabase.getInstance().reference.child("Public")
                            val pathFav =
                                FirebaseDatabase.getInstance().reference.child("UserNotesFavourite")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)

                            if (recyclerViewDatas.visibility == View.VISIBLE) {
                            } else if (recyclerViewF.visibility == View.VISIBLE) {
                            } else if (recyclerViewP.visibility == View.VISIBLE) {
                                showNotesPublic(options, search, pathPublic)
                            }


                        }
                        true

                    }
                    else -> {
                        true
                    }
                }
            })


            val newStoryBtn = findViewById<FloatingActionButton>(R.id.add)
            newStoryBtn.setOnClickListener {
                val notePath = FirebaseDatabase.getInstance().getReference("UserNotes")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                notePath.get().addOnSuccessListener {
                    var numbers = listOf<String>()
                    var title = ""
                    if (it.value.toString() == "null") {
                        title = ("Черновик" + 1)

                    } else {
                        for (note in it.children) {
                            if (note.key.toString().contains("Черновик")) {
                                numbers = numbers + ((note.key.toString().replace("Черновик", "")))
                            }
                        }
                        Log.i("check", numbers.toString())
                        if (numbers.size.toString() != "0") {
                            numbers.sorted()
                            title = ("Черновик" + (it.children.count().toInt() + 1))
                        } else {
                            title = ("Черновик" + 1)
                        }
                    }
                    val titleKey =
                        FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(title).push().key.toString()
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).child("accountImage")
                        .get()
                        .addOnSuccessListener {
                            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid).child(titleKey)
                                .child("accountImage").setValue(it.value.toString())
                                .addOnSuccessListener {
                                    val sdf = SimpleDateFormat("dd/M/yyyy")
                                    val date = sdf.format(Date())
                                    FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child(titleKey).child("data").setValue(date.toString())
                                    FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child(titleKey).child("author")
                                        .setValue(FirebaseAuth.getInstance().currentUser!!.uid)
                                    FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child(titleKey).child("newTitle")
                                        .setValue(title)
                                    FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child(titleKey).child("title").setValue(titleKey)
                                        .addOnSuccessListener {
                                            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child(titleKey).child("oldTitle").setValue(title)
                                            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .child(titleKey).get().addOnSuccessListener {
                                                    FirebaseDatabase.getInstance().reference.child("DatasUsers").get().
                                                        addOnSuccessListener {
                                                            for (note in it.children){
                                                                if (note.child("data").value.toString() != date){
                                                                    FirebaseDatabase.getInstance().reference.child("DatasUsers")
                                                                        .child(titleKey)
                                                                        .child("data")
                                                                        .setValue(date)
                                                                }
                                                            }
                                                        }

                                                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                        .child(date.replace("/", ""))
                                                        .child(titleKey).setValue(it.value)
                                                }
                                            var intent = Intent(this, WriteNote::class.java)
                                            intent.putExtra("title", title)
                                            intent.putExtra("titleKey", titleKey)
                                            intent.putExtra("data", date.toString())
                                            intent.putExtra(
                                                "author",
                                                FirebaseAuth.getInstance().currentUser!!.uid
                                            )
                                            startActivity(intent)
                                        }
                                }
                        }

                }

            }


            val material = findViewById<MaterialToolbar>(R.id.materialToolbar)
            setSupportActionBar(material)
            FirebaseDatabase.getInstance().reference.child("Notifications")
                .child(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener {
                    if (it.value.toString() != "null") {
                        material.menu.getItem(0).setIcon(R.drawable.baseline_notifications_24)
                    }
                }

        }


    }

    private fun showNotesPublic(
        options: FirebaseRecyclerOptions<ModelStory>,
        search: TextInputEditText,
        pathPublic: DatabaseReference?
    ) {

        firebaseAdapterPublic =
            object : FirebaseRecyclerAdapter<ModelStory, HolderPublic>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, i: Int): HolderPublic {
                    val itemView =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_pod_public, parent, false)
                    val viewHolder = HolderPublic(itemView, ModelStory())
                    return viewHolder
                }

                override fun onBindViewHolder(
                    holder: HolderPublic, position: Int, model: ModelStory
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
                        search,
                        pathPublic

                    )

                    holder.itemView.setOnClickListener {
                        var intent = Intent(this@MainActivity, ReadActivty::class.java)
                        intent.putExtra("author", model.author)
                        intent.putExtra("title", model.title)
                        intent.putExtra("newTitle", model.newTitle)

                        startActivity(intent)
                    }

                }

            }
        recyclerViewPublic.adapter = firebaseAdapterPublic
        recyclerViewPublic.layoutManager = linearLayoutManagerPublic
        firebaseAdapterPublic.notifyDataSetChanged()
        firebaseAdapterPublic.startListening()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.notification) {
            if (findViewById<MaterialCardView>(R.id.notifications).visibility == View.VISIBLE) {
                findViewById<MaterialCardView>(R.id.notifications).visibility = View.GONE
            } else {
                val itemView = layoutInflater.inflate(R.layout.request_pod, null)
                changeTheme(
                    null,
                    listOf(
                        itemView.findViewById(R.id.cancel),
                        itemView.findViewById(R.id.requestAccept)
                    ),
                    listOf(
                        itemView.findViewById<TextView>(R.id.username),
                        itemView.findViewById<TextView>(R.id.storyText),
                        itemView.findViewById<TextView>(R.id.message)
                    ),
                    null,
                    itemView.findViewById(R.id.gif),
                    null,
                    listOf(itemView.findViewById(R.id.card_view)),
                    null,
                    null,
                    this@MainActivity,
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
                findViewById<MaterialCardView>(R.id.notifications).visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    showRequests()

                }, 500)


            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context, permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }



    private fun showDatas(
        search: TextInputEditText?,
        pathS: DatabaseReference?,
        value: DatabaseReference
    ) {
        val options = FirebaseRecyclerOptions.Builder<ModelDate>()
            .setQuery(
                FirebaseDatabase.getInstance().reference.child("DatasUsers")
                    ,
                ModelDate::class.java,
            ).build()
        firebaseRecyclerAdapterDatas =
            object : FirebaseRecyclerAdapter<ModelDate, DataHolder>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, i: Int): DataHolder {
                    val itemView =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_pod, parent, false)
                    val viewHolder = DataHolder(itemView, ModelDate())
                    return viewHolder
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onBindViewHolder(
                    holder: DataHolder, position: Int, model: ModelDate
                ) {
                    holder.setDetails(
                        this@MainActivity,
                        model.data,
                        model.key,
                        model.author,
                        search,
                        pathS,
                        value

                    )

                }
            }
        recyclerViewDatas.adapter = firebaseRecyclerAdapterDatas
        recyclerViewDatas.layoutManager = linearLayoutManagerDatas
        firebaseRecyclerAdapterDatas.notifyDataSetChanged()
        firebaseRecyclerAdapterDatas.startListening()


    }

    private fun showRequests() {


        val options = FirebaseRecyclerOptions.Builder<Person>().setQuery(
            FirebaseDatabase.getInstance().reference.child("Notifications")
                .child(FirebaseAuth.getInstance().currentUser!!.uid), Person::class.java
        ).build()

        firebaseAdapterP =
            object : FirebaseRecyclerAdapter<Person, HolderNotification>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): HolderNotification {
                    val itemView = layoutInflater.inflate(R.layout.request_pod, parent, false)

                    val viewHolderRequests = HolderNotification(itemView)


                    return viewHolderRequests
                }

                override fun onBindViewHolder(
                    holder: HolderNotification, position: Int, model: Person
                ) {
                    holder.setDetails(
                        model.username,
                        model.uid,
                        model.accountImage,
                        model.accountImageType,
                        this@MainActivity as Activity,
                        model.token,
                        model.status,
                        model.newTitle,
                        model.title,
                        model.key

                    )


                }

            }
        recyclerViewP.adapter = firebaseAdapterP
        recyclerViewP.layoutManager = linearLayoutManagerP
        firebaseAdapterP.startListening()
    }

    override fun onRestart() {
        super.onRestart()
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        findViewById<ConstraintLayout>(R.id.constraint).isEnabled = false
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler(Looper.getMainLooper()).postDelayed({
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE

        }, 2000)
        Handler(Looper.getMainLooper()).postDelayed({
            findViewById<ConstraintLayout>(R.id.constraint).isEnabled = true

        }, 1700)
    }

    override fun onStart() {
        super.onStart()


        val options = FirebaseRecyclerOptions.Builder<ModelStory>().setQuery(
            FirebaseDatabase.getInstance().reference.child("UserNotes")
                .child(FirebaseAuth.getInstance().currentUser!!.uid), ModelStory::class.java
        ).build()
        changeTheme(
            findViewById(R.id.constraint),
            listOf(findViewById(R.id.add)),
            null,
            null,
            null,
            null,
            null,
            findViewById(R.id.materialToolbar),
            null,
            this,
            findViewById(R.id.bottomNavigationView),
            null,
            null,
            null,
            findViewById(R.id.progressBar)
        )


    }

    fun search(note: DataSnapshot) {

        FirebaseDatabase.getInstance().reference.child("UserNotes")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child(note.key.toString())
            .get()
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().reference.child("Searching")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(note.key.toString())
                    .setValue(it.value)
                val firebaseDatabase = FirebaseDatabase.getInstance()
                val firebaseDatabaseReferenceStories =
                    firebaseDatabase.getReference("Searching")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                val options = FirebaseRecyclerOptions.Builder<ModelStory>().setQuery(
                    firebaseDatabaseReferenceStories, ModelStory::class.java
                ).build()

            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }
}


data class ModelStory(
    var firstText: String = "",
    var title: String = "",
    var accountImage: String = "",
    var firstImage: String = "",
    val author: String = "",
    var accountImageType: String = "",
    var oldTitle: String = "",
    var authorIdea: String = "",
    var newTitle: String = "",
    val authorImage: String = "",
    val key: String = ""

)


data class ModelDate(
    val data: String = "",
    val key: String = "",
    val author: String = "",
)

