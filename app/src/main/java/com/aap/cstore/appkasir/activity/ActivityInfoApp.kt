package com.aap.cstore.appkasir.activity

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.utils.setToolbar
import kotlinx.android.synthetic.main.activity_info_app.*

class ActivityInfoApp: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_info_app)
        setToolbar(this, "Info Aplikasi")
        nameApp.setText("Name : App Cashier")
        version.setText("Version : 3.0")
        author.setText("Author : aap07 | c_store07")
        release.setText("Release : 5 Desember 2020")
        update.setText("Update : 17 Agustus 2021")
    }
}