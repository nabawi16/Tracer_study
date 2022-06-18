package com.example.tracerstady.Alumni.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tracerstady.Admin.Model.Working
import com.example.tracerstady.Alumni.Adapter.RateAdapter
import com.example.tracerstady.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class DetailWorkingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var img_working: ImageView
    private lateinit var tv_judul: TextView
    private lateinit var tv_durasi: TextView
    private lateinit var tv_deskripsi: TextView
    private lateinit var tv_lokasi: TextView
    private lateinit var tv_entry: TextView
    private lateinit var btn_pesan: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var tv_rate_num: TextView
    private lateinit var rb_applicant: RatingBar
    private lateinit var tv_total_user: TextView

    private lateinit var ll_first: LinearLayout
    private lateinit var ll: LinearLayout
    private lateinit var arrowBtn: ImageView
    private lateinit var view_rate: View
    private lateinit var tv_rate: TextView

    private lateinit var reference : DatabaseReference

    lateinit var mWorking: MutableList<Working>
    private lateinit var rateAdapter: RateAdapter

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_working)

        img_working  = findViewById(R.id.img_working)
        tv_judul  = findViewById(R.id.tv_judul)
        tv_durasi  = findViewById(R.id.tv_durasi)
        tv_deskripsi  = findViewById(R.id.tv_deskripsi)
        tv_lokasi  = findViewById(R.id.tv_lokasi)
        tv_entry  = findViewById(R.id.tv_entry)
        btn_pesan  = findViewById(R.id.btn_pesan)
        tv_rate_num  = findViewById(R.id.tv_rate_num)
        rb_applicant  = findViewById(R.id.rb_applicant)
        tv_total_user  = findViewById(R.id.tv_total_user)

        ll_first  = findViewById(R.id.ll_first)
        ll = findViewById(R.id.ll)
        arrowBtn = findViewById(R.id.arrowBtn)
        view_rate = findViewById(R.id.view_rate)
        tv_rate = findViewById(R.id.tv_rate)

        val img = intent.getStringExtra("img")
        val judul = intent.getStringExtra("judul")
        val durasi = intent.getStringExtra("durasi")
        val lokasi = intent.getStringExtra("lokasi")
        val entry = intent.getStringExtra("entry")
        val deskripsi = intent.getStringExtra("deskripsi")
        val id_working = intent.getStringExtra("id_working")

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = judul

        Picasso.get()
            .load(img)
            .centerCrop()
            .fit()
            .into(img_working)

        val localeID = Locale("in", "ID")
        val formatEntry = NumberFormat.getCurrencyInstance(localeID)
        tv_entry.text = formatEntry.format(entry.toDouble())

        tv_judul.text = judul
        tv_durasi.text = durasi
        tv_deskripsi.text = deskripsi
        tv_lokasi.text = lokasi

        recyclerView = findViewById(R.id.rv_review)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mWorking = mutableListOf()
        getRate(id_working)

        ll_first.setOnClickListener{
            if (recyclerView.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(ll, AutoTransition())
                recyclerView.visibility = View.VISIBLE
                arrowBtn.setBackgroundResource(R.drawable.ic_arrow_down_blue)
            } else {
                TransitionManager.beginDelayedTransition(ll, AutoTransition())
                recyclerView.visibility = View.GONE
                arrowBtn.setBackgroundResource(R.drawable.ic_arrow_right_blue)
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btn_pesan.setOnClickListener {
            startActivity(
                Intent(this,FormPesananActivity::class.java )
                .putExtra("img",img)
                .putExtra("entry",entry)
                .putExtra("nama_working",judul)
                .putExtra("id_working",id_working)
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val lokasi = intent.getStringExtra("lokasi")
        val lat = intent.getStringExtra("lat").toDouble()
        val lon = intent.getStringExtra("lon").toDouble()
        val location = LatLng(lat, lon)
        mMap.addMarker(MarkerOptions().position(location).title(lokasi))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,15f))
    }

    private fun getRate(id_working: String?) {
        reference = FirebaseDatabase.getInstance().reference
            .child("Working").child(id_working!!).child("Rate")
        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    mWorking.clear()
                    var total_rate = 0f
                    for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                        val p: Working = datasnapshot1.getValue(Working::class.java) as Working
                        mWorking.add(p)

                        val t_rate = p.rate.toFloat()
                        total_rate += t_rate
                    }
                    rateAdapter = RateAdapter(mWorking, this@DetailWorkingActivity)
                    recyclerView.adapter = rateAdapter
                    rateAdapter.notifyDataSetChanged()

                    val t_user = recyclerView.adapter!!.itemCount
                    val total_user = t_user.toString()
                    tv_total_user.text = "$total_user reviews"

                    val average_rate = (total_rate/t_user)
//                    val df = DecimalFormat("#.##")
//                    tv_rate_num.text = df.format(average_rate)
                    tv_rate_num.text = average_rate.toString()

                    rb_applicant.rating = average_rate

                }else{
                    tv_rate.visibility = View.GONE
                    ll.visibility = View.GONE
                    view_rate.visibility = View.GONE
                }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}