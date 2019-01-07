package com.ichirotech.kedaiichi.Activity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.ActivityInfo
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.google.gson.Gson
import com.ichirotech.kedaiichi.*
import com.ichirotech.kedaiichi.Adapter.MenuAdapter
import com.ichirotech.kedaiichi.Adapter.PesananAdapter
import com.ichirotech.kedaiichi.MODEL.Makanan
import com.ichirotech.kedaiichi.MODEL.Pesanan
import com.ichirotech.kedaiichi.PRESENTER.MakananPresenter
import com.ichirotech.kedaiichi.PrintLibrary.BluetoothPrinter
import com.ichirotech.kedaiichi.REST_API.ApiReposirtory
import com.ichirotech.kedaiichi.VIEW.ViewMakanan
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.*
import org.jetbrains.anko.db.*
import org.jetbrains.anko.support.v4.onRefresh
import java.io.File
import java.io.OutputStream
import java.util.*

class HOME : AppCompatActivity(), ViewMakanan {


    override fun showData(data: List<Makanan>) {

        swapTengah.isRefreshing = false
        loading.visibility=View.GONE
        listMenu.clear()
        listMenu.addAll(data)
        rvTengah.smoothScrollToPosition(0)
        adapterTengah.notifyDataSetChanged()

    }

    lateinit var adapterTengah: MenuAdapter
    lateinit var adapterKanan: PesananAdapter
    lateinit var listMenu: MutableList<Makanan>
    lateinit var listPesanan: MutableList<Pesanan>
    lateinit var pesanan: List<Pesanan>
    lateinit var gson: Gson
    lateinit var apiReposirtory: ApiReposirtory
    lateinit var presenter: MakananPresenter
    var hargaTotal: Int = 0
    var Bayar: Int = 0
    var Kebalian: Int = 0
    var jumlahPesanan = 1
    lateinit var noInvoice: String
    lateinit var btAdapter: BluetoothAdapter
    lateinit var btDevice: BluetoothDevice
    lateinit var btPrinter: BluetoothPrinter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_home)

        val displa = DisplayMetrics()
        val wm: WindowManager = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(displa)
        val lebar = displa.widthPixels
        val tinggi  = displa.heightPixels

        Log.d("LEBAR","$lebar")
        Log.d("TINGGI","$tinggi")

        btAdapter = BluetoothAdapter.getDefaultAdapter()
        btDevice = btAdapter.getRemoteDevice("DC:0D:30:29:6E:2E")
        btPrinter = BluetoothPrinter(btDevice)
        listMenu = mutableListOf()
        listPesanan = mutableListOf()
        adapterTengah = MenuAdapter(listMenu, {
            jumlahPesanan = 1
            tambahPesanan(it)
        },
            {
                if (jumlahPesanan == 1) {
                    hapusPesananJumlah(it, 1)
                }
                ambilDataDatabase()

            })



        adapterKanan = PesananAdapter(listPesanan,{
                var makanan : Makanan = Makanan(it.id,it.nama,it.harga.toString(),it.gambar,null)
            if (jumlahPesanan == 1) {
                hapusPesananJumlah(makanan, 1)
            }
            ambilDataDatabase()
        },{
            var makanan : Makanan = Makanan(it.id,it.nama,it.harga.toString(),it.gambar,null)
            jumlahPesanan = 1
            tambahPesanan(makanan)

        })




        btnPrint.setOnClickListener {
            alert("Cetak Invoice", "Invoice") {
                yesButton {
                    printBill()
                }
                noButton {}
            }.show()


        }
        btnNew.setOnClickListener {
            alert("Buat invoice baru?", "Inovice Baru") {
                yesButton {
                    flushDatabase()
                }
                noButton { }
            }.show()


        }
        if (lebar == 1280 && tinggi == 720){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            rvTengah.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)

        }else{

            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            rvTengah.layoutManager = LinearLayoutManager(this)
            rvInvoice.layoutManager = LinearLayoutManager(this)

        }

        rvInvoice.adapter = adapterKanan
        rvTengah.adapter = adapterTengah
        apiReposirtory = ApiReposirtory()
        gson = Gson()
        presenter = MakananPresenter(this, gson, apiReposirtory)
        presenter.getNasiList()
        ambilDataDatabase()
        swapTengah.onRefresh {
            ambilDataDatabase()
            swapTengah.isRefreshing = false
        }
        btnMakanan.setOnClickListener {
            loading.visibility = View.VISIBLE
            presenter.getMakanList()

        }
        btnMinuman.setOnClickListener {
            loading.visibility = View.VISIBLE
            presenter.getMinumanList()
        }
        btnSnack.setOnClickListener {
            loading.visibility = View.VISIBLE
            presenter.getSnackList()
        }
        btnPaket.setOnClickListener {
            loading.visibility = View.VISIBLE
            presenter.getNasiList()
        }
        btndll.setOnClickListener {
            loading.visibility = View.VISIBLE
            presenter.getDllList()
        }
        btnBayar.setOnClickListener {
            loading.visibility = View.VISIBLE
            var bayar = tvBayar.text.toString()
            Bayar = bayar.toInt()
            var total = tvTotalHarga.text.toString()
            var Total = total.toInt()
            Kebalian = 0

            if (Bayar < Total) {
                loading.visibility = View.GONE
                alert("Hi,Coba Cek kembali input uang nya", "Uang Bayar Kurang") {
                    yesButton { toast("Ok…") }
                    noButton {}
                }.show()
            } else {
                Kebalian = Bayar - Total
                tvKebalian.text = Kebalian.toString()
                noInvoice = ambilInvoice()
                for (i in pesanan.indices) {
                    presenter.save(
                        noInvoice,
                        pesanan[i].jumlah.toString(),
                        pesanan[i].id!!,
                        Bayar.toString(),
                        Kebalian.toString()
                    )
                    loading.visibility = View.GONE
                }
                alert("Cetak Invoice", "Invoice") {
                    yesButton {
                        printBill()

                    }
                    noButton {
                    }
                }.show()
            }
        }

        btnCari.setOnClickListener {

            var cari = edtCari.text.toString()
             if (cari.equals("CACHECLEAR")) {
                deleteMemoryData(this)
            }else if(cari.equals("DATABASE")){

                 startActivity<TambahData>()



            }else if(cari.equals("TAMBAH_")){

            }else{
                 loading.visibility= View.VISIBLE
                presenter.getCari(cari)
            }

        }
    }

    private fun hapusPesananJumlah(it: Makanan, i: Int) {
        try {
            database?.use {
                var jumlah = 0

                val cek = select(Pesanan.Table_Pesanan)
                val hasil = cek.parseList(classParser<Pesanan>())
                for (i in hasil.indices) {
                    val id = hasil[i].id

                    if (id == it.id) {
                        jumlah = hasil[i].jumlah!!
                    }
                }
                update(
                    Pesanan.Table_Pesanan, Pesanan.jumlah to jumlah.minus(i)
                )
                    .whereArgs("${Pesanan.Id} = ${it.id}")
                    .exec()

                listPesanan.clear()
                ambilDataDatabase()
            }
        } catch (e: SQLiteConstraintException) {
            e.localizedMessage
        }
        adapterKanan.notifyDataSetChanged()
    }

    private fun tambahPesananJumlah(it: Makanan, i: Int) {

        try {
            database?.use {
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
                    update(
                        Pesanan.Table_Pesanan, Pesanan.jumlah to jumlah.plus(i + 2)
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
                        Pesanan.jumlah to jumlah.plus(i + 2)
                    )
                    toast("Pesanan Sudah ditambahkan")

                }


            }


        } catch (e: SQLiteConstraintException) {
            e.localizedMessage
            Log.d("Tag", "" + e.message)


        }

        ambilDataDatabase()

    }

    private fun tambahPesanan(it: Makanan) {
        try {
            database?.use {
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

        ambilDataDatabase()
    }

    private fun ambilDataDatabase() {
        hargaTotal = 0
        database?.use {
            val resault = select(Pesanan.Table_Pesanan)
            pesanan = resault.parseList(classParser())
            if (pesanan.size > 0) {
                listPesanan.clear()
                for (i in pesanan.indices) {
                    var jumlah = pesanan[i].jumlah
                    var hargaSekarang: Int? = pesanan[i].harga
                    var hitungTotal = jumlah!! * hargaSekarang!!
                    var HargaTotal = hargaTotal.plus(hitungTotal!!)
                    hargaTotal = HargaTotal

                    if (jumlah == 0) {
                        jumlahPesanan = 0
                        try {
                            database.use {
                                delete(Pesanan.Table_Pesanan, "Id_Pesanan ={id}", "id" to pesanan[i].idPesana)
                            }
                            ambilDataDatabase()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                listPesanan.addAll(pesanan)

                tvTotalHarga.text = hargaTotal.toString()
            }
            adapterKanan.notifyDataSetChanged()


        }
    }

    private fun hapusPesanan(it: Pesanan) {
        try {
            database?.use {
                delete(Pesanan.Table_Pesanan, "Id_Pesanan ={id}", "id" to it.idPesana)
            }
        } catch (e: SQLiteConstraintException) {
            e.localizedMessage
        }
    }

    private fun ambilInvoice(): String {
        val tahun = Calendar.getInstance().get(Calendar.YEAR)
        val bulan = Calendar.getInstance().get(Calendar.MONTH + 1)
        val tanggal = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val jam = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val menit = Calendar.getInstance().get(Calendar.MINUTE)
        val detik = Calendar.getInstance().get(Calendar.SECOND)
        var Invoice = "$tahun$bulan$tanggal$jam$menit$detik"

        return Invoice
    }

    private fun printBill() {
        btPrinter.connectPrinter(object : BluetoothPrinter.PrinterConnectListener {
            override fun onConnected() {


//                    btPrinter.printImage(decodeSampledBitmapFromResource(resources,R.drawable.kedaiichi,20,20))

                var BILL = ""

                BILL = (
                        "          KEDAI ICHI         \n" +
                                "       Citra Raya Cikupa    \n " +
                                "  Ruko Evergreen Blok k30/51 \n" +
                                "    Tlp : 021 - 2259 7926    \n" +
                                "        INV $noInvoice       \n")

                BILL = BILL + "--------------------------------\n"
                BILL = BILL + String.format("%1$-10s %2$5s %3$12s ", "Qty", "Harga", "Jumlah")
                BILL = BILL + "\n"
                BILL = BILL + "--------------------------------"
                for (i in pesanan.indices) {

                    var harga = pesanan[i].harga
                    var jumlah = pesanan[i].jumlah
                    var total = jumlah!! * harga!!
                    BILL = BILL + "\n " + String.format("%1$-10s", "* " + pesanan[i].nama)
                    BILL = BILL + "\n " + String.format(
                        "%1$-10s %2$5s %3$12s ",
                        "$jumlah",
                        "$harga",
                        "$total"
                    )
                }
                BILL = BILL + "\n--------------------------------"

                BILL = BILL + "\n " +
                        String.format("%1$15s %2$-6s ", "Total Belanja          ", "$hargaTotal")
                BILL = BILL + "\n " + String.format("%1$15s %2$-6s ", "Bayar                  ", "$Bayar")
                BILL = BILL + "\n " +
                        String.format("%1$15s %2$-6s ", "Kembali                ", "$Kebalian")
                BILL = BILL + "--------------------------------\n"
                BILL = BILL + "          TERIMA KASIH          "
                BILL = BILL + "\n\n "


                var oss: OutputStream = btPrinter.socket.outputStream
                oss.write(BILL.toByteArray())
            }

            override fun onFailed() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    private fun flushDatabase() {
        try {
            database.use {
                val resault = select(Pesanan.Table_Pesanan)
                pesanan = resault.parseList(classParser())
                if (pesanan.size > 0) {
                    listPesanan.clear()
                    for (i in pesanan.indices) {
                        delete(Pesanan.Table_Pesanan, "Id_Pesanan ={id}", "id" to pesanan[i].idPesana)
                    }
                }
            }
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
        }
        tvTotalHarga.text = "0"
        tvBayar.setText("0")
        tvKebalian.text = "0"
        ambilDataDatabase()
    }

    private fun deleteMemoryData(context: Context) {
        try {
            var dir: File = context.cacheDir
            deleteDir(dir)

        } catch (e: java.lang.Exception) {

        }

    }

    private fun deleteDir(dir: File): Boolean {
        if (dir != null && dir.isDirectory) {
            var child: Array<String> = dir.list()
            for (i in child.indices) {
                var sukses: Boolean = deleteDir(File(dir, child[i]))
                if (!sukses) {
                    return false
                }
            }
            return dir.delete()
        } else if (dir != null && dir.isFile) {
            return dir.delete()
        } else {
            return false
        }

    }


}
