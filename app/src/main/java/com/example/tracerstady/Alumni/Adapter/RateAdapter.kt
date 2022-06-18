package com.example.tracerstady.Alumni.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tracerstady.Admin.Model.Working
import com.example.tracerstady.R
import com.squareup.picasso.Picasso

class RateAdapter(val mWorking :List<Working>, val context: Context) : RecyclerView.Adapter<RateAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false))
    }

    override fun getItemCount(): Int {
        return mWorking.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val working = mWorking[position]
        holder.nama.text = working.nama
        holder.date.text = working.date
        holder.rate.rating = working.rate.toFloat()
        holder.comment.text = working.comment

        Picasso.get()
            .load(working.photo)
            .centerCrop()
            .fit()
            .into(holder.image)

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var date : TextView = itemView.findViewById(R.id.tv_date)
        var nama : TextView = itemView.findViewById(R.id.tv_nama)
        var rate : RatingBar = itemView.findViewById(R.id.rb_applicant)
        var image : ImageView = itemView.findViewById(R.id.img_user)
        var comment : TextView = itemView.findViewById(R.id.tv_comment)

    }
}