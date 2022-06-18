package com.example.tracerstady.Admin.Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.tracerstady.Admin.Activity.CategoryEditActivity
import com.example.tracerstady.Admin.Activity.CategoryViewActivity
import com.example.tracerstady.Admin.Model.Category
import com.example.tracerstady.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Transaction.success
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class CategoryAdminAdapter(val mCategory :ArrayList<Category>, val context: Context) : RecyclerView.Adapter<CategoryAdminAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category_admin, parent, false))
    }

    override fun getItemCount(): Int {
        return mCategory.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val category = mCategory[position]
        holder.mName.text = category.name

        val img = category.image

        Picasso.get()
            .load(img)
            .centerCrop()
            .fit()
            .into(holder.mImage)

        holder.ll_view.setOnClickListener {
            val intent = Intent(context, CategoryViewActivity::class.java)
            intent.putExtra("image", img)
            intent.putExtra("name", category.name)
            intent.putExtra("lat", category.lat)
            intent.putExtra("lon", category.lon)
            context.startActivity(intent)
        }

        holder.ll_edit.setOnClickListener {
            val intent = Intent(context, CategoryEditActivity::class.java)
            intent.putExtra("id_category", category.id_category)
            intent.putExtra("name", category.name)
            intent.putExtra("image", category.image)
            intent.putExtra("lat", category.lat)
            intent.putExtra("lon", category.lon)
            context.startActivity(intent)
        }

        holder.ll_delete.setOnClickListener {
            val dialog = Dialog(context)
            val inflater = LayoutInflater.from(context).inflate(R.layout.layout_confirm_delete,null)
            dialog.setContentView(inflater)
            dialog.setCancelable(true)
            dialog.show()

            val btn_cancel = inflater.findViewById<Button>(R.id.btn_cancel)
            val btn_confirm = inflater.findViewById<Button>(R.id.btn_confirm)
            val tv_close = inflater.findViewById<TextView>(R.id.tv_close)

            tv_close.setOnClickListener {
                dialog.dismiss()
            }

            btn_cancel.setOnClickListener {
                dialog.dismiss()
            }

            btn_confirm.setOnClickListener {

                val reference = FirebaseDatabase.getInstance().getReference("Category").child(category.id_category)
                reference.removeValue().addOnCompleteListener {
                    val ref = FirebaseDatabase.getInstance().getReference("Working")
                    ref.orderByChild("lokasi").equalTo(category.name).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()) {
                                for (datasnapshot1: DataSnapshot in p0.children) {
                                    val id_wisata = datasnapshot1.key
                                    ref.child(id_wisata!!).removeValue()
                                }
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })
                    Toast.makeText(context, "Data has been deleted", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mImage : ImageView = itemView.findViewById(R.id.img_category)
        var mName : TextView = itemView.findViewById(R.id.tv_name)

        var ll_view : LinearLayout = itemView.findViewById(R.id.ll_view)
        var ll_edit : LinearLayout = itemView.findViewById(R.id.ll_edit)
        var ll_delete : LinearLayout = itemView.findViewById(R.id.ll_delete)
    }
}