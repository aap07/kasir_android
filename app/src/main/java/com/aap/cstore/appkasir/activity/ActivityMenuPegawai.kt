package com.aap.cstore.appkasir.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.adapter.RclvUser
import com.aap.cstore.appkasir.models.User
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_menu.*

class ActivityMenuPegawai : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu)
        tvEmpty.setText("Belum Ada Daftar Pegawai")
        /*Setting toolbar*/
        setToolbar(this,"Tambah Pegawai")
        /*Menampilkan item pegawai*/
        setRecyclerView()
        val userId = com.aap.cstore.appkasir.utils.getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val user = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        if (user?.role.equals(User.userSysSuperAdmin)){
            btnAdd.visibility = View.VISIBLE
        }else{
            btnAdd.visibility = View.GONE
        }
        btnAdd.setOnClickListener {
            startActivity(Intent(this,ActivityTambahPegawai::class.java))
        }
    }

    private fun setRecyclerView() {
        val pegawai = SugarRecord.find(User::class.java,"role = ? or role = ?",User.userAdmin,User.userSysAdmin)
        val userId = com.aap.cstore.appkasir.utils.getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val listRole = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        if(pegawai.isEmpty()){
            tvEmpty.visibility = View.VISIBLE
        }else{
            tvEmpty.visibility = View.GONE
            rclv.apply {
                adapter = listRole?.let { RclvUser(this@ActivityMenuPegawai, pegawai, it) }
                layoutManager = GridLayoutManager(this@ActivityMenuPegawai, calculateNoOfColumns(this@ActivityMenuPegawai,250F))
                setHasFixedSize(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setRecyclerView()
    }
}