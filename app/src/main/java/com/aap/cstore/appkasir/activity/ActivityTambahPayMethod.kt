package com.aap.cstore.appkasir.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.models.MetodePembayaran
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_add.*

class ActivityTambahPayMethod : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add)
        /*Setting tombol back dan title*/
        setToolbar(this, "Tambah Pay Method")
        textInputSatu.hint = "Nama Payment"
        textInputDua.visibility = View.GONE
        textInputTiga.visibility = View.GONE
        textInputEmpat.visibility = View.GONE
        spn.visibility = View.GONE
        val update = intent.getBooleanExtra("update", false)
        val idMethod = intent.getLongExtra("payMethod",-1)
        val method = SugarRecord.findById(MetodePembayaran::class.java,idMethod)
        btnSimpan.setOnClickListener {
            if(checkInput(textInputSatu)){
                if(update){
                    /*Update meja*/
                    updateMethod(method)
                }else{
                    /*Insert meja*/
                    insertMethod()
                }
            }
        }
        if(method != null){
            textInputSatu.editText?.setText(method.nama)
        }

    }

    fun insertMethod(){
        val checkMethod = SugarRecord.find(MetodePembayaran::class.java, "nama = ?", textInputSatu.editText?.text.toString()).firstOrNull()
        if (checkMethod == null) {
            MetodePembayaran(
                nama = textInputSatu.editText?.text.toString(),
            ).save()
            Toast.makeText(this, "Pay Method berhasil disimpan!", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(this, "Pay Method sudah ada!", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateMethod(method: MetodePembayaran){
        method.nama = textInputSatu.editText?.text.toString()
        method.save()
        Toast.makeText(this, "Pay Method berhasil disimpan!", Toast.LENGTH_SHORT).show()
        finish()
    }
}