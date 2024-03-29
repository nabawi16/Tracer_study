package com.example.tracerstady.Admin.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tracerstady.Admin.Adapter.FAQAdminAdapter
import com.example.tracerstady.Admin.Model.FAQ
import com.example.tracerstady.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class FAQAdminActivtiy : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btn_add_faq: FloatingActionButton
    private lateinit var backup_faq: RelativeLayout

    private lateinit var reference : DatabaseReference

    lateinit var mFAQ: MutableList<FAQ>
    private lateinit var faqAdapter: FAQAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq_admin_activtiy)

        backup_faq = findViewById(R.id.backup_faq)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Frequently And Questions"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        reference = FirebaseDatabase.getInstance().reference.child("FAQ")

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mFAQ = mutableListOf()
        getFAQ()

        btn_add_faq = findViewById(R.id.btn_add_faq)
        btn_add_faq.setOnClickListener {
            startActivity(Intent(this, FAQAdminAddActivity::class.java))
        }
    }

    private fun getFAQ() {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    backup_faq.visibility = View.GONE
                    mFAQ.clear()
                    for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                        val faq: FAQ = datasnapshot1.getValue(FAQ::class.java) as FAQ
                        mFAQ.add(faq)
                    }
                    faqAdapter = FAQAdminAdapter(mFAQ, this@FAQAdminActivtiy)
                    recyclerView.adapter = faqAdapter
                    faqAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}