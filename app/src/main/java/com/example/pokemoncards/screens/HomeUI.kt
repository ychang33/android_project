package com.example.pokemoncards.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pokemoncards.R
import com.example.pokemoncards.ui.theme.PokemonCardsTheme


@Composable
fun MyAppPortrait() {
    PokemonCardsTheme {
        Scaffold(
            bottomBar = { BottomNavigation() }
        ) { padding ->
            HomeScreen(Modifier.padding(padding))
        }
    }
}

@Composable
private fun BottomNavigation(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            label = {
                Text(stringResource(R.string.bottom_navigation_text_search))
            },
            selected = false,
            onClick = { Toast.makeText(context, R.string.bottom_navigation_text_search, Toast.LENGTH_SHORT).show()}
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null
                )
            },
            label = {
                Text(stringResource(R.string.bottom_navigation_home))
            },
            selected = true,
            onClick = { Toast.makeText(context, R.string.bottom_navigation_home, Toast.LENGTH_SHORT).show()}
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Face,
                    contentDescription = null
                )
            },
            label = {
                Text(stringResource(R.string.bottom_navigation_image_search))
            },
            selected = false,
            onClick = { Toast.makeText(context, R.string.bottom_navigation_image_search, Toast.LENGTH_SHORT).show()}
        )
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
    ) {
/*        Spacer(Modifier.height(16.dp))
        SearchBar(Modifier.padding(horizontal = 16.dp))*/
        Spacer(Modifier.height(16.dp))
        LoginSection(Modifier.padding(horizontal = 16.dp))
        /*        Spacer(Modifier.height(16.dp))
                HomeSection(title = R.string.align_your_body) {
                    AlignYourBodyRow()
                }
                HomeSection(title = R.string.favorite_collections) {
                    FavoriteCollectionsGrid()
                }
                Spacer(Modifier.height(16.dp))*/
    }
}

@Composable
fun LoginSection(
    modifier: Modifier = Modifier
) {
    var userid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier) {
        Text(
            text = "Pokémon Card Look Up",
            modifier = modifier
                .padding(top = 24.dp, bottom = 24.dp)
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
                //val user = loginViewModel.authenticateUser(userid, password)
                //onLoginClick(user != null)
            } ){
            Text("Login")
        }

    }
}

@Composable
fun MyTextField(modifier: Modifier = Modifier,
                aValue: String,
                onChange: (String) -> Unit,
                aLabel: String,
                aVisualTransformation: VisualTransformation,
                aKeyboardOptions: KeyboardOptions) {
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