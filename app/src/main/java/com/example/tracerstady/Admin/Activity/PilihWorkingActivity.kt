package com.example.tracerstady.Admin.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tracerstady.Admin.Adapter.WorkingAdminAdapter
import com.example.tracerstady.Admin.Model.Working
import com.example.tracerstady.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class PilihWorkingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btn_add_working: FloatingActionButton
    private lateinit var backup: RelativeLayout

    private lateinit var reference : DatabaseReference

    lateinit var mWorking: MutableList<Working>
    private lateinit var workingAdapter: WorkingAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_working)

        backup = findViewById(R.id.backup)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Pilih Working"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btn_add_working = findViewById(R.id.btn_add_working)

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mWorking = mutableListOf()
        getWorking()

        btn_add_working.setOnClickListener {
            reference = FirebaseDatabase.getInstance().reference.child("Category")
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()){
                        startActivity(Intent(this@PilihWorkingActivity, PilihWorkingAddActivity::class.java))
                    }else{
                        Toast.makeText(this@PilihWorkingActivity, "Create a category first", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }
    }

    private fun getWorking() {
        reference = FirebaseDatabase.getInstance().reference.child("Working")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    mWorking.clear()
                    backup.visibility = View.GONE
                    for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                        val p: Working = datasnapshot1.getValue(Working::class.java) as Working
                        mWorking.add(p)
                    }
                    workingAdapter = WorkingAdminAdapter(mWorking, this@PilihWorkingActivity)
                    recyclerView.adapter = workingAdapter
                    workingAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}