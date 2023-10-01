package com.example.myapplication

import android.app.Activity
import android.content.Intent
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

class Friends : AppCompatActivity() {

    lateinit var recyclerViewP: RecyclerView
    lateinit var linearLayoutManagerP: LinearLayoutManager
    lateinit var firebaseAdapterP: FirebaseRecyclerAdapter<Person, FriendsHolder>
    lateinit var flb: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friends)

        recyclerViewP = findViewById(R.id.recycler_persons)
        linearLayoutManagerP = LinearLayoutManager(this)
        linearLayoutManagerP.reverseLayout = true
        linearLayoutManagerP.stackFromEnd = true

        flb = FloatingActionButton(this)

        flb = findViewById<FloatingActionButton>(R.id.newFriends)
        flb.setOnClickListener {
            startActivity(Intent(this, NewFriend::class.java))
        }


        changeTheme(
            findViewById(R.id.constraint),
            listOf(flb),
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

        firebaseAdapterP = object : FirebaseRecyclerAdapter<Person, FriendsHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsHolder {
                val itemView = layoutInflater.inflate(R.layout.person_layout, parent, false)
                itemView.findViewById<FloatingActionButton>(R.id.addPerson).visibility = View.GONE
                val viewHolderRequests = FriendsHolder(itemView)
                return viewHolderRequests
            }

            override fun onBindViewHolder(holder: FriendsHolder, position: Int, model: Person) {
                holder.setDetails(
                    model.username,
                    model.uid,
                    model.accountImage,
                    model.accountImageType,
                    this@Friends as Activity,
                    model.token,
                    model.status
                )

            }

        }
        recyclerViewP.adapter = firebaseAdapterP
        recyclerViewP.layoutManager = linearLayoutManagerP
        firebaseAdapterP.startListening()

    }


}


