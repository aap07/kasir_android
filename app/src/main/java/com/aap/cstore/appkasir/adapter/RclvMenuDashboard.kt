package com.aap.cstore.appkasir.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.activity.*
import com.aap.cstore.appkasir.models.Menu
import kotlinx.android.synthetic.main.layout_item_menu.view.*

/*Adapter recycler view untuk menu dashboard*/
class RclvMenuDashboard(val context: Context, var listItemMenu: MutableList<Menu>) :
    RecyclerView.Adapter<RclvMenuDashboard.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(menu: Menu, context: Context) {
            itemView.iconMenu.setImageDrawable(context.getDrawable(menu.icon!!))
            itemView.namaMenu.text = menu.nama
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_menu, parent, false)
        )
    }

    override fun getItemCount(): Int = listItemMenu.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItemMenu[position]
        holder.onBind(item, context)
        holder.itemView.setOnClickListener {
            when(item.icon){
                R.drawable.ic_list_kategory ->{
                    context.startActivity(Intent(context, ActivityMenuKategori::class.java))
                }
                R.drawable.ic_list_produk ->{
                    context.startActivity(Intent(context, ActivityMenuProduk::class.java))
                }
                R.drawable.ic_dinein_method ->{
                    context.startActivity(Intent(context, ActivityMenuDinein::class.java))
                }
                R.drawable.ic_take_away_method ->{
                    context.startActivity(Intent(context, ActivityMenuTakeAway::class.java))
                }
                R.drawable.ic_payment_method ->{
                    context.startActivity(Intent(context, ActivityMenuPayment::class.java))
                }
                R.drawable.ic_add_user ->{
                    context.startActivity(Intent(context, ActivityMenuPegawai::class.java))
                }
                R.drawable.ic_profile ->{
                    context.startActivity(Intent(context,ActivityProfileUser::class.java))
                }
                R.drawable.ic_store ->{
                    context.startActivity(Intent(context,ActivityProfileToko::class.java))
                }
                R.drawable.ic_laporan ->{
                    context.startActivity(Intent(context,ActivityMenuLaporan::class.java))
                }
                R.drawable.ic_info ->{
                    context.startActivity(Intent(context,ActivityInfoApp::class.java))
                }
            }
        }
    }

}