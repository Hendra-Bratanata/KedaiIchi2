package com.ichirotech.kedaiichi.Adapter

import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.ichirotech.kedaiichi.MODEL.Makanan
import com.ichirotech.kedaiichi.R
import com.squareup.picasso.Picasso
import org.jetbrains.anko.*

class MenuAdapter(val menus: List<Makanan>, val Plus: (Makanan) -> Unit, val Min: (Makanan) -> Unit) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

//        return ViewHolder(MakananUi().createView(AnkoContext.create(p0.context,p0)))
        val view: View = LayoutInflater.from(p0.context).inflate(R.layout.list_item, p0, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int = menus.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bindItem(menus[p1], Plus, Min)
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gambarMakanan: ImageView = view.find(R.id.Gambar)
        val tvNamaMakana: TextView = view.find(R.id.tvNamaMakanan)
        val tvHargaMakanan: TextView = view.find(R.id.tvTanggal)
        val btnTambah: Button = view.find(R.id.btnPlus)
        val btnMin: Button = view.find(R.id.btnMin)
        val tvKet: TextView = view.find(R.id.tvket)


        fun bindItem(menus: Makanan, Plus: (Makanan) -> Unit, Min: (Makanan) -> Unit) {

            Picasso.get().load(menus.gambar).into(gambarMakanan)
            tvHargaMakanan.text = "Rp. " + menus.harga.toString()
            tvNamaMakana.text = menus.nama
            tvKet.text = menus.ket
            btnMin.setOnClickListener {
                Min(menus)
            }
            btnTambah.setOnClickListener {
                Plus(menus)


            }

        }
    }
}