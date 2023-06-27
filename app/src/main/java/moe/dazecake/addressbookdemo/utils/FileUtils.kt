package moe.dazecake.addressbookdemo.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import moe.dazecake.addressbookdemo.model.Contact
import java.io.File

object FileUtils {

    private lateinit var basePath: String

    private lateinit var jsonPath: String

    fun init(context: android.content.Context) {
        basePath = context.filesDir.absolutePath + "/"
        jsonPath = basePath + "contacts.json"
        //判断文件是否存在
        val file = File(jsonPath)
        if (!file.exists()) {
            file.createNewFile()
            val json = Gson().toJson(emptyList<Contact>())
            file.writeText(json)
        }
    }

    fun readFile(filePath: String): String {
        val file = File(filePath)
        val content = StringBuilder()

        file.forEachLine {
            content.append(it)
            content.append("\n")
        }

        return content.toString()
    }

    fun loadContactsFromJson(json: String): MutableList<Contact> {
        val gson = Gson()
        val result = gson.fromJson(json, Array<Contact>::class.java)
        return result.toMutableList()
    }

    fun loadContacts(): MutableList<Contact> {
        val json = readFile(jsonPath)
        return loadContactsFromJson(json)
    }

    fun saveContacts(contacts: List<Contact>) {
        val jsonPath = basePath + "contacts.json"
        val gson = Gson()
        val json = gson.toJson(contacts)
        File(jsonPath).writeText(json)
    }

}