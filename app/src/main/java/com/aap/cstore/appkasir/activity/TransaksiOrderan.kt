package com.aap.cstore.appkasir.activity

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.adapter.RclvDineIn
import com.aap.cstore.appkasir.adapter.RclvTakeAway
import com.aap.cstore.appkasir.models.Layanan
import com.aap.cstore.appkasir.models.Meja
import com.aap.cstore.appkasir.models.Orderan
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_menu.*

class TransaksiOrderan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu)
        tvEmpty.setText("Belum Ada Orderan")
        btnAdd.visibility = View.GONE
        val method = intent.getLongExtra("method",-1)
        if (method.toString() == "1") {
            /*Setting toolbar*/
            setToolbar(this, "Take-Away")
            /*Setting list item meja*/
            setToRecyclerView()
        }else if (method.toString() == "2"){
            /*Setting toolbar*/
            setToolbar(this, "Dine-In")
            /*Setting list item meja*/
            setToRecyclerView()
        }
    }


    fun setToRecyclerView(): Boolean {
        val method = intent.getLongExtra("method",-1)
        val listOrderan = SugarRecord.findById(Orderan::class.java, method)
        if (method.toString() == "1"){
            val listLayanan = SugarRecord.listAll(Layanan::class.java)
            if (listLayanan.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
            } else {
                tvEmpty.visibility = View.GONE
                val rclvadapter = RclvTakeAway(this, listLayanan,listOrderan)
                rclv.apply {
                    adapter = rclvadapter
                    layoutManager = GridLayoutManager(context, calculateNoOfColumns(context,250F))
                    setHasFixedSize(true)
                }
            }
        }else if (method.toString() == "2"){
            val listMeja = SugarRecord.listAll(Meja::class.java)
            if (listMeja.isEmpty()) {
                tvEmpty.visibility = View.VISIBLE
            } else {
                tvEmpty.visibility = View.GONE
                val rclvadapter = RclvDineIn(this, listMeja,listOrderan)
                rclv.apply {
                    adapter = rclvadapter
                    layoutManager = GridLayoutManager(context, calculateNoOfColumns(context,250F))
                    setHasFixedSize(true)
                }
            }
        }
        return true
    }

//    fun setToRecyclerViewDineIn(): Boolean {
//        val listMeja = SugarRecord.listAll(Meja::class.java)
//        if (listMeja.isEmpty()) {
//            tvEmpty.visibility = View.VISIBLE
//        } else {
//            tvEmpty.visibility = View.GONE
//            val rclvadapter = RclvDineIn(this, listMeja)
//            rclv.apply {
//                adapter = rclvadapter
//                layoutManager = GridLayoutManager(context, calculateNoOfColumns(context,250F))
//                setHasFixedSize(true)
//            }
//        }
//        return true
//    }

    override fun onResume() {
        super.onResume()
        setToRecyclerView()
    }
}