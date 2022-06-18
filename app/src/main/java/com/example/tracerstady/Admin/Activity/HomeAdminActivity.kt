package com.example.tracerstady.Admin.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.tracerstady.Admin.Interface.Potrait
import com.example.tracerstady.Alumni.Model.Applicant
import com.example.tracerstady.R
import com.google.firebase.database.*
import com.google.zxing.integration.android.IntentIntegrator
import java.text.SimpleDateFormat
import java.util.*

class HomeAdminActivity : AppCompatActivity() {

    private lateinit var btn_account : CardView
    private lateinit var btn_category : CardView
    private lateinit var btn_scan : CardView
    private lateinit var btn_faq : CardView
    private lateinit var btn_order : CardView
    private lateinit var btn_pilih_working : CardView

    private lateinit var tv_date: TextView
    private lateinit var tv_total_applicant: TextView
    private lateinit var ll_go_order: LinearLayout

    private lateinit var reference : DatabaseReference

    lateinit var mApplicant: MutableList<Applicant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_admin)

        btn_scan = findViewById(R.id.btn_scan)
        btn_account = findViewById(R.id.btn_account)
        btn_category = findViewById(R.id.btn_category)
        btn_faq = findViewById(R.id.btn_faq)
        btn_order = findViewById(R.id.btn_order)
        btn_pilih_working = findViewById(R.id.btn_pilih_working)

        tv_date = findViewById(R.id.tv_date)
        tv_total_applicant = findViewById(R.id.tv_total_applicant)
        ll_go_order = findViewById(R.id.ll_go_order)

        val calendar = Calendar.getInstance()
        val pattern = "d MMMM yyyy"
        val simpleDateFormat = SimpleDateFormat(pattern)
        val date = (simpleDateFormat.format(calendar.time)).toUpperCase()

        tv_date.text = date
        mApplicant = mutableListOf()
        getData(date)

        ll_go_order.setOnClickListener {
            startActivity(
                Intent(this, QueryAndSearchAct::class.java)
                .putExtra("date", date))
        }

        btn_pilih_working.setOnClickListener {
            startActivity(Intent(this, PilihWorkingActivity::class.java))
        }

        btn_category.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        btn_order.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }

        btn_scan.setOnClickListener {
            scanNow()
        }

        btn_faq.setOnClickListener {
            startActivity(Intent(this, FAQAdminActivtiy::class.java))
        }

        btn_account.setOnClickListener {
            startActivity(Intent(this, AccountAdminActivity::class.java))
        }
    }

    private fun getData(date: String?) {
        reference = FirebaseDatabase.getInstance().getReference("Apply")
        reference.orderByChild("date").equalTo(date).addListenerForSingleValueEvent(object :
            ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    mApplicant.clear()
                    var tot_applicant = 0
                    for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                        val p: Applicant = datasnapshot1.getValue(Applicant::class.java) as Applicant
                        mApplicant.add(p)

                        tot_applicant += p.jumlah_applicant.toInt()
                    }
                    tv_total_applicant.text = "$tot_applicant Applicant"

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_SHORT).show()
            } else {
                val code = result.contents

                reference = FirebaseDatabase.getInstance().reference.child("Apply")
                reference.orderByChild("id_applicant").equalTo(code).addListenerForSingleValueEvent(object:
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if(dataSnapshot.exists()) {
                            for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                                val status = datasnapshot1.getValue(Applicant::class.java)?.status

                                if (status == "Sudah Digunakan"){
                                    Toast.makeText(applicationContext,"Sudah Digunakan", Toast.LENGTH_SHORT).show()
                                }else{
                                    startActivity(Intent(this@HomeAdminActivity, ScanActivity::class.java).putExtra("code",code))
                                }
                            }
                        }else{
                            Toast.makeText(applicationContext,"No Data Found", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun scanNow() {
        val integrator = IntentIntegrator(this)
        integrator.captureActivity = Potrait::class.java
        integrator.setOrientationLocked(false)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan Your Barcode")
        integrator.initiateScan()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val exit = Intent(Intent.ACTION_MAIN)
        exit.addCategory(Intent.CATEGORY_HOME)
        exit.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(exit)
        finish()
    }
}