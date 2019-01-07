package com.ichirotech.kedaiichi.Fragment


import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.res.Resources
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.ichirotech.kedaiichi.Adapter.PesananAdapter
import com.ichirotech.kedaiichi.MODEL.Pesanan
import com.ichirotech.kedaiichi.PrintLibrary.BluetoothPrinter
import com.ichirotech.kedaiichi.R
import com.ichirotech.kedaiichi.database
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.select
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.onRefresh
import java.io.OutputStream


class HomeFragmen : Fragment() {
    lateinit var mutableList: MutableList<Pesanan>
    lateinit var adapter: PesananAdapter
    lateinit var pesanan: List<Pesanan>
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var tvHargaTotal: TextView


    var hargaTotal: Int = 0
    lateinit var mBtnBayar: Button

    lateinit var btAdapter: BluetoothAdapter
    lateinit var btDevice: BluetoothDevice
    lateinit var btPrinter: BluetoothPrinter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = view.findViewById(R.id.swipe)
        tvHargaTotal = view.findViewById(R.id.tvTOTAL)
        mBtnBayar = view.find(R.id.btnBayar)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btAdapter = BluetoothAdapter.getDefaultAdapter()
        btDevice = btAdapter.getRemoteDevice("DC:0D:30:29:6E:2E")
        btPrinter = BluetoothPrinter(btDevice)

        mutableList = mutableListOf()
//        adapter = PesananAdapter(mutableList)
        rvHomeFragment.layoutManager = LinearLayoutManager(requireContext())
        rvHomeFragment.adapter = adapter

        ambilDataDatabase()

        swipeRefreshLayout.onRefresh {
            ambilDataDatabase()
        }
        mBtnBayar.setOnClickListener {
            btPrinter.connectPrinter(object : BluetoothPrinter.PrinterConnectListener {
                override fun onConnected() {


//                    btPrinter.printImage(decodeSampledBitmapFromResource(resources,R.drawable.kedaiichi,20,20))

                    var BILL = ""

                    BILL = (
                            "          KEDAI ICHI         \n" +
                                    "       Citra Raya Cikupa    \n " +
                                    "  Ruko Evergreen Blok k30/51 \n" +
                                    "    Tlp : 021 - 2259 7926    \n" +
                                    "        INV 590019091        \n")

                    BILL = BILL + "--------------------------------\n"
                    BILL = BILL + String.format("%1$-10s", "Item")
                    BILL = BILL + "\n"
                    BILL = BILL + String.format("%1$-10s %2$5s %3$12s ", "Qty", "Harga", "Jumlah")
                    BILL = BILL + "\n"
                    BILL = BILL + "--------------------------------"
                    for (i in pesanan.indices) {

                        var harga = pesanan[i].harga
                        var jumlah = pesanan[i].jumlah
                        var total = jumlah!! * harga!!
                        BILL = BILL + "\n " + String.format("%1$-10s","* "+ pesanan[i].nama)
                        BILL = BILL + "\n " + String.format(
                            "%1$-10s %2$5s %3$12s ",
                            "$jumlah",
                            "$harga",
                            "$total"
                        )
                    }
                    BILL = BILL + "\n--------------------------------"
                    BILL = BILL + "\n\n "
//                              BILL = BILL + "Total Qty:"+ "         85" + "\n"
                    BILL = BILL + "\n " + String.format("%1$15s %2$-13s ", "Total Belanja:", "$hargaTotal")
//                              BILL = BILL + "Total Value:" + "      700.00" + "\n"

                    BILL = BILL + "--------------------------------\n"
                    BILL = BILL + "\n\n "


                    var oss: OutputStream = btPrinter.socket.outputStream
                    oss.write(BILL.toByteArray())
                }

                override fun onFailed() {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
        }


    }

    private fun hapusPesanan(it: Pesanan) {
        try {
            context?.database?.use {
                delete(Pesanan.Table_Pesanan, "Id_Pesanan ={id}", "id" to it.idPesana)
                ambilDataDatabase()
            }
        } catch (e: SQLiteConstraintException) {
            e.localizedMessage
        }
    }

    private fun ambilDataDatabase() {
        hargaTotal = 0
        context?.database?.use {
            val resault = select(Pesanan.Table_Pesanan)
            pesanan = resault.parseList(classParser())
            swipeRefreshLayout.isRefreshing = false
            mutableList.clear()
            for (i in pesanan.indices) {
                var jumlah = pesanan[i].jumlah
                var hargaSekarang: Int? = pesanan[i].harga
                var hitungTotal = jumlah!! * hargaSekarang!!
                var HargaTotal = hargaTotal.plus(hitungTotal!!)
                hargaTotal = HargaTotal


            }
            mutableList.addAll(pesanan)
            adapter.notifyDataSetChanged()
            tvHargaTotal.text = hargaTotal.toString()
        }
    }

    fun decodeSampledBitmapFromResource(
        res: Resources,
        resId: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeResource(res, resId, this)

            // Calculate inSampleSize
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            inJustDecodeBounds = false

            BitmapFactory.decodeResource(res, resId, this)
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
