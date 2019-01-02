package com.ichirotech.kedaiichi

import com.google.gson.annotations.SerializedName

data  class  Makanan(
    @SerializedName("id_makanan")
    var id :String? = null,
    @SerializedName("nama")
    var nama :String? = null,
    @SerializedName("harga")
    var harga :String? = null,
    @SerializedName("gambar")
    var gambar :String? = null,
    @SerializedName("ket")
    var ket :String? = null
)
