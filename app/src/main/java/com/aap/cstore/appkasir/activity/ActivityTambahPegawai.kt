package com.aap.cstore.appkasir.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.models.User
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_add.*

class ActivityTambahPegawai : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add)
        /*Setting tombol back dan title*/
        setToolbar(this, "Tambah Pegawai")
        textInputSatu.hint = "Nama Pegawai"
        textInputDua.hint = "Kontak"
        textInputTiga.hint = "Usernam"
        textInputEmpat.hint = "Password"
        spn.hint = "Role"
        val role = getResources().getStringArray(R.array.Role)
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_list_item_1, role)
        spn.adapter = adapterSpinner

        val update = intent.getBooleanExtra("update", false)
        val idUser = intent.getLongExtra("user", -1)
        val user = SugarRecord.findById(User::class.java, idUser)
        btnSimpan.setOnClickListener {
            if (checkInput(textInputSatu) && checkInput(textInputTiga) && checkInputUsername(
                    textInputDua
                ) && checkInputPassword(textInputEmpat)
            ) {
                if (update) {
                    /*Update karyawan*/
                    updateKaryawan(user)
                } else {
                    /*Insert karyawan*/
                    insertKaryawan()
                }
            }

        }
        if (user != null) {
            textInputSatu.editText?.setText(user.nama)
            spn.selection = adapterSpinner.getPosition(user.role)
            textInputTiga.editText?.setText(user.kontak)
            textInputDua.editText?.setText(user.username)
            textInputEmpat.editText?.setText(user.password)
        }
    }

    private fun insertKaryawan() {
        val checkUser = SugarRecord.find(User::class.java, "username = ?", textInputDua.editText?.text.toString()).firstOrNull()
        if (checkUser == null) {
            User(
                nama = textInputSatu.editText?.text.toString(),
                kontak = textInputTiga.editText?.text.toString(),
                username = textInputDua.editText?.text.toString(),
                password = textInputEmpat.editText?.text.toString(),
                role = spn.selectedItem.toString()
            ).save()
            Toast.makeText(this, "Pegawai berhasil disimpan!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Username sudah terdaftar!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateKaryawan(user: User?) {
        user?.nama = textInputSatu.editText?.text.toString()
        user?.kontak = textInputTiga.editText?.text.toString()
        user?.username = textInputDua.editText?.text.toString()
        user?.password = textInputEmpat.editText?.text.toString()
        user?.role = spn.selectedItem.toString()
        user?.save()
        Toast.makeText(this, "Pegawai berhasil disimpan!", Toast.LENGTH_SHORT).show()
        finish()

    }
}