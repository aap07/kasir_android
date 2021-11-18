package com.aap.cstore.appkasir.activity

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.adapter.RclvItemTransaksi
import com.aap.cstore.appkasir.models.*
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.list_transaksi.*
import java.text.SimpleDateFormat
import java.util.*

class ListTransaksi : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.list_transaksi)
        val idMeja = intent.getLongExtra("mejaId",-1)
        val idLayanan = intent.getLongExtra("layananId",-1)
        val idOrderan = intent.getLongExtra("orderanId", -1)
        val listOrderan = SugarRecord.findById(Orderan::class.java,idOrderan)
        if (idMeja > 0){
            val listMeja = SugarRecord.findById(Meja::class.java, idMeja)
            val namaMeja = listMeja.nama
            /*Setting toolbar*/
            setToolbar(this, "List Pesanan Meja " + namaMeja)
            setToRecyclerView()
            btnAdd.setOnClickListener {
                listMeja.status = true
                listMeja.save()
                val transaksi = SugarRecord.find(Transaksi::class.java,"status = ? and id_meja = ?","1", idMeja.toString()).firstOrNull()
                if ( transaksi == null) {
                    Transaksi(
                        status = true,
                        idOrder = idOrderan,
                        namaOrderan = listOrderan.nama,
                        idMeja = idMeja,
                        namaMeja = namaMeja,
                        tanggalTransaksi = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault()).format(Date().time).toString()
                    ).save()
                    val inten = Intent(this, ProdukTransaksi::class.java)
                    inten.putExtra("mejaId", listMeja.id)
                    startActivity(inten)
                    finish()
                }else{
                    val inten = Intent(this, ProdukTransaksi::class.java)
                    inten.putExtra("mejaId", listMeja.id)
                    startActivity(inten)
                    finish()
                }
            }
            btnBtl.setOnClickListener {
                listMeja.status = false
                listMeja.save()
                val transaksi = SugarRecord.find(Transaksi::class.java,"status = ? and id_meja = ?","1", idMeja.toString()).firstOrNull()
                val itemTransaksi = SugarRecord.find(ItemTransaksi::class.java,"id_transaksi = ?",transaksi?.id.toString())
                val item = itemTransaksi.distinctBy { l -> l.produkId }
                for (it in item){
                    val produk = SugarRecord.findById(Produk::class.java,it.produkId)
                    produk.totalTerjual = produk.totalTerjual?.minus(it.jumlah!!)
                    produk.save()
                }
                for (itTrans in itemTransaksi){
                    itTrans.delete()
                }
                transaksi?.delete()
                finish()
            }
            btnPay.setOnClickListener {
                val inten = Intent(this, CheckTransaksi::class.java)
                inten.putExtra("mejaId", listMeja.id)
                startActivity(inten)
                finish()
            }
        }
        if (idLayanan > 0 ){
            val listLayanan = SugarRecord.findById(Layanan::class.java, idLayanan)
            val namaLayanan = listLayanan.nama
            /*Setting toolbar*/
            setToolbar(this, "List Pesanan " + namaLayanan)
            setToRecyclerView()
            btnAdd.setOnClickListener {
                val transaksi = SugarRecord.find(Transaksi::class.java,"status = ? and id_layanan = ?","1", idLayanan.toString()).firstOrNull()
                if ( transaksi == null) {
                    Transaksi(
                        status = true,
                        idOrder = idOrderan,
                        namaOrderan = listOrderan.nama,
                        idLayanan = idLayanan,
                        namaLayanan = namaLayanan,
                        tanggalTransaksi = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault()).format(Date().time).toString()
                    ).save()
                    val inten = Intent(this, ProdukTransaksi::class.java)
                    inten.putExtra("layananId", listLayanan.id)
                    startActivity(inten)
                    finish()
                }else{
                    val inten = Intent(this, ProdukTransaksi::class.java)
                    inten.putExtra("layananId", listLayanan.id)
                    startActivity(inten)
                    finish()
                }
            }
            btnBtl.setOnClickListener {
                val transaksi = SugarRecord.find(Transaksi::class.java,"status = ? and id_layanan = ?","1", idLayanan.toString()).firstOrNull()
                val itemTransaksi = SugarRecord.find(ItemTransaksi::class.java,"id_transaksi = ?",transaksi?.id.toString())
                val item = itemTransaksi.distinctBy { l -> l.produkId }
                for (it in item){
                    val produk = SugarRecord.findById(Produk::class.java,it.produkId)
                    produk.totalTerjual = produk.totalTerjual?.minus(it.jumlah!!)
                    produk.save()
                }
                for (itTrans in itemTransaksi){
                    itTrans.delete()
                }
                transaksi?.delete()
                finish()

            }
            btnPay.setOnClickListener {
                val inten = Intent(this, CheckTransaksi::class.java)
                inten.putExtra("layananId", listLayanan.id)
                startActivity(inten)
                finish()
            }
        }
    }

    fun setToRecyclerView():Boolean {
        val idMeja = intent.getLongExtra("mejaId",-1)
        if (idMeja > 0) {
            val transaksi = SugarRecord.find(Transaksi::class.java,"status = ? and id_meja = ?","1", idMeja.toString()).firstOrNull()
            if (transaksi != null){
                tvEmpty.visibility = View.GONE
                val itemTransaksi = SugarRecord.find(ItemTransaksi::class.java, "id_transaksi = ?", "${transaksi?.id}")
                rclv.apply {
                    adapter = RclvItemTransaksi(context, itemTransaksi)
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    setHasFixedSize(true)
                }
            }else{
                tvEmpty.visibility = View.VISIBLE
            }
        }
        val idLayanan = intent.getLongExtra("layananId",-1)
        if (idLayanan > 0) {
            val transaksi = SugarRecord.find(Transaksi::class.java,"status = ? and id_layanan = ?","1", idLayanan.toString()).firstOrNull()
            if (transaksi != null){
                tvEmpty.visibility = View.GONE
                val itemTransaksi = SugarRecord.find(ItemTransaksi::class.java, "id_transaksi = ?", "${transaksi?.id}")
                rclv.apply {
                    adapter = RclvItemTransaksi(context, itemTransaksi)
                    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    setHasFixedSize(true)
                }
            }else{
                tvEmpty.visibility = View.VISIBLE
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        setToRecyclerView()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(currentFocus != null){
            this.hideSoftKeyboard()
        }
        return super.dispatchTouchEvent(ev)
    }
}