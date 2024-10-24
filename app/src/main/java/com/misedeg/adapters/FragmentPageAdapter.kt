package com.misedeg.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.misedeg.FirstFragment
import com.misedeg.SecondFragment
import com.misedeg.ThirdFragment
import com.misedeg.FourthFragment
import com.misedeg.FifthFragment

class FragmentPageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val locationName: String,
    private val start_location: String
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        // Crear el fragmento según la posición
        val fragment = when (position) {
            0 -> FirstFragment()
            1 -> SecondFragment()
            2 -> ThirdFragment()
            3 -> FourthFragment()
            4 -> FifthFragment()
            else -> throw IllegalStateException("Position $position is invalid for this adapter")
        }

        // Pasar el nombre de la ubicación al fragmento mediante argumentos
        fragment.arguments = Bundle().apply {
            putString("location_name", locationName)
            putString("start_location", start_location)
        }

        return fragment
    }
}
