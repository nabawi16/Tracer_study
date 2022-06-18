package com.example.tracerstady.Admin.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
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
import com.example.tracerstady.Admin.Adapter.OrdersAdapter
import com.example.tracerstady.Admin.Adapter.UsersAdapter
import com.example.tracerstady.Alumni.Model.Applicant
import com.example.tracerstady.Alumni.Model.User
import com.example.tracerstady.R
import com.google.firebase.database.*
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.android.synthetic.main.activity_query_and_search_alumni.*
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Month
import java.util.*

class QueryAndSearchAlumniActivity : AppCompatActivity() {

    private lateinit var pick_date: ImageView
    private lateinit var date: TextView
    private lateinit var tv_alumni: TextView
    private lateinit var tv_jurusan: TextView
    private lateinit var tv_tahun_lulus: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var loading: RelativeLayout

    private var STORAGE_CODE: Int = 100;

    private lateinit var reference : DatabaseReference

    lateinit var mUser: MutableList<User>
    private lateinit var usersAdapter: UsersAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query_and_search_alumni)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Query And Search"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        pick_date = findViewById(R.id.pick_date)
        date = findViewById(R.id.date)
        tv_alumni = findViewById(R.id.tv_alumni)
        tv_jurusan = findViewById(R.id.tv_jurusan)
        tv_tahun_lulus = findViewById(R.id.tv_tahun_lulus)
        loading = findViewById(R.id.loading)

        reference = FirebaseDatabase.getInstance().getReference("Users")

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mUser = mutableListOf()

        val getDate = intent.getStringExtra("date")
        val getAlumni = intent.getStringExtra("fullname")
        val getJurusan = intent.getStringExtra("jurusan")
        val getTahunLulus = intent.getStringExtra("tahun_lulus")
        date.text = getDate
        tv_alumni.text = getAlumni
        tv_jurusan.text = getJurusan
        tv_tahun_lulus.text = getTahunLulus
        if (getDate != null) {
            getDateQuery(getDate)
        }
        if (getAlumni != null) {
            getQuery(getAlumni)
        }
        if (getJurusan != null) {
            getQuery(getJurusan)
        }
        if (getTahunLulus != null) {
            getQuery(getTahunLulus)
        }

        btn_save_pdf.setOnClickListener {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permissions, STORAGE_CODE)
                }
                else{
                    btnsavePdf()
                }
            }
            else{
                btnsavePdf()
            }
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

    private fun btnsavePdf(){
        val mDoc = Document()
        val mFilename = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val mFilePath = Environment.getExternalStorageDirectory().toString() + "/" + mFilename + ".pdf"
        try {
            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))
            mDoc.open()

            val mText = date.text.toString()
            val mUser = tv_alumni.text.toString()
            val mjurusan = tv_jurusan.text.toString()
            val mtahun_lulus = tv_tahun_lulus.text.toString()

            mDoc.addAuthor("Users")

            mDoc.add(Paragraph(mText))
            mDoc.add(Paragraph(mUser))
            mDoc.add(Paragraph(mjurusan))
            mDoc.add(Paragraph(mtahun_lulus))

            mDoc.close()

            Toast.makeText(this, "$mFilename.pdf\nis saved to\n$mFilePath", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception){
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            STORAGE_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    btnsavePdf()
                }
                else{
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Memanggil/Memasang menu item pada toolbar dari layout menu_bar.xml
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            @SuppressLint("SetTextI18n")
            override fun onQueryTextSubmit(query: String): Boolean {
                getQuery(query)
                date.text = ""
                tv_alumni.text = ""
                tv_jurusan.text = ""
                tv_tahun_lulus.text = ""

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
        reference.orderByChild("tahun_lulus").equalTo(date).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    recyclerView.visibility = View.VISIBLE
                    loading.visibility = View.GONE
                    mUser.clear()
                    var tot_jurusan = 0
                    var tot_tahun_lulus = 0
                    for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                        val p: User = datasnapshot1.getValue(User::class.java) as User
                        mUser.add(p)

                        tot_jurusan += p.jurusan.toInt()
                        tot_tahun_lulus += p.tahun_lulus.toInt()
                    }
                    usersAdapter = UsersAdapter(mUser, this@QueryAndSearchAlumniActivity)
                    recyclerView.adapter = usersAdapter
                    usersAdapter.notifyDataSetChanged()

                    val tot_alumni = recyclerView.adapter!!.itemCount

                    tv_alumni.text = tot_alumni.toString()
                    tv_jurusan.text = tot_jurusan.toString()
                    val localeID = Locale("in", "ID")
                    val NPM = NumberFormat.getCurrencyInstance(localeID)
                    tv_tahun_lulus.text = NPM.format(tot_tahun_lulus.toDouble())

                } else {
                    tv_alumni.text = ""
                    tv_jurusan.text = ""
                    tv_tahun_lulus.text = ""
                    recyclerView.visibility = View.GONE
                    loading.visibility = View.GONE
                    Toast.makeText(applicationContext, "No Data Found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getQuery(searchtext: String) {
        loading.visibility = View.VISIBLE
        val query = searchtext.toUpperCase()
        val firebasQuery: Query = reference.orderByChild("jurusan").startAt(query).endAt(query + "\uf8ff")
        firebasQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    recyclerView.visibility = View.VISIBLE
                    loading.visibility = View.GONE
                    mUser.clear()
                    var tot_jurusan = 0
                    var tot_tahun_lulus = 0
                    for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                        val p: User = datasnapshot1.getValue(User::class.java) as User
                        mUser.add(p)

                        tot_jurusan += p.jurusan.toInt()
                        tot_tahun_lulus += p.tahun_lulus.toInt()
                    }
                    usersAdapter = UsersAdapter(mUser, this@QueryAndSearchAlumniActivity)
                    recyclerView.adapter = usersAdapter
                    usersAdapter.notifyDataSetChanged()

                    val tot_alumni = recyclerView.adapter!!.itemCount

                    tv_alumni.text = tot_alumni.toString()
                    tv_jurusan.text = tot_jurusan.toString()
                    val localeID = Locale("in", "ID")
                    val NPM = NumberFormat.getCurrencyInstance(localeID)
                    tv_tahun_lulus.text = NPM.format(tot_tahun_lulus.toDouble())
                } else {
                    tv_alumni.text = ""
                    tv_jurusan.text = ""
                    tv_tahun_lulus.text = ""
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