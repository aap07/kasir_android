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
import com.aap.cstore.appkasir.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.layout_item_keranjang.view.*

/*Adapter recycler view untuk menampilkan item transaksi*/
class RclvItemTransaksi(val context: Context, var listItemTransaksi: MutableList<ItemTransaksi>) :
    RecyclerView.Adapter<RclvItemTransaksi.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(itemTransaksi: ItemTransaksi, context: Context) {
            val activity = context as ListTransaksi
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
        holder.onBind(item, context)
        /*Menghapus produk di keranjang*/
        holder.itemView.btnHapus.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle("Hapus")
                .setMessage("Apakah anda yakin ingin menghapus?")
                .setPositiveButton("Hapus", DialogInterface.OnClickListener { dialogInterface, i ->
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
            item.jumlah = holder.itemView.tvJumlah.text.toString().toInt()
            item.save()
            notifyDataSetChanged()
        }
        /*Mengurangi jumlah item*/
        holder.itemView.btnMinus.setOnClickListener {
            if(holder.itemView.tvJumlah.text.toString().toInt() != 1){
                holder.itemView.tvJumlah.setText((holder.itemView.tvJumlah.text.toString().toInt()-1).toString())
                item.jumlah = holder.itemView.tvJumlah.text.toString().toInt()
                item.save()
                notifyDataSetChanged()
            }
        }

    }

}