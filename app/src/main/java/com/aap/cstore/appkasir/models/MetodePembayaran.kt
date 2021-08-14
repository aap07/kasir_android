package com.aap.cstore.appkasir.models

import com.orm.SugarRecord

data class MetodePembayaran(
    var nama: String? = null,
    var totalMetode : Int? = 0
) : SugarRecord<MetodePembayaran>(){
    override fun toString(): String {
        return this.nama.toString()
    }
}
