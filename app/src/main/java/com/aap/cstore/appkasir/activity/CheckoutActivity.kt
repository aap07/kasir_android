package com.aap.cstore.appkasir.activity

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.models.*
import com.aap.cstore.appkasir.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.layout_table_row.view.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CheckoutActivity : AppCompatActivity() {
    var mBluetoothAdapter: BluetoothAdapter? = null
    var applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    lateinit var mBluetoothConnectProgressDialog: ProgressDialog
    lateinit var mBluetoothSocket: BluetoothSocket
    var mBluetoothDevice: BluetoothDevice? = null
    var deviceName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_checkout)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        /*Setting toolbar*/
        setToolbar(this, "Checkout")
        val idMeja = intent.getLongExtra("mejaId",-1)
        val idLayanan = intent.getLongExtra("layananId",-1)
        if (idMeja > 0) {
            val listMeja = SugarRecord.findById(Meja::class.java,idMeja)
            val transaksi = SugarRecord.find(Transaksi::class.java,"status = ? and id_meja = ?","1", idMeja.toString()).firstOrNull()
            val methhodPay = SugarRecord.find(MetodePembayaran::class.java,"nama = ?",transaksi?.metodePembayaran.toString()).firstOrNull()
            /*Setting tabel bukti transaksi*/
            if (transaksi != null) {
                initTransaksi(transaksi)
            }
            btnPrint.setOnClickListener {
                /*Fungsi untuk melakukan print*/
                if (transaksi != null) {
                    printDine(transaksi)
                }
            }
            btnSelesai.setOnClickListener {
                /*Fungsi untuk menyelesaikan pesanan*/
                MaterialAlertDialogBuilder(this)
                    .setMessage("Selesaikan pesanan?")
                    .setNegativeButton(
                        "Tidak",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                        if (transaksi != null) {
                            val itemTransaksi = SugarRecord.find(ItemTransaksi::class.java,"id_transaksi = ?",transaksi.id.toString())
                            val item = itemTransaksi.distinctBy { l -> l.produkId }
                            for (it in item){
                                val produk = SugarRecord.findById(Produk::class.java,it.produkId)
                                produk.totalTerjual = produk.totalTerjual?.plus(it.jumlah!!)
                                produk.save()
                            }
                            transaksi.status = false
                            transaksi.save()
                            listMeja.totalPengunjung = listMeja.totalPengunjung?.plus(1)
                            listMeja.status = false
                            listMeja.save()
                            methhodPay?.totalMetode = methhodPay?.totalMetode?.plus(1)
                            methhodPay?.save()
                            finish()
                        }
                    }).show()
            }
        }
        if (idLayanan > 0) {
            val listLayanan = SugarRecord.findById(Layanan::class.java,idLayanan)
            val transaksi = SugarRecord.find(Transaksi::class.java,"status = ? and id_layanan = ?","1", idLayanan.toString()).firstOrNull()
            val methhodPay = SugarRecord.find(MetodePembayaran::class.java,"nama = ?",transaksi?.metodePembayaran.toString()).firstOrNull()
            /*Setting tabel bukti transaksi*/
            if (transaksi != null) {
                initTransaksi(transaksi)
            }
            btnPrint.setOnClickListener {
                /*Fungsi untuk melakukan print*/
                if (transaksi != null) {
                    printTake(transaksi)
                }
            }
            btnSelesai.setOnClickListener {
                /*Fungsi untuk menyelesaikan pesanan*/
                MaterialAlertDialogBuilder(this)
                    .setMessage("Selesaikan pesanan?")
                    .setNegativeButton(
                        "Tidak",
                        DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                        if (transaksi != null) {
                            transaksi.status = false
                            transaksi.save()
                            listLayanan.totalLayanan = listLayanan.totalLayanan?.plus(1)
                            listLayanan.save()
                            methhodPay?.totalMetode = methhodPay?.totalMetode?.plus(1)
                            methhodPay?.save()
                            finish()
                        }
                    }).show()
            }
        }
        /*Check printer yang tersimpan*/
        checkPairedDevice()
        btnConnectBluetooth.setOnClickListener {
            if (btnConnectBluetooth.text.equals("Connect")) {
                if (mBluetoothAdapter == null) {
                    Toast.makeText(this, "Bluetooth Adapter null", Toast.LENGTH_SHORT).show()
                } else {
                    /*Check bluetooth status*/
                    if (!mBluetoothAdapter!!.isEnabled) {
                        startActivityForResult(
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                            MyConstant.REQUEST_ENABLE_BT
                        )
                    } else {
                        /*Menampilkan list device bluetooth dan printer*/
                        startActivityForResult(
                            Intent(this, BluetoothDeviceListActivity::class.java),
                            MyConstant.REQUEST_CONNECT_DEVICE
                        )
                    }
                }
            } else if (btnConnectBluetooth.text.equals("Disconnect")) {
                /*Memutuskan koneksi printer*/
                if (mBluetoothAdapter != null) mBluetoothAdapter!!.disable()
                if (mBluetoothSocket.isConnected) closeSocket(mBluetoothSocket)
                savePreferences(this, MyConstant.DEVICE_ADDRESS, "")
                tvPrinterStatus.setText("Disconnected")
                btnPrint.isEnabled = false
                btnConnectBluetooth.text = "Connect"
            }
        }
    }

    private fun initTransaksi(transaksi: Transaksi) {
        val profile = SugarRecord.listAll(Profile::class.java).firstOrNull()
        if (profile != null) {
            tvNamaCafe.text = profile.namaToko
            tvAlamatCafe.text = profile.tagLine
        }
        if (transaksi != null) {
            val itemTransaksi = SugarRecord.find(ItemTransaksi::class.java, "id_transaksi = ?", transaksi.id.toString())
            var count = 0
            count += itemTransaksi.count()
            for (item in itemTransaksi) {
                val view = LayoutInflater.from(this).inflate(R.layout.layout_table_row, table, false)
                view.tvNamaProduk.text = item.namaProduk
                view.tvHargaProduk.text = numberToCurrency(item.hargaProduk!!)
                view.tvJumlah.text = item.jumlah.toString()
                view.tvTotal.text = numberToCurrency(item.hargaProduk!! * item.jumlah!!)
                table.addView(view)
            }
            val tanggal = transaksi.tanggalTransaksi
            val time = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").parse(tanggal)
            tvTanggal.text = "Tanggal : " + SimpleDateFormat("dd/MM/yyyy").format(time).toString()
            tvJam.text = "Jam : " + SimpleDateFormat("hh:mm:ss a").format(time).toString()
            val idMeja = intent.getLongExtra("mejaId",-1)
            val idLayanan = intent.getLongExtra("layananId",-1)
            if (idMeja > 0) {
//                val listMeja = SugarRecord.findById(Meja::class.java,idMeja)
                tvLayanan.text = transaksi.namaOrderan + " : Meja Nomor" + transaksi.namaMeja
            }
            if (idLayanan > 0) {
//                val listLayanan = SugarRecord.findById(Layanan::class.java,idLayanan)
                tvLayanan.text = transaksi.namaOrderan + " : " + transaksi.namaLayanan
            }
            textView9.text = "Total ( " + count + " item )"
            tvTotalBayar.text = numberToCurrency(transaksi.totalPembayaran!!)
            textView11.text = "Ppn "+ profile?.ppn.toString() + " %"
            tvPpn.text = numberToCurrency(transaksi.nominalPpn!!)
            tvDiskon.text = numberToCurrency(transaksi.nominalDiskon!!)
            tvSubTotal.text = numberToCurrency((transaksi.totalPembayaran!!+transaksi.nominalPpn!!)-transaksi.nominalDiskon!!)
            tvBayar.text = numberToCurrency(transaksi.bayar!!)
            tvKembalian.text = numberToCurrency(transaksi.bayar!!-((transaksi.totalPembayaran!!+transaksi.nominalPpn!!)-transaksi.nominalDiskon!!))
        } else {
            Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPairedDevice() {
        if (mBluetoothAdapter == null) Toast.makeText(
            this,
            "Bluetooth Adapter null",
            Toast.LENGTH_SHORT
        ).show()
        else {
            if (!mBluetoothAdapter!!.isEnabled)
                startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            else {
                val address = getPreferences(this).getString(MyConstant.DEVICE_ADDRESS,"")!!
                if (address.isNotEmpty()) {
                    mBluetoothDevice = mBluetoothAdapter?.getRemoteDevice(address)
                    deviceName = mBluetoothDevice?.name
                    mBluetoothConnectProgressDialog = ProgressDialog.show(
                        this, "Connecting...",
                        mBluetoothDevice!!.name + "\n" + mBluetoothDevice!!.address,
                        true, false
                    )
                    Thread {
                        try {
                            mBluetoothSocket =
                                mBluetoothDevice?.createInsecureRfcommSocketToServiceRecord(
                                    applicationUUID
                                )!!
                            mBluetoothAdapter?.cancelDiscovery()
                            mBluetoothSocket.connect()
                            mHandler.sendEmptyMessage(0)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            closeSocket(mBluetoothSocket)
                        } finally {
                            Thread.sleep(7000)
                            if (!mBluetoothSocket.isConnected) {
                                mBluetoothConnectProgressDialog.dismiss()
                                runOnUiThread {
                                    Toast.makeText(
                                        this,
                                        "Device tidak merespon!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }.start()
                }
            }
        }

    }

    private fun printDine(transaksi: Transaksi) {
        val profile = SugarRecord.listAll(Profile::class.java).firstOrNull()
        val itemsTrasaksi = SugarRecord.find(ItemTransaksi::class.java, "id_transaksi = ?", "${transaksi?.id}")
        val time = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").parse(transaksi?.tanggalTransaksi)
        var count = 0
        count += itemsTrasaksi.count()
        Thread {
            try {
                val outputStream = mBluetoothSocket.outputStream
                val printer = BluetoothPrinterUtils(outputStream)
                printer.setFontStyle(BluetoothPrinterUtils.f4)
                printer.printText(profile?.namaToko!!, BluetoothPrinterUtils.ALIGN_CENTER)
                printer.setFontStyle(BluetoothPrinterUtils.f1)
                printer.printText(profile?.tagLine!!, BluetoothPrinterUtils.ALIGN_CENTER)
                printer.printNewLines(2)
                printer.printText("Tanggal : " + SimpleDateFormat("dd/MM/yyyy").format(time).toString(), BluetoothPrinterUtils.ALIGN_LEFT)
                printer.printText("Jam : " + SimpleDateFormat("hh:mm:ss a").format(time).toString(), BluetoothPrinterUtils.ALIGN_LEFT)
                val idMeja = intent.getLongExtra("mejaId",-1)
                val idLayanan = intent.getLongExtra("layananId",-1)
                if (idMeja > 0) {
                    printer.printText(transaksi.namaOrderan + " : Meja Nomor " + transaksi.namaMeja, BluetoothPrinterUtils.ALIGN_LEFT)
                }
                if (idLayanan > 0) {
                    printer.printText(transaksi.namaOrderan + " : " + transaksi.namaLayanan, BluetoothPrinterUtils.ALIGN_LEFT)
                }
//                printer.printText("Pelanggan Ke : " + transaksi?.id!!, BluetoothPrinterUtils.ALIGN_LEFT)
                printer.printLine()
                val item = java.lang.String.format(
                    "%1$-16s %2$-9s %3$-4s %4$-9s",
                    "Menu",
                    "Harga",
                    "Qty",
                    "Total"
                )
                printer.printText(item, BluetoothPrinterUtils.ALIGN_CENTER)
                printer.printLineOne()
                for (it in itemsTrasaksi) {
                    val item = java.lang.String.format(
                        "%1$-16s %2$-9s %3$-4s %4$-9s",
                        if(it.namaProduk.toString().length >= 14) it.namaProduk?.substring(0,14) else it.namaProduk.toString(),
                        numberToCurrency(it.hargaProduk!!).removePrefix("Rp. "),
                        it.jumlah.toString(),
                        numberToCurrency(it.jumlah?.times(it.hargaProduk!!)!!).removePrefix("Rp. ")
                    )
                    printer.printText(item)
                }
                printer.printLineOne()
                var str = java.lang.String.format(
                    "%1$-17s %2$-8s %3$-15s",
                    "Total ( " + count + " item )",
                    " ",
                    numberToCurrency(transaksi.totalPembayaran!!)
                )
                printer.printString(str, BluetoothPrinterUtils.ALIGN_LEFT)
                str = java.lang.String.format(
                    "%1$-17s %2$-8s %3$-15s",
                    "Ppn " + profile.ppn + "%",
                    " ",
                    numberToCurrency(transaksi.nominalPpn!!)
                )
                printer.printString(str, BluetoothPrinterUtils.ALIGN_LEFT)
                str = java.lang.String.format(
                    "%1$-17s %2$-8s %3$-15s",
                    "Diskon",
                    " ",
                    numberToCurrency(transaksi.nominalDiskon!!)
                )
                printer.printString(str, BluetoothPrinterUtils.ALIGN_LEFT)
                str = java.lang.String.format(
                    "%1$-17s %2$-8s %3$-15s",
                    "Subtotal",
                    " ",
                    numberToCurrency((transaksi.totalPembayaran!!+transaksi.nominalPpn!!)-transaksi.nominalDiskon!!)
                )
                printer.printString(str, BluetoothPrinterUtils.ALIGN_LEFT)
                str = java.lang.String.format(
                    "%1$-17s %2$-8s %3$-15s",
                    "Bayar",
                    " ",
                    numberToCurrency(transaksi.bayar!!)
                )
                printer.printString(str, BluetoothPrinterUtils.ALIGN_LEFT)
                str = java.lang.String.format(
                    "%1$-17s %2$-8s %3$-15s",
                    "Kembalian",
                    " ",
                    numberToCurrency(transaksi.bayar!!-((transaksi.totalPembayaran!!+transaksi.nominalPpn!!)-transaksi.nominalDiskon!!))
                )
                printer.printString(str, BluetoothPrinterUtils.ALIGN_LEFT)
                printer.printLine()
                printer.printNewLines(1)
                printer.setFontStyle(BluetoothPrinterUtils.f1)
                printer.printText("Terima Kasih", BluetoothPrinterUtils.ALIGN_CENTER)
                printer.printText("Atas Kunjungan Anda", BluetoothPrinterUtils.ALIGN_CENTER)
                printer.printText("Follow Me On Instagram", BluetoothPrinterUtils.ALIGN_CENTER)
                printer.printText("@aap__07 & @c_store07", BluetoothPrinterUtils.ALIGN_CENTER)
                printer.printNewLines(3)

            } catch (e: Exception) {
                Log.e("Bluetooth", "Exe ", e)
            }
        }.start()
    }

    private fun printTake(transaksi: Transaksi) {
        val profile = SugarRecord.listAll(Profile::class.java).firstOrNull()
        val itemsTrasaksi = SugarRecord.find(ItemTransaksi::class.java, "id_transaksi = ?", "${transaksi?.id}")
        val time = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").parse(transaksi?.tanggalTransaksi)
        var count = 0
        count += itemsTrasaksi.count()
        Thread {
            try {
                val outputStream = mBluetoothSocket.outputStream
                val printer = BluetoothPrinterUtils(outputStream)
                printer.setFontStyle(BluetoothPrinterUtils.f4)
                printer.printText(profile?.namaToko!!, BluetoothPrinterUtils.ALIGN_CENTER)
                printer.setFontStyle(BluetoothPrinterUtils.f1)
                printer.printText("- Ngopi Kok Di Rencanain -", BluetoothPrinterUtils.ALIGN_CENTER)
                printer.printNewLines(2)
                printer.printText("Tanggal : " + SimpleDateFormat("dd/MM/yyyy").format(time).toString(), BluetoothPrinterUtils.ALIGN_LEFT)
                printer.printText("Jam : " + SimpleDateFormat("hh:mm:ss a").format(time).toString(), BluetoothPrinterUtils.ALIGN_LEFT)
                printer.printText("Pelanggan Ke : " + transaksi?.id!!, BluetoothPrinterUtils.ALIGN_LEFT)
                printer.printLine()
                val item = java.lang.String.format(
                    "%1$-16s %2$-9s %3$-4s %4$-9s",
                    "Menu",
                    "Harga",
                    "Qty",
                    "Total"
                )
                printer.printText(item, BluetoothPrinterUtils.ALIGN_CENTER)
                printer.printLineOne()
                for (it in itemsTrasaksi) {
                    val item = java.lang.String.format(
                        "%1$-16s %2$-9s %3$-4s %4$-9s",
                        if(it.namaProduk.toString().length >= 14) it.namaProduk?.substring(0,14) else it.namaProduk.toString(),
                        numberToCurrency(it.hargaProduk!!).removePrefix("Rp. "),
                        it.jumlah.toString(),
                        numberToCurrency(it.jumlah?.times(it.hargaProduk!!)!!).removePrefix("Rp. ")
                    )
                    printer.printText(item)
                }
                printer.printLineOne()
                var str = java.lang.String.format(
                    "%1$-17s %2$-8s %3$-15s",
                    "Total ( " + count + " item )",
                    " ",
                    numberToCurrency(transaksi.totalPembayaran!!)
                )
                printer.printString(str, BluetoothPrinterUtils.ALIGN_LEFT)
                str = java.lang.String.format(
                    "%1$-17s %2$-8s %3$-15s",
                    "Ppn " + profile.ppn + "%",
                    " ",
                    numberToCurrency(transaksi.nominalPpn!!)
                )
                printer.printString(str, BluetoothPrinterUtils.ALIGN_LEFT)
                str = java.lang.String.format(
                    "%1$-17s %2$-8s %3$-15s",
                    "Diskon",
                    " ",
                    numberToCurrency(transaksi.nominalDiskon!!)
                )
                printer.printString(str, BluetoothPrinterUtils.ALIGN_LEFT)
                str = java.lang.String.format(
                    "%1$-17s %2$-8s %3$-15s",
                    "Subtotal",
                    " ",
                    numberToCurrency((transaksi.totalPembayaran!!+transaksi.nominalPpn!!)-transaksi.nominalDiskon!!)
                )
                printer.printString(str, BluetoothPrinterUtils.ALIGN_LEFT)
                str = java.lang.String.format(
                    "%1$-17s %2$-8s %3$-15s",
                    "Bayar",
                    " ",
                    numberToCurrency(transaksi.bayar!!)
                )
                printer.printString(str, BluetoothPrinterUtils.ALIGN_LEFT)
                str = java.lang.String.format(
                    "%1$-17s %2$-8s %3$-15s",
                    "Kembalian",
                    " ",
                    numberToCurrency(transaksi.bayar!!-((transaksi.totalPembayaran!!+transaksi.nominalPpn!!)-transaksi.nominalDiskon!!))
                )
                printer.printString(str, BluetoothPrinterUtils.ALIGN_LEFT)
                printer.printLine()
                printer.printNewLines(1)
                printer.setFontStyle(BluetoothPrinterUtils.f1)
                printer.printText("Terima Kasih", BluetoothPrinterUtils.ALIGN_CENTER)
                printer.printText("Atas Kunjungan Anda", BluetoothPrinterUtils.ALIGN_CENTER)
                printer.printText("Follow Us Instagram:", BluetoothPrinterUtils.ALIGN_CENTER)
                printer.printText("Tiba-Tiba Ngopi = @tibatiba_ngopii", BluetoothPrinterUtils.ALIGN_CENTER)
                printer.printText("My Kasir Android = @aap__07", BluetoothPrinterUtils.ALIGN_CENTER)
                printer.printNewLines(3)

            } catch (e: Exception) {
                Log.e("Bluetooth", "Exe ", e)
            }
        }.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MyConstant.REQUEST_CONNECT_DEVICE -> {
                if (resultCode == Activity.RESULT_OK) {
                    mBluetoothDevice =
                        mBluetoothAdapter?.getRemoteDevice(data?.getStringExtra(MyConstant.DEVICE_ADDRESS))
                    deviceName = mBluetoothDevice?.name
                    savePreferences(this, MyConstant.DEVICE_ADDRESS,mBluetoothDevice?.address!!)
                    mBluetoothConnectProgressDialog = ProgressDialog.show(
                        this, "Connecting...",
                        mBluetoothDevice!!.name + "\n" + mBluetoothDevice!!.address,
                        true, false
                    )

                    Thread {
                        try {
                            mBluetoothSocket =
                                mBluetoothDevice?.createInsecureRfcommSocketToServiceRecord(
                                    applicationUUID
                                )!!
                            mBluetoothAdapter?.cancelDiscovery()
                            mBluetoothSocket.connect()
                            mHandler.sendEmptyMessage(0)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            closeSocket(mBluetoothSocket)
                        } finally {
                            Thread.sleep(7000)
                            if (!mBluetoothSocket.isConnected) {
                                mBluetoothConnectProgressDialog.dismiss()
                                runOnUiThread {
                                    Toast.makeText(
                                        this,
                                        "Device tidak merespon!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }.start()
                }
            }
            MyConstant.REQUEST_ENABLE_BT -> {
                if (resultCode == Activity.RESULT_OK) {
                    startActivityForResult(
                        Intent(this, BluetoothDeviceListActivity::class.java),
                        MyConstant.REQUEST_ENABLE_BT
                    )
                }
            }


        }

    }

    private fun closeSocket(nOpenSocket: BluetoothSocket?) {
        try {
            nOpenSocket?.close()
            Log.d("Bluetooth", "SocketClosed")
        } catch (ex: IOException) {
            Log.d("Bluetooth", "CouldNotCloseSocket")
        }
    }

    private val mHandler =
        Handler(Handler.Callback {
            mBluetoothConnectProgressDialog.dismiss()
            tvPrinterStatus.text = "Connected to $deviceName"
            btnPrint.isEnabled = true
            btnConnectBluetooth.text = "Disconnect"
            return@Callback true
        })

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (mBluetoothSocket != null) mBluetoothSocket.close()
        } catch (e: java.lang.Exception) {
            Log.e("Bluetooth", "Exe ", e)
        }
    }
}