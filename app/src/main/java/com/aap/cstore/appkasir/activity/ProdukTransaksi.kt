package com.aap.cstore.appkasir.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.adapter.RclvProdukDine
import com.aap.cstore.appkasir.adapter.RclvProdukTake
import com.aap.cstore.appkasir.models.*
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.produk_transaksi.*

class ProdukTransaksi : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.produk_transaksi)
        tvEmpty.setText("Belum Ada Daftar Produk")
        val idMeja = intent.getLongExtra("mejaId",-1)
        if (idMeja > 0) {
            val listMeja = SugarRecord.findById(Meja::class.java, idMeja)
            val namaMeja = listMeja.nama.toString()
            /*Setting toolbar*/
            setToolbar(this, "Produk Pesanan Meja " + namaMeja)
            /*Setting list item kategori*/
            setToRecyclerView()
        }
        val idLayanan = intent.getLongExtra("layananId",-1)
        if (idLayanan > 0) {
            val listLayanan = SugarRecord.findById(Layanan::class.java, idLayanan)
            val namaLayanan = listLayanan.nama.toString()
            /*Setting toolbar*/
            setToolbar(this, "Produk Pesanan " + namaLayanan)
            /*Setting list item kategori*/
            setToRecyclerView()
        }

        btnKeranjang.setOnClickListener {
            val idMeja = intent.getLongExtra("mejaId",-1)
            if (idMeja > 0) {
                val inten = Intent(this, ListTransaksi::class.java)
                inten.putExtra("mejaId",idMeja)
                startActivity(inten)
                finish()
            }
            val idLayanan = intent.getLongExtra("layananId",-1)
            if (idLayanan > 0) {
                val inten = Intent(this, ListTransaksi::class.java)
                inten.putExtra("layananId",idLayanan)
                startActivity(inten)
                finish()
            }
        }
        btnSort.setOnClickListener {
            if (btnSort.tag == null || btnSort.tag.equals("Dsc")) {
                btnSort.tag = "Asc"
                btnSort.icon = getDrawable(R.drawable.ic_arrow_downward_24)
                sortItem("Dsc")
            } else {
                btnSort.tag = "Dsc"
                btnSort.icon = getDrawable(R.drawable.ic_arrow_upward_24)
                sortItem("Asc")
            }

        }

        edtPencarian.setOnFocusChangeListener { view, b ->
            if (b) {
                guideline6.setGuidelinePercent(.8f)
            } else {
                guideline6.setGuidelinePercent(.5f)
            }
        }
        /*Fungsi Pencarian*/
        edtPencarian.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                searchItem(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    private fun searchItem(search: String) {
        val adapter = rclv.adapter
        if(adapter != null){
            adapter as RclvProdukDine
            adapter.searchItem(search)
        }
    }

    private fun sortItem(sort: String) {
        val adapter = rclv.adapter
        if(adapter != null){
            adapter as RclvProdukDine
            adapter.sortItem(sort)
        }
    }

    open fun setBadgeKeranjang() {
        val idMeja = intent.getLongExtra("mejaId",-1)
        if (idMeja > 0) {
            val transaksi = SugarRecord.find(Transaksi::class.java, "status = ? and id_meja = ?", "1", idMeja.toString()).firstOrNull()
            if (transaksi != null) {
                val itemTransaksi = SugarRecord.find(ItemTransaksi::class.java, "id_transaksi = ?", transaksi.id.toString())
                var count = 0
                for (item in itemTransaksi) {
                    count += item.jumlah!!
                }
                btnKeranjang.count = count
            } else {
                btnKeranjang.count = 0
            }
        }
        val idLayanan = intent.getLongExtra("layananId",-1)
        if (idLayanan > 0) {
            val transaksi = SugarRecord.find(Transaksi::class.java, "status = ? and id_layanan = ?", "1", idLayanan.toString()).firstOrNull()
            if (transaksi != null) {
                val itemTransaksi = SugarRecord.find(ItemTransaksi::class.java, "id_transaksi = ?", transaksi.id.toString())
                var count = 0
                for (item in itemTransaksi) {
                    count += item.jumlah!!
                }
                btnKeranjang.count = count
            } else {
                btnKeranjang.count = 0
            }
        }
    }

    fun setToRecyclerView() : Boolean{
        val idMeja = intent.getLongExtra("mejaId",-1)
        if (idMeja > 0){
            val listMeja = SugarRecord.findById(Meja::class.java, idMeja)
            val listProduk = SugarRecord.listAll(Produk::class.java)
            if (listProduk.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
            } else {
                tvEmpty.visibility = View.GONE
                val rclvadapter = RclvProdukDine(this, listProduk,listMeja,true,true)
                rclv.apply {
                    adapter = rclvadapter
                    layoutManager = GridLayoutManager(context, calculateNoOfColumns(context,250F))
                    setHasFixedSize(true)
                }
            }
        }
        val idLayanan = intent.getLongExtra("layananId",-1)
        if (idLayanan > 0){
            val listLayanan = SugarRecord.findById(Layanan::class.java, idLayanan)
            val listProduk = SugarRecord.listAll(Produk::class.java)
            if (listProduk.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
            } else {
                tvEmpty.visibility = View.GONE
                val rclvadapter = RclvProdukTake(this, listProduk,listLayanan,true,true)
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