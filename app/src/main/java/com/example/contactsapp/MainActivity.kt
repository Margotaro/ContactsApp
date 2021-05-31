package com.example.contactsapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


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
            contactList = it.getParcelableArray("contactsArray")?.toList() as List<Contact>
        }
    }

    override fun onStart() {
        super.onStart()
        contactsSimulator = ContactSimulator(this)

        if (contactList == null) {
            contactList = contactsSimulator.generateContactList()
        }
        contactList?.let { contactList ->
            val adapter = ContactListAdapter(this, R.layout.list_elements_holder, contactList,
                rvList.layoutManager as? GridLayoutManager, this)
            rvList.adapter = adapter
        }

        simulateChangesButton.setOnClickListener {
            contactList = contactsSimulator.simulateChanges()
            contactList?.let { contactList ->
                val adapter = ContactListAdapter(this, R.layout.list_elements_holder, contactList,
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
        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item = menu?.findItem(R.id.changelayout)
        if (item?.title == getString(R.string.show_as_list))
            item.title = getString(R.string.show_as_grid)
        else
            item?.title = getString(R.string.show_as_list)
        return super.onPrepareOptionsMenu(menu)
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

