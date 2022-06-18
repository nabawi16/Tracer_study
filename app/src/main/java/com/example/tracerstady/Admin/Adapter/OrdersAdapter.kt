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
import com.example.tracerstady.Alumni.Model.Applicant
import com.example.tracerstady.R

class OrdersAdapter(val mApplicant :List<Applicant>, val context: Context) : RecyclerView.Adapter<OrdersAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false))
    }

    override fun getItemCount(): Int {
        return mApplicant.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val applicant = mApplicant[position]
        holder.id_applicant.text = applicant.id_applicant
        holder.nama_working.text = applicant.nama_working
        holder.date.text = applicant.date
        holder.jumlah_applicant.text = applicant.jumlah_applicant + " Applicant"
        holder.status.text = applicant.status

        if(applicant.status == "Sudah Dimasukin"){
            holder.status.background.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.MULTIPLY)
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(context, OrdersDetailActivity::class.java)
            intent.putExtra("key",applicant.key)
            context.startActivity(intent)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nama_working : TextView = itemView.findViewById(R.id.tv_nama_working)
        var date : TextView = itemView.findViewById(R.id.tv_date)
        var jumlah_applicant : TextView = itemView.findViewById(R.id.tv_jumlah_applicant)
        var status : TextView = itemView.findViewById(R.id.tv_status)
        var id_applicant : TextView = itemView.findViewById(R.id.tv_id_applicant)

    }
}