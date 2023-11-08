package com.ajay.encryptdecrypt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.*
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ajay.encryptdecrypt.ui.theme.EncryptDecryptTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EncryptDecryptTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting() {
    val fileDirectory = LocalContext.current.filesDir
    val cryptoManager = CryptoManager()
    EncryptDecryptTheme {
        var messageEncrypt by remember {
            mutableStateOf("")
        }

        var messageToDecrypt by remember {
            mutableStateOf("")
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)) {
            TextField(value = messageEncrypt, onValueChange = {
                messageEncrypt = it
            }
                ,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {Text(text = "Encrypt string")}
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Button(
                    modifier = Modifier.padding(1.dp),
                    onClick = {
                        val bytes = messageEncrypt.encodeToByteArray()
                        val file = File(fileDirectory, "secret.txt")
                        if(!file.exists()){
                            file.createNewFile()
                        }
                        val fos = FileOutputStream(file)
                        messageToDecrypt = cryptoManager.encrypt(bytes ,fos ).decodeToString();
                    }
                ){
                    Text(text ="Encrypt")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    modifier = Modifier.padding(1.dp),
                    onClick = {
                        val file =File(fileDirectory , "secret.txt")
                        messageEncrypt = cryptoManager.decrypt(FileInputStream(file)).decodeToString()
                    }
                ){
                    Text(text ="Decrypt")
                }

            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text=messageToDecrypt
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Greeting()
}