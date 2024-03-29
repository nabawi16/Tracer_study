package com.example.tracerstady.Alumni.Activity

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.airbnb.lottie.utils.Logger.warning
import com.example.tracerstady.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.NumberFormat
import java.time.Month
import java.util.*
import kotlin.random.Random

class FormPesananActivity : AppCompatActivity() {
    private lateinit var ll_pick_date: LinearLayout
    private lateinit var tv_date: TextView
    private lateinit var btn_applicant_minus: Button
    private lateinit var btn_applicant_plus: Button
    private lateinit var tv_applicant_amount: TextView
    private lateinit var tv_total_entry: TextView
    private lateinit var tv_entry: TextView
    private lateinit var btn_bayar: Button
    private lateinit var tv_isi: TextView
    private lateinit var tv_nama: TextView
    private lateinit var tv_no_hp: TextView
    private lateinit var tv_email: TextView
    private lateinit var cb_pemesan: CheckBox
    private lateinit var et_name: EditText
    private lateinit var et_no_hp: EditText
    private lateinit var et_email: EditText
    private lateinit var btn_save: Button
    private lateinit var tv_close: TextView

    private var item = 1
    private var nomor_transaksi = Random.nextInt()

    internal lateinit var dialog: Dialog
    internal lateinit var inflater: LayoutInflater
    internal lateinit var dialogView: View

    private lateinit var reference: DatabaseReference
    internal var firebaseUser: FirebaseUser? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_pesanan)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Form Pesanan"

        ll_pick_date = findViewById(R.id.ll_pick_date)
        tv_date = findViewById(R.id.tv_date)
        btn_applicant_minus = findViewById(R.id.btn_applicant_minus)
        btn_applicant_plus = findViewById(R.id.btn_applicant_plus)
        tv_applicant_amount = findViewById(R.id.tv_applicant_amount)
        tv_total_entry = findViewById(R.id.tv_total_entry)
        tv_entry = findViewById(R.id.tv_entry)
        btn_bayar = findViewById(R.id.btn_bayar)
        tv_isi = findViewById(R.id.tv_isi)
        tv_nama = findViewById(R.id.tv_nama)
        tv_no_hp = findViewById(R.id.tv_no_hp)
        tv_email = findViewById(R.id.tv_email)
        cb_pemesan = findViewById(R.id.cb_pemesan)

        val img = intent.getStringExtra("img")
        val entry = intent.getStringExtra("entry")
        val nama_working = intent.getStringExtra("nama_working")
        val id_working = intent.getStringExtra("id_working")

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val userid = firebaseUser?.uid.toString()
        reference = FirebaseDatabase.getInstance().reference

        val localeID = Locale("in", "ID")
        val formatEntry = NumberFormat.getCurrencyInstance(localeID)
        tv_entry.text = formatEntry.format(entry.toDouble())

        tv_total_entry.text = formatEntry.format(entry.toDouble())
        tv_applicant_amount.text = "1"

        btn_applicant_plus.setOnClickListener {
            val price = entry.toInt()
            increment(price)
        }

        btn_applicant_minus.setOnClickListener {
            val price = entry.toInt()
            decrement(price)
        }

        ll_pick_date.setOnClickListener {
            val calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, mYear, mMonth, mDay ->
                tv_date.text =  mDay.toString() + " " + Month.of(mMonth + 1) + " " + mYear
            }, year, month,day
            )
            dpd.show()
        }

        tv_isi.setOnClickListener {
            dialogIsi()
        }

        cb_pemesan.setOnClickListener {
            if (cb_pemesan.isChecked){

                reference.child("Users").child(userid!!).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val name = dataSnapshot.child("fullname").value.toString()
                        val no_hp = dataSnapshot.child("no_telp").value.toString()
                        val email = dataSnapshot.child("email").value.toString()

                        tv_nama.text = name
                        tv_no_hp.text = no_hp
                        tv_email.text = email
                    }
                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
            }else{
                tv_nama.text = ""
                tv_no_hp.text = ""
                tv_email.text = ""
            }
        }

        btn_bayar.setOnClickListener {
            val date  = tv_date.text.toString()
            val nama  = tv_nama.text.toString()
            val no_hp  = tv_no_hp.text.toString()
            val email  = tv_email.text.toString()
            val jumlah_applicant = tv_applicant_amount.text.toString()
            val total_entry = (tv_total_entry.text.toString()).replace("","").replace(".","")

            val id_applicant = (date + nomor_transaksi).replace(" ","").replace("-","")

            if(jumlah_applicant=="0"){
                Toast.makeText(this, "Masukkan Jumlah Peserta", Toast.LENGTH_SHORT).show()
            }else if(date.isEmpty() || nama.isEmpty() || no_hp.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "All fill required", Toast.LENGTH_SHORT).show()
            }else {
                val key = reference.child("Applicant").push().key.toString()

                val hashMap = HashMap<String, String>()
                hashMap["id_applicant"] = id_applicant
                hashMap["id_working"] = id_working
                hashMap["img_working"] = img
                hashMap["user"] = userid
                hashMap["date"] = date
                hashMap["nama"] = nama
                hashMap["no_hp"] = no_hp
                hashMap["email"] = email
                hashMap["jumlah_applicant"] = jumlah_applicant
                hashMap["total_entry"] = total_entry
                hashMap["nama_working"] = nama_working
                hashMap["status"] = "Applicant Aktif"
                hashMap["key"] = key

                reference.child("Applicant").child(userid).child(key).setValue(hashMap).addOnCompleteListener {
                    reference.child("Apply").child(key).setValue(hashMap).addOnCompleteListener {
                        startActivity(Intent(this, SuccessApplicantActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun dialogIsi() {
        dialog = Dialog(this)
        inflater = layoutInflater
        dialog.setContentView(R.layout.layout_isi_data_pemesan)
        dialog.setCancelable(true)

        et_name = dialog.findViewById(R.id.et_name)
        et_no_hp = dialog.findViewById(R.id.et_no_hp)
        et_email = dialog.findViewById(R.id.et_email)
        btn_save = dialog.findViewById(R.id.btn_save)
        tv_close = dialog.findViewById(R.id.tv_close)

        tv_close.setOnClickListener {
            dialog.dismiss()
        }

        btn_save.setOnClickListener {
            val name = et_name.text.toString()
            val no_hp = et_no_hp.text.toString()
            val email = et_email.text.toString()

            if (name.isEmpty() || no_hp.isEmpty() || email.isEmpty()) {
                Toast.makeText(applicationContext, "Data harus diisi semua", Toast.LENGTH_SHORT).show()
            } else {
                tv_nama.text = name
                tv_no_hp.text = no_hp
                tv_email.text = email

                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun increment(price: Int) {
        item++
        tv_applicant_amount.text = Integer.toString(item)

        val entry = Integer.toString(sumOfProduct(price))

        val localeID = Locale("in", "ID")
        val formatEntry = NumberFormat.getCurrencyInstance(localeID)
        tv_total_entry.text = formatEntry.format(entry.toDouble())
    }

    private fun decrement(price: Int) {
        if (item > 0) {
            item--

        }

        tv_applicant_amount.text = Integer.toString(item)

        val entry = Integer.toString(sumOfProduct(price))

        val localeID = Locale("in", "ID")
        val formatEntry = NumberFormat.getCurrencyInstance(localeID)
        tv_total_entry.text = formatEntry.format(entry.toDouble())
    }

    private fun sumOfProduct(price: Int): Int {
        return item * price
    }
}