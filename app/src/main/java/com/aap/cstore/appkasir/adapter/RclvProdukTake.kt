package com.aap.cstore.appkasir.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.activity.ListTransaksi
import com.aap.cstore.appkasir.activity.ProdukTransaksi
import com.aap.cstore.appkasir.models.*
import com.aap.cstore.appkasir.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.layout_item_produk.view.*
import kotlinx.android.synthetic.main.layout_total_order.view.*
import java.text.SimpleDateFormat
import java.util.*

/*Adapter recycler view untuk menapilkan item produk*/
class RclvProdukTake :
    RecyclerView.Adapter<RclvProdukTake.ViewHolder> {
    val context: Context
    var listProduk: MutableList<Produk>
    var listLayanan: Layanan
    val order: Boolean
    var saveListProduk: MutableList<Produk>

    constructor(
        context: Context,
        listProduk: MutableList<Produk>,
        listLayanan: Layanan,
        sort: Boolean,
        order: Boolean
    ) : super() {
        this.context = context
        this.listProduk = if (sort) listProduk.sortedBy { it.nama } as MutableList<Produk> else listProduk
        this.listLayanan = listLayanan
        this.order = order
        this.saveListProduk = listProduk
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(produk: Produk, layanan: Layanan, context: Context) {
            itemView.tvNamaProduk.setText(produk.nama)
            itemView.tvKategori.setText(produk.kategori?.nama)
            itemView.tvHargaProduk.setText(
                numberToCurrency(produk.harga!!)
            )
            itemView.tvTerjual.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_produk, parent, false)
        )
    }

    override fun getItemCount(): Int = listProduk.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProduk[position]
        val layanan = listLayanan
        holder.bind(produk, layanan, context)
        holder.itemView.setOnClickListener {
            val activity = context as ProdukTransaksi
            val view = LayoutInflater.from(context).inflate(R.layout.layout_total_order, null, false)
            MaterialAlertDialogBuilder(context)
                .setView(view)
                .setNegativeButton(
                    "Batal",
                    DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                .setPositiveButton(
                    "Tambah",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        val transaksi = SugarRecord.find(Transaksi::class.java,"status = ? and id_layanan = ?","1", layanan.id.toString()).firstOrNull()
                        val profile = SugarRecord.listAll(Profile::class.java).firstOrNull()
                        if (transaksi != null) {
                            val itemTransaksi = SugarRecord.find(ItemTransaksi::class.java, "produk_id = ? and id_transaksi = ?", produk.id.toString(), transaksi.id.toString()).firstOrNull()
                            if (itemTransaksi != null) {
                                itemTransaksi.jumlah = itemTransaksi.jumlah?.plus(
                                    view.tvJumlah.text.toString().toInt()
                                )
                                itemTransaksi.save()
                                transaksi.totalPembayaran = transaksi.totalPembayaran?.plus(
                                    produk.harga?.times(itemTransaksi.jumlah!!)!!
                                )
                                if (profile?.statusPpn == true) {
                                    transaksi.nominalPpn =
                                        (profile.ppn!! / 100) * transaksi.totalPembayaran!!
                                }
                                transaksi.save()
                            } else {
                                val item = ItemTransaksi(
                                    jumlah = view.tvJumlah.text.toString().toInt(),
                                    idTransaksi = transaksi.id,
                                    namaProduk = produk.nama,
                                    hargaProduk = produk.harga,
                                    produkId = produk.id,
                                    kategori = produk.kategori?.nama
                                )
                                item.save()
                                transaksi.totalPembayaran =
                                    transaksi.totalPembayaran?.plus(produk.harga?.times(item.jumlah!!)!!)
                                if (profile?.statusPpn == true) {
                                    transaksi.nominalPpn =
                                        (profile.ppn!! / 100) * transaksi.totalPembayaran!!
                                }
                                transaksi.save()
                            }
                        }
                        activity.setBadgeKeranjang()
                    }).show()

            view.btnPlus.setOnClickListener {
                view.tvJumlah.text = (view.tvJumlah.text.toString().toInt() + 1).toString()
            }
            view.btnMinus.setOnClickListener {
                if (view.tvJumlah.text.toString().toInt() != 1) {
                    view.tvJumlah.text =
                        (view.tvJumlah.text.toString().toInt() - 1).toString()
                }
            }
        }
    }

    public fun sortItem(sort: String) {
        when (sort) {
            "Asc" -> {
                listProduk.sortBy { it.nama }
                notifyDataSetChanged()
            }
            "Dsc" -> {
                listProduk.sortBy { it.nama }
                listProduk.reverse()
                notifyDataSetChanged()
            }
        }
    }

    public fun searchItem(search: String) {
        if (search.isNotEmpty()) {
            val search = saveListProduk.filter { produk ->
                produk.nama!!.trim().toLowerCase().contains(search.trim().toLowerCase()) || produk.kategori?.nama!!.trim().toLowerCase().contains(search.trim().toLowerCase())
            }
            listProduk = search as MutableList<Produk>
            notifyDataSetChanged()
        } else {
            listProduk = saveListProduk
            notifyDataSetChanged()
        }
    }
}

