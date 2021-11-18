package com.aap.cstore.appkasir.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.adapter.RclvMenuDashboard
import com.aap.cstore.appkasir.databinding.ActivityDashboardBinding
import com.aap.cstore.appkasir.models.Menu
import com.aap.cstore.appkasir.models.Profile
import com.aap.cstore.appkasir.models.Transaksi
import com.aap.cstore.appkasir.models.User
import com.aap.cstore.appkasir.utils.MyConstant
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
//import kotlinx.android.synthetic.main.activity_dashboard.*
import java.text.SimpleDateFormat
import java.util.*

class ActivityDashboard1 : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        setContentView(R.layout.activity_dashboard)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.bottomNavView.background = null
        val userId = getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val user = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        if(user != null){
            initMenu(user)
        }
        transaksi()
        greeting()
        initProfile()
        binding.btnBukaMeja.setOnClickListener {
            startActivity(Intent(this,ActivityMenuTransaksi::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.btnLogout){
            savePreferences(this,MyConstant.CURRENT_USER,"")
            startActivity(Intent(this,ActivityLogin::class.java))
            finishAffinity()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SimpleDateFormat")
    fun transaksi(){
        val tanggal = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(Date().time)
        val iTransaksi = SugarRecord.listAll(Transaksi::class.java).filter { l -> l.tanggalTransaksi?.substring(0, l.tanggalTransaksi?.indexOf(" ")!!).equals(tanggal.substring(0, tanggal.indexOf(" "))) && l.status == false}
        val iTransaksiBulan = SugarRecord.listAll(Transaksi::class.java).filter { l -> l.tanggalTransaksi?.substring(3, l.tanggalTransaksi?.indexOf(" ")!!).equals(tanggal.substring(3, tanggal.indexOf(" "))) && l.status == false }
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
        binding.textSalesToday.text = numberToCurrency(totalToday)
        binding.textSalesMonthly.text = numberToCurrency(totalMonthly)
        binding.textToday.text = SimpleDateFormat("EEEE, dd-MM-yyyy", Locale.getDefault()).format(Date().time)
    }

    private fun initMenu(user: User) {
        val listMenu : MutableList<Menu>
        val colSpan : Int
        val man_cat: String = getString(R.string.category_management)
        val man_pro: String = getString(R.string.product_management)
        val man_din: String = getString(R.string.dine_in_management)
        val man_tak: String = getString(R.string.take_away_management)
        val man_pay: String = getString(R.string.payment_management)
        val man_emp: String = getString(R.string.employee_management)
        val man_sto: String = getString(R.string.store_management)
        val sal_rep: String = getString(R.string.sales_report)
        val prof: String = getString(R.string.profile)
        val about: String = getString(R.string.about)
        val cat: String = getString(R.string.category)
        val pro: String = getString(R.string.product)
        val tak: String = getString(R.string.take_away)
        val din: String = getString(R.string.dine_in)
        val pay: String = getString(R.string.payment)
        if (user.role.equals(User.userSysSuperAdmin)){
            colSpan = 2
            listMenu = mutableListOf(
                Menu(R.drawable.ic_list_kategory, man_cat),
                Menu(R.drawable.ic_list_produk,man_pro),
                Menu(R.drawable.ic_dinein_method,man_din),
                Menu(R.drawable.ic_take_away_method,man_tak),
                Menu(R.drawable.ic_payment_method,man_pay),
                Menu(R.drawable.ic_add_user,man_emp),
                Menu(R.drawable.ic_store,man_sto),
                Menu(R.drawable.ic_laporan,sal_rep),
                Menu(R.drawable.ic_info,about)
            )
        }else if(user.role.equals(User.userSysAdmin)){
            colSpan = 2
            listMenu = mutableListOf(
                Menu(R.drawable.ic_list_kategory, man_cat),
                Menu(R.drawable.ic_list_produk,man_pro),
                Menu(R.drawable.ic_dinein_method,man_din),
                Menu(R.drawable.ic_take_away_method,man_tak),
                Menu(R.drawable.ic_payment_method,man_pay),
                Menu(R.drawable.ic_add_user,man_emp),
                Menu(R.drawable.ic_store,man_sto),
                Menu(R.drawable.ic_laporan,sal_rep),
                Menu(R.drawable.ic_profile,prof),
                Menu(R.drawable.ic_info,about)
            )
        }else{
            colSpan = 2
            listMenu = mutableListOf(
                Menu(R.drawable.ic_list_kategory,cat),
                Menu(R.drawable.ic_list_produk,pro),
                Menu(R.drawable.ic_dinein_method,din),
                Menu(R.drawable.ic_take_away_method,tak),
                Menu(R.drawable.ic_payment_method,pay),
                Menu(R.drawable.ic_profile,prof),
                Menu(R.drawable.ic_info,about)
            )
        }

        binding.rclv.apply {
            adapter = RclvMenuDashboard(this@ActivityDashboard1,listMenu)
            layoutManager = GridLayoutManager(this@ActivityDashboard1, colSpan)
            setHasFixedSize(true)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun greeting() {
        val userId = getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val user = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        val calendar: Calendar = Calendar.getInstance()
        val morning: String = getString(R.string.greetings_morning)
        val afternoon: String = getString(R.string.greetings_afternoon)
        val night: String = getString(R.string.greetings_night)
        when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 1..11 -> {
                binding.textGreeting.text = "$morning, " + user?.nama
            }
            in 12..17 -> {
                binding.textGreeting.text = "$afternoon, " + user?.nama
            }
            in 18..23 -> {
                binding.textGreeting.text = "$night, " + user?.nama
            }
        }
    }

    private fun initProfile(){
        val profile  = SugarRecord.listAll(Profile::class.java).firstOrNull()
        if(profile != null){
            binding.textNamaToko.text = profile.namaToko
        }
    }

    override fun onResume() {
        super.onResume()
        initProfile()
        transaksi()
        greeting()
    }

}