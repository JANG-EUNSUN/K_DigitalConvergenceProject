package com.goni99.smartlibrarysystem.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.goni99.smartlibrarysystem.fragment.BookRentStatusFragment
import com.goni99.smartlibrarysystem.fragment.BookRecommendFragment

class LibraryPageAdapter(
    fragment: FragmentActivity
): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> BookRentStatusFragment()
            else -> BookRecommendFragment()
        }
    }
}