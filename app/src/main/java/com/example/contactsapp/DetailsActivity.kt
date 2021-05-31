package com.example.contactsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val avatarView = findViewById<ImageView>(R.id.detailsAvatar)
        val nameView = findViewById<TextView>(R.id.detailsName)
        val onlineView = findViewById<TextView>(R.id.detailsOnlineStatus)
        val gmailView = findViewById<TextView>(R.id.detailsGmail)

        (intent.getParcelableExtra<Contact>("contact"))?.let {
            val iconPixelSize = (resources.displayMetrics.density * 200).toInt()
            Picasso.get()
                .load(it.avatar)
                .resize(iconPixelSize, iconPixelSize).into(avatarView)
            nameView.setText(it.name)
            if (it.accountStatus == false)
            {
                onlineView.setText("Offline")
            }
            gmailView.setText(it.email)
        }

    }
}