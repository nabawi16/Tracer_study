package com.example.tracerstady.Admin.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.airbnb.lottie.utils.Logger.warning
import com.example.tracerstady.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FAQAdminEditActivity : AppCompatActivity() {

    private lateinit var et_question: EditText
    private lateinit var et_answer: EditText
    private lateinit var btn_save: Button
    private lateinit var loading: RelativeLayout

    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq_admin_edit)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Edit FAQ"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        et_question = findViewById(R.id.et_question)
        et_answer = findViewById(R.id.et_answer)
        btn_save = findViewById(R.id.btn_save)
        loading = findViewById(R.id.loading)

        val id = intent.getStringExtra("id")
        val answer1 = intent.getStringExtra("answer")
        val question1 = intent.getStringExtra("question")

        et_answer.setText(answer1)
        et_question.setText(question1)

        reference = FirebaseDatabase.getInstance().getReference("FAQ")

        btn_save.setOnClickListener {
            btn_save.isEnabled = false
            btn_save.text  = "Loading..."
            loading.visibility = View.VISIBLE
            val question = et_question.text.toString()
            val answer = et_answer.text.toString()

            if(id.isEmpty() || question.isEmpty() || answer.isEmpty()){
                btn_save.isEnabled = true
                btn_save.text  = "Save"
                loading.visibility = View.GONE
                Toast.makeText(this, "All fill required", Toast.LENGTH_SHORT).show()

            }else{
                saveFAQ(id, question, answer)
            }
        }
    }

    private fun saveFAQ(id:String, question: String, answer: String) {
        val faq = HashMap<String, String>()
        faq["id"] = id
        faq["question"] = question
        faq["answer"] = answer
        reference.child(id).setValue(faq).addOnCompleteListener {
            btn_save.isEnabled = true
            btn_save.text  = "Save"
            loading.visibility = View.GONE
            Toast.makeText(this, "Berhasil update data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}