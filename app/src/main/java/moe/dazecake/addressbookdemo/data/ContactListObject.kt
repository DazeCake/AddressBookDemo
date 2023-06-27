package moe.dazecake.addressbookdemo.data

import androidx.compose.runtime.mutableStateListOf
import moe.dazecake.addressbookdemo.model.Contact
import moe.dazecake.addressbookdemo.model.ContactInfo
import moe.dazecake.addressbookdemo.utils.FileUtils

object ContactListObject {
    private val it = mutableStateListOf<Contact>()

    fun init() {
        it.clear()
        it.addAll(FileUtils.loadContacts())
    }

    fun save() {
        FileUtils.saveContacts(it)
    }

    fun add(contact: Contact) {
        it.add(contact)
    }

    fun remove(contact: Contact) {
        it.remove(contact)
    }

    fun update(contact: Contact) {
        val old = it.findLast { it.name == contact.name }
        it.replaceAll {
            if(it.name == contact.name){
                contact
            }else{
                it
            }
        }
    }

    fun get(key: String, info:ContactInfo): Contact? {
        return when (info) {
            ContactInfo.NAME -> it.find { it.name == key }
            ContactInfo.PHONE -> it.find { it.phone == key }
        }
    }

    fun getAll(): List<Contact> {
        return it
    }

}