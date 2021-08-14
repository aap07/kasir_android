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
import com.aap.cstore.appkasir.activity.ActivityTambahKategori
import com.aap.cstore.appkasir.models.Kategori
import com.aap.cstore.appkasir.models.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.layout_item_kategori.view.*

/*Adapter recycler view untuk menapilkan item kategori*/
class RclvKategori(val context: Context,var listKategori : MutableList<Kategori>, var listUser: User): RecyclerView.Adapter<RclvKategori.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(kategori: Kategori, user: User, context: Context){
            itemView.tvKategori.setText(kategori.nama)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_kategori, parent, false))
    }

    override fun getItemCount(): Int = listKategori.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val kategori = listKategori[position]
        val user = listUser
        holder.bind(kategori, user, context)
        if (user.role.equals(User.userSysAdmin) || user.role.equals(User.userSysSuperAdmin)) {
            holder.itemView.setOnClickListener {
                val popupMenu = PopupMenu(context, it)
                popupMenu.menuInflater.inflate(R.menu.menu_edit_item, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menuEdit -> editItem(kategori)
                        R.id.menuHapus -> hapusItem(kategori)
                    }
                    return@setOnMenuItemClickListener true
                }
                popupMenu.show()
            }
        }
    }

    private fun hapusItem(kategori: Kategori) {
        MaterialAlertDialogBuilder(context).setTitle("Hapus").setMessage("Apakah anda yakin ingin menghapus?")
            .setPositiveButton("Hapus", DialogInterface.OnClickListener { dialogInterface, i ->
                listKategori.remove(kategori)
                kategori.delete()
                notifyDataSetChanged()
            })
            .setNegativeButton("Batal", DialogInterface.OnClickListener { dialogInterface, i ->  dialogInterface.dismiss()})
            .show()
    }

    private fun editItem(kategori: Kategori) {
        val intent = Intent(context, ActivityTambahKategori::class.java)
        intent.putExtra("kategori", kategori.id)
        intent.putExtra("update", true)
        context.startActivity(intent)
    }
}