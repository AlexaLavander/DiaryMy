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

class Registration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val registrationText = findViewById<TextView>(R.id.registration_text)
        registrationText.setTextColor(R.drawable.gradient_pink)

        val haveText = findViewById<TextView>(R.id.haveAccountAlready)
        haveText.setOnClickListener {
            startActivity(Intent(this@Registration, Login_Activity::class.java))
            finish()
        }

        val registrationBtn = findViewById<Button>(R.id.registerInBtn)
        registrationBtn.setOnClickListener {
            registration()
        }
    }

    fun registration() {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val mAuth = Firebase.auth

        var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        val emailInput = findViewById<TextInputEditText>(R.id.email)
        val passwordInput = findViewById<TextInputEditText>(R.id.password)

        val confirmPasswordInput = findViewById<TextInputEditText>(R.id.confirm_password)
        val usernameInput = findViewById<TextInputEditText>(R.id.username)




        if (usernameInput.text.toString().count() < 4) {
            showError("Вы указали меньше 4 символов", usernameInput)
        } else {
            if ((!emailInput.text!!.toString()
                    .matches(emailPattern.toRegex())) || (emailInput.text.toString() == "")
            ) {
                showError("Некорректный адрес", emailInput)
            } else {
                if ((passwordInput.text.toString() != confirmPasswordInput.text.toString())) {
                    showError("Пароли не совпадают", confirmPasswordInput)
                } else {
                    if (passwordInput.text.toString().count() < 7) {
                        showError("Короткий пароль", passwordInput)
                    } else {
                        firebaseDatabase.reference.child("Users").get().addOnSuccessListener {
                            if (it.value.toString().contains(usernameInput.text.toString())) {
                                showError("Это имя уже занято", usernameInput)
                            } else {
                                mAuth.createUserWithEmailAndPassword(
                                    emailInput.text.toString(),
                                    passwordInput.text.toString()
                                ).addOnSuccessListener {
                                    val dataMap: MutableMap<String, Any> =
                                        LinkedHashMap<String, Any>()

                                    dataMap["username"] = usernameInput.text.toString()
                                    dataMap["email"] = emailInput.text.toString()
                                    dataMap["uid"] = mAuth.currentUser!!.uid

                                    firebaseDatabase.reference.child("Users")
                                        .child(mAuth.currentUser!!.uid)
                                        .setValue(dataMap)
                                    mAuth!!.currentUser!!.sendEmailVerification()

                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                }
                            }

                        }

                    }
                }
            }
        }
    }
}

fun showError(error: String, text: TextInputEditText) {
    text.error = error
    text.requestFocus()
}
