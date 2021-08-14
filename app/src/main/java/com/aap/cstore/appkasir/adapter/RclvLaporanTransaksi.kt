package com.aap.cstore.appkasir.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.activity.CheckoutRecord
import com.aap.cstore.appkasir.models.Transaksi
import com.aap.cstore.appkasir.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.layout_item_laporan.view.*
import java.text.SimpleDateFormat

/*Adapter recycler view untuk menapilkan item laporan penjualan*/
class RclvLaporanTransaksi(val context: Context, var listTransaksi : MutableList<Transaksi>): RecyclerView.Adapter<RclvLaporanTransaksi.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(transaksi: Transaksi) {
            var total = (transaksi.totalPembayaran!! + transaksi.nominalPpn!!)-transaksi.nominalDiskon!!
            val tanggal = transaksi.tanggalTransaksi
            val time = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").parse(tanggal)
            itemView.btnListTrans.visibility = View.GONE
//            itemView.btnSavePdf.visibility = View.GONE
            itemView.btnSavePdf.setText("Delete")
            itemView.tvBlnPenjualan.text = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(time).toString()
            itemView.tvTotalPendapatan.text = total?.let { numberToCurrency(it) }
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
            val inten = Intent(context, CheckoutRecord::class.java)
            inten.putExtra("id",transaksi.id.toString())
            context.startActivity(inten)
        }
        holder.itemView.btnSavePdf.setOnClickListener {
            hapusTrans(transaksi)
        }
    }

    private fun hapusTrans(transaksi: Transaksi){
        MaterialAlertDialogBuilder(context).setTitle("Hapus").setMessage("Apakah anda yakin ingin menghapus?")
            .setPositiveButton("Hapus", DialogInterface.OnClickListener { dialogInterface, i ->
                listTransaksi.removeAt(listTransaksi.indexOf(transaksi))
                transaksi.delete()
                notifyDataSetChanged()
            })
            .setNegativeButton("Batal", DialogInterface.OnClickListener { dialogInterface, i ->  dialogInterface.dismiss()})
            .show()
    }
}