package com.aap.cstore.appkasir.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aap.cstore.appkasir.R
import com.aap.cstore.appkasir.adapter.RclvLaporan
import com.aap.cstore.appkasir.models.ItemTransaksi
import com.aap.cstore.appkasir.models.Transaksi
import com.aap.cstore.appkasir.utils.*
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.text.pdf.PdfPTable
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.orm.SugarRecord
import kotlinx.android.synthetic.main.activity_laporan.*
import java.io.File
import java.text.SimpleDateFormat

class ActivityMenuLaporan : AppCompatActivity() {
    var transaksi : Transaksi? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_laporan)
        setToolbar(this,"Laporan Bulanan")

        lapBulanan()

    }

    private fun lapBulanan(){
        val transaksi = SugarRecord.listAll(Transaksi::class.java).distinctBy { it.tanggalTransaksi?.substring(3, it.tanggalTransaksi?.indexOf(" ")!!) }
        val sorted = transaksi.sortedBy { l -> l.tanggalTransaksi }.reversed()
        if (transaksi.isNotEmpty()){
            tvEmpty.visibility = View.GONE
            textView12.visibility = View.VISIBLE
            textView12.setText("Laporan Bulanan")
            rclv.apply {
                adapter = RclvLaporan(this@ActivityMenuLaporan, sorted as MutableList<Transaksi>)
                layoutManager = LinearLayoutManager(this@ActivityMenuLaporan)
                setHasFixedSize(true)
            }
        }else{
            tvEmpty.visibility = View.VISIBLE
            textView12.visibility = View.GONE
        }

    }

    fun savePDF(transaksi: Transaksi){
        this.transaksi = transaksi
        Dexter.withContext(this)
            .withPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener{
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if(p0?.areAllPermissionsGranted()!!){
                        createPDF(transaksi)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                    p1?.continuePermissionRequest()
                }

            })
            .check()

    }

    fun createPDF(transaksi: Transaksi){
        val folder = File(Environment.getExternalStorageDirectory(),"Laporan Penjualan")
        if(!folder.exists()) folder.mkdir()
        val date = SimpleDateFormat("dd/MM/yyyy mm:hh:ss").parse(transaksi!!.tanggalTransaksi)
        val title = SimpleDateFormat("MMMM").format(date)
        val fileName = "LAPORAN_BULANAN_$title.pdf"
        val file = File(folder.absolutePath,fileName)
        val doc = PdfUtils(file.absolutePath)
        val bulan = transaksi!!.tanggalTransaksi?.substring(3, transaksi!!.tanggalTransaksi?.indexOf(" ")!!)
        val iTransaksi = SugarRecord.listAll(Transaksi::class.java).filter { l -> l.status == false && l.tanggalTransaksi?.substring(3, l.tanggalTransaksi!!.indexOf(" ")).equals(bulan) }
        var totalPenjualan = 0.0
        var netto = 0.0
        var ppn = 0.0
        var diskon = 0.0
        var totalProduk = 0
        totalProduk += iTransaksi.count()
        var listItemTransaksi = mutableListOf<ItemTransaksi>()

        if(iTransaksi.isNotEmpty()){
            for (it in iTransaksi){
                totalPenjualan += (it.totalPembayaran!!+it.nominalPpn!!)-it.nominalDiskon!!
                netto += it.totalPembayaran!!
                ppn += it.nominalPpn!!
                diskon += it.nominalDiskon!!

                val itemTransaksi = SugarRecord.find(ItemTransaksi::class.java,"id_transaksi = ?",it.id.toString())
                if(itemTransaksi.isNotEmpty()){
                    for(item in itemTransaksi){
                        listItemTransaksi.add(item)
//                        totalProduk += item.jumlah!!
                    }
                }
            }
        }

        doc.addParagraf("Laporan Bulanan", PdfUtils.fontTitle, PdfUtils.align_center)
        doc.addNewEnter()
        doc.addNewEnter()

        var table = PdfPTable(2)
        table.addCell(doc.createCell("Bulan",PdfUtils.fontNormal,PdfUtils.no_border))
        table.addCell(doc.createCell(": "+SimpleDateFormat("MMMM").format(date).toString(),PdfUtils.fontNormal,PdfUtils.no_border))

        table.addCell(doc.createCell("Total Transaksi",PdfUtils.fontNormal,PdfUtils.no_border))
        table.addCell(doc.createCell(": $totalProduk Transaksi",PdfUtils.fontNormal,PdfUtils.no_border))

        table.addCell(doc.createCell("Total Pendapatan",PdfUtils.fontNormal,PdfUtils.no_border))
        table.addCell(doc.createCell(": "+numberToCurrency(totalPenjualan),PdfUtils.fontNormal,PdfUtils.no_border))

        table.addCell(doc.createCell("Total Pendapatan Bersih",PdfUtils.fontNormal,PdfUtils.no_border))
        table.addCell(doc.createCell(": "+numberToCurrency(netto),PdfUtils.fontNormal,PdfUtils.no_border))

        table.addCell(doc.createCell("Total Ppn",PdfUtils.fontNormal,PdfUtils.no_border))
        table.addCell(doc.createCell(": "+numberToCurrency(ppn),PdfUtils.fontNormal,PdfUtils.no_border))

        table.addCell(doc.createCell("Total Diskon",PdfUtils.fontNormal,PdfUtils.no_border))
        table.addCell(doc.createCell(": "+numberToCurrency(diskon),PdfUtils.fontNormal,PdfUtils.no_border))

        table.addCell(doc.createCell("List Produk Terjual",PdfUtils.fontNormal,PdfUtils.no_border))
        table.addCell(doc.createCell(":",PdfUtils.fontNormal,PdfUtils.no_border))
        doc.addTable(table, floatArrayOf(120f, 120f), PdfUtils.align_left)

        doc.addNewEnter()
        table = PdfPTable(2)
        table.addCell(doc.createCell("Nama Produk",PdfUtils.fontHeader,PdfUtils.no_border))
        table.addCell(doc.createCell("Jumlah",PdfUtils.fontHeader,PdfUtils.no_border))

        val itemDistint = listItemTransaksi.distinctBy { l -> l.produkId }
        for(item in itemDistint){
            val itemCount = listItemTransaksi.filter { l -> l.produkId == item.produkId }
            var jml = 0
            itemCount.forEach { l -> jml += l.jumlah!! }
            table.addCell(doc.createCell("${item.namaProduk}",PdfUtils.fontNormal,PdfUtils.no_border))
            table.addCell(doc.createCell("$jml",PdfUtils.fontNormal,PdfUtils.no_border))
        }
        doc.addTable(table, floatArrayOf(200f, 120f), PdfUtils.align_center)
        doc.close()
        val snackbar = Snackbar.make(rclv.rootView,"Laporan berhasil tersimpan!",Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("Tampilkan", View.OnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setType("application/pdf")
            val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if(list.isNotEmpty()){
                val uri =  FileProvider.getUriForFile(this, packageName+".provider", file)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri,"application/pdf")
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Tidak ada aplikasi untuk membuka file!", Toast.LENGTH_SHORT).show()
            }

        }).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MyConstant.REQUEST_OPEN_FILE){
            if(transaksi != null){
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lapBulanan()
    }
}