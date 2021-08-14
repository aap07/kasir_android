package com.aap.cstore.appkasir.models

import com.orm.SugarRecord

data class Layanan(
    var nama : String? = null,
    var totalLayanan : Int? = 0
): SugarRecord<Layanan>()
