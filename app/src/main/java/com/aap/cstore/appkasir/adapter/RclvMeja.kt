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
import com.aap.cstore.appkasir.activity.ActivityTambahMeja
import com.aap.cstore.appkasir.models.Meja
import com.aap.cstore.appkasir.models.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.layout_item_meja.view.*

/*Adapter recycler view untuk menapilkan item kategori*/
class RclvMeja(val context: Context, var listMeja : MutableList<Meja>, var listUser:User): RecyclerView.Adapter<RclvMeja.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(meja: Meja, user: User, context: Context){
            itemView.tvNamaMeja.setText("Meja "+meja.nama)
            itemView.tvPengunjung.setText("Pengunjung " + meja.totalPengunjung.toString())
            itemView.tvStatusMeja.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_meja, parent, false))
    }

    override fun getItemCount(): Int = listMeja.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meja = listMeja[position]
        val user = listUser
        holder.bind(meja, user, context)
        if (user.role.equals(User.userSysAdmin) || user.role.equals(User.userSysSuperAdmin)) {
            holder.itemView.setOnClickListener {
                val popupMenu = PopupMenu(context, it)
                popupMenu.menuInflater.inflate(R.menu.menu_edit_item, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menuEdit -> editItem(meja)
                        R.id.menuHapus -> hapusItem(meja)
                    }
                    return@setOnMenuItemClickListener true
                }
                popupMenu.show()
            }
        }
    }

    private fun hapusItem(meja: Meja) {
        MaterialAlertDialogBuilder(context).setTitle("Hapus").setMessage("Apakah anda yakin ingin menghapus?")
            .setPositiveButton("Hapus", DialogInterface.OnClickListener { dialogInterface, i ->
                listMeja.remove(meja)
                meja.delete()
                notifyDataSetChanged()
            })
            .setNegativeButton("Batal", DialogInterface.OnClickListener { dialogInterface, i ->  dialogInterface.dismiss()})
            .show()
    }

    private fun editItem(meja: Meja) {
        val intent = Intent(context, ActivityTambahMeja::class.java)
        intent.putExtra("meja", meja.id)
        intent.putExtra("update", true)
        context.startActivity(intent)
    }
}