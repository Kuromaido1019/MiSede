package com.misedeg

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.misedeg.adapters.FragmentPageAdapter

class Recorrer : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adapter: FragmentPageAdapter
    private var stepsCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorrer)

        // Ajustar los márgenes para las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el nombre de la ubicación seleccionada
        val selectedLocation = intent.getStringExtra("location_name") ?: ""
        val startLocation = intent.getStringExtra("start_location") ?: ""
        intent.putExtra("start_location", startLocation)

        // Inicializar TabLayout y ViewPager2
        tabLayout = findViewById(R.id.tabLayout)
        viewPager2 = findViewById(R.id.viewPager)

        // Pasar la ubicación seleccionada al adaptador
        adapter = FragmentPageAdapter(supportFragmentManager, lifecycle, selectedLocation, startLocation)

        // Imprimir por Log los Steps
        val steps = intent.getIntExtra("steps", 0)
        Log.d("Recorrer", "Steps recibidos: $steps")

        // Limitar a un máximo de 5 pasos
        val maxSteps = if (steps > 5) 5 else steps

        // Agregar las pestañas dinámicamente según el número de pasos
        for (i in 1..maxSteps) {
            tabLayout.addTab(tabLayout.newTab().setText("Paso $i"))
        }

        // Configurar el adapter para ViewPager2
        viewPager2.adapter = adapter

        // Sincronizar el TabLayout con el ViewPager2
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

}
