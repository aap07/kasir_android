package com.aap.cstore.appkasir.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.adapter.RclvKategori
import com.aap.cstore.appkasir.databinding.ActivityMenuBinding
import com.aap.cstore.appkasir.models.Kategori
import com.aap.cstore.appkasir.models.User
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord

class ActivityMenuKategori : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val no_category: String = getString(R.string.no_category)
        val cat: String = getString(R.string.category)
        binding.tvEmpty.text = no_category
        /*Setting toolbar*/
        setToolbar(this, cat)
        /*Setting list item kategori*/
        setToRecyclerView()

        val userId = getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val user = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        if (user?.role.equals(User.userSysAdmin) || user?.role.equals(User.userSysSuperAdmin)){
            binding.btnAdd.visibility = View.VISIBLE
        }else{
            binding.btnAdd.visibility = View.GONE
        }
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this,ActivityTambahKategori::class.java))
        }
    }


    private fun setToRecyclerView(): Boolean {
        val listKategori = SugarRecord.listAll(Kategori::class.java)
        val userId = getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val listuser = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        if (listKategori.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
        } else {
            binding.tvEmpty.visibility = View.GONE
            val rclvadapter = listuser?.let { RclvKategori(this, listKategori, it) }
            binding.rclv.apply {
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