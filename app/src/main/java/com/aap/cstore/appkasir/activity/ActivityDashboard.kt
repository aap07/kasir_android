package com.aap.cstore.appkasir.activity

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.adapter.RclvMenuDashboard
import com.aap.cstore.appkasir.models.Menu
import com.aap.cstore.appkasir.models.Profile
import com.aap.cstore.appkasir.models.Transaksi
import com.aap.cstore.appkasir.models.User
import com.aap.cstore.appkasir.utils.MyConstant
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.text.SimpleDateFormat
import java.util.*

class ActivityDashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard)
        val userId = com.aap.cstore.appkasir.utils.getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val user = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        if(user != null){
            initMenu(user)
        }
        transaksi()
        greeting()
        initProfile()
        val btnLog = findViewById(R.id.btnLogout) as ImageView
        btnLog.setOnClickListener{
            savePreferences(this,MyConstant.CURRENT_USER,"")
            startActivity(Intent(this,ActivityLogin::class.java))
            finishAffinity()
            finish()
        }
        btnBukaMeja.setOnClickListener {
            startActivity(Intent(this,ActivityMenuTransaksi::class.java))
        }
    }

    fun transaksi(){
        val tanggal = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(Date().time)
        val iTransaksi = SugarRecord.listAll(Transaksi::class.java).filter { l -> l.tanggalTransaksi?.substring(0, l.tanggalTransaksi?.indexOf(" ")!!).equals(tanggal.substring(0, tanggal.indexOf(" ")!!)!!) }
        val iTransaksiBulan = SugarRecord.listAll(Transaksi::class.java).filter { l -> l.tanggalTransaksi?.substring(3, l.tanggalTransaksi?.indexOf(" ")!!).equals(tanggal.substring(3, tanggal.indexOf(" ")!!)!!) }
        var totalToday = 0.0
        var totalMonthly = 0.0
        if(iTransaksi.isNotEmpty()){
            for (item in iTransaksi){
                totalToday += (item.totalPembayaran!!+item.nominalPpn!!)-item.nominalDiskon!!
            }
        }
        if(iTransaksiBulan.isNotEmpty()){
            for (item in iTransaksiBulan){
                totalMonthly += (item.totalPembayaran!!+item.nominalPpn!!)-item.nominalDiskon!!
            }
        }
        textSalesToday.setText(numberToCurrency(totalToday))
        textSalesMonthly.setText(numberToCurrency(totalMonthly))
        textToday.setText(SimpleDateFormat("EEEE, dd-MM-yyyy", Locale.getDefault()).format(Date().time))
    }

    private fun initMenu(user: User) {
        val listMenu : MutableList<Menu>
        val colSpan : Int
        if (user.role.equals(User.userSysSuperAdmin)){
            colSpan = 2
            listMenu = mutableListOf(
                Menu(R.drawable.ic_list_kategory,"Management Kategori"),
                Menu(R.drawable.ic_list_produk,"Management Produk"),
                Menu(R.drawable.ic_dinein_method,"Management Dine-In"),
                Menu(R.drawable.ic_take_away_method,"Management Take-Away"),
                Menu(R.drawable.ic_payment_method,"Management Pembayaran"),
                Menu(R.drawable.ic_add_user,"Management Pegawai"),
                Menu(R.drawable.ic_store,"Management Toko"),
                Menu(R.drawable.ic_laporan,"Laporan Penjualan"),
                Menu(R.drawable.ic_info,"About")
            )
        }else if(user.role.equals(User.userSysAdmin)){
            colSpan = 2
            listMenu = mutableListOf(
                Menu(R.drawable.ic_list_kategory,"Management Kategori"),
                Menu(R.drawable.ic_list_produk,"Management Produk"),
                Menu(R.drawable.ic_dinein_method,"Management Dine-In"),
                Menu(R.drawable.ic_take_away_method,"Management Take-Away"),
                Menu(R.drawable.ic_payment_method,"Management Pembayaran"),
                Menu(R.drawable.ic_add_user,"Management Pegawai"),
                Menu(R.drawable.ic_store,"Management Toko"),
                Menu(R.drawable.ic_laporan,"Laporan Penjualan"),
                Menu(R.drawable.ic_profile,"Profile"),
                Menu(R.drawable.ic_info,"About")
            )
        }else{
            colSpan = 2
            listMenu = mutableListOf(
                Menu(R.drawable.ic_list_kategory,"Kategori"),
                Menu(R.drawable.ic_list_produk,"Produk"),
                Menu(R.drawable.ic_dinein_method,"Dine-In"),
                Menu(R.drawable.ic_take_away_method,"Take-Away"),
                Menu(R.drawable.ic_payment_method,"Pembayaran"),
                Menu(R.drawable.ic_profile,"Profile"),
                Menu(R.drawable.ic_info,"About")
            )
        }

        rclv.apply {
            adapter = RclvMenuDashboard(this@ActivityDashboard,listMenu)
            layoutManager = GridLayoutManager(this@ActivityDashboard, colSpan)
            setHasFixedSize(true)
        }
    }

    private fun greeting() {
        val userId = com.aap.cstore.appkasir.utils.getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val user = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        val calendar: Calendar = Calendar.getInstance()
        val timeOfDay: Int = calendar.get(Calendar.HOUR_OF_DAY)
        if (timeOfDay in 0..11) {
            textGreeting.setText("Selamat Pagi, " + user?.nama);
        } else if (timeOfDay in 12..14) {
            textGreeting.setText("Selamat Siang, " + user?.nama);
        } else if (timeOfDay in 15..17) {
            textGreeting.setText("Selamat Sore, " + user?.nama);
        } else if (timeOfDay in 18..23) {
            textGreeting.setText("Selamat Malam, " + user?.nama);
        }
    }

    fun initProfile(){
        val profile  = SugarRecord.listAll(Profile::class.java).firstOrNull()
        if(profile != null){
            textNamaToko.setText(profile.namaToko)
        }
    }

    override fun onResume() {
        super.onResume()
        initProfile()
        transaksi()
        greeting()
    }


}