package com.ichirotech.kedaiichi

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ichirotech.kedaiichi.PrintLibrary.BluetoothPrinter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ViewMakanan {

    var menus: MutableList<Makanan> = mutableListOf()
    lateinit var makananPresenter: MakananPresenter
    lateinit var makananAdapter: MenuMakananAdapter



    override fun showData(data: List<Makanan>) {
        menus.clear()
        menus.addAll(data)
        makananAdapter.notifyDataSetChanged()
    }

//    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
//        when (item.itemId) {
//            R.id.navigation_home -> {
//
//            }
//            R.id.navigation_dashboard -> {
//                val actionBar = supportActionBar
//                actionBar?.title = "Keranjang"
//                loadKeranjangFragment()
//            }
//            R.id.navigation_notifications -> {
//
//            }
//        }
//        false
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadHomeFragmen()
                }
                R.id.navigation_dashboard -> {
                    val actionBar = supportActionBar
                    actionBar?.title = "Keranjang"
                    loadKeranjangFragment(savedInstanceState)
                }
                R.id.navigation_notifications -> {

                }
            }
            true
        }
        bottom_navigation.selectedItemId = R.id.navigation_home


    }

    private fun loadHomeFragmen() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container,HomeFragmen(),HomeFragmen::class.java.simpleName)
            .commit()
    }

    fun loadKeranjangFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, Keranjang(), Keranjang::class.java.simpleName)
                .commit()
        }


    }
}
