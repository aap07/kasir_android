package com.aap.cstore.appkasir.models

import com.orm.SugarRecord

data class ItemTransaksi(
    var jumlah: Int? = 0,
    var namaProduk: String? = null,
    var hargaProduk: Double? = 0.0,
    var kategori: String? = null,
    var produkId: Long? = 0,
    val idTransaksi: Long? = 0
) : SugarRecord<ItemTransaksi>()
