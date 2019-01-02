package com.ichirotech.kedaiichi

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find

class HomeAdapter(val menus: List<Pesanan>) :
    RecyclerView.Adapter<HomeAdapter.ViewHolder>() {



    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

//        return ViewHolder(MakananUi().createView(AnkoContext.create(p0.context,p0)))
        val view: View = LayoutInflater.from(p0.context).inflate(R.layout.item_list,p0,false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int = menus.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItem(menus[p1])
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gambar: ImageView = view.find(R.id.imageItem)
        val nama: TextView = view.find(R.id.tvNamaItem)
        val harga: TextView = view.find(R.id.tvHargaItem)
        val jumlah: TextView = view.find(R.id.tvJumlahItem)

        fun bindItem(menus: Pesanan) {
            Picasso.get().load(menus.gambar).into(gambar)
            jumlah.text = menus.jumlah.toString()
            nama.text = menus.nama.toString()
            harga.text = menus.harga.toString()
            Log.d("Tag", "adapter " + menus.harga)

        }

    }
}