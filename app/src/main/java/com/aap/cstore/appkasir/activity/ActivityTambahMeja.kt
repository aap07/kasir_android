package com.aap.cstore.appkasir.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.models.Meja
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_add.*

class ActivityTambahMeja : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add)
        /*Setting tombol back dan title*/
        setToolbar(this, "Tambah Meja")
        textInputSatu.hint = "Nama Meja"
        textInputDua.visibility = View.GONE
        textInputTiga.visibility = View.GONE
        textInputEmpat.visibility = View.GONE
        spn.visibility = View.GONE
        val update = intent.getBooleanExtra("update", false)
        val idMeja = intent.getLongExtra("meja",-1)
        val meja = SugarRecord.findById(Meja::class.java,idMeja)
        btnSimpan.setOnClickListener {
            if(checkInput(textInputSatu)){
                if(update){
                    /*Update meja*/
                    updateMeja(meja)
                }else{
                    /*Insert meja*/
                    insertMeja()
                }
            }
        }
        if(meja != null){
            textInputSatu.editText?.setText(meja.nama)
        }

    }

    fun insertMeja(){
        val checkMeja = SugarRecord.find(Meja::class.java, "nama = ?", textInputSatu.editText?.text.toString()).firstOrNull()
        if (checkMeja == null) {
            Meja(
                nama = textInputSatu.editText?.text.toString(),
                status = false
            ).save()
            Toast.makeText(this, "Meja berhasil disimpan!", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(this, "Meja sudah ada!", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateMeja(meja: Meja){
        meja.nama = textInputSatu.editText?.text.toString()
        meja.save()
        Toast.makeText(this, "Meja berhasil disimpan!", Toast.LENGTH_SHORT).show()
        finish()
    }
}