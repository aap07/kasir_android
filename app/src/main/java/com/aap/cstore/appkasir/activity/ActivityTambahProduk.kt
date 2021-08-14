package com.aap.cstore.appkasir.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.models.Kategori
import com.aap.cstore.appkasir.models.Produk
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_add.*

class ActivityTambahProduk : AppCompatActivity() {
    lateinit var adapterSpinner : ArrayAdapter<Kategori>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add)
        /*Setting tombol back dan title*/
        setToolbar(this, "Tambah Produk")
        textInputSatu.hint = "Nama Produk"
        textInputDua.visibility = View.GONE
        textInputTiga.visibility = View.GONE
        textInputEmpat.hint = "Harga Produk"
        spn.hint = "Kategori"
        /*Setting adapter spinner*/
        adapterSpinner = ArrayAdapter<Kategori>(this, android.R.layout.simple_list_item_1, SugarRecord.listAll(Kategori::class.java))
        spn.adapter = adapterSpinner

        val update = intent.getBooleanExtra("update", false)
        val produkId = intent.getLongExtra("produk",-1)
        val produk = SugarRecord.findById(Produk::class.java, produkId)

        btnSimpan.setOnClickListener {
            if(checkInput(textInputSatu) && checkInput(spn) && checkInput(textInputEmpat) ){
                if(update){
                    /*Update produk*/
                    updateProduk(produk)
                }else{
                    /*Insert produk*/
                    insertProduk()
                }
            }
        }

        if(produk != null){
            textInputSatu.editText?.setText(produk.nama)
            textInputEmpat.editText?.setText(produk.harga?.toInt().toString())
            spn.selection = adapterSpinner.getPosition(produk.kategori)
        }
    }

    fun insertProduk(){
        val checkProduk = SugarRecord.find(Produk::class.java, "nama = ?", textInputSatu.editText?.text.toString()).firstOrNull()
        if (checkProduk == null) {
            Produk(
                nama = textInputSatu.editText?.text.toString(),
                harga = textInputEmpat.editText?.text.toString().toDouble(),
                kategori = spn.selectedItem as Kategori
            ).save()
            Toast.makeText(this, "Produk berhasil disimpan!", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(this, "Produk sudah ada!", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateProduk(produk: Produk){
        produk.nama = textInputSatu.editText?.text.toString()
        produk.harga = textInputEmpat.editText?.text.toString().toDouble()
        produk.kategori = spn.selectedItem as Kategori
        produk.save()
        Toast.makeText(this, "Produk berhasil disimpan!", Toast.LENGTH_SHORT).show()
        finish()
    }

}