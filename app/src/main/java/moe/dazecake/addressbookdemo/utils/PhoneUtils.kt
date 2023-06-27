package moe.dazecake.addressbookdemo.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

object PhoneUtils {

    fun call(phone: String, context: Context) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${phone}")
        }
        context.startActivity(intent)
        Log.i("Call", "Call ${phone}")
    }

}