package com.aap.cstore.appkasir.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.models.Kategori
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_add.*

class ActivityTambahKategori : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add)
        /*Setting tombol back dan title*/
        setToolbar(this, "Tambah Kategori")
        textInputSatu.hint = "Nama Kategori"
        textInputDua.visibility = View.GONE
        textInputTiga.visibility = View.GONE
        textInputEmpat.visibility = View.GONE
        spn.visibility = View.GONE
        val update = intent.getBooleanExtra("update", false)
        val idKategori = intent.getLongExtra("kategori",-1)
        val kategori = SugarRecord.findById(Kategori::class.java,idKategori)
        btnSimpan.setOnClickListener {
            if(checkInput(textInputSatu)){
                if(update){
                    /*Update kategori*/
                    updateKategori(kategori)
                }else{
                    /*Insert kategori*/
                    insertKategori()
                }
            }
        }
        if(kategori != null){
            textInputSatu.editText?.setText(kategori.nama)
        }

    }

    fun insertKategori(){
        val checkKategori = SugarRecord.find(Kategori::class.java, "nama = ?", textInputSatu.editText?.text.toString()).firstOrNull()
        if (checkKategori == null) {
            Kategori(
                nama = textInputSatu.editText?.text.toString()
            ).save()
            Toast.makeText(this, "Kategori berhasil disimpan!", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(this, "Kategori sudah ada!", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateKategori(kategori: Kategori){
        kategori.nama = textInputSatu.editText?.text.toString()
        kategori.save()
        Toast.makeText(this, "Kategori berhasil disimpan!", Toast.LENGTH_SHORT).show()
        finish()
    }
}