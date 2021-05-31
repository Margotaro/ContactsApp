package com.example.contactsapp

import android.app.Activity
import android.app.ActivityOptions
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.security.MessageDigest
import java.util.*
import kotlin.text.Charsets.UTF_8
import de.hdodenhof.circleimageview.CircleImageView


class MainActivity : AppCompatActivity(), OnItemClickedListener {
    private lateinit var rvList: RecyclerView
    private lateinit var contactsSimulator: ContactSimulator
    private lateinit var simulateChangesButton: Button
    private var contactList: List<Contact>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvList = findViewById(R.id.rvList) as RecyclerView
        rvList.layoutManager = GridLayoutManager(this, 1)
        simulateChangesButton = findViewById(R.id.simulateChangesButton)

        savedInstanceState?.let {
            contactList = it.getParcelableArray("contactsArray")?.toList() as? List<Contact>
        }
    }

    override fun onStart() {
        super.onStart()
        contactsSimulator = ContactSimulator(this)

        if (contactList == null) {
            contactList = contactsSimulator.generateContactList()
        }
        contactList?.let { contactList ->
            val adapter = ContactListAdapter(this, R.layout.item_listview, contactList,
                rvList.layoutManager as? GridLayoutManager, this)
            rvList.adapter = adapter
        }

        simulateChangesButton.setOnClickListener {
            contactList = contactsSimulator.simulateChanges()
            contactList?.let { contactList ->
                val adapter = ContactListAdapter(this, R.layout.item_listview, contactList,
                    rvList.layoutManager as? GridLayoutManager, this)
                rvList.adapter = adapter
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        (rvList.layoutManager as? GridLayoutManager)?.let {
            if (it.spanCount > 3) {
                it.spanCount = 1
            } else {
                it.spanCount = 5
            }
            rvList.adapter?.let {
                it.notifyItemRangeChanged(0, it.itemCount)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(viewHolder: ContactListAdapter.BindableViewHolder, contact: Contact) {
        val options = viewHolder.transitionOptions(this)

        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("contact", contact)
        startActivity(intent, options.toBundle())
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        val contactArray = contactList?.toTypedArray()
        outState.putParcelableArray("contactsArray", contactArray)
        super.onSaveInstanceState(outState, outPersistentState)
    }
}

class ContactSimulator(val context: Context) {
    var contactList: MutableList<Contact>
    val nameOptions: List<String>

    init {
        contactList = mutableListOf()
        nameOptions = mutableListOf(
            "Ekaterina Pavlovna", "Anastasia", "Tetiana", "Iryna", "Anna", "Svetlana", "Nikolai XX", "Peter", "Andrey", "Pavel", "Vladislav")
        contactList = generateContactList()
    }

    fun generateContactList(): MutableList<Contact> {
        addRandomContact(getRandomNumberInRange(0,20))
        return contactList
    }

    fun simulateChanges(): MutableList<Contact> {
        val numOfContacts = contactList.size - 1
        val randomizer = (0..numOfContacts).shuffled().take(2)
        shuffleNames(randomizer[0], numOfContacts)
        shuffleStatus(randomizer[1], numOfContacts)
        deleteRandomContact(getRandomNumberInRange(1,numOfContacts), numOfContacts)
        addRandomContact(getRandomNumberInRange(0,20))
        return contactList
    }

    fun addRandomContact(count: Int) {
        for (i in 0..count) {
            contactList.add(makeRandomContact())
        }
    }

    fun makeRandomContact(): Contact {
        val nameIndex = getRandomNumberInRange(0, nameOptions.size - 1)
        val name = nameOptions[nameIndex]
        val email = makeEmail(name)
        val avatar = GravatarGenerator().getFromGmail(email)
        val status = if(getRandomNumberInRange(0,1) == 0 ) false else true
        return Contact(avatar, status, name, email)
    }
    fun deleteRandomContact(count: Int, numOfContacts: Int) {
        val deleteIndexes = (0..numOfContacts - 1).shuffled().take(count).toSet().sortedDescending()
        for (i in deleteIndexes) {
            contactList.removeAt(i)
        }
    }

    fun shuffleStatus(count: Int, numOfContacts: Int) {
        val shuffleIndexes = (0..(numOfContacts - 1)).shuffled().take(count)
        for (i in shuffleIndexes) {
            contactList[i].accountStatus = !(contactList[i].accountStatus == true)
        }
    }

    fun shuffleNames(count: Int, numOfContacts: Int) {
        val shuffleIndexes = (0..(numOfContacts - 1)).shuffled().take(count)
        for (i in shuffleIndexes) {
            val nameIndex = getRandomNumberInRange(0, nameOptions.size - 1)
            contactList[i].name = nameOptions[nameIndex]
        }
    }

    fun getRandomNumberInRange(start: Int, end: Int): Int {
        return (start..end).random()
    }

    fun makeEmail(name: String): String {
        return name.split(' ').joinToString(".") + "@gmail.com"
    }

    fun getPetUriStringFromDrawable(imageName: String, context: Context) : String {
        val resources = context.resources

        val packageName = context.packageName
        val imageId = context.getResources().getIdentifier(imageName, "drawable", packageName)
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(imageId))
            .appendPath(resources.getResourceTypeName(imageId))
            .appendPath(resources.getResourceEntryName(imageId))
            .build().toString()
    }
}

class GravatarGenerator() {
    fun getFromGmail(gmailAddress: String, defaultIconMode: String = "identicon"): String {
        val formattedAddress = gmailAddress.trim().toLowerCase(Locale.getDefault())
        val urlAddress = "https://www.gravatar.com/avatar/" + md5(formattedAddress).toHex() + "?d=" + defaultIconMode
        return urlAddress
    }
    private fun md5(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))
    private fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }

}