package com.example.tracerstady.Alumni.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.example.tracerstady.Alumni.Adapter.MyPagerAdapterAlm
import com.example.tracerstady.R
import com.google.android.material.tabs.TabLayout

class MyApplicantFragment : Fragment() {

    private lateinit var view_pager: ViewPager
    private lateinit var tab_layout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_applicant, container, false)

        view_pager = view.findViewById(R.id.view_pager)
        tab_layout = view.findViewById(R.id.tab_layout)

        view_pager.adapter = MyPagerAdapterAlm(childFragmentManager)
        tab_layout.setupWithViewPager(view_pager)

        return view
    }


}