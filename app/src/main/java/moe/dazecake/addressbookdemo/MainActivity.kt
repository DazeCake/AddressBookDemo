package moe.dazecake.addressbookdemo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moe.dazecake.addressbookdemo.data.ContactListObject
import moe.dazecake.addressbookdemo.model.Contact
import moe.dazecake.addressbookdemo.ui.theme.AddressBookDemoTheme
import moe.dazecake.addressbookdemo.utils.FileUtils
import moe.dazecake.addressbookdemo.utils.PhoneUtils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AddressBookDemoTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    AddressBookDemo()
                }
            }
        }
    }
}

@Composable
fun AddressBookDemo() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showCreateContactDialog by remember { mutableStateOf(false) }
    val contacts = ContactListObject.getAll()

    init(context)
    Box {
        //联系人列表
        LazyColumn {
            items(contacts) { contact ->
                ContactItem(contact, context)
            }
        }

        //新增联系人按钮
        FloatingActionButton(
            onClick = {
                showCreateContactDialog = !showCreateContactDialog
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
            if (showCreateContactDialog) {
                CreateContact(
                    onAddClicked = { name, phone ->
                        ContactListObject.add(Contact(name, phone))
                        showCreateContactDialog = false
                        Log.i("联系人",ContactListObject.getAll().toString())
                        coroutineScope.launch {
                            Toast.makeText(
                                context,
                                "已添加联系人",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onCancelClicked = {
                        showCreateContactDialog = false
                        coroutineScope.launch {
                            Toast.makeText(
                                context,
                                "放弃添加",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }
    }

}

@Composable
fun init(context: Context) {
    LaunchedEffect(key1 = Unit){
        FileUtils.init(context)
        ContactListObject.init()
        ContactListObject.add(Contact("John Doe", "1234567890"))
        ContactListObject.add(Contact("Jane Smith", "9876543210"))
    }



}

//@Composable
//fun ContactList(contacts: List<Contact>, context: Context) {
//    LazyColumn {
//        items(contacts) { contact ->
//            ContactItem(contact, context)
//        }
//    }
//}

@Composable
fun ContactItem(contact: Contact, context: Context) {
    Box(
        modifier = Modifier.clickable {
            PhoneUtils.call(contact.phone, context)
        }
    ) {
        Column(Modifier.padding(16.dp)) {
            Row {
                Text(text = contact.name, fontWeight = FontWeight.Bold)
                Text(
                    text = contact.phone,
                    Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )
            }
        }
    }
}

@Composable
fun CreateContact(
    onAddClicked: (String, String) -> Unit,
    onCancelClicked: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onCancelClicked() },
//            title = { Text(text = "新建联系人", modifier = Modifier.padding(bottom = 16.dp)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(text = "姓名") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = number,
                        onValueChange = { number = it },
                        label = { Text(text = "电话") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddClicked(name, number)
                        showDialog = false
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(text = "增加")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onCancelClicked()
                        showDialog = false
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = "取消")
                }
            },
        )
    }
}