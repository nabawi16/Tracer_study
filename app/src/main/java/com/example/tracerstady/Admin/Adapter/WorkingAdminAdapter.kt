package com.example.tracerstady.Admin.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.tracerstady.Admin.Activity.PilihWorkingDetailActivity
import com.example.tracerstady.Admin.Activity.PilihWorkingEditActivity
import com.example.tracerstady.Admin.Model.Category
import com.example.tracerstady.Admin.Model.Working
import com.example.tracerstady.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class WorkingAdminAdapter(val mWorking :List<Working>, val context: Context) : RecyclerView.Adapter<WorkingAdminAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_working_admin, parent, false))
    }

    override fun getItemCount(): Int {
        return mWorking.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val working = mWorking[position]

//        val localeID = Locale("in", "ID")
//        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
//        val entry = working.entry.toInt()
//        val entry2 = formatRupiah.format(entry.toDouble())
//        holder.tv_entry.text = "$entry2 / pack"

        val entry = working.entry
        holder.tv_entry.text = " $entry / post"

        holder.tv_judul.text = working.judul
        holder.tv_durasi.text = working.durasi
        holder.tv_lokasi.text = working.lokasi

        val img = working.image

        Picasso.get()
            .load(img)
            .centerCrop()
            .fit()
            .into(holder.img_working)

        holder.ll_view.setOnClickListener{

            val reference = FirebaseDatabase.getInstance().reference.child("Category")
            reference.orderByChild("name").equalTo(working.lokasi).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()) {
                        for (datasnapshot1: DataSnapshot in p0.children) {
                            val category: Category = datasnapshot1.getValue(Category::class.java) as Category
                            val lat = category.lat
                            val lon = category.lon

                            val intent = Intent(context, PilihWorkingDetailActivity::class.java)
                            intent.putExtra("img", img)
                            intent.putExtra("id_working", working.id_working)
                            intent.putExtra("judul", working.judul)
                            intent.putExtra("durasi", working.durasi)
                            intent.putExtra("lokasi", working.lokasi)
                            intent.putExtra("entry", working.entry)
                            intent.putExtra("deskripsi", working.deskripsi)
                            intent.putExtra("lat", lat)
                            intent.putExtra("lon", lon)
                            context.startActivity(intent)
                        }
                    }
                }
                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }

        holder.ll_edit.setOnClickListener {

            val intent = Intent(context, PilihWorkingEditActivity::class.java)
            intent.putExtra("img", img)
            intent.putExtra("id_working", working.id_working)
            intent.putExtra("judul", working.judul)
            intent.putExtra("durasi", working.durasi)
            intent.putExtra("lokasi", working.lokasi)
            intent.putExtra("entry", working.entry)
            intent.putExtra("deskripsi", working.deskripsi)
            context.startActivity(intent)
        }

        holder.ll_delete.setOnClickListener {
            val reference = FirebaseDatabase.getInstance().getReference("Working").child(working.id_working)
            reference.removeValue().addOnCompleteListener {
                Toast.makeText(context, "Data has been deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var img_working : ImageView = itemView.findViewById(R.id.img_working)
        var tv_judul : TextView = itemView.findViewById(R.id.tv_judul)
        var tv_durasi : TextView = itemView.findViewById(R.id.tv_durasi)
        var tv_lokasi : TextView = itemView.findViewById(R.id.tv_lokasi)
        var tv_entry : TextView = itemView.findViewById(R.id.tv_entry)

        var ll_view : LinearLayout = itemView.findViewById(R.id.ll_view)
        var ll_edit : LinearLayout = itemView.findViewById(R.id.ll_edit)
        var ll_delete : LinearLayout = itemView.findViewById(R.id.ll_delete)

    }
}
