package com.aap.cstore.appkasir.models

import com.orm.SugarRecord

data class Kategori(
    var nama : String? = null
): SugarRecord<Kategori>(){
    override fun toString(): String {
        return this.nama.toString()
    }
}
