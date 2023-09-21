package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class Login_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val registrationText = findViewById<TextView>(R.id.login_text)
        registrationText.setTextColor(R.drawable.gradient_pink)

        val getAccountBtn = findViewById<TextView>(R.id.getAccount)
        getAccountBtn.setOnClickListener {
            startActivity(Intent(this@Login_Activity, Registration::class.java))
            finish()
        }

        findViewById<Button>(R.id.logInBtn).setOnClickListener {
            login()
        }

    }

    fun login() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val mAuth = Firebase.auth

        val emailInput = findViewById<TextInputEditText>(R.id.email)
        val passwordInput = findViewById<TextInputEditText>(R.id.password)

        mAuth.signInWithEmailAndPassword(emailInput.text.toString(), passwordInput.text.toString()).addOnSuccessListener {
            startActivity(Intent(this@Login_Activity, MainActivity::class.java))
            finish()
        }


    }
}
