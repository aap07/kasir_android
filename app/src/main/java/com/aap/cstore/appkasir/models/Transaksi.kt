package com.aap.cstore.appkasir.models

import com.orm.SugarRecord

data class Transaksi(
    var status: Boolean? = false,
    var tanggalTransaksi: String? = null,
    var namaPembeli: String? = "n/a",
    var idOrder: Long? = 0,
    var namaOrderan: String? = null,
    var idLayanan: Long? = 0,
    var namaLayanan: String? = null,
    var idMeja: Long? = 0,
    var namaMeja: String? = null,
    var totalPembayaran: Double? = 0.0,
    var bayar: Double? = 0.0,
    var diskon: Double? = 0.0,
    var nominalPpn: Double? = 0.0,
    var nominalDiskon: Double? = 0.0
): SugarRecord<Transaksi>()
