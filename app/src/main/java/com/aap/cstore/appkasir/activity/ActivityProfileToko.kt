package com.aap.cstore.appkasir.activity

import android.os.Bundle
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.models.Profile
import com.aap.cstore.appkasir.models.User
import com.aap.cstore.appkasir.utils.*
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_profile_toko.*


class ActivityProfileToko : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_toko)
        setToolbar(this, "Profile Toko")
        val userId = com.aap.cstore.appkasir.utils.getPreferences(this).getString(MyConstant.CURRENT_USER,"")
        val user = SugarRecord.find(User::class.java,"id = ?",userId).firstOrNull()
        if (user?.role.equals(User.userSysSuperAdmin)) {
            textPpn.isEnabled = true
            cbDiskon.isEnabled = true
            cbPpn.isEnabled = true
        }
        initProfile()

        btnSave.setOnClickListener {
            /*Melakukan update data profile dan data user*/
            if(checkInput(textInputNamaToko) && checkInput(textInputAlamat)){
                val profile = SugarRecord.listAll(Profile::class.java).firstOrNull()
                if (profile != null) {
                    profile.namaToko = textInputNamaToko.editText?.text.toString()
                    profile.tagLine = textInputTagLineToko.editText?.text.toString()
                    profile.alamatToko = textInputAlamat.editText?.text.toString()
                    profile.ppn = textPpn.editText?.text.toString().toDouble()
                    profile.statusPpn = cbPpn.isChecked
                    profile.statusDiskon = cbDiskon.isChecked
                    profile.save()
                    Toast.makeText(this, "Profil berhasil diubah!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun initProfile() {
        val profile = SugarRecord.listAll(Profile::class.java).firstOrNull()
        if (profile != null) {
                val ppn = profile.ppn.toString()
                textInputAlamat.editText?.setText(profile.alamatToko)
                textInputTagLineToko.editText?.setText(profile.tagLine)
                textInputNamaToko.editText?.setText(profile.namaToko)
                textPpn.editText?.setText(ppn)
                if(profile.statusPpn==true){
                    cbPpn.setChecked(true)
                }else{
                    cbPpn.setChecked(false)
                }
                if(profile.statusDiskon==true){
                    cbDiskon.setChecked(true)
                }else{
                    cbDiskon.setChecked(false)
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