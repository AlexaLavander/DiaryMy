package com.example.myapplication
import com.google.firebase.database.FirebaseDatabase

val firebaseDatabase = FirebaseDatabase.getInstance()
val firebaseDatabaseReferenceStories = firebaseDatabase.getReference("UserNotes")