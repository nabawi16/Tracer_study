package com.example.tracerstady.Admin.Activity

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.tracerstady.R
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class PilihWorkingAddActivity : AppCompatActivity() {

    private lateinit var sp: Spinner
    private lateinit var img_working: ImageView
    private lateinit var edt_judul: EditText
    private lateinit var edt_durasi: EditText
    private lateinit var edt_entry: EditText
    private lateinit var edt_deskripsi: EditText
    private lateinit var btn_save: Button

    private lateinit var reference: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var listener: ValueEventListener
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var spinnerDataList:ArrayList<String>
    private lateinit var loading: RelativeLayout

    private var photo_location: Uri? = null
    private var PHOTO_MAX: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_working_add)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Add Pilih Working"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        img_working = findViewById(R.id.img_working)
        edt_judul = findViewById(R.id.edt_judul)
        edt_durasi = findViewById(R.id.edt_durasi)
        edt_entry = findViewById(R.id.edt_entry)
        edt_deskripsi = findViewById(R.id.edt_deskripsi)
        btn_save = findViewById(R.id.btn_save)
        sp = findViewById(R.id.sp)
        loading = findViewById(R.id.loading)

        reference = FirebaseDatabase.getInstance().reference.child("Category")
        spinnerDataList = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerDataList)
        sp.adapter = adapter
        retrieveData()

        img_working.setOnClickListener {
            findPhoto()
        }

        btn_save.setOnClickListener {
            btn_save.isEnabled = false
            btn_save.text = "Loading..."
            loading.visibility = View.VISIBLE

            val nama = edt_judul.text.toString()
            val judul = nama.toUpperCase()
            val lokasi = sp.selectedItem.toString()
            val durasi = edt_durasi.text.toString()
            val entry = edt_entry.text.toString()
            val deskripsi = edt_deskripsi.text.toString()

            if ( judul.isEmpty() || lokasi.isEmpty() || durasi.isEmpty() || entry.isEmpty() || deskripsi.isEmpty()){
                btn_save.isEnabled = true
                btn_save.text = "Save"
                loading.visibility = View.GONE
                Toast.makeText(this, "All fill required", Toast.LENGTH_SHORT).show()
            }else{
                saveWorking( judul, lokasi, durasi, entry, deskripsi)
            }
        }
    }

    private fun retrieveData() {
        listener = reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (datasnapshot1 : DataSnapshot in p0.children){
                    val nama = datasnapshot1.child("name").value.toString()
                    spinnerDataList.add(nama)
                }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }

    private fun saveWorking(judul: String, lokasi: String, durasi: String, entry: String, deskripsi: String) {
        reference = FirebaseDatabase.getInstance().reference.child("Working")
        storage = FirebaseStorage.getInstance().reference.child("Photoworking").child(judul)

        if (photo_location != null){

            val mStorageReference: StorageReference = storage.child( System.currentTimeMillis().toString() + "." + getFileExtension(
                photo_location!!
            ))

            mStorageReference.putFile(photo_location!!)
                .addOnFailureListener {

                }
                .addOnSuccessListener {
                    mStorageReference.downloadUrl.addOnSuccessListener { uri ->
                        val id_working = reference.push().key.toString()
                        reference.child(id_working).ref.child("image").setValue(uri.toString())
                        reference.child(id_working).ref.child("id_working").setValue(id_working)
                        reference.child(id_working).ref.child("judul").setValue(judul)
                        reference.child(id_working).ref.child("lokasi").setValue(lokasi)
                        reference.child(id_working).ref.child("durasi").setValue(durasi)
                        reference.child(id_working).ref.child("entry").setValue(entry)
                        reference.child(id_working).ref.child("deskripsi").setValue(deskripsi)
                    }
                }.addOnCompleteListener {
                    loading.visibility = View.GONE
                    Toast.makeText(this, "Success add data", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }else{
            loading.visibility = View.GONE
            btn_save.isEnabled = true
            btn_save.text = "Save"
            Toast.makeText(this, "Image harus diisi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun findPhoto() {
        val pictureIntent = Intent()
        pictureIntent.type = "image/*"
        pictureIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(pictureIntent, PHOTO_MAX)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PHOTO_MAX && resultCode == Activity.RESULT_OK && data != null && data.data != null){

            photo_location = data.data!!
            Picasso.get()
                .load(photo_location)
                .centerCrop()
                .fit()
                .into(img_working)

        }
    }

    fun getFileExtension(uri: Uri): String? {
        val contentResolver: ContentResolver = contentResolver
        val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }
}