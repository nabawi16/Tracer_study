package com.example.tracerstady.Admin.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tracerstady.Admin.Activity.OrdersDetailActivity
import com.example.tracerstady.Alumni.Activity.EditProfileActivity
import com.example.tracerstady.Alumni.Model.Applicant
import com.example.tracerstady.Alumni.Model.User
import com.example.tracerstady.R

class UsersAdapter(val mUsers :List<User>, val context: Context) : RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersAdapter.MyViewHolder {
        return UsersAdapter.MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val alumni = mUsers[position]
        holder.NPM.text = alumni.npm
        holder.nama_alumni.text = alumni.fullname
        holder.date.text = alumni.tahun_lulus
        holder.jumlah_alumni.text = alumni.email + "Alumni"
        holder.status.text = alumni.pekerjaan

        if(alumni.pekerjaan == "Sudah Dimasukin"){
            holder.status.background.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.MULTIPLY)
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nama_alumni : TextView = itemView.findViewById(R.id.tv_nama_alumni)
        var date : TextView = itemView.findViewById(R.id.tv_date)
        var jumlah_alumni : TextView = itemView.findViewById(R.id.tv_jumlah_alumni)
        var status : TextView = itemView.findViewById(R.id.tv_status)
        var NPM : TextView = itemView.findViewById(R.id.tv_id_alumni)

    }
}