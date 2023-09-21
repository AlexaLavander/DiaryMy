package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<ModelStory, Holder>
    var linearLayoutManager: LinearLayoutManager? = null

    val mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (mAuth.currentUser == null) {
            startActivity(Intent(this, Registration::class.java))
            finish()
        }
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager!!.reverseLayout = true
        linearLayoutManager!!.stackFromEnd = true
        recyclerView = findViewById(R.id.recycler_view)

        val wall = findViewById<ImageView>(R.id.wallpaper)
        wall.visibility = View.VISIBLE
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE
        showNotesLonely()
        progressBar.visibility = View.GONE
        wall.visibility = View.GONE


        var bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        bottomAppBar.backgroundTint = getColorStateList(R.color.white)
        var navigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationView.background = null
        navigationView.menu.getItem(2).isEnabled = false

        navigationView.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            when (it.itemId) {
                R.id.mAccount -> {
                    mAuth.signOut()
                    finish()
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
                if (it.value.toString() != "null") {
                    val title = ("Черновик" + (it.children.count() + 1))
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).child("accountImage")
                        .get().addOnSuccessListener {
                            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(title).child("accountImage").setValue(it.value.toString())
                        }
                    var intent = Intent(this, WriteNote::class.java)
                    intent.putExtra("title", title)
                    startActivity(intent)
                } else {
                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).child("accountImage")
                        .get().addOnSuccessListener {
                            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child("Черновик").child("accountImage")
                                .setValue(it.value.toString())
                            var intent = Intent(this, WriteNote::class.java)
                            intent.putExtra("title", "Черновик")
                            startActivity(intent)
                        }
                }
            }

        }
    }

    private fun showNotesLonely() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val firebaseDatabaseReferenceStories = firebaseDatabase.getReference("UserNotes")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        val options = FirebaseRecyclerOptions.Builder<ModelStory>()
            .setQuery(firebaseDatabaseReferenceStories, ModelStory::class.java).build()

        firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<ModelStory, Holder>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, i: Int): Holder {
                    val itemView = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_pod, parent, false)
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
                        model.first_text,
                        model.firstImage
                    )

                    holder.itemView.setOnLongClickListener {
                        val builder1: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                        builder1.setMessage("Вы уверены, что хотите удалить запись?")
                        builder1.setCancelable(false)

                        builder1.setPositiveButton(
                            "Оставить запись"
                        ) { dialog, id ->
                          dialog.cancel()
                        }

                        builder1.setNegativeButton(
                            "Удалить запись"
                        ) { dialog, id ->
                            FirebaseDatabase.getInstance().reference.child("UserNotes")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(model.title).removeValue()
                            FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(model.title).removeValue()
                            dialog.cancel()
                        }

                        val alert11 = builder1.create()
                        alert11.show()
                        true
                    }
                    holder.itemView.setOnClickListener {
                        FirebaseDatabase.getInstance().reference.child("UserNotes")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(model.title).get().addOnSuccessListener {
                                FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child(model.title).setValue(it.value)

                                FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .child(model.title).child("title")
                                    .setValue(model.title)
                            }

                        var intent = Intent(this@MainActivity, WriteNote::class.java)
                        intent.putExtra("title", model.title)
                        startActivity(intent)
                    }
                }


            }
        recyclerView.adapter = firebaseRecyclerAdapter
        recyclerView.layoutManager = linearLayoutManager
        firebaseRecyclerAdapter.notifyDataSetChanged()
        firebaseRecyclerAdapter.startListening()


    }
}

data class ModelStory(
    var first_text: String = "",
    var title: String = "",
    var accountImage: String = "",
    var firstImage: String = ""
) {

}
