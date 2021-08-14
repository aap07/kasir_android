package com.aap.cstore.appkasir.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.activity.ActivityMenuLaporan
import com.aap.cstore.appkasir.activity.DetailBulanan
import com.aap.cstore.appkasir.activity.LaporanHarian
import com.aap.cstore.appkasir.models.Transaksi
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.layout_item_laporan.view.*
import java.text.SimpleDateFormat

/*Adapter recycler view untuk menapilkan item laporan penjualan*/
class RclvLaporan(val context: Context, var listTransaksi : MutableList<Transaksi>): RecyclerView.Adapter<RclvLaporan.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(transaksi: Transaksi) {
            val date = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(transaksi.tanggalTransaksi)
            val iTransaksi = SugarRecord.listAll(Transaksi::class.java).filter { l -> l.status == false && l.tanggalTransaksi?.substring(2, l.tanggalTransaksi?.indexOf(" ")!!).equals(transaksi.tanggalTransaksi?.substring(2, transaksi.tanggalTransaksi?.indexOf(" ")!!)!!) }
            var total = 0.0
            var ppn = 0.0
            var diskon = 0.0
            if(iTransaksi.isNotEmpty()){
                for (item in iTransaksi){
                    total += item.totalPembayaran!!
                    ppn += item.nominalPpn!!
                    diskon += item.nominalDiskon!!
                }
            }
            itemView.btnListTrans.setText("Harian")
            itemView.tvBlnPenjualan.text = SimpleDateFormat("MMMM").format(date).toString()
            itemView.tvTotalPendapatan.text = numberToCurrency((total+ppn)-diskon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_laporan, parent, false))
    }

    override fun getItemCount(): Int = listTransaksi.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaksi = listTransaksi[position]
        holder.bind(transaksi)
        holder.itemView.btnDetail.setOnClickListener {
            val inten = Intent(context, DetailBulanan::class.java)
            inten.putExtra("bulan",transaksi.tanggalTransaksi?.substring(3, transaksi.tanggalTransaksi?.indexOf(" ")!!))
            context.startActivity(inten)
        }
        holder.itemView.btnSavePdf.setOnClickListener {
            (context as ActivityMenuLaporan).savePDF(transaksi)
        }
        holder.itemView.btnListTrans.setOnClickListener {
            val inten = Intent(context, LaporanHarian::class.java)
            inten.putExtra("bulan",transaksi.tanggalTransaksi?.substring(3, transaksi.tanggalTransaksi?.indexOf(" ")!!))
            context.startActivity(inten)
        }
    }
}