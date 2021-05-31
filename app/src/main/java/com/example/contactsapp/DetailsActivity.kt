package com.example.contactsapp

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
                .load(it.avatar + "&s=" + iconPixelSize.toString())
                .resize(iconPixelSize, iconPixelSize).into(avatarView)
            nameView.setText(it.name)
            if (!it.accountStatus)
            {
                onlineView.setText("Offline")
            }
            gmailView.setText(it.email)
        }
        this.supportActionBar?.setTitle("Detalis")
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
/*
    override fun finish() {
        super.finish()
        overridePendingTransition()
    }*/
}