package com.aap.cstore.appkasir.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.models.User
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_login.*

class ActivityLogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login)

        btnMasuk.setOnClickListener {
            if (checkInputUsername(textInputUsername) && checkInputPassword(textInputPassword)) {
                val username = textInputUsername.editText?.text.toString().trim()
                val password = textInputPassword.editText?.text.toString().trim()
                val progressbar = BtnLoadingProgressbar(it)
                val user = SugarRecord.find(
                    User::class.java,
                    "username = ? and password = ?",
                    username,
                    password
                ).firstOrNull()
                if (user != null) {
                    progressbar.setLoading()
                    Handler().postDelayed({
                        progressbar.setState(true){
                            savePreferences(this, MyConstant.CURRENT_USER, user.id.toString())
                            startActivity(Intent(this, ActivityDashboard::class.java))
                            finish()
                        }
                    },1500)
                } else {
                    progressbar.setLoading()
                    Handler().postDelayed({
                        progressbar.setState(false){ // executed after animation end
                            Toast.makeText(this, "Login gagal user / password salah!", Toast.LENGTH_SHORT)
                                .show()
                            Handler().postDelayed({
                                progressbar.reset()
                            },1000)
                        }
                    },1500)
                }

            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(currentFocus != null){
            hideSoftKeyboard()
        }
        return super.dispatchTouchEvent(ev)
    }

}