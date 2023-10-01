package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.inappmessaging.MessagesProto.Text

class NewFriend : AppCompatActivity() {

    lateinit var recyclerViewP: RecyclerView
    lateinit var linearLayoutManagerP: LinearLayoutManager
    lateinit var firebaseAdapterP: FirebaseRecyclerAdapter<Person, HolderAddFriend>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_friends)

        recyclerViewP = findViewById(R.id.recycler_persons)
        linearLayoutManagerP = LinearLayoutManager(this)
        linearLayoutManagerP.reverseLayout = true
        linearLayoutManagerP.stackFromEnd = true


        showPersons()

    }

    override fun onStart() {
        changeTheme(
            findViewById(R.id.constraint),
            null,
            null,
            null,
            null,
            null,
            null,
            findViewById(R.id.materialToolbar),
            null,
            this,
            null,
            null,
            null,
            null,
            null
        )
        super.onStart()
    }
    private fun showPersons() {
        val options = FirebaseRecyclerOptions.Builder<Person>()
            .setQuery(FirebaseDatabase.getInstance().reference.child("Users"), Person::class.java)
            .build()

        firebaseAdapterP = object : FirebaseRecyclerAdapter<Person, HolderAddFriend>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAddFriend {
                val itemView = layoutInflater.inflate(R.layout.person_layout, parent, false)

                val viewHolderRequests = HolderAddFriend(itemView)
                return viewHolderRequests
            }

            override fun onBindViewHolder(holder: HolderAddFriend, position: Int, model: Person) {
                holder.setDetails(
                    model.username,
                    model.uid,
                    model.accountImage,
                    model.accountImageType,
                    this@NewFriend as Activity,
                    model.token,
                    model.status
                )

                findViewById<TextInputEditText>(R.id.search).setOnEditorActionListener { textView, i, keyEvent ->

                    if (i == EditorInfo.IME_ACTION_DONE){
                        if(textView.text.toString() in model.username){
                            holder.item.visibility = View.VISIBLE
                        }
                        else{
                            holder.item.visibility = View.GONE

                        }
                    }

                    true
                }




            }

        }
        recyclerViewP.adapter = firebaseAdapterP
        recyclerViewP.layoutManager = linearLayoutManagerP
        firebaseAdapterP.startListening()

    }


}
