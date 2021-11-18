package com.aap.cstore.appkasir.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.adapter.RclvProduk
import com.aap.cstore.appkasir.models.Produk
import com.aap.cstore.appkasir.models.User
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_menu.*

class ActivityMenuProduk : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_menu)
        val no_product: String = getString(R.string.no_product)
        val pro: String = getString(R.string.product)
        tvEmpty.text = no_product
        /*Setting toolbar*/
        setToolbar(this, pro)
        /*Setting list item produk*/
        setToRecyclerView()
        val userId = getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val user = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        if (user?.role.equals(User.userSysAdmin) || user?.role.equals(User.userSysSuperAdmin)){
            btnAdd.visibility = View.VISIBLE
        }else{
            btnAdd.visibility = View.GONE
        }

        btnAdd.setOnClickListener {
            startActivity(Intent(this, ActivityTambahProduk::class.java))
        }
    }

    private fun setToRecyclerView() : Boolean{
        val listProduk = SugarRecord.listAll(Produk::class.java)
        val userId = getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val listuser = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        if(listProduk.isEmpty()){
            tvEmpty.visibility = View.VISIBLE
        }else{
            tvEmpty.visibility = View.GONE
            val rclvadapter = listuser?.let { RclvProduk(this,listProduk, it) }
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