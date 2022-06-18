package com.example.tracerstady.Admin.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.tracerstady.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class OrdersDetailActivity : AppCompatActivity() {

    private lateinit var img_working: ImageView
    private lateinit var tv_nama_working: TextView
    private lateinit var tv_date: TextView
    private lateinit var tv_qty: TextView
    private lateinit var tv_total_entry: TextView
    private lateinit var tv_nama: TextView
    private lateinit var tv_no_hp: TextView
    private lateinit var tv_email: TextView
    private lateinit var text_barcode_number : TextView

    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Detail Applicant"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val key = intent.getStringExtra("key")

        img_working = findViewById(R.id.img_working)
        tv_nama_working = findViewById(R.id.tv_nama_working)
        tv_date = findViewById(R.id.tv_date)
        tv_qty = findViewById(R.id.tv_qty)
        tv_total_entry = findViewById(R.id.tv_total_entry)
        tv_nama = findViewById(R.id.tv_nama)
        tv_no_hp = findViewById(R.id.tv_no_hp)
        tv_email = findViewById(R.id.tv_email)
        text_barcode_number = findViewById(R.id.text_barcode_number)

        getData(key!!)
    }

    private fun getData( key: String) {
        reference = FirebaseDatabase.getInstance().reference.child("Apply").child(key)
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    val nama_working = dataSnapshot.child("nama_working").value.toString()
                    val date =  dataSnapshot.child("date").value.toString()
                    val qty =  dataSnapshot.child("jumlah_applicant").value.toString()
                    val total =  dataSnapshot.child("total_entry").value.toString()
                    val nama =  dataSnapshot.child("nama").value.toString()
                    val no_hp =  dataSnapshot.child("no_hp").value.toString()
                    val email =  dataSnapshot.child("email").value.toString()
                    val id_applicant =  dataSnapshot.child("id_aplicant").value.toString()
                    val img =  dataSnapshot.child("img_working").value.toString()

                    tv_nama_working.text = nama_working
                    tv_date.text = date
                    tv_qty.text = qty
                    tv_nama.text = nama
                    tv_no_hp.text = no_hp
                    tv_email.text = email
                    text_barcode_number.text = id_applicant

                    Picasso.get()
                        .load(img)
                        .centerCrop()
                        .fit()
                        .into(img_working)

                    val localeID = Locale("in", "ID")
                    val npm = NumberFormat.getCurrencyInstance(localeID)
                    tv_total_entry.text = npm.format(total.toDouble())

                }else{
                    Toast.makeText(applicationContext,"No Data Found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}