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
import com.aap.cstore.appkasir.activity.ActivityTambahPayMethod
import com.aap.cstore.appkasir.models.MetodePembayaran
import com.aap.cstore.appkasir.models.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.layout_item_pay_method.view.*

/*Adapter recycler view untuk menapilkan item kategori*/
class RclvPayMethod(val context: Context, var listPayMethod : MutableList<MetodePembayaran>, var listUser:User): RecyclerView.Adapter<RclvPayMethod.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(payMethod: MetodePembayaran, user: User, context: Context){
            itemView.tvNamaMeja.setText(payMethod.nama)
            itemView.tvPengunjung.setText("Total " + payMethod.totalMetode.toString() + " Pay Method")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_pay_method, parent, false))
    }

    override fun getItemCount(): Int = listPayMethod.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val payMethod = listPayMethod[position]
        val user = listUser
        holder.bind(payMethod, user, context)
        if (user.role.equals(User.userSysSuperAdmin)) {
            holder.itemView.setOnClickListener {
                val popupMenu = PopupMenu(context, it)
                popupMenu.menuInflater.inflate(R.menu.menu_edit_item, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menuEdit -> editItem(payMethod)
                        R.id.menuHapus -> hapusItem(payMethod)
                    }
                    return@setOnMenuItemClickListener true
                }
                popupMenu.show()
            }
        }
    }

    private fun hapusItem(payMethod: MetodePembayaran) {
        MaterialAlertDialogBuilder(context).setTitle("Hapus").setMessage("Apakah anda yakin ingin menghapus?")
            .setPositiveButton("Hapus", DialogInterface.OnClickListener { dialogInterface, i ->
                listPayMethod.remove(payMethod)
                payMethod.delete()
                notifyDataSetChanged()
            })
            .setNegativeButton("Batal", DialogInterface.OnClickListener { dialogInterface, i ->  dialogInterface.dismiss()})
            .show()
    }

    private fun editItem(payMethod: MetodePembayaran) {
        val intent = Intent(context, ActivityTambahPayMethod::class.java)
        intent.putExtra("payMethod", payMethod.id)
        intent.putExtra("update", true)
        context.startActivity(intent)
    }
}