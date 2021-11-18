package com.aap.cstore.appkasir.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.databinding.ActivitySplashScreenBinding
import com.aap.cstore.appkasir.models.Orderan
import com.aap.cstore.appkasir.models.Profile
import com.aap.cstore.appkasir.models.User
import com.aap.cstore.appkasir.utils.MyConstant
import com.aap.cstore.appkasir.utils.savePreferences
import com.orm.SugarRecord
import java.util.*


class ActivitySplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firstInsertToDatabase()
        val profile = SugarRecord.listAll(Profile::class.java).firstOrNull()
        if(profile != null){
            binding.splashText.text = profile.namaToko
        }
        greeting()
        Handler(mainLooper).postDelayed({
            if (com.aap.cstore.appkasir.utils.getPreferences(this).getString(
                    MyConstant.CURRENT_USER,
                    ""
                )!!.isEmpty()
            ) {
                startActivity(Intent(this, ActivityLogin::class.java))
                finish()
            } else {
                startActivity(Intent(this, ActivityDashboard::class.java))
                finish()
            }
        }, 3000)

    }

    private  fun firstInsertToDatabase() {
        val firstStart =
            com.aap.cstore.appkasir.utils.getPreferences(this).getBoolean(MyConstant.FIRST_START, false)
        if (!firstStart) {
            savePreferences(this, MyConstant.FIRST_START, true)
            Profile(
                namaToko = "C-Store07"
            ).save()

            User(
                nama = "Super Admin",
                username = "superadmin",
                password = "superadmin123",
                role = User.userSysSuperAdmin
            ).save()

            Orderan(
                nama ="Take Away"
            ).save()

            Orderan(
                nama ="Dine In"
            ).save()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun greeting() {
        val calendar: Calendar = Calendar.getInstance()
        val morning: String = getString(R.string.greetings_morning)
        val afternoon: String = getString(R.string.greetings_afternoon)
        val night: String = getString(R.string.greetings_night)
        val welcome: String = getString(R.string.welcome)
        when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 1..11 -> {
                binding.splashTextGreeting.text = "$morning, $welcome"
            }
            in 12..17 -> {
                binding.splashTextGreeting.text = "$afternoon, $welcome"
            }
            in 18..23 -> {
                binding.splashTextGreeting.text = "$night, $welcome"
            }
        }
    }
}