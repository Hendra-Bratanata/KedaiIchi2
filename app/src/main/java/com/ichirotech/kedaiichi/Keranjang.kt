package com.ichirotech.kedaiichi


import android.content.DialogInterface
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_keranjang.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.update
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.toast


class Keranjang : Fragment(), ViewMakanan {
    lateinit var presenter: MakananPresenter
    lateinit var mutableList: MutableList<Makanan>
    lateinit var gson: Gson
    lateinit var apiReposirtory: ApiReposirtory
    lateinit var adapter: MenuMakananAdapter


    override fun showData(data: List<Makanan>) {
        mutableList.clear()
        mutableList.addAll(data)
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_keranjang, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mutableList = mutableListOf()
        adapter = MenuMakananAdapter(mutableList ,{

            Toast.makeText(requireContext(), "YAng anda pilih adalah" + it.nama, Toast.LENGTH_SHORT).show()
            val list = listOf("Ya", "Tidak")
            selector(
                "Apa anda ingin memensan ${it.nama}",
                list
            ) { dialogInterface: DialogInterface, i: Int ->
                toast("${list[i]}")
                if (list[i].equals("Ya", true)) {

                    tambahPesanan(it)
                }
            }
        },{})

        rvMakanan.layoutManager = LinearLayoutManager(requireContext())
        rvMakanan.adapter = adapter
        apiReposirtory = ApiReposirtory()
        gson = Gson()
        presenter = MakananPresenter(this, gson, apiReposirtory)
        presenter.getMakanList()

    }

    fun tambahPesanan(it: Makanan) {
        try {
            context?.database?.use {
                var ada = false
                var jumlah = 0

                val cek = select(Pesanan.Table_Pesanan)
                val hasil = cek.parseList(classParser<Pesanan>())

                for (i in hasil.indices) {
                    val id = hasil[i].id

                    if (id == it.id) {
                        ada = true
                        jumlah = hasil[i].jumlah!!
                    }
                }


                if (ada) {
                    toast("pesanan sudah ada")
                    jumlah.plus(1)
                    update(
                        Pesanan.Table_Pesanan, Pesanan.jumlah to jumlah.plus(1)
                    )
                        .whereArgs("${Pesanan.Id} = ${it.id}")
                        .exec()
                }
                if (!ada) {
                    insert(

                        Pesanan.Table_Pesanan,
                        Pesanan.Id to it.id,
                        Pesanan.Nama to it.nama,
                        Pesanan.Harga to it.harga,
                        Pesanan.Gambar to it.gambar,
                        Pesanan.jumlah to jumlah.plus(1)
                    )
                    toast("Pesanan Sudah ditambahkan")

                }


            }

        } catch (e: SQLiteConstraintException) {
            e.localizedMessage
            Log.d("Tag", "" + e.message)


        }
    }

}
