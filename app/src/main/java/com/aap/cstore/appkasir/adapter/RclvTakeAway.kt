package com.aap.cstore.appkasir.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.activity.ListTransaksi
import com.aap.cstore.appkasir.models.Layanan
import com.aap.cstore.appkasir.models.Orderan
import kotlinx.android.synthetic.main.layout_item_layanan.view.*

/*Adapter recycler view untuk menapilkan item kategori*/
class RclvTakeAway(val context: Context, var listLayanan : MutableList<Layanan>, var listOrderan: Orderan): RecyclerView.Adapter<RclvTakeAway.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(layanan: Layanan, orderan: Orderan, context: Context){
            itemView.tvNamaMeja.setText(layanan.nama)
            itemView.tvPengunjung.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_layanan, parent, false))
    }

    override fun getItemCount(): Int = listLayanan.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layanan = listLayanan[position]
        val orderan = listOrderan
        holder.bind(layanan, orderan, context)
        holder.itemView.setOnClickListener {
            val inten = Intent(context, ListTransaksi::class.java)
            inten.putExtra("layananId", layanan.id)
            inten.putExtra("orderanId", orderan.id)
            context.startActivity(inten)
        }
    }
}