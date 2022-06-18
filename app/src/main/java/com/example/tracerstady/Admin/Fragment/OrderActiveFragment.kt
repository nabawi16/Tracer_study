package com.example.tracerstady.Admin.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tracerstady.Admin.Adapter.OrdersAdapter
import com.example.tracerstady.Alumni.Model.Applicant
import com.example.tracerstady.R
import com.google.firebase.database.*


class OrderActiveFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var empty: RelativeLayout

    private lateinit var reference : DatabaseReference

    lateinit var mApplicant: MutableList<Applicant>
    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_active, container, false)

        empty = view.findViewById(R.id.empty)

        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        mApplicant = mutableListOf()
        getOrders()

        return view
    }

    private fun getOrders() {
        reference = FirebaseDatabase.getInstance().reference.child("Apply")
        reference.orderByChild("status").equalTo("Applicant Aktif").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()) {
                    mApplicant.clear()
                    for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                        val p: Applicant = datasnapshot1.getValue(Applicant::class.java) as Applicant
                        mApplicant.add(p)
                    }
                    ordersAdapter = context?.let { OrdersAdapter(mApplicant, it) }!!
                    recyclerView.adapter = ordersAdapter
                    ordersAdapter.notifyDataSetChanged()
                }else{
                    empty.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}