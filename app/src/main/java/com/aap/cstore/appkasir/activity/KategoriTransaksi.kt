package com.aap.cstore.appkasir.activity

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.adapter.RclvKategoriDine
import com.aap.cstore.appkasir.adapter.RclvKategoriTake
import com.aap.cstore.appkasir.models.Kategori
import com.aap.cstore.appkasir.models.Layanan
import com.aap.cstore.appkasir.models.Meja
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_menu.*


class KategoriTransaksi : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu)
        tvEmpty.setText("Belum Ada Daftar Kategori")
        btnAdd.visibility = View.GONE
        val idMeja = intent.getLongExtra("mejaId",-1)
        if (idMeja > 0) {
            val listMeja = SugarRecord.findById(Meja::class.java, idMeja)
            val namaMeja = listMeja.nama
            /*Setting toolbar*/
            setToolbar(this, "Kategori Pesanan Meja " + namaMeja)
            /*Setting list item kategori*/
            setToRecyclerView()
        }
        val idLayanan = intent.getLongExtra("layananId", -1)
        if (idLayanan > 0) {
            val listLayanan = SugarRecord.findById(Layanan::class.java, idLayanan)
            val namaLayanan = listLayanan.nama.toString()
            /*Setting toolbar*/
            setToolbar(this, "Kategori Pesanan " + namaLayanan)
            /*Setting list item kategori*/
            setToRecyclerView()
        }
    }


    fun setToRecyclerView(): Boolean {
        val idLayanan = intent.getLongExtra("layananId",-1)
        val listLayanan = SugarRecord.findById(Layanan::class.java, idLayanan)
        if (idLayanan > 0){
            val listKategori = SugarRecord.listAll(Kategori::class.java)
            if (listKategori.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
            } else {
                tvEmpty.visibility = View.GONE
                val rclvadapter = RclvKategoriTake(this, listKategori,listLayanan)
                rclv.apply {
                    adapter = rclvadapter
                    layoutManager = GridLayoutManager(context, calculateNoOfColumns(context,250F))
                    setHasFixedSize(true)
                }
            }
        }
        val idMeja = intent.getLongExtra("mejaId",-1)
        val listMeja = SugarRecord.findById(Meja::class.java, idMeja)
        if (idMeja > 0){
            val listKategori = SugarRecord.listAll(Kategori::class.java)
            if (listKategori.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
            } else {
                tvEmpty.visibility = View.GONE
                val rclvadapter = RclvKategoriDine(this, listKategori,listMeja)
                rclv.apply {
                    adapter = rclvadapter
                    layoutManager = GridLayoutManager(context, calculateNoOfColumns(context,250F))
                    setHasFixedSize(true)
                }
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        setToRecyclerView()
    }
}