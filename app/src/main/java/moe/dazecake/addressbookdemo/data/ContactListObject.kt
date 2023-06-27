package moe.dazecake.addressbookdemo.data

import moe.dazecake.addressbookdemo.model.Contact
import moe.dazecake.addressbookdemo.model.ContactInfo
import moe.dazecake.addressbookdemo.utils.FileUtils

object ContactListObject {
    private var it: MutableList<Contact> = FileUtils.loadContacts()

    fun reLoad() {
        it = FileUtils.loadContacts()
    }

    fun save() {
        FileUtils.saveContacts(it)
    }

    fun add(contact: Contact) {
        it = (it + contact).toMutableList()
    }

    fun remove(contact: Contact) {
        it = (it - contact).toMutableList()
    }

    fun update(contact: Contact) {
        it = it.map {
            if (it.name == contact.name) {
                contact
            } else {
                it
            }
        }.toMutableList()
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