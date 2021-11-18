package com.aap.cstore.appkasir.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.activity.ActivityTambahProduk
import com.aap.cstore.appkasir.models.Produk
import com.aap.cstore.appkasir.models.User
import com.aap.cstore.appkasir.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import com.itextpdf.text.factories.GreekAlphabetFactory.getString
import kotlinx.android.synthetic.main.layout_item_produk.view.*
import java.util.*

/*Adapter recycler view untuk menapilkan item produk*/
class RclvProduk(val context: Context, private var listProduk : MutableList<Produk>, var  listUser: User): RecyclerView.Adapter<RclvProduk.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(produk: Produk) {
            val sold: String = itemView.context.getString(R.string.sold)
            itemView.tvNamaProduk.text = produk.nama
            itemView.tvKategori.text = produk.kategori?.nama
            itemView.tvHargaProduk.text = numberToCurrency(produk.harga!!)
            itemView.tvTerjual.text = "$sold " + produk.totalTerjual.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_produk, parent, false)
        )
    }

    override fun getItemCount(): Int = listProduk.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProduk[position]
        val user = listUser
        holder.bind(produk)
        if (user.role.equals(User.userSysAdmin) || user.role.equals(User.userSysSuperAdmin)) {
            holder.itemView.setOnClickListener {
                val popupMenu = PopupMenu(context, it)
                popupMenu.menuInflater.inflate(R.menu.menu_edit_item, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menuEdit -> editItem(produk)
                        R.id.menuHapus -> hapusItem(produk)
                    }
                    return@setOnMenuItemClickListener true
                }
                popupMenu.show()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun hapusItem(produk: Produk) {
        MaterialAlertDialogBuilder(context).setTitle(R.string.delete)
            .setMessage(R.string.sure_to_delete)
            .setPositiveButton(R.string.delete) { dialogInterface, i ->
                listProduk.removeAt(listProduk.indexOf(produk))
                produk.delete()
                notifyDataSetChanged()
            }
            .setNegativeButton(
                R.string.cancel
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            .show()

    }

    private fun editItem(produk: Produk) {
        val intent = Intent(context, ActivityTambahProduk::class.java)
        intent.putExtra("produk", produk.id)
        intent.putExtra("update", true)
        context.startActivity(intent)
    }

}

