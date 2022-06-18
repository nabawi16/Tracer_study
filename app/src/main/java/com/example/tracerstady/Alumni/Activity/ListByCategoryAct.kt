package com.example.tracerstady.Alumni.Activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tracerstady.Admin.Model.Working
import com.example.tracerstady.Alumni.Adapter.WorkingAdapter
import com.example.tracerstady.R
import com.google.firebase.database.*

class ListByCategoryAct : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reference : DatabaseReference
    private lateinit var backup: RelativeLayout

    lateinit var mWorking: MutableList<Working>
    private lateinit var workingAdapter: WorkingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_by_category)

        backup = findViewById(R.id.backup)

        val name = intent.getStringExtra("nameofcategory")

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = name
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        reference = FirebaseDatabase.getInstance().reference.child("Working")

        recyclerView = findViewById(R.id.rv_working)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mWorking = mutableListOf()
        getWorking(name)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Memanggil/Memasang menu item pada toolbar dari layout menu_bar.xml
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        val searchIem = menu.findItem(R.id.search)
        val searchView = searchIem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @SuppressLint("SetTextI18n")
            override fun onQueryTextSubmit(query: String): Boolean {
                getWorkingQuery(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })
        return true
    }

    private fun getWorkingQuery(searchText: String) {
        val query = searchText.toUpperCase()
        val firebasQuery: Query = reference.orderByChild("judul").startAt(query).endAt(query + "\uf8ff")
        firebasQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    backup.visibility = View.GONE
                    mWorking.clear()
                    for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                        val p: Working = datasnapshot1.getValue(Working::class.java) as Working
                        mWorking.add(p)
                    }
                    workingAdapter = WorkingAdapter(mWorking, this@ListByCategoryAct)
                    recyclerView.adapter = workingAdapter
                    workingAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getWorking(name: String?) {
        if (name != "Semua Destinasi Pekerjaan") {
            reference.orderByChild("lokasi").equalTo(name).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        backup.visibility = View.GONE
                        mWorking.clear()
                        for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                            val p: Working = datasnapshot1.getValue(Working::class.java) as Working
                            mWorking.add(p)
                        }
                        workingAdapter = WorkingAdapter(mWorking, this@ListByCategoryAct)
                        recyclerView.adapter = workingAdapter
                        workingAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }else{
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        backup.visibility = View.GONE
                        mWorking.clear()
                        for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                            val p: Working = datasnapshot1.getValue(Working::class.java) as Working
                            mWorking.add(p)
                        }
                        workingAdapter = WorkingAdapter(mWorking, this@ListByCategoryAct)
                        recyclerView.adapter = workingAdapter
                        workingAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }
}