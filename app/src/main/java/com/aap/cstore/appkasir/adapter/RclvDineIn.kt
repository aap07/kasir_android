package com.aap.cstore.appkasir.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.activity.ListTransaksi
import com.aap.cstore.appkasir.models.Meja
import com.aap.cstore.appkasir.models.Orderan
import kotlinx.android.synthetic.main.layout_item_meja.view.*

/*Adapter recycler view untuk menapilkan item kategori*/
class RclvDineIn(val context: Context, var listMeja : MutableList<Meja>, var listOrderan: Orderan): RecyclerView.Adapter<RclvDineIn.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(meja: Meja, orderan: Orderan, context: Context){
            itemView.tvNamaMeja.setText("Meja "+meja.nama)
            itemView.tvPengunjung.visibility = View.GONE
            if (meja.status == false) {
                itemView.tvStatusMeja.setText("Available")
                itemView.bg_meja.setBackgroundColor(Color.rgb(61,255,87))
            }else{
                itemView.tvStatusMeja.setText("Not Available")
                itemView.bg_meja.setBackgroundColor(Color.rgb(255,61,61))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_meja, parent, false))
    }

    override fun getItemCount(): Int = listMeja.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meja = listMeja[position]
        val orderan = listOrderan
        holder.bind(meja, orderan, context)
        holder.itemView.setOnClickListener {
            val inten = Intent(context, ListTransaksi::class.java)
            inten.putExtra("mejaId", meja.id)
            inten.putExtra("orderanId", orderan.id)
            context.startActivity(inten)
        }
    }
}