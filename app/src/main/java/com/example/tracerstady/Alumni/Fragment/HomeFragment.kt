package com.example.tracerstady.Alumni.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.tracerstady.Admin.Model.Category
import com.example.tracerstady.Admin.Model.Working
import com.example.tracerstady.Alumni.Activity.ListByCategoryAct
import com.example.tracerstady.Alumni.Adapter.CategoryAdapter
import com.example.tracerstady.Alumni.Adapter.WorkingAdapter
import com.example.tracerstady.R
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var txt_viewall: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var backup_category: LottieAnimationView
    private lateinit var backup_paket: LottieAnimationView

    private lateinit var reference : DatabaseReference

    private var mCategory: ArrayList<Category> = arrayListOf()
    private lateinit var categoryAdapter: CategoryAdapter
    lateinit var mWorking: MutableList<Working>
    private lateinit var workingAdapter: WorkingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        txt_viewall = view.findViewById(R.id.txt_viewall)
        backup_category = view.findViewById(R.id.backup_category)
        backup_paket = view.findViewById(R.id.backup_paket)

        recyclerView = view.findViewById(R.id.rv_category)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        getCategory()

        recyclerView2 = view.findViewById(R.id.rv_working)
        recyclerView2.setHasFixedSize(true)
        recyclerView2.layoutManager = LinearLayoutManager(context)
        mWorking = mutableListOf()
        getWorking()

        txt_viewall.setOnClickListener {
            startActivity(Intent(activity, ListByCategoryAct::class.java).putExtra("nameofcategory","Semua Destinasi Pekerjaan"))
        }

        return view
    }

    private fun getCategory() {
        reference = FirebaseDatabase.getInstance().reference.child("Category")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    mCategory.clear()
                    for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                        val p: Category = datasnapshot1.getValue(Category::class.java) as Category
                        mCategory.add(p)
                    }
                    categoryAdapter = context?.let { CategoryAdapter(mCategory, it) }!!
                    recyclerView.adapter = categoryAdapter
                    categoryAdapter.notifyDataSetChanged()

                }else{
                    recyclerView.visibility = View.GONE
                    backup_category.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getWorking() {
        reference = FirebaseDatabase.getInstance().reference.child("Working")
        reference.limitToFirst(2).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    mWorking.clear()
                    for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                        val p: Working = datasnapshot1.getValue(Working::class.java) as Working
                        mWorking.add(p)
                    }
                    workingAdapter = context?.let { WorkingAdapter(mWorking, it) }!!
                    recyclerView2.adapter = workingAdapter
                    workingAdapter.notifyDataSetChanged()

                }else{
                    txt_viewall.visibility = View.GONE
                    recyclerView2.visibility = View.GONE
                    backup_paket.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}