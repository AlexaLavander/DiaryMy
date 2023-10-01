package com.example.myapplication

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Images : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var fireBaseAdapter: FirebaseRecyclerAdapter<ImageModel, ImagesHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)

        recyclerView = findViewById(R.id.recycler)
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        showImages()
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



    }

    private fun showImages() {
        val options = FirebaseRecyclerOptions.Builder<ImageModel>()
            .setQuery(FirebaseDatabase.getInstance().reference.child("Images").child(FirebaseAuth.getInstance().currentUser!!.uid)
            , ImageModel::class.java).build()
        fireBaseAdapter = object: FirebaseRecyclerAdapter<ImageModel, ImagesHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesHolder {
                val itemView = layoutInflater.inflate(R.layout.image, parent, false)
                val v = ImagesHolder(itemView)
                return v
            }

            override fun onBindViewHolder(holder: ImagesHolder, position: Int, model: ImageModel) {
                holder.setDetails(
                    model.image1,
                    model.image2,
                    model.image3,
                    this@Images
                )

            }

        }
        recyclerView.adapter = fireBaseAdapter
        recyclerView.layoutManager = linearLayoutManager
        fireBaseAdapter.startListening()
    }

    data class ImageModel(
        val image1: String = "",
        val image2: String = "",
        val image3: String = "",

        )
}