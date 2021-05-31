package com.example.contactsapp

import java.security.MessageDigest
import java.util.*

class GravatarGenerator() {
    fun getFromGmail(gmailAddress: String, defaultIconMode: String = "identicon"): String {
        val formattedAddress = gmailAddress.trim().toLowerCase(Locale.getDefault())
        val urlAddress = "https://www.gravatar.com/avatar/" + md5(formattedAddress).toHex() + "?d=" + defaultIconMode
        return urlAddress
    }
    private fun md5(str: String): ByteArray = MessageDigest.getInstance("MD5")
        .digest(str.toByteArray(Charsets.UTF_8))
    private fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }

}