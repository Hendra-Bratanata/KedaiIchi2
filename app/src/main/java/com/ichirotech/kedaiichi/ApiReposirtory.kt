package com.ichirotech.kedaiichi

import android.util.Log
import java.net.URL

class ApiReposirtory{
    fun doRequest(url: String):String{
        var data = URL(url).readText()
        Log.d("tag","ApiRepository"+data)
        return data
    }
}