package com.example.tracerstady.Alumni.Activity

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.airbnb.lottie.utils.Logger.warning
import com.example.tracerstady.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class EditProfileActivity : AppCompatActivity() {

    private lateinit var photo_profile: CircleImageView
    private lateinit var edt_nama: EditText
    private lateinit var edt_no_telp: EditText
    private lateinit var edtNpm: EditText
    private lateinit var edtJurusan: EditText
    private lateinit var edtFakultas: EditText
    private lateinit var edtIPK: EditText
    private lateinit var edtTahunMasuk: EditText
    private lateinit var edtTahunLulus: EditText
    private lateinit var edtPekerjaan: EditText
    private lateinit var edtNamaPerusahaan: EditText
    private lateinit var edtJenisKelamin: EditText
    private lateinit var edtTempatLahir: EditText
    private lateinit var edtTanggalLahir: EditText
    private lateinit var edtAlamat: EditText
    private lateinit var edtAgama: EditText
    private lateinit var btn_save: Button
    private lateinit var btn_add_photo: Button
    private lateinit var loading: RelativeLayout

    private lateinit var reference: DatabaseReference
    private lateinit var storage: StorageReference

    private var photo_location: Uri? = null
    private var PHOTO_MAX: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Edit Profile"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        btn_save = findViewById(R.id.btn_save)
        btn_add_photo = findViewById(R.id.btn_add_photo)
        photo_profile = findViewById(R.id.photo_profile)
        edt_nama = findViewById(R.id.edt_nama)
        edt_no_telp = findViewById(R.id.edt_no_telp)
        edtNpm = findViewById(R.id.edt_npm)
        edtJurusan = findViewById(R.id.edt_jurusan)
        edtFakultas = findViewById(R.id.edt_fakultas)
        edtIPK = findViewById(R.id.edt_ipk)
        edtTahunMasuk = findViewById(R.id.edt_tahun_masuk)
        edtTahunLulus = findViewById(R.id.edt_tahun_lulus)
        edtPekerjaan = findViewById(R.id.edt_pekerjaan)
        edtNamaPerusahaan = findViewById(R.id.edt_nama_perusahaan)
        edtJenisKelamin = findViewById(R.id.edt_jenis_kelamin)
        edtTempatLahir = findViewById(R.id.edt_tempat_lahir)
        edtTanggalLahir = findViewById(R.id.edt_tanggal_lahir)
        edtAlamat = findViewById(R.id.edt_alamat)
        edtAgama = findViewById(R.id.edt_agama)
        loading = findViewById(R.id.loading)

        var firebaseUser = FirebaseAuth.getInstance().currentUser?.uid.toString()
        reference = FirebaseDatabase.getInstance().reference.child("Users")
        reference.child(firebaseUser).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val photo = p0.child("photo").value.toString()
                    val nama = p0.child("fullname").value.toString()
                    val no_hp = p0.child("no_telp").value.toString()
                    val npm = p0.child("npm").value.toString()
                    val jurusan = p0.child("jurusan").value.toString()
                    val fakultas = p0.child("fakultas").value.toString()
                    val ipk = p0.child("ipk").value.toString()
                    val tahun_masuk = p0.child("tahun_masuk").value.toString()
                    val tahun_lulus = p0.child("tahun_lulus").value.toString()
                    val pekerjaan = p0.child("pekerjaan").value.toString()
                    val nama_perusahaan = p0.child("nama_perusahaan").value.toString()
                    val jenis_kelamin = p0.child("jenis_kelamin").value.toString()
                    val tempat_lahir = p0.child("tempat_lahir").value.toString()
                    val tanggal_lahir = p0.child("tanggal_lahir").value.toString()
                    val alamat = p0.child("alamat").value.toString()
                    val agama = p0.child("agama").value.toString()

                    edt_nama.setText(nama)
                    edt_no_telp.setText(no_hp)
                    edtNpm.setText(npm)
                    edtJurusan.setText(jurusan)
                    edtFakultas.setText(fakultas)
                    edtIPK.setText(ipk)
                    edtTahunMasuk.setText(tahun_masuk)
                    edtTahunLulus.setText(tahun_lulus)
                    edtPekerjaan.setText(pekerjaan)
                    edtNamaPerusahaan.setText(nama_perusahaan)
                    edtJenisKelamin.setText(jenis_kelamin)
                    edtTempatLahir.setText(tempat_lahir)
                    edtTanggalLahir.setText(tanggal_lahir)
                    edtAlamat.setText(alamat)
                    edtAgama.setText(agama)
                    Picasso.get()
                        .load(photo)
                        .centerCrop()
                        .fit()
                        .into(photo_profile)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        btn_add_photo.setOnClickListener {
            findPhoto()
        }

        val user = FirebaseAuth.getInstance().currentUser?.uid.toString()
        reference = FirebaseDatabase.getInstance().reference.child("Users").child(user)
        storage = FirebaseStorage.getInstance().reference.child("Photousers").child(user)

        btn_save.setOnClickListener {
            btn_save.isEnabled = false
            btn_save.text = "Loading ..."
            loading.visibility = View.VISIBLE

            val name = edt_nama.text.toString()
            val no_telp = edt_no_telp.text.toString()
            val npm = edtNpm.text.toString()
            val jurusan = edtJurusan.text.toString()
            val fakultas = edtFakultas.text.toString()
            val ipk = edtIPK.text.toString()
            val tahun_masuk = edtTahunMasuk.text.toString()
            val tahun_lulus = edtTahunLulus.text.toString()
            val pekerjaan = edtPekerjaan.text.toString()
            val nama_perusahaan = edtNamaPerusahaan.text.toString()
            val jenis_kelamin = edtJenisKelamin.text.toString()
            val tempat_lahir = edtTempatLahir.text.toString()
            val tanggal_lahir = edtTanggalLahir.text.toString()
            val alamat = edtAlamat.text.toString()
            val agama = edtAgama.text.toString()

            if (name.isEmpty() || no_telp.isEmpty() || npm.isEmpty() || jurusan.isEmpty() || fakultas.isEmpty() || ipk.isEmpty()
                || tahun_masuk.isEmpty() || tahun_lulus.isEmpty() || pekerjaan.isEmpty() || nama_perusahaan.isEmpty()
                || jenis_kelamin.isEmpty() || tempat_lahir.isEmpty() || tanggal_lahir.isEmpty() || alamat.isEmpty() || agama.isEmpty()){
                btn_save.isEnabled = true
                btn_save.text = "Save"
                loading.visibility = View.GONE
                Toast.makeText(this,"All fill required", Toast.LENGTH_SHORT).show()
            }else{
                savePhoto( name, no_telp, npm, jurusan, fakultas, ipk, tahun_masuk, tahun_lulus, pekerjaan, nama_perusahaan, jenis_kelamin, tempat_lahir, tanggal_lahir, alamat, agama)
            }
        }
    }

    private fun savePhoto(name: String, no_telp: String, npm: String, jurusan: String, fakultas: String, ipk: String, tahun_masuk: String, tahun_lulus: String, pekerjaan: String, nama_perusahaan: String, jenis_kelamin:String, tempat_lahir:String, tanggal_lahir:String, alamat:String, agama:String) {
        if(photo_location != null){
            val mStorageReference: StorageReference = storage.child( System.currentTimeMillis().toString() + "." + getFileExtension(
                photo_location!!
            ))

            mStorageReference.putFile(photo_location!!)
                .addOnFailureListener {

                }
                .addOnSuccessListener {

                    mStorageReference.downloadUrl.addOnSuccessListener { uri ->
                        reference.ref.child("photo").setValue(uri.toString())
                        reference.ref.child("fullname").setValue(name)
                        reference.ref.child("no_telp").setValue(no_telp)
                        reference.ref.child("npm").setValue(npm)
                        reference.ref.child("jurusan").setValue(jurusan)
                        reference.ref.child("fakultas").setValue(fakultas)
                        reference.ref.child("ipk").setValue(ipk)
                        reference.ref.child("tahun_masuk").setValue(tahun_masuk)
                        reference.ref.child("tahun_lulus").setValue(tahun_lulus)
                        reference.ref.child("pekerjaan").setValue(pekerjaan)
                        reference.ref.child("nama_perusahaan").setValue(nama_perusahaan)
                        reference.ref.child("jenis_kelamin").setValue(jenis_kelamin)
                        reference.ref.child("tempat_lahir").setValue(tempat_lahir)
                        reference.ref.child("tanggal_lahir").setValue(tanggal_lahir)
                        reference.ref.child("alamat").setValue(alamat)
                        reference.ref.child("agama").setValue(agama)
                    }
                }.addOnCompleteListener {
                    loading.visibility = View.GONE
                    Toast.makeText(this, "Berhasil update profile",Toast.LENGTH_SHORT).show()
                    finish()
                }
        }else{
            loading.visibility = View.GONE
            reference.ref.child("fullname").setValue(name)
            reference.ref.child("no_telp").setValue(no_telp)
            reference.ref.child("npm").setValue(npm)
            reference.ref.child("jurusan").setValue(jurusan)
            reference.ref.child("fakultas").setValue(fakultas)
            reference.ref.child("ipk").setValue(ipk)
            reference.ref.child("tahun_masuk").setValue(tahun_masuk)
            reference.ref.child("tahun_lulus").setValue(tahun_lulus)
            reference.ref.child("pekerjaan").setValue(pekerjaan)
            reference.ref.child("nama_perusahaan").setValue(nama_perusahaan)
            reference.ref.child("jenis_kelamin").setValue(jenis_kelamin)
            reference.ref.child("tempat_lahir").setValue(tempat_lahir)
            reference.ref.child("tanggal_lahir").setValue(tanggal_lahir)
            reference.ref.child("alamat").setValue(alamat)
            reference.ref.child("agama").setValue(agama)
            Toast.makeText(this, "Berhasil update profile",Toast.LENGTH_SHORT).show()
            finish()
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
                .into(photo_profile)

        }
    }

    fun getFileExtension(uri: Uri): String? {
        val contentResolver: ContentResolver = contentResolver
        val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))

    }
}