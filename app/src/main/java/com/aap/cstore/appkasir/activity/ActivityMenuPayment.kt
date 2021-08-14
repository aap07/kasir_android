package com.aap.cstore.appkasir.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.adapter.RclvPayMethod
import com.aap.cstore.appkasir.models.MetodePembayaran
import com.aap.cstore.appkasir.models.User
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_menu.*

class ActivityMenuPayment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu)
        tvEmpty.setText("Belum Ada Daftar Payment Method")
        /*Setting toolbar*/
        setToolbar(this, "Payment Method")
        /*Setting list item meja*/
        setToRecyclerView()
        val userId = com.aap.cstore.appkasir.utils.getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val user = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        if (user?.role.equals(User.userSysSuperAdmin)){
            btnAdd.visibility = View.VISIBLE
        }else{
            btnAdd.visibility = View.GONE
        }
        btnAdd.setOnClickListener {
            startActivity(Intent(this,ActivityTambahPayMethod::class.java))
        }
    }


    fun setToRecyclerView(): Boolean {
        val listPayMethod = SugarRecord.listAll(MetodePembayaran::class.java)
        val userId = com.aap.cstore.appkasir.utils.getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val listuser = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        if (listPayMethod.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
        } else {
            tvEmpty.visibility = View.GONE
            val rclvadapter = listuser?.let { RclvPayMethod(this, listPayMethod, it) }
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