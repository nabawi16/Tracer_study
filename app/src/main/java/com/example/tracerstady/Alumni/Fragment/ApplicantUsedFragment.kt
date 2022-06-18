package com.example.tracerstady.Alumni.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tracerstady.Alumni.Adapter.ApplicantAdapter
import com.example.tracerstady.Alumni.Model.Applicant
import com.example.tracerstady.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ApplicantUsedFragment : Fragment() {

    private lateinit var ll_no_applicant: LinearLayout
    private lateinit var recyclerView: RecyclerView

    private lateinit var reference : DatabaseReference
    internal var firebaseUser: FirebaseUser? = null

    lateinit var mApplicant: MutableList<Applicant>
    private lateinit var applicantAdapter: ApplicantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_applicant_used, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val userid = firebaseUser?.uid.toString()

        ll_no_applicant = view.findViewById(R.id.ll_no_applicant)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        mApplicant = mutableListOf()
        getApplicant(userid)

        return view
    }

    private fun getApplicant(userid: String) {
        reference = FirebaseDatabase.getInstance().reference.child("Applicant").child(userid)
        reference.orderByChild("status").equalTo("Sudah Dimasukan")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()) {
                        ll_no_applicant.visibility = View.GONE
                        mApplicant.clear()
                        for (datasnapshot1: DataSnapshot in dataSnapshot.children) {
                            val p: Applicant = datasnapshot1.getValue(Applicant::class.java) as Applicant
                            mApplicant.add(p)
                        }
                        applicantAdapter = context?.let { ApplicantAdapter(mApplicant, it) }!!
                        recyclerView.adapter = applicantAdapter
                        applicantAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }
}