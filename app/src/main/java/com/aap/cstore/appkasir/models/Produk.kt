package com.aap.cstore.appkasir.models

import com.orm.SugarRecord

data class Produk(
    var nama: String? = null,
    var kategori: Kategori? = null,
    var harga: Double? = null,
    var totalTerjual: Int? = 0
) : SugarRecord<Produk>()
