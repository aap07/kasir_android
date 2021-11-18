package com.aap.cstore.appkasir.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.activity.ActivityTambahKategori
import com.aap.cstore.appkasir.databinding.LayoutItemKategoriBinding
import com.aap.cstore.appkasir.models.Kategori
import com.aap.cstore.appkasir.models.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/*Adapter recycler view untuk menapilkan item kategori*/
class RclvKategori(val context: Context, private var listKategori : MutableList<Kategori>, var listUser: User): RecyclerView.Adapter<RclvKategori.ViewHolder>() {
    class ViewHolder(private val binding: LayoutItemKategoriBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(kategori: Kategori){
            binding.tvKategori.text = kategori.nama
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutItemKategoriBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = listKategori.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val kategori = listKategori[position]
        val user = listUser
        holder.bind(kategori)
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

    @SuppressLint("NotifyDataSetChanged")
    private fun hapusItem(kategori: Kategori) {
        MaterialAlertDialogBuilder(context).setTitle(R.string.delete).setMessage(R.string.sure_to_delete)
            .setPositiveButton(R.string.delete) { dialogInterface, i ->
                listKategori.remove(kategori)
                kategori.delete()
                notifyDataSetChanged()
            }
            .setNegativeButton(R.string.cancel) { dialogInterface, i -> dialogInterface.dismiss() }
            .show()
    }

    private fun editItem(kategori: Kategori) {
        val intent = Intent(context, ActivityTambahKategori::class.java)
        intent.putExtra("kategori", kategori.id)
        intent.putExtra("update", true)
        context.startActivity(intent)
    }
}