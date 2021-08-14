package com.aap.cstore.appkasir.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.activity.TransaksiOrderan
import com.aap.cstore.appkasir.models.Orderan
import kotlinx.android.synthetic.main.layout_item_orderan.view.*

/*Adapter recycler view untuk menapilkan item kategori*/
class RclvOrderan(val context: Context, var listOrderan : MutableList<Orderan>): RecyclerView.Adapter<RclvOrderan.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(orderan: Orderan){
            itemView.tvKategori.setText(orderan.nama)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_orderan, parent, false))
    }

    override fun getItemCount(): Int = listOrderan.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderan = listOrderan[position]
        holder.bind(orderan)
        holder.itemView.setOnClickListener {
            val inten = Intent(context, TransaksiOrderan::class.java)
            inten.putExtra("method",orderan.id)
            context.startActivity(inten)
        }
//        if (orderan.nama.equals("Take Away")) {
//            holder.itemView.setOnClickListener {
//                val inten = Intent(context, TransaksiOrderan::class.java)
//                inten.putExtra("method","Take Away")
//                context.startActivity(inten)
//            }
//        }else if (orderan.nama.equals("Dine In")) {
//            holder.itemView.setOnClickListener {
//                val inten = Intent(context, TransaksiOrderan::class.java)
//                inten.putExtra("method","Dine In")
//                context.startActivity(inten)
//            }
//        }
    }
}