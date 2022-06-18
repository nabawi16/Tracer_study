package com.example.tracerstady

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.airbnb.lottie.utils.Logger.warning
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    internal lateinit var firebaseAuth: FirebaseAuth
    private lateinit var btn_reset: Button
    private lateinit var send_email: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        send_email = findViewById(R.id.send_email)
        btn_reset = findViewById(R.id.btn_reset)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Forgot Password"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        firebaseAuth = FirebaseAuth.getInstance()

        btn_reset.setOnClickListener {
            val email = send_email.text.toString()

            if (email.equals("")){
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            }else{
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this, "Please check your Email", Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        val error = it.exception!!.message.toString()
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}