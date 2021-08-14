package com.aap.cstore.appkasir.models

import com.orm.SugarRecord

data class Meja(
    var status: Boolean? = false,
    var nama : String? = null,
    var totalPengunjung : Int? = 0
): SugarRecord<Meja>()
