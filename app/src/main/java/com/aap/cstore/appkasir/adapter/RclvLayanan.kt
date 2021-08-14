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
import com.aap.cstore.appkasir.activity.ActivityTambahLayanan
import com.aap.cstore.appkasir.models.Layanan
import com.aap.cstore.appkasir.models.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.layout_item_layanan.view.*

/*Adapter recycler view untuk menapilkan item kategori*/
class RclvLayanan(val context: Context, var listLayanan : MutableList<Layanan>, var listUser:User): RecyclerView.Adapter<RclvLayanan.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(layanan: Layanan, user: User, context: Context){
            itemView.tvNamaMeja.setText(layanan.nama)
            itemView.tvPengunjung.setText("Total " + layanan.totalLayanan.toString() + " Take Away")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_layanan, parent, false))
    }

    override fun getItemCount(): Int = listLayanan.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layanan = listLayanan[position]
        val user = listUser
        holder.bind(layanan, user, context)
        if (user.role.equals(User.userSysAdmin) || user.role.equals(User.userSysSuperAdmin)) {
            holder.itemView.setOnClickListener {
                val popupMenu = PopupMenu(context, it)
                popupMenu.menuInflater.inflate(R.menu.menu_edit_item, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menuEdit -> editItem(layanan)
                        R.id.menuHapus -> hapusItem(layanan)
                    }
                    return@setOnMenuItemClickListener true
                }
                popupMenu.show()
            }
        }
    }

    private fun hapusItem(layanan: Layanan) {
        MaterialAlertDialogBuilder(context).setTitle("Hapus").setMessage("Apakah anda yakin ingin menghapus?")
            .setPositiveButton("Hapus", DialogInterface.OnClickListener { dialogInterface, i ->
                listLayanan.remove(layanan)
                layanan.delete()
                notifyDataSetChanged()
            })
            .setNegativeButton("Batal", DialogInterface.OnClickListener { dialogInterface, i ->  dialogInterface.dismiss()})
            .show()
    }

    private fun editItem(layanan: Layanan) {
        val intent = Intent(context, ActivityTambahLayanan::class.java)
        intent.putExtra("layanan", layanan.id)
        intent.putExtra("update", true)
        context.startActivity(intent)
    }
}