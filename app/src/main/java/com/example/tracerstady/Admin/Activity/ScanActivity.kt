package com.example.tracerstady.Admin.Activity

import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.tracerstady.Alumni.Model.Applicant
import com.example.tracerstady.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class ScanActivity : AppCompatActivity() {

    private lateinit var img_working: ImageView
    private lateinit var tv_nama_working: TextView
    private lateinit var tv_date: TextView
    private lateinit var tv_qty: TextView
    private lateinit var tv_total_entry: TextView
    private lateinit var tv_nama: TextView
    private lateinit var tv_no_hp: TextView
    private lateinit var tv_email: TextView
    private lateinit var text_barcode_number: TextView
    private lateinit var btn_status: Button

    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        val code = intent.getStringExtra("code")

        img_working = findViewById(R.id.img_working)
        tv_nama_working = findViewById(R.id.tv_nama_working)
        tv_date = findViewById(R.id.tv_date)
        tv_qty = findViewById(R.id.tv_qty)
        tv_total_entry = findViewById(R.id.tv_total_entry)
        tv_nama = findViewById(R.id.tv_nama)
        tv_no_hp = findViewById(R.id.tv_no_hp)
        tv_email = findViewById(R.id.tv_email)
        text_barcode_number = findViewById(R.id.text_barcode_number)
        btn_status = findViewById(R.id.btn_status)

        getData(code)

        btn_status.setOnClickListener {
            btn_status.isEnabled = false
            btn_status.text = "Sudah Dimasukan"
            btn_status.background.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), PorterDuff.Mode.MULTIPLY)
            reference = FirebaseDatabase.getInstance().reference.child("Order")
            reference.orderByChild("id_applicant").equalTo(code).addListenerForSingleValueEvent(object:
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                            val key = datasnapshot1.getValue(Applicant::class.java)?.key
                            val userid = datasnapshot1.getValue(Applicant::class.java)?.user

                            reference = FirebaseDatabase.getInstance().reference.child("Order").child(key.toString())
                            reference.ref.child("status").setValue("Sudah Dimasukan").addOnCompleteListener {
                                reference = FirebaseDatabase.getInstance().reference.child("MyApplicant").child(userid!!).child(key.toString())
                                reference.ref.child("status").setValue("Sudah Dimasukan")
                            }

                            val handler = Handler()
                            handler.postDelayed({
                                startActivity(Intent(this@ScanActivity, HomeAdminActivity::class.java))
                                finish()
                            }, 1000)
                        }
                    }else{
                        Toast.makeText(applicationContext,"No Data Found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }

    private fun getData(code: String?) {
        reference = FirebaseDatabase.getInstance().reference.child("Order")
        reference.orderByChild("id_applicant").equalTo(code).addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                        val nama_working = datasnapshot1.getValue(Applicant::class.java)?.nama_working
                        val id_working = datasnapshot1.getValue(Applicant::class.java)?.id_working
                        val date = datasnapshot1.getValue(Applicant::class.java)?.date
                        val qty = datasnapshot1.getValue(Applicant::class.java)?.jumlah_applicant
                        val total = datasnapshot1.getValue(Applicant::class.java)?.total_entry
                        val nama = datasnapshot1.getValue(Applicant::class.java)?.nama
                        val no_hp = datasnapshot1.getValue(Applicant::class.java)?.no_hp
                        val email = datasnapshot1.getValue(Applicant::class.java)?.email

                        tv_nama_working.text = nama_working
                        tv_date.text = date
                        tv_qty.text = qty
                        tv_nama.text = nama
                        tv_no_hp.text = no_hp
                        tv_email.text = email
                        text_barcode_number.text = code

                        val localeID = Locale("in", "ID")
                        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                        tv_total_entry.text = formatRupiah.format(total?.toDouble())

                        reference = FirebaseDatabase.getInstance().reference.child("Working").child(id_working!!)
                        reference.addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val photoLink: String = dataSnapshot.child("image").value.toString()
                                Picasso.get()
                                    .load(photoLink)
                                    .centerCrop()
                                    .fit()
                                    .into(img_working)
                            }

                            override fun onCancelled(databaseError: DatabaseError) {

                            }
                        })
                    }
                }else{
                    Toast.makeText(applicationContext,"No Data Found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}