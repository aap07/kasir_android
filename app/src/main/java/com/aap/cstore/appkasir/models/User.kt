package com.aap.cstore.appkasir.models

import com.orm.SugarRecord

data class User (
    var nama:String? = null,
    var kontak:String? = null,
    var username:String? = null,
    var password:String? = null,
    var role:String? = null
):SugarRecord<User>(){
    companion object{
        val userAdmin = "Kasir"
        val userSysAdmin = "Admin"
        val userSysSuperAdmin = "Super Admin"
    }
}
