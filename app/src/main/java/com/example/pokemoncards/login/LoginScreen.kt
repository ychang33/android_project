package com.example.pokemoncards.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pokemoncards.PokemonCardsApp
import com.example.pokemoncards.R
import com.example.pokemoncards.destinations.SearchScreenDestination
import com.example.pokemoncards.destinations.SignUpScreenDestination
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination
@Composable
fun LoginScreen(destinationsNavigator: DestinationsNavigator) {
    Box(
        modifier = with (Modifier){
            fillMaxSize()
                .background(color = Color(255, 204, 51))
                .paint(
                    painterResource(id = R.drawable.background),
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.TopCenter)
        })
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(100.dp))
            if (PokemonCardsApp.isLoginSuccessful) {
                destinationsNavigator.navigate(SearchScreenDestination)
            }else{
                LoginSection(Modifier.padding(horizontal = 16.dp), destinationsNavigator)
            }
        }
    }
}

@Composable
fun LoginSection(
    modifier: Modifier = Modifier, destinationsNavigator: DestinationsNavigator
) {
    var userid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        Text(
            text = "Pokémon Card Look Up",
            modifier = Modifier
                .padding(top = 24.dp, bottom = 24.dp),
            fontSize = 24.sp,  // Adjust the font size
            fontWeight = FontWeight.Bold,  // Make it bold
            fontFamily = FontFamily.Serif,  // Choose a specific font family if needed
            color = Color.DarkGray  // Set the text color
        )
        Spacer(Modifier.height(16.dp))
        MyTextField(modifier, aValue = userid,
            onChange = {userid = it},
            aLabel = stringResource(R.string.placeholder_user_id),
            aKeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            aVisualTransformation = VisualTransformation.None)
        Spacer(Modifier.height(16.dp))
        MyTextField(modifier, aValue = password,
            onChange = {password = it},
            aLabel = stringResource(R.string.placeholder_password),
            aKeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            aVisualTransformation = PasswordVisualTransformation() )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val db = Firebase.firestore
                val userRef = db.collection("users").document(userid)

                userRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val userPassword = document.data?.get("password")
                            PokemonCardsApp.isLoginSuccessful = (userPassword == password)
                            if (PokemonCardsApp.isLoginSuccessful) {
                                PokemonCardsApp.currentUserId = userid
                                Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show()
                                destinationsNavigator.navigate(SearchScreenDestination)
                            }else
                            {
                                Toast.makeText(context, "Login fail", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { _ ->
                        Toast.makeText(context, "Fail to locate the document", Toast.LENGTH_SHORT).show()
                    }
            } ){
            // Button text
            Text("Login")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account? Click here to",
                color = Color.Gray
            )
            ClickableText(
                text = AnnotatedString(" Sign Up"),
                onClick = { _ ->
                    destinationsNavigator.navigate(SignUpScreenDestination)
                },
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue
                )
            )
        }
    }
}

@Composable
fun MyTextField(modifier: Modifier = Modifier,
                aValue: String,
                onChange: (String) -> Unit,
                aLabel: String,
                aVisualTransformation: VisualTransformation,
                aKeyboardOptions: KeyboardOptions
) {
    TextField(
        value = aValue,
        onValueChange = onChange,
        label = { Text(aLabel) },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        visualTransformation = aVisualTransformation,
        keyboardOptions = aKeyboardOptions,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}