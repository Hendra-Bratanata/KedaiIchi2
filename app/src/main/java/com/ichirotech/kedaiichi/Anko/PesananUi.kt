package com.ichirotech.kedaiichi.Anko

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.ichirotech.kedaiichi.R
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView

class PesananUi<T> : AnkoComponent<T> {
    override fun createView(ui: AnkoContext<T>): View {
        return with(ui) {
            cardView {
                lparams(width = matchParent, height = wrapContent) {
                    margin = dip(5)

                }
                radius = 30f
                linearLayout {
                    orientation = LinearLayout.VERTICAL
                    linearLayout {
                        orientation = LinearLayout.HORIZONTAL
                        padding = dip(5)

                        imageView {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            id = R.id.imgPesanan
                        }.lparams(width = dip(80), height = dip(60))

                        linearLayout {
                            orientation = LinearLayout.VERTICAL
                            textView {
                                id = R.id.tv_pesanan
                                textSize = 20f
                                textColor = Color.BLACK
                            }.lparams(matchParent, wrapContent) {
                                topMargin = dip(5)
                                leftMargin = dip(5)
                        }
                            textView {
                                id = R.id.tvHarga
                                textSize = 20f
                                textColor = Color.BLACK
                                Typeface.BOLD
                            }.lparams(matchParent, wrapContent) {
                                topMargin = dip(5)
                                leftMargin = dip(5)
                            }


                        }

                        textView {
                            id = R.id.jumlah
                            textSize = 20f
                            textColor = Color.BLACK

                        }
                    }.lparams(dip(400), wrapContent) {
                        margin = dip(10)
                    }

                }
            }
        }
    }
}