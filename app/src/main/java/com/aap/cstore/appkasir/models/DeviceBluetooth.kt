package com.aap.cstore.appkasir.models

data class DeviceBluetooth(
    var name: String? = null,
    var address: String? = null
) {
    override fun toString(): String {
        return "$name\n$address"

    }
}
