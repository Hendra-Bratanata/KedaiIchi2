package com.ichirotech.kedaiichi.PRESENTER

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.ichirotech.kedaiichi.Activity.TambahData
import com.ichirotech.kedaiichi.MODEL.MakananRespons
import com.ichirotech.kedaiichi.REST_API.ApiReposirtory
import com.ichirotech.kedaiichi.REST_API.MenuApi
import com.ichirotech.kedaiichi.VIEW.ViewMakanan
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MakananPresenter(val view: ViewMakanan,
                       val gson: Gson,
                       val apiReposirtory: ApiReposirtory
){

    fun getMakanList(){
        doAsync {
            val data = gson.fromJson(apiReposirtory.doRequest(MenuApi.getMenuMakanan())
                , MakananRespons::class.java
            )
            uiThread {
                view.showData(data.mk)
                Log.d("tag","getMakanan"+data.mk)

            }

        }
    }
    fun getMinumanList(){
        doAsync {
            val data = gson.fromJson(apiReposirtory.doRequest(MenuApi.getMenuMinuman()),
                MakananRespons::class.java)

        uiThread {
            view.showData(data.mk)
        }
        }
    }
    fun getSnackList(){
        doAsync {
            val data = gson.fromJson(apiReposirtory.doRequest(MenuApi.getMenuSnack()),
                MakananRespons::class.java)

            uiThread {
                view.showData(data.mk)
            }
        }
    }

    fun getNasiList(){
        doAsync {
            val data = gson.fromJson(apiReposirtory.doRequest(MenuApi.getMenuNasi()),
                MakananRespons::class.java)

            uiThread {
                view.showData(data.mk)
            }
        }
    }
    fun getDllList(){
        doAsync {
            val data = gson.fromJson(apiReposirtory.doRequest(MenuApi.getDll()),
                MakananRespons::class.java)

            uiThread {
                view.showData(data.mk)
            }
        }
    }
    fun getCari(cari : String){
        doAsync {
            val data = gson.fromJson(apiReposirtory.doRequest(MenuApi.getCari(cari)),
                MakananRespons::class.java)

            uiThread {
                view.showData(data.mk)
            }
        }
    }
    fun save(idT :String,jumlah :String,IdM :String,bayar :String,kembali :String){
        doAsync {
           apiReposirtory.doRequest(MenuApi.getSave(idT,jumlah,IdM,bayar,kembali))
        }
    }
    fun insertDatabase(ctx:Context,nama :String,harga :String,jenis :String,ket :String,gambar :String){
        doAsync {
            val data = apiReposirtory.doRequest(MenuApi.insertDatabase(nama,harga,jenis,ket,gambar))
        uiThread {
            Toast.makeText(ctx,data,Toast.LENGTH_SHORT).show()
        }

        }

    }


}