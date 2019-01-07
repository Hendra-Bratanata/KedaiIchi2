package com.ichirotech.kedaiichi.Activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.ichirotech.kedaiichi.MODEL.Makanan
import com.ichirotech.kedaiichi.PRESENTER.MakananPresenter
import com.ichirotech.kedaiichi.R
import com.ichirotech.kedaiichi.REST_API.ApiReposirtory
import com.ichirotech.kedaiichi.VIEW.ViewMakanan
import kotlinx.android.synthetic.main.activity_tambah_data.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class TambahData : AppCompatActivity(), ViewMakanan {
    override fun showData(data: List<Makanan>) {

    }

    lateinit var gson: Gson
    lateinit var apiReposirtory: ApiReposirtory
    lateinit var presenter: MakananPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_data)
        gson = Gson()
        apiReposirtory = ApiReposirtory()
        presenter = MakananPresenter(this, gson, apiReposirtory)



        btnInsertDatabase.setOnClickListener {
            var namaBarang = edtNamaBarang.text.toString()
            var hargaBarang = edtHargaBarang.text.toString()
            var jenisBarang: String = ""
            when (rg.checkedRadioButtonId) {
                rbPaket.id -> jenisBarang = rbPaket.text.toString()
                rbMakanan.id -> jenisBarang = rbMakanan.text.toString()
                rbMinuman.id -> jenisBarang = rbMinuman.text.toString()
                rbSnack.id -> jenisBarang = rbSnack.text.toString()
                rbDll.id -> jenisBarang = rbDll.text.toString()
                else -> {
                    jenisBarang = "kosong"
                }


            }
            presenter.insertDatabase(this,
                namaBarang,
                hargaBarang,
                jenisBarang.toLowerCase(),
                "*",
                "http://kedai-ichi.store/Pic/logokedai.png"
            )
            alert("Ke Menu Utama", "Konfirmasi") {
                yesButton { close() }
                noButton {
                }
            }.show()
            edtHargaBarang.text.clear()
            edtNamaBarang.text.clear()

        }
    }

    fun close() {
        this.finish()

    }
}
