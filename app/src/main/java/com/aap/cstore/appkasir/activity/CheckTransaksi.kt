package com.aap.cstore.appkasir.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.models.*
import com.aap.cstore.appkasir.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.check_transaksi.*
import kotlinx.android.synthetic.main.layout_input_dialog.view.*
import kotlinx.android.synthetic.main.layout_input_spiner.view.*
import java.text.SimpleDateFormat
import java.util.*

class CheckTransaksi : AppCompatActivity() {
    lateinit var adapterSpinner : ArrayAdapter<MetodePembayaran>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.check_transaksi)
        /*Setting toolbar*/
        setToolbar(this, "Data Transaksi")

        /*Menapilkan list item produk di keranjang*/
        val profile = SugarRecord.listAll(Profile::class.java).firstOrNull()
        if(profile?.statusPpn==true){
            layoutPpn.visibility = View.VISIBLE
        }else{
            layoutPpn.visibility = View.GONE
        }
        if(profile?.statusDiskon==true){
            layoutDiskon.visibility = View.VISIBLE
        }else{
            layoutDiskon.visibility = View.GONE
        }
        val idMeja = intent.getLongExtra("mejaId",-1)
        val idLayanan = intent.getLongExtra("layananId",-1)
        if (idMeja > 0) {
            val transaksi = SugarRecord.find(Transaksi::class.java,"status = ? and id_meja = ?","1", idMeja.toString()).firstOrNull()
            btnCheckout.setOnClickListener {
//                val transaksi = SugarRecord.find(Transaksi::class.java,"status = ? and id_meja = ?","1", idMeja.toString()).firstOrNull()
                if (transaksi != null) {
                    /*Mengupdate tanggal transaksi */
                    transaksi.tanggalTransaksi = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault()).format(Date().time).toString()
                    transaksi.save()
                }
                val inten = Intent(this, CheckoutActivity::class.java)
                inten.putExtra("mejaId", idMeja)
                startActivity(inten)
                finish()
            }
            /*Setting nama pembeli*/
            layoutNamaPembeli.setOnClickListener {
                val layout = LayoutInflater.from(this).inflate(R.layout.layout_input_dialog, null, false)
                layout.textInput.hint = "Nama Pembeli"
                layout.textInput.editText?.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                layout.textInput.editText?.setText(transaksi?.namaPembeli.toString())
                MaterialAlertDialogBuilder(this)
                    .setView(layout)
                    .setNegativeButton(
                        "Batal",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .setPositiveButton(
                        "Oke",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            if (checkInput(layout.textInput)) {
                                if (transaksi != null) {
                                    tvNamaPembeli.setText(layout.textInput.editText?.text)
                                    transaksi.namaPembeli = layout.textInput.editText?.text.toString()
                                    transaksi.save()
                                }
                            }
                        }).show()
            }
            /*Setting jenis pembayaran*/
            layoutPayMethod.setOnClickListener {
                /*Setting adapter spinner*/
                val layout = LayoutInflater.from(this).inflate(R.layout.layout_input_spiner, null, false)
                adapterSpinner = ArrayAdapter<MetodePembayaran>(this, android.R.layout.simple_list_item_1, SugarRecord.listAll(MetodePembayaran::class.java))
                layout.spn.adapter = adapterSpinner
                layout.spn.hint = "Pay Method"
                if (transaksi != null) {
                    layout.spn.selection = adapterSpinner.getPosition(transaksi.metodePembayaran)
                }
                MaterialAlertDialogBuilder(this)
                    .setView(layout)
                    .setNegativeButton(
                        "Batal",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .setPositiveButton(
                        "Oke",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            if (checkInput(layout.spn)) {
                                if (transaksi != null) {
                                    tvMethodPay.setText(layout.spn.selectedItem.toString())
                                    transaksi.metodePembayaran = layout.spn.selectedItem as MetodePembayaran
                                    transaksi.save()
                                }
                            }
                        }).show()
            }
            /*Setting nilai bayar*/
            layoutBayar.setOnClickListener {
                val layout = LayoutInflater.from(this).inflate(R.layout.layout_input_dialog, null, false)
                layout.textInput.hint = "Bayar"
                layout.textInput.prefixText = "Rp. "
                layout.textInput.editText?.inputType = InputType.TYPE_CLASS_NUMBER
                layout.textInput.editText?.setText(transaksi?.bayar?.toInt().toString())
                MaterialAlertDialogBuilder(this)
                    .setView(layout)
                    .setNegativeButton(
                        "Batal",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .setPositiveButton(
                        "Oke",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            if (checkInput(layout.textInput)) {
                                if (transaksi != null) {
                                    tvBayar.setText(numberToCurrency(layout.textInput.editText?.text.toString().toDouble()))
                                    transaksi.bayar = layout.textInput.editText?.text.toString().toDouble()
                                    transaksi.save()
                                    tvKembalian.setText(numberToCurrency(transaksi.bayar!! - (transaksi.totalPembayaran!! + transaksi.nominalPpn!! - transaksi.nominalDiskon!!)))
                                }
                            }
                        }).show()
            }
            /*Setting nilai diskon*/
            layoutDiskon.setOnClickListener {
                val layout = LayoutInflater.from(this).inflate(R.layout.layout_input_dialog, null, false)
                layout.textInput.hint = "Diskon ( % )"
                layout.textInput.editText?.inputType = InputType.TYPE_CLASS_NUMBER
                layout.textInput.editText?.setText(transaksi?.diskon?.toInt().toString())
                MaterialAlertDialogBuilder(this)
                    .setView(layout)
                    .setNegativeButton(
                        "Batal",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .setPositiveButton(
                        "Oke",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            if (checkInput(layout.textInput)) {
                                if (transaksi != null) {
                                    var diskon = layout.textInput.editText?.text.toString().toDouble()
                                    var totalDiskon = (diskon / 100) * transaksi.totalPembayaran!!
                                    var subTotal = (transaksi.totalPembayaran!! + transaksi.nominalPpn!!) - totalDiskon
                                    tvDiskon.setText(numberToCurrency(totalDiskon))
                                    tvSubTotal.setText(numberToCurrency(subTotal))
                                    tvKembalian.setText(numberToCurrency(transaksi.bayar!! - (subTotal)))
                                    transaksi.diskon = diskon
                                    transaksi.nominalDiskon = totalDiskon
                                    transaksi.save()
                                }
                            }
                        }).show()
            }
            setInfoTransaksi(transaksi)
        }
        if (idLayanan > 0) {
            val transaksi = SugarRecord.find(Transaksi::class.java,"status = ? and id_layanan = ?","1", idLayanan.toString()).firstOrNull()
            btnCheckout.setOnClickListener {
//                val transaksi = SugarRecord.find(Transaksi::class.java,"status = ? and id_meja = ?","1", idMeja.toString()).firstOrNull()
                if (transaksi != null) {
                    /*Mengupdate tanggal transaksi */
                    transaksi.tanggalTransaksi = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault()).format(Date().time).toString()
                    transaksi.save()
                }
                val inten = Intent(this, CheckoutActivity::class.java)
                inten.putExtra("layananId", idLayanan)
                startActivity(inten)
                finish()
            }
            /*Setting nama pembeli*/
            layoutNamaPembeli.setOnClickListener {
                val layout = LayoutInflater.from(this).inflate(R.layout.layout_input_dialog, null, false)
                layout.textInput.hint = "Nama Pembeli"
                layout.textInput.editText?.inputType = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                layout.textInput.editText?.setText(transaksi?.namaPembeli.toString())
                MaterialAlertDialogBuilder(this)
                    .setView(layout)
                    .setNegativeButton(
                        "Batal",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .setPositiveButton(
                        "Oke",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            if (checkInput(layout.textInput)) {
                                if (transaksi != null) { tvNamaPembeli.setText(layout.textInput.editText?.text)
                                    transaksi.namaPembeli = layout.textInput.editText?.text.toString()
                                    transaksi.save()
                                }
                            }
                        }).show()
            }
            /*Setting jenis pembayaran*/
            layoutPayMethod.setOnClickListener {
                /*Setting adapter spinner*/
                val layout = LayoutInflater.from(this).inflate(R.layout.layout_input_spiner, null, false)
                adapterSpinner = ArrayAdapter<MetodePembayaran>(this, android.R.layout.simple_list_item_1, SugarRecord.listAll(MetodePembayaran::class.java))
                layout.spn.adapter = adapterSpinner
                layout.spn.hint = "Pay Method"
                if (transaksi != null) {
                    layout.spn.selection = adapterSpinner.getPosition(transaksi.metodePembayaran)
                }
                MaterialAlertDialogBuilder(this)
                    .setView(layout)
                    .setNegativeButton(
                        "Batal",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .setPositiveButton(
                        "Oke",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            if (checkInput(layout.spn)) {
                                if (transaksi != null) {
                                    tvMethodPay.setText(layout.spn.selectedItem.toString())
                                    transaksi.metodePembayaran = layout.spn.selectedItem as MetodePembayaran
                                    transaksi.save()
                                }
                            }
                        }).show()
            }
            /*Setting nilai bayar*/
            layoutBayar.setOnClickListener {
                val layout = LayoutInflater.from(this).inflate(R.layout.layout_input_dialog, null, false)
                layout.textInput.hint = "Bayar"
                layout.textInput.prefixText = "Rp. "
                layout.textInput.editText?.inputType = InputType.TYPE_CLASS_NUMBER
                layout.textInput.editText?.setText(transaksi?.bayar?.toInt().toString())
                MaterialAlertDialogBuilder(this)
                    .setView(layout)
                    .setNegativeButton(
                        "Batal",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .setPositiveButton(
                        "Oke",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            if (checkInput(layout.textInput)) {
                                if (transaksi != null) {
                                    tvBayar.setText(numberToCurrency(layout.textInput.editText?.text.toString().toDouble()))
                                    transaksi.bayar = layout.textInput.editText?.text.toString().toDouble()
                                    transaksi.save()
                                    tvKembalian.setText(numberToCurrency(transaksi.bayar!! - (transaksi.totalPembayaran!! + transaksi.nominalPpn!! - transaksi.nominalDiskon!!)))
                                }
                            }
                        }).show()
            }
            /*Setting nilai diskon*/
            layoutDiskon.setOnClickListener {
                val layout = LayoutInflater.from(this).inflate(R.layout.layout_input_dialog, null, false)
                layout.textInput.hint = "Diskon ( % )"
                layout.textInput.editText?.inputType = InputType.TYPE_CLASS_NUMBER
                layout.textInput.editText?.setText(transaksi?.diskon?.toInt().toString())
                MaterialAlertDialogBuilder(this)
                    .setView(layout)
                    .setNegativeButton(
                        "Batal",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .setPositiveButton(
                        "Oke",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            if (checkInput(layout.textInput)) {
                                if (transaksi != null) {
                                    var diskon = layout.textInput.editText?.text.toString().toDouble()
                                    var totalDiskon = (diskon / 100) * transaksi.totalPembayaran!!
                                    var subTotal = (transaksi.totalPembayaran!! + transaksi.nominalPpn!!) - totalDiskon
                                    tvDiskon.setText(numberToCurrency(totalDiskon))
                                    tvSubTotal.setText(numberToCurrency(subTotal))
                                    tvKembalian.setText(numberToCurrency(transaksi.bayar!! - (subTotal)))
                                    transaksi.diskon = diskon
                                    transaksi.nominalDiskon = totalDiskon
                                    transaksi.save()
                                }
                            }
                        }).show()
            }
            setInfoTransaksi(transaksi)
        }
    }

    /*Fungsi untuk setting data transaksi*/
    fun setInfoTransaksi(transaksi: Transaksi?) {
        if (transaksi != null) {
            btnCheckout.isEnabled = true
            tvTanggal.setText(SimpleDateFormat("dd-MM-yyyy hh:mm:ss a",Locale.getDefault()).format(Date().time))
            tvNamaPembeli.setText(transaksi.namaPembeli)
            tvMethodPay.setText(transaksi.metodePembayaran?.nama)
            var subTotal = (transaksi.totalPembayaran!!+transaksi.nominalPpn!!)-transaksi.nominalDiskon!!
            tvTotalBayar.setText(
                numberToCurrency(transaksi.totalPembayaran!!)
            )
            tvPpn.setText(
                numberToCurrency(transaksi.nominalPpn!!)
            )
            tvDiskon.setText(
                numberToCurrency(transaksi.nominalDiskon!!)
            )
            tvSubTotal.setText(
                numberToCurrency(subTotal)
            )
            tvBayar.setText(
                numberToCurrency(transaksi.bayar!!)
            )
            tvKembalian.setText(
                numberToCurrency(transaksi.bayar!!-subTotal)
            )
            val item = SugarRecord.find(ItemTransaksi::class.java, "id_transaksi = ?", "${transaksi.id}").firstOrNull()
            if (item == null) {
                btnCheckout.isEnabled = false
            } else {
                btnCheckout.isEnabled = true
            }
        }else{
            btnCheckout.isEnabled = false
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(currentFocus != null){
            this.hideSoftKeyboard()
        }
        return super.dispatchTouchEvent(ev)
    }
}