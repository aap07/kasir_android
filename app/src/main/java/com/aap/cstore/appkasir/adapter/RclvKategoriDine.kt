package com.aap.cstore.appkasir.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.activity.ProdukTransaksi
import com.aap.cstore.appkasir.models.Kategori
import com.aap.cstore.appkasir.models.Meja
import kotlinx.android.synthetic.main.layout_item_kategori.view.*

/*Adapter recycler view untuk menapilkan item kategori*/
class RclvKategoriDine(val context: Context, var listKategori : MutableList<Kategori>, var listMeja: Meja): RecyclerView.Adapter<RclvKategoriDine.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(kategori: Kategori, meja: Meja, context: Context){
            itemView.tvKategori.setText(kategori.nama)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_kategori, parent, false))
    }

    override fun getItemCount(): Int = listKategori.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val kategori = listKategori[position]
        val meja = listMeja
        holder.bind(kategori, meja, context)
        holder.itemView.setOnClickListener {
            val inten = Intent(context, ProdukTransaksi::class.java)
            inten.putExtra("mejaId", meja.id)
            context.startActivity(inten)
        }
    }
}