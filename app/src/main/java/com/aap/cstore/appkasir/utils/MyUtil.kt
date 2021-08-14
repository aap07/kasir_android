@file:JvmName("MyUtil")
@file:JvmMultifileClass

package com.aap.cstore.appkasir.utils


import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.DisplayMetrics
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.layout_toolbar_with_back.*
import java.text.NumberFormat
import java.util.*

fun numberToCurrency(number: Long): String {
    return NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(number).toString()
        .let { it.substring(0, it.length - 3) }
}

/*Fungsi untuk mengconvert ke format uang*/
fun numberToCurrency(number: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(number).toString().removeSuffix(",00").replace("Rp", "Rp. ")
}

fun calculateNoOfColumns(
    context: Context,
    columnWidthDp: Float
): Int { // For example columnWidthdp=180
    val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
    val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
    return (screenWidthDp / columnWidthDp + 0.5).toInt()
}

/*Fungsi untuk mengatur tombol kembali dan title di toolbar*/
fun setToolbar(
    activity: Activity,
    title: String
) {
    activity.btnBack.setOnClickListener {
        activity.onBackPressed()
    }
    activity.tvTitle.setText(title)
}

fun Activity.hideSoftKeyboard() {
    currentFocus?.let {
        val inputMethodManager =
            ContextCompat.getSystemService(this, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)

    }
    if (currentFocus is TextInputEditText) {
        (currentFocus as TextInputEditText).clearFocus()
    }
}

fun checkInput(input: Any): Boolean {
    var check = false
    if (input is TextInputLayout) {
        if (input.editText?.text.toString().trim().isEmpty()) {
            check = false
            input.isErrorEnabled = true
            input.error = "${input.hint} tidak boleh kosong!"
//            Snackbar.make(input.rootView,"${input.hint} tidak boleh kosong!",Snackbar.LENGTH_SHORT).show()
        } else {
            check = true
            input.isErrorEnabled = false
        }
    }
    return check
}

fun checkInputUsername(input: Any): Boolean {
    var check = false
    if (input is TextInputLayout) {

        if (input.editText?.text.toString().trim().isEmpty()) {
            check = false
            input.error = "Username tidak boleh kosong!"
        } else if (input.editText?.text.toString().length < 5) {
            check = false
            input.error = "Username minimal 5 karakter!"
        } else {
            check = true
        }
        input.isErrorEnabled = !check
    }
    return check
}

fun checkInputPassword(input: Any): Boolean {
    var check = false
    if (input is TextInputLayout) {
        if (input.editText?.text.toString().trim().isEmpty()) {
            check = false
            input.error = "Password tidak boleh kosong!"
        } else if (input.editText?.text.toString().length < 8) {
            check = false
            input.error = "Password minimal 8 karakter!"
        } else {
            check = true
        }
        input.isErrorEnabled = !check
    }
    return check
}

fun savePreferences(context: Context, namePreferences: String, value: Any): Boolean {
    val preferences = context.getSharedPreferences("prefs", 0)
    val editor = preferences.edit()
    when (value) {
        is Boolean -> {
            editor.putBoolean(namePreferences, value)
            editor.apply()
            return true
        }
        is String -> {
            editor.putString(namePreferences, value)
            editor.apply()
            return true
        }
        is Int -> {
            editor.putInt(namePreferences, value)
            editor.apply()
            return true
        }
        is Long -> {
            editor.putLong(namePreferences, value)
            editor.apply()
            return true
        }
        is Float -> {
            editor.putFloat(namePreferences, value)
            editor.apply()
            return true
        }
        else -> {
            return false
        }
    }
}

fun getPreferences(context: Context):SharedPreferences{
    return context.getSharedPreferences("prefs", 0)
}




