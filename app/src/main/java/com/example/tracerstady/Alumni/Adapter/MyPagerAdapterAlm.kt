package com.example.tracerstady.Alumni.Adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tracerstady.Alumni.Fragment.ApplicantActiveFragment
import com.example.tracerstady.Alumni.Fragment.ApplicantUsedFragment
import com.example.tracerstady.Alumni.Model.Applicant

class MyPagerAdapterAlm(fm: FragmentManager): FragmentPagerAdapter(fm) {

    private val pages = listOf(
        ApplicantActiveFragment(),
        ApplicantUsedFragment()
    )

    override fun getItem(position: Int): Fragment {
        return pages[position]
    }

    override fun getCount(): Int {
        return pages.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position){
            0 -> "Active"
            else -> "Used"
        }
    }
}