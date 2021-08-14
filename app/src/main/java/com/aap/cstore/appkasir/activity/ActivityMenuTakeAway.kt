package com.aap.cstore.appkasir.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.adapter.RclvLayanan
import com.aap.cstore.appkasir.models.Layanan
import com.aap.cstore.appkasir.models.User
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_menu.*

class ActivityMenuTakeAway : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu)
        tvEmpty.setText("Belum Ada Daftar Take Away")
        /*Setting toolbar*/
        setToolbar(this, "Take-Away")
        /*Setting list item meja*/
        setToRecyclerView()
        val userId = com.aap.cstore.appkasir.utils.getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val user = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        if (user?.role.equals(User.userSysAdmin) || user?.role.equals(User.userSysSuperAdmin)){
            btnAdd.visibility = View.VISIBLE
        }else{
            btnAdd.visibility = View.GONE
        }
        btnAdd.setOnClickListener {
            startActivity(Intent(this,ActivityTambahLayanan::class.java))
        }
    }


    fun setToRecyclerView(): Boolean {
        val listLayanan = SugarRecord.listAll(Layanan::class.java)
        val userId = com.aap.cstore.appkasir.utils.getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val listuser = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        if (listLayanan.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
        } else {
            tvEmpty.visibility = View.GONE
            val rclvadapter = listuser?.let { RclvLayanan(this, listLayanan, it) }
            rclv.apply {
                adapter = rclvadapter
                layoutManager = GridLayoutManager(context, calculateNoOfColumns(context,250F))
                setHasFixedSize(true)
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        setToRecyclerView()
    }
}