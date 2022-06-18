package com.example.tracerstady.Admin.Activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.utils.Logger.error
import com.example.tracerstady.Admin.Adapter.OrdersAdapter
import com.example.tracerstady.Alumni.Model.Applicant
import com.example.tracerstady.R
import com.google.firebase.database.*
import java.text.NumberFormat
import java.time.Month
import java.util.*

class QueryAndSearchAct : AppCompatActivity() {

    private lateinit var pick_date: ImageView
    private lateinit var date: TextView
    private lateinit var tv_item: TextView
    private lateinit var tv_applicant: TextView
    private lateinit var tv_price: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var loading: RelativeLayout

    private lateinit var reference : DatabaseReference

    lateinit var mApplicant: MutableList<Applicant>
    private lateinit var ordersAdapter: OrdersAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query_and_search)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Query And Search"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        pick_date = findViewById(R.id.pick_date)
        date = findViewById(R.id.date)
        tv_item = findViewById(R.id.tv_item)
        tv_applicant = findViewById(R.id.tv_applicant)
        tv_price = findViewById(R.id.tv_price)
        loading = findViewById(R.id.loading)

        reference = FirebaseDatabase.getInstance().getReference("Order")

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mApplicant = mutableListOf()

        val getDate = intent.getStringExtra("date")
        date.text = getDate
        if (getDate != null){
            getDateQuery(getDate)
        }

        pick_date.setOnClickListener {
            val calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, mYear, mMonth, mDay ->
                val getDate =  mDay.toString() + " " + Month.of(mMonth + 1) + " " + mYear
                date.text = getDate
                getDateQuery(getDate)
            }, year, month,day
            )
            dpd.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Memanggil/Memasang menu item pada toolbar dari layout menu_bar.xml
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        val searchIem = menu.findItem(R.id.search)
        val searchView = searchIem.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            @SuppressLint("SetTextI18n")
            override fun onQueryTextSubmit(query: String): Boolean {
                getQuery(query)
                date.text = ""
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })
        return true
    }


    private fun getDateQuery(date: String) {
        loading.visibility = View.VISIBLE
        reference.orderByChild("date").equalTo(date).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    recyclerView.visibility = View.VISIBLE
                    loading.visibility = View.GONE
                    mApplicant.clear()
                    var tot_applicant = 0
                    var tot_price = 0
                    for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                        val p: Applicant = datasnapshot1.getValue(Applicant::class.java) as Applicant
                        mApplicant.add(p)

                        tot_applicant += p.jumlah_applicant.toInt()
                        tot_price += p.total_entry.toInt()
                    }
                    ordersAdapter = OrdersAdapter(mApplicant, this@QueryAndSearchAct)
                    recyclerView.adapter = ordersAdapter
                    ordersAdapter.notifyDataSetChanged()

                    val tot_item = recyclerView.adapter!!.itemCount

                    tv_item.text = tot_item.toString()
                    tv_applicant.text = tot_applicant.toString()
                    val localeID = Locale("in", "ID")
                    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                    tv_price.text = formatRupiah.format(tot_price.toDouble())

                } else {
                    tv_item.text = ""
                    tv_applicant.text = ""
                    tv_price.text = ""
                    recyclerView.visibility = View.GONE
                    loading.visibility = View.GONE
                    Toast.makeText(applicationContext, "No Data Found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getQuery(searchText: String) {
        loading.visibility = View.VISIBLE
        val query = searchText.toUpperCase()
        val firebasQuery: Query = reference.orderByChild("id_applicant").startAt(query).endAt(query + "\uf8ff")
        firebasQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    recyclerView.visibility = View.VISIBLE
                    loading.visibility = View.GONE
                    mApplicant.clear()
                    var tot_applicant = 0
                    var tot_price = 0
                    for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                        val p: Applicant = datasnapshot1.getValue(Applicant::class.java) as Applicant
                        mApplicant.add(p)

                        tot_applicant += p.jumlah_applicant.toInt()
                        tot_price += p.total_entry.toInt()
                    }
                    ordersAdapter = OrdersAdapter(mApplicant, this@QueryAndSearchAct)
                    recyclerView.adapter = ordersAdapter
                    ordersAdapter.notifyDataSetChanged()

                    val tot_item = recyclerView.adapter!!.itemCount

                    tv_item.text = tot_item.toString()
                    tv_applicant.text = tot_applicant.toString()
                    val localeID = Locale("in", "ID")
                    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                    tv_price.text = formatRupiah.format(tot_price.toDouble())
                } else {
                    tv_item.text = ""
                    tv_applicant.text = ""
                    tv_price.text = ""
                    recyclerView.visibility = View.GONE
                    loading.visibility = View.GONE
                    Toast.makeText(applicationContext, "No Data Found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}