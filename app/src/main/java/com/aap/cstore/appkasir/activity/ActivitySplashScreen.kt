package com.aap.cstore.appkasir.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.models.Orderan
import com.aap.cstore.appkasir.models.Profile
import com.aap.cstore.appkasir.models.User
import com.aap.cstore.appkasir.utils.MyConstant
import com.aap.cstore.appkasir.utils.savePreferences
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_splash_screen.*
import java.util.*


class ActivitySplashScreen : AppCompatActivity() {

    var constraintLayout: ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_splash_screen)
        firstInsertToDatabase()
        val profile = SugarRecord.listAll(Profile::class.java).firstOrNull()
        if(profile != null){
            splashText.setText(profile.namaToko)
        }
        greeting()
        Handler().postDelayed({
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

    private fun greeting() {
        val calendar: Calendar = Calendar.getInstance()
        val timeOfDay: Int = calendar.get(Calendar.HOUR_OF_DAY)
        if (timeOfDay >= 0 && timeOfDay < 12) {
            splashTextGreeting.setText("Selamat Pagi, Selamat Datang")
        } else if (timeOfDay >= 12 && timeOfDay < 15) {
            splashTextGreeting.setText("Selamat Siang, Selamat Datang")
        } else if (timeOfDay >= 15 && timeOfDay < 18) {
            splashTextGreeting.setText("Selamat Sore, Selamat Datang")
        } else if (timeOfDay >= 18 && timeOfDay < 24) {
            splashTextGreeting.setText("Selamat Malam, Selamat Datang")
        }
    }
}