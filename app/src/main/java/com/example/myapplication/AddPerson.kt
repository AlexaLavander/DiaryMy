package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddPerson : AppCompatActivity() {

    lateinit var recyclerViewP: RecyclerView
    lateinit var linearLayoutManagerP: LinearLayoutManager
    lateinit var firebaseAdapterP: FirebaseRecyclerAdapter<Person, HolderPersons>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_person)

        recyclerViewP = findViewById(R.id.recycler_requests)
        linearLayoutManagerP = LinearLayoutManager(this)
        linearLayoutManagerP.reverseLayout = true
        linearLayoutManagerP.stackFromEnd = true


        changeTheme(
            findViewById(R.id.constraint),
            null,
            null,
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


        showPersons()

    }

    private fun showPersons() {
        val options = FirebaseRecyclerOptions.Builder<Person>()
            .setQuery(
                FirebaseDatabase.getInstance().reference.child("Friends")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid), Person::class.java
            )
            .build()

        firebaseAdapterP = object : FirebaseRecyclerAdapter<Person, HolderPersons>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPersons {
                val itemView = layoutInflater.inflate(R.layout.person_layout, parent, false)

                val viewHolderRequests = HolderPersons(itemView)
                return viewHolderRequests
            }

            override fun onBindViewHolder(holder: HolderPersons, position: Int, model: Person) {
                holder.setDetails(
                    model.username,
                    model.uid,
                    model.accountImage,
                    model.accountImageType,
                    this@AddPerson as Activity,
                    intent.extras!!.getString("newTitle").toString(),
                    model.token,
                    model.status,
                    intent.extras!!.getString("titleKey").toString(),
                )

                FirebaseDatabase.getInstance().reference.child("UserNotes")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(intent.extras!!.getString("titleKey").toString())
                    .child("partners").get().addOnSuccessListener {
                        if (it.value.toString().contains(model.uid)) {
                            holder.item.findViewById<FloatingActionButton>(R.id.addPerson)
                                .visibility = View.GONE
                        }
                    }
                FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(intent.extras!!.getString("titleKey").toString())
                    .child("partners").get().addOnSuccessListener {
                        if (it.value.toString().contains(model.uid)) {
                            holder.item.findViewById<FloatingActionButton>(R.id.addPerson)
                                .visibility = View.GONE

                        }
                    }



            }


        }
        recyclerViewP.adapter = firebaseAdapterP
        recyclerViewP.layoutManager = linearLayoutManagerP
        firebaseAdapterP.startListening()

    }


}

data class Person(
    val username: String = "",
    val uid: String = "",
    val accountImage: String = "",
    val accountImageType: String = "",
    val token: String = "",
    val status: String = "",
    val newTitle: String = "",
    val title: String = "",
    val key: String = ""
)
