package moe.dazecake.addressbookdemo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Preview(showBackground = true)
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
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
                // 侧滑删除所需State
                val dismissState = rememberDismissState()
                // 按指定方向触发删除后的回调，在此处变更具体数据
                if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                    ContactListObject.remove(contact)
                    ContactListObject.save()
                }
                SwipeToDismiss(
                    state = dismissState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement()
                        .height(80.dp),
                    // 下面这个参数为触发滑动删除的移动阈值
                    dismissThresholds = { direction ->
                        FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.15f else 0.5f)
                    },
                    // 允许滑动删除的方向
                    directions = setOf(DismissDirection.EndToStart),
                    background = {
                        val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                DismissValue.Default -> Color.LightGray
                                DismissValue.DismissedToEnd -> Color.Green
                                DismissValue.DismissedToStart -> Color.Red
                            }
                        )
                        val alignment = when (direction) {
                            DismissDirection.EndToStart -> Alignment.CenterStart
                            DismissDirection.StartToEnd -> Alignment.CenterEnd
                        }
                        val icon = when (direction) {
                            DismissDirection.EndToStart -> Icons.Default.Delete
                            DismissDirection.StartToEnd -> Icons.Default.Done
                        }
                        val scale by animateFloatAsState(
                            if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                        )

                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = alignment
                        ) {
                            Icon(
                                icon,
                                contentDescription = "Localized description",
                                modifier = Modifier.scale(scale).align(Alignment.CenterEnd)
                            )
                        }
                    }
                ) {
                    ContactItem(contact, context)
                }
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
                        ContactListObject.save()
                        showCreateContactDialog = false
                        Log.i("新增联系人", "name:$name, phone:$phone")
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
    LaunchedEffect(key1 = Unit) {
        FileUtils.init(context)
        ContactListObject.init()
//        ContactListObject.add(Contact("张三", "1234567890"))
//        ContactListObject.add(Contact("李四", "9876543210"))
    }


}

@Composable
fun ContactItem(contact: Contact, context: Context) {
    Box(
        modifier = Modifier
            .clickable {
                PhoneUtils.call(contact.phone, context)
            }
    ) {
        Column(Modifier.align(Alignment.Center).padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = contact.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
                Text(
                    text = contact.phone,
                    Modifier.weight(1f).align(Alignment.Bottom),
                    textAlign = TextAlign.End,
                    color = Color.Gray
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
            title = { Text(text = "新建联系人", modifier = Modifier.padding(bottom = 16.dp)) },
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
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray),
                ) {
                    Text(text = "取消")
                }
            },
        )
    }
}