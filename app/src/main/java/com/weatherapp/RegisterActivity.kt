package com.weatherapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.weatherapp.db.fb.FBDatabase
import com.weatherapp.ui.model.User
import com.weatherapp.ui.theme.WeatherAppTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                RegisterPage()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPage(modifier: Modifier = Modifier) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordConf by rememberSaveable { mutableStateOf("") }

    val activity = LocalContext.current as? Activity

    Column(
        modifier = modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cadastre-se",
            fontSize = 24.sp
        )
        Spacer(modifier = modifier.size(24.dp))
        OutlinedTextField(
            value = name,
            label = { Text(text = "Digite seu nome") },
            modifier = modifier.fillMaxWidth(),
            onValueChange = { name = it }
        )
        OutlinedTextField(
            value = email,
            label = { Text(text = "Digite seu e-mail") },
            modifier = modifier.fillMaxWidth(),
            onValueChange = { email = it }
        )
        OutlinedTextField(
            value = password,
            label = { Text(text = "Digite sua senha") },
            modifier = modifier.fillMaxWidth(),
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = passwordConf,
            label = { Text(text = "Confirme sua senha") },
            modifier = modifier.fillMaxWidth(),
            onValueChange = { passwordConf = it },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = modifier.size(24.dp))
        Row(modifier = modifier) {
            Button(
                onClick = {
                    if (password == passwordConf) {
                        Firebase.auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(activity!!) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(activity, "Registro OK!", Toast.LENGTH_LONG).show()
                                    FBDatabase().register(User(name, email))
                                    activity.startActivity(
                                        Intent(activity, MainActivity::class.java).setFlags(
                                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        )
                                    )
                                    activity.finish()
                                } else {
                                    Toast.makeText(activity, "Registro FALHOU!", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(activity, "Senhas não coincidem!", Toast.LENGTH_LONG).show()
                    }
                },
                enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && passwordConf.isNotEmpty()
            ) {
                Text("Registrar")
            }
            Button(
                onClick = { name = ""; email = ""; password = ""; passwordConf = "" }
            ) {
                Text("Limpar")
            }
        }
    }
}