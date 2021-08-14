package com.aap.cstore.appkasir.activity

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.adapter.RclvOrderan
import com.aap.cstore.appkasir.models.Orderan
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_menu.*

class ActivityMenuTransaksi : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu)
        tvEmpty.visibility = View.GONE
        btnAdd.visibility = View.GONE
        /*Setting toolbar*/
        setToolbar(this, "Orderan Transaksi")
        /*Setting list item kategori*/
        setToRecyclerView()

    }


    fun setToRecyclerView(): Boolean {
        val listOrderan = SugarRecord.listAll(Orderan::class.java)
        if (listOrderan.isNotEmpty()) {
            val rclvadapter = RclvOrderan(this, listOrderan)
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