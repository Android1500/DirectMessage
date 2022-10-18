package com.android1500.directmessage

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android1500.directmessage.ui.theme.DirectMessageTheme
import com.togitech.ccp.component.TogiCountryCodePicker
import com.togitech.ccp.data.utils.checkPhoneNumber
import com.togitech.ccp.data.utils.getDefaultLangCode
import com.togitech.ccp.data.utils.getDefaultPhoneCode
import com.togitech.ccp.data.utils.getLibCountries

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DirectMessageTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainScreen(){

    val uriHandler = LocalUriHandler.current
    fun openUrl(url: String) {
        uriHandler.openUri(url)
    }

    var msg by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    var phoneCode by remember { mutableStateOf(getDefaultPhoneCode(context).ifBlank { "fr" }) }
    val phoneNumber = remember { mutableStateOf("") }
    var fullPhoneNumber = "$phoneCode${phoneNumber.value}"
    var defaultLang by remember { mutableStateOf(getDefaultLangCode(context)) }
    val isValidPhone by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text (
                        text = stringResource(R.string.app_name),
                        modifier = Modifier.padding(start = 8.dp)
                    )}
                )
            }, floatingActionButton = {

                FloatingActionButton(
                    onClick = { if (!isPackageInstalled(context.packageManager))
                        Toast.makeText(context,"Whatsapp not installed",Toast.LENGTH_SHORT).show()
                    else
                        openUrl("https://api.whatsapp.com/send?phone=$fullPhoneNumber&text=$msg")
                    }
                ,modifier = Modifier.padding(vertical = 12.dp),

                    ) {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = null
                    )
                }
            },
            content = {
                Column(
                    modifier = Modifier.padding(it)
                ) {

                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)


                    ) {

                        TogiCountryCodePicker(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .fillMaxWidth(),
                            pickedCountry = {
                                phoneCode = it.countryPhoneCode
                                defaultLang = it.countryCode.ifBlank { "tr" }
                            },
                            defaultCountry = getLibCountries().single { it.countryCode == defaultLang },
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                            text = phoneNumber.value,
                            error = isValidPhone,
                            onValueChange = { phoneNumber.value = it }
                        )

                        fullPhoneNumber = "$phoneCode${phoneNumber.value}"

                    }

                    OutlinedTextField (
                        value = msg,
                        onValueChange = {
                            msg = it
                        },
                        label = { Text(stringResource(R.string.write_msg)) },
                        modifier = Modifier
                            .padding(start = 24.dp, end = 24.dp)
                            .fillMaxWidth(),

                        textStyle = MaterialTheme.typography.bodyLarge
                    )


                }
            }
        )
    }
}


fun isPackageInstalled(packageManager: PackageManager): Boolean {
    return try {
        packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}





