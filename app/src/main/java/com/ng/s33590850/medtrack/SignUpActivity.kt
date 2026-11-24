package com.ng.s33590850.medtrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ng.s33590850.medtrack.data.patients.Patient
import com.ng.s33590850.medtrack.data.patients.PatientViewModel
import com.ng.s33590850.medtrack.ui.theme.MedTrackTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val patientViewModel: PatientViewModel = ViewModelProvider(this,
                PatientViewModel.PatientViewModelFactory(this@SignUpActivity))[PatientViewModel::class.java]
            MedTrackTheme {
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold( snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SignUpScreen(modifier = Modifier.padding(innerPadding),snackbarHostState,scope,patientViewModel)
                }
            }
        }
    }
}

//sign up screen
@Composable
fun SignUpScreen(modifier: Modifier= Modifier,snackbarHostState: SnackbarHostState,scope: CoroutineScope,patientViewModel: PatientViewModel){
    var errorDialogName by remember { mutableStateOf("") }
    var errorDialogPhone by remember { mutableStateOf("") }
    var errorDialogPassword by remember { mutableStateOf("") }
    var errorDialogConfirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text="Sign Up", fontSize = 30.sp)
            Spacer(modifier.height(5.dp))

            // name input
            OutlinedTextField(
                value = name,
                onValueChange = {name = it},
                label = { Text(text = "Full Name")},
                modifier = Modifier.fillMaxWidth()
            )
            Text(errorDialogName, color = Color.Red)

            // phone number input
            OutlinedTextField(
                value = phoneNumber,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = {phoneNumber = it},
                label = { Text(text = "Phone Number")},
                modifier = Modifier.fillMaxWidth()
            )
            Text(errorDialogPhone, color = Color.Red)

            // password input
            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                label = { Text(text = "Password")},
                modifier = Modifier.fillMaxWidth()
            )
            Text(errorDialogPassword, color = Color.Red)

            // confirm password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {confirmPassword = it},
                label = { Text(text = "Confirm Password")},
                modifier = Modifier.fillMaxWidth()
            )
            Text(errorDialogConfirmPassword, color = Color.Red)

            Spacer(modifier.height(10.dp))
            Button(onClick = {
                errorDialogName = ""
                errorDialogPhone = ""
                errorDialogPassword = ""
                errorDialogConfirmPassword = ""
                var error = false

                // name check
                if (name.isEmpty()){errorDialogName="Fill in this field"}

                scope.launch {
                    // phone number check
                    val phoneContainsChar = phoneNumber.any { it.isLetter() }
                    if (phoneNumber.isEmpty()){
                        error = true
                        errorDialogPhone="Fill in this field"
                    } else if (patientViewModel.getPatientByPhone(phoneNumber) != null){
                        error = true
                        errorDialogPhone = "Use another phone number, this is already in use. "
                    } else if (phoneNumber.length != 10) {
                        error = true
                        errorDialogPhone = "Phone number must have only 10 digits."
                    } else if (phoneNumber.take(2) != "04"){
                        error = true
                        errorDialogPhone = "Phone number must start with 04."
                    }
                    else if (phoneContainsChar){
                        error = true
                        errorDialogPhone = "Phone number must not contain characters"
                    }

                    //password check
                    val passwordContainsChar = password.any { it.isLetter() }
                    val passwordContainsNum = password.any { it.isDigit() }
                    if (password.isEmpty() && confirmPassword.isEmpty()){
                        error = true
                        errorDialogPassword="Fill in both password fields."
                        errorDialogConfirmPassword="Fill in both password fields."
                    } else if (password != confirmPassword){
                        error = true
                        errorDialogConfirmPassword = "Passwords must match. "
                    } else if (password.length < 8 || !(passwordContainsChar && passwordContainsNum)){
                        error = true
                        errorDialogPassword = "Password must have at least 8 characters, " +
                                "and 1 number and 1 letter"
                    }

                    // successfully sign up
                    if (!error){
                        val newIDNum = (patientViewModel.getLargestPatientID() + 1).toString()
                        val newID = "P" + newIDNum
                        val newPatient = Patient(
                            id = newID,
                            name = name,
                            phoneNumber = phoneNumber,
                            password = password
                        )
                        patientViewModel.insertPatient(newPatient)

                        scope.launch {
                            snackbarHostState.showSnackbar("Profile successfully added. Going back to login screen")
                            delay(2000)
                            context.startActivity(Intent(context, LoginActivity::class.java))
                        }
                    }
                }
            }){ Text("Sign Up") }
        }
    }
}