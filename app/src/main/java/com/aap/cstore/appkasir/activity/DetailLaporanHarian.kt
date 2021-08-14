package com.aap.cstore.appkasir.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.models.ItemTransaksi
import com.aap.cstore.appkasir.models.Transaksi
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.detail_laporan.*
import kotlinx.android.synthetic.main.layout_table_row_detail_laporan.view.*
import java.text.SimpleDateFormat

class DetailLaporanHarian : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.detail_laporan)
        setToolbar(this,"Detail Laporan Harian")

        val tanggal = intent.getStringExtra("tanggal")
        if(tanggal != null){
            val transaksi = SugarRecord.listAll(Transaksi::class.java).filter { l -> l.status == false && l.tanggalTransaksi?.substring(0, l.tanggalTransaksi!!.indexOf(" ")).equals(tanggal) }
            var totItemTransaksi = mutableListOf<ItemTransaksi>()
            var total = 0.0
            var totalPpn = 0.0
            var totalDiskon = 0.0
            var totalTransaksi = 0
            totalTransaksi += transaksi.count()
            if(transaksi.isNotEmpty()){
                for (item in transaksi){
                    val itemTransaksi = SugarRecord.find(ItemTransaksi::class.java,"id_transaksi = ?",item.id.toString())

                    if(itemTransaksi.isNotEmpty()){
                        for (it in itemTransaksi){
                            totItemTransaksi.add(it)
                        }
                    }
                    total += item.totalPembayaran!!
                    totalPpn += item.nominalPpn!!
                    totalDiskon += item.nominalDiskon!!
                }
            }
            val itemTransaksiDistinc = totItemTransaksi.distinctBy { l -> l.produkId }
            for (it in itemTransaksiDistinc){
                val row = LayoutInflater.from(this).inflate(R.layout.layout_table_row_detail_laporan, table, false)
                val count = totItemTransaksi.filter { i -> i.produkId == it.produkId }
                var jml = 0
                count.forEach { s -> jml += s.jumlah!! }
                row.tvNamaProduk.text = it.namaProduk
                row.tvJumlah.text = jml.toString()
                table.addView(row)
            }

            val date = SimpleDateFormat("dd/MM/yyyy").parse(tanggal)
            tvTanggal.text = ": " +SimpleDateFormat("dd MMMM yyyy").format(date)
            tvTotalPendapatan.text = ": " +numberToCurrency(total)
            tvTotalPpn.text = ": " +numberToCurrency(totalPpn)
            tvTotalDiskon.text = ": "+ numberToCurrency(totalDiskon)
            tvTotalProduk.text = ": " +totalTransaksi.toString() + " Transaksi"
        }
    }
}