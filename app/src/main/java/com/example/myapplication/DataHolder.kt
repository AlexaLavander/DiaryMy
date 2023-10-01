package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

open class DataHolder(itemView: View, Date: ModelDate) : RecyclerView.ViewHolder(itemView) {
    var recyclerView: RecyclerView = itemView.findViewById(R.id.recyler_view_pods)
    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<ModelStory, Holder>
    var linearLayoutManager: LinearLayoutManager? = null

    fun setDetails(
        applicationContext: Activity,
        data: String,
        key: String,
        author: String,
        search: TextInputEditText?,
        pathS: DatabaseReference?,
        value: DatabaseReference
    ) {
        changeTheme(
            null,
            null,
            listOf(itemView.findViewById<TextView>(R.id.data)),
            null,
            null,
            null,
            null,
            null,
            null,
            applicationContext,
            null,
            null,
            null,
            null,
            null
        )

        itemView.findViewById<TextView>(R.id.data).setText(data)
        val options = FirebaseRecyclerOptions.Builder<ModelStory>()
            .setQuery(
                value.child(data.replace("/", "")),
                ModelStory::class.java,
            ).build()
         recyclerView = itemView.findViewById(R.id.recyler_view_pods)
        linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager!!.reverseLayout = true
        linearLayoutManager!!.stackFromEnd = true
        i(options, search, pathS, applicationContext, data.replace("/", ""), data )

    }
     fun i(
        options: FirebaseRecyclerOptions<ModelStory>,
        search: TextInputEditText?,
        pathS: DatabaseReference?,
        applicationContext: Activity,
        data: String,
        notChangesData: String
    ) {

        firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<ModelStory, Holder>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, i: Int): Holder {
                    val itemView =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_pod_recycler, parent, false)
                    val viewHolder = Holder(itemView, ModelStory())
                    return viewHolder
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onBindViewHolder(
                    holder: Holder, position: Int, model: ModelStory
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
                        search,
                        pathS

                    )




                    holder.itemView.setOnLongClickListener {


                        val dialog_layout =
                            (applicationContext).layoutInflater.inflate(R.layout.delete, null)
                        val alert = AlertDialog.Builder(applicationContext)
                        alert.setView(dialog_layout)
                        val show = alert.show()
                        dialog_layout.findViewById<TextView>(R.id.yes).setOnClickListener {
                            var list = listOf<String>()
                            FirebaseDatabase.getInstance().reference.child("UserNotes")
                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(data).get()
                                .addOnSuccessListener {
                                    for (note in it.children) {
                                        list = list + note.key.toString()
                                    }

                                    FirebaseDatabase.getInstance().reference.child("UserNotes")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child(data)
                                        .child(model.title)
                                        .child("data").get().addOnSuccessListener { it ->
                                            val index = (list.indexOf(model.title))
                                            var indexes = listOf<Int>()
                                            for (notes in list) {
                                                indexes += list.indexOf(notes)
                                            }
                                            if (index == indexes.max()) {
                                                val title = list[index]
                                                val index2 = list.indexOf(title) - 1
                                                val newTitle = list[index2]
                                                FirebaseDatabase.getInstance().reference.child("Dates")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child(it.value.toString().replace("/", ""))
                                                    .setValue(newTitle).addOnSuccessListener {
                                                        FirebaseDatabase.getInstance().reference.child(
                                                            "UserNotes"
                                                        )
                                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                            .child(data)
                                                            .child(model.title)
                                                            .removeValue()
                                                        FirebaseDatabase.getInstance().reference.child(
                                                            "UserNotesSketches"
                                                        )
                                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                            .child(model.title)
                                                            .removeValue()
                                                        FirebaseDatabase.getInstance().reference.child(
                                                            "Public"
                                                        )
                                                            .child(data)
                                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                            .child(model.title)
                                                            .removeValue()
                                                    }

                                            } else {
                                                FirebaseDatabase.getInstance().reference.child("UserNotes")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child(data)
                                                    .child(model.title)
                                                    .removeValue()
                                                FirebaseDatabase.getInstance().reference.child("UserNotesSketches")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child(data)
                                                    .child(model.title)
                                                    .removeValue()
                                                FirebaseDatabase.getInstance().reference.child("Public")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child(data)
                                                    .child(model.title)
                                                    .removeValue()
                                            }
                                            FirebaseDatabase.getInstance().reference.child("UserNotes")
                                                .child(model.author).child(model.title)
                                                .child(data)
                                                .child("partners")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .removeValue()
                                        }
                                }

                            show.dismiss()


                        }

                        dialog_layout.findViewById<TextView>(R.id.no).setOnClickListener {
                            show.dismiss()

                        }

                        changeTheme(
                            null,
                            null,
                            listOf(
                                dialog_layout.findViewById(R.id.no),
                                dialog_layout.findViewById(R.id.yes),
                                dialog_layout.findViewById(R.id.message)
                            ),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            applicationContext,
                            null,
                            null,
                            null,
                            null,
                            null
                        )


                        true
                    }
                    holder.itemView.setOnClickListener {

                        var intent = Intent(applicationContext, ReadActivty::class.java)
                        intent.putExtra("author", model.author)
                        intent.putExtra("title", model.title)
                        intent.putExtra("newTitle", model.newTitle)
                        intent.putExtra("data", notChangesData)
                        applicationContext.startActivity(intent)

                    }

                }

            }
        recyclerView.adapter = firebaseRecyclerAdapter
        recyclerView.layoutManager = linearLayoutManager
        firebaseRecyclerAdapter.notifyDataSetChanged()
        firebaseRecyclerAdapter.startListening()


    }
}
