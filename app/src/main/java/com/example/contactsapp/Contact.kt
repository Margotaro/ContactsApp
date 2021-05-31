package com.example.contactsapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Contact(var avatar: String, var accountStatus: Boolean, var name: String, var email: String):
    Parcelable {

}