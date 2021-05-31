package com.example.contactsapp

import android.content.ContentResolver
import android.content.Context
import android.net.Uri

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
            contactList[i].accountStatus = !contactList[i].accountStatus
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
}