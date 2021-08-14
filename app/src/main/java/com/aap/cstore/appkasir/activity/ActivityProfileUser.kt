package com.aap.cstore.appkasir.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.models.User
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_profile_user.*


class ActivityProfileUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_user)
        setToolbar(this, "Profile User")
        /*Setting data profile*/
        val userId = com.aap.cstore.appkasir.utils.getPreferences(this).getString(MyConstant.CURRENT_USER,"")!!
        val user = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        initProfile(user)

        btnSave.setOnClickListener {
            /*Melakukan update data profile dan data user*/
            if(checkInput(textInputNama) && checkInput(textInputKontak) && checkInputUsername(textInputUsernameAdmin) && checkInputPassword(textInputPasswordAdmin)){
                user?.nama = textInputNama.editText?.text.toString()
                user?.kontak = textInputKontak.editText?.text.toString()
                user?.username = textInputUsernameAdmin.editText?.text.toString()
                user?.password = textInputPasswordAdmin.editText?.text.toString()
                user?.save()
                Toast.makeText(this, "Profil berhasil diubah!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun initProfile(user:User?) {
        if(user != null){
            textInputNama.editText?.setText(user.nama)
            textInputKontak.editText?.setText(user.kontak)
            textInputUsernameAdmin.editText?.setText(user.username)
            textInputPasswordAdmin.editText?.setText(user.password)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(currentFocus != null){
            hideSoftKeyboard()
        }
        return super.dispatchTouchEvent(ev)
    }

}