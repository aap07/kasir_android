package com.aap.cstore.appkasir.models

import com.orm.SugarRecord

data class Profile (
    var statusPpn: Boolean? = false,
    var statusDiskon: Boolean? = false,
    var ppn: Double? = 0.0,
    var namaToko : String? = null,
    var tagLine : String? = null,
    var alamatToko : String? = null
): SugarRecord<Profile>()
