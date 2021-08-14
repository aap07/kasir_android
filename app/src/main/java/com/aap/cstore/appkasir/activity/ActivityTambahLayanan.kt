package com.aap.cstore.appkasir.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.models.Layanan
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_add.*

class ActivityTambahLayanan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add)
        /*Setting tombol back dan title*/
        setToolbar(this, "Tambah Take Away")
        textInputSatu.hint = "Nama Layanan"
        textInputDua.visibility = View.GONE
        textInputTiga.visibility = View.GONE
        textInputEmpat.visibility = View.GONE
        spn.visibility = View.GONE
        val update = intent.getBooleanExtra("update", false)
        val idLayanan = intent.getLongExtra("layanan",-1)
        val layanan = SugarRecord.findById(Layanan::class.java,idLayanan)
        btnSimpan.setOnClickListener {
            if(checkInput(textInputSatu)){
                if(update){
                    /*Update meja*/
                    updateLayanan(layanan)
                }else{
                    /*Insert meja*/
                    insertLayanan()
                }
            }
        }
        if(layanan != null){
            textInputSatu.editText?.setText(layanan.nama)
        }

    }

    fun insertLayanan(){
        val checkLayanan = SugarRecord.find(Layanan::class.java, "nama = ?", textInputSatu.editText?.text.toString()).firstOrNull()
        if (checkLayanan == null) {
            Layanan(
                nama = textInputSatu.editText?.text.toString(),
            ).save()
            Toast.makeText(this, "Take Away berhasil disimpan!", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(this, "Take Away sudah ada!", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateLayanan(layanan: Layanan){
        layanan.nama = textInputSatu.editText?.text.toString()
        layanan.save()
        Toast.makeText(this, "Take Away berhasil disimpan!", Toast.LENGTH_SHORT).show()
        finish()
    }
}