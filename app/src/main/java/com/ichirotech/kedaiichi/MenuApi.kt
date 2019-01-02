package com.ichirotech.kedaiichi

import android.util.Log

object MenuApi{
    fun getMenuMakanan():String{

        val url = BuildConfig.BASE_URL+"showMakanan.php"
        Log.d("tag","Menu Api "+url)
        return url

    }
    fun getMenuMinuman():String{

        val url = BuildConfig.BASE_URL+"showMinuman.php"
        Log.d("tag","Menu Api "+url)
        return url

    }
    fun getMenuSnack():String{

        val url = BuildConfig.BASE_URL+"showSnack.php"
        Log.d("tag","Menu Api "+url)
        return url

    }
    fun getMenuNasi():String{

        val url = BuildConfig.BASE_URL+"showMakananNasi.php"
        Log.d("tag","Menu Api "+url)
        return url

    }
    fun getDll():String{

        val url = BuildConfig.BASE_URL+"showDll.php"
        Log.d("tag","Menu Api "+url)
        return url

    }
    fun getCari(cari :String):String{

        val url = BuildConfig.BASE_URL+"cariMenu.php?cari=$cari"
        Log.d("tag","Menu Api "+url)
        return url

    }
    fun getSave(idT :String,jumlah :String,IdM :String,bayar :String,kembali :String):String{

        val url = BuildConfig.BASE_URL+"saveTransaksi.php?idT=$idT&jumlah=$jumlah&idM=$IdM&bayar=$bayar&kembali=$kembali"
        Log.d("tag","Menu Api "+url)
        return url

    }

}

