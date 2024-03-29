package com.example.tracerstady.Admin.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tracerstady.Admin.Adapter.CategoryAdminAdapter
import com.example.tracerstady.Admin.Model.Category
import com.example.tracerstady.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class CategoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btn_add_category: FloatingActionButton
    private lateinit var backup: RelativeLayout

    private lateinit var reference : DatabaseReference

    private var mCategory: ArrayList<Category> = arrayListOf()
    private lateinit var categoryAdapter: CategoryAdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        backup = findViewById(R.id.backup)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Category"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        getCategory()

        btn_add_category = findViewById(R.id.btn_add_category)
        btn_add_category.setOnClickListener {
            startActivity(Intent(this, CategoryAddActivity::class.java))
        }
    }

    private fun getCategory() {
        reference = FirebaseDatabase.getInstance().reference.child("Category")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    backup.visibility = View.GONE
                    mCategory.clear()
                    for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                        val p: Category = datasnapshot1.getValue(Category::class.java) as Category
                        mCategory.add(p)
                    }
                    categoryAdapter = CategoryAdminAdapter(mCategory , this@CategoryActivity)
                    recyclerView.adapter = categoryAdapter
                    categoryAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}