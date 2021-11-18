package com.aap.cstore.appkasir.adapter

import android.content.Context
import android.content.DialogInterface
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.activity.ListTransaksi
import com.aap.cstore.appkasir.models.ItemTransaksi
import com.aap.cstore.appkasir.models.Produk
import com.aap.cstore.appkasir.models.Profile
import com.aap.cstore.appkasir.models.Transaksi
import com.aap.cstore.appkasir.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.layout_item_keranjang.view.*

/*Adapter recycler view untuk menampilkan item transaksi*/
class RclvItemTransaksi(val context: Context, var listItemTransaksi: MutableList<ItemTransaksi>):RecyclerView.Adapter<RclvItemTransaksi.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(itemTransaksi: ItemTransaksi, context: Context) {
            itemView.tvNamaProduk.setText(itemTransaksi.namaProduk)
            itemView.tvKategori.setText(itemTransaksi.kategori)
            itemView.tvHargaProduk.setText(
                numberToCurrency(itemTransaksi.hargaProduk!!)
            )
            itemView.tvJumlah.setText(itemTransaksi.jumlah.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_keranjang, parent, false))
    }

    override fun getItemCount(): Int = listItemTransaksi.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItemTransaksi[position]
        val produk = SugarRecord.findById(Produk::class.java, item.produkId)
        val transaksi = SugarRecord.findById(Transaksi::class.java,item.idTransaksi)
        val profile = SugarRecord.listAll(Profile::class.java).firstOrNull()
        holder.onBind(item, context)
        /*Menghapus produk di keranjang*/
        holder.itemView.btnHapus.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle("Hapus")
                .setMessage("Apakah anda yakin ingin menghapus?")
                .setPositiveButton("Hapus", DialogInterface.OnClickListener { dialogInterface, i ->
//                    produk.totalTerjual = produk.totalTerjual?.minus(item.jumlah!!)
//                    produk.save()
                    transaksi.totalPembayaran = transaksi.totalPembayaran?.minus(item.jumlah!!.times(item.hargaProduk!!))
                    if (profile?.statusPpn == true) {
                        transaksi.nominalPpn =
                            (profile.ppn!! / 100) * transaksi.totalPembayaran!!
                    }
                    transaksi.save()
                    item.delete()
                    dialogInterface.dismiss()
                    listItemTransaksi.removeAt(position)
                    notifyDataSetChanged()
                })
                .setNegativeButton(
                    "Batal",
                    DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                .show()
        }
        /*Menambah jumlah item*/

        holder.itemView.btnPlus.setOnClickListener {
            holder.itemView.tvJumlah.setText((holder.itemView.tvJumlah.text.toString().toInt()+1).toString())
            transaksi.totalPembayaran = transaksi.totalPembayaran?.plus(item.hargaProduk!!.times(1))
            transaksi.save()
            item.jumlah = holder.itemView.tvJumlah.text.toString().toInt()
            item.save()
//            produk.totalTerjual = produk.totalTerjual?.plus(1)
//            produk.save()
            notifyDataSetChanged()
        }
        /*Mengurangi jumlah item*/
        holder.itemView.btnMinus.setOnClickListener {
            if(holder.itemView.tvJumlah.text.toString().toInt() != 1){
                holder.itemView.tvJumlah.setText((holder.itemView.tvJumlah.text.toString().toInt()-1).toString())
                transaksi.totalPembayaran = transaksi.totalPembayaran?.minus(item.hargaProduk!!.times(1))
                transaksi.save()
                item.jumlah = holder.itemView.tvJumlah.text.toString().toInt()
                item.save()
//                produk.totalTerjual = produk.totalTerjual?.minus(1)
//                produk.save()
                notifyDataSetChanged()
            }
        }

    }

}