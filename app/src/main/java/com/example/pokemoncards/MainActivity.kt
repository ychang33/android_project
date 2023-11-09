package com.example.pokemoncards

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.pokemoncards.destinations.CardDetailDestination
import com.example.pokemoncards.destinations.LoginScreenDestination
import com.example.pokemoncards.destinations.SearchScreenDestination
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch


@AndroidEntryPoint
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val viewModel by viewModels<MainViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContent {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }


@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
){
    //navigator.navigate(SearchScreenDestination)
    navigator.navigate(LoginScreenDestination)
}

@Destination
@Composable
fun SearchScreen(
    destinationsNavigator: DestinationsNavigator
){
    var cards by remember { mutableStateOf<List<Data>>(emptyList()) }

    Column {
        SearchBar(onSearch = { newCards -> cards = newCards as List<Data> })

        CardList(cards, destinationsNavigator)
    }
}

@Composable
fun CardList(cards: List<Data>, destinationsNavigator: DestinationsNavigator){

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(10.dp)
    )
    {
        items(cards) { card ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SubcomposeAsyncImage(
                    model = card.images.small,
                    loading = {
                        CircularProgressIndicator()
                    },
                    modifier = Modifier.clickable(){ destinationsNavigator.navigate(CardDetailDestination(1, card))
                        },
                    contentDescription = card.id
                )
                Spacer(Modifier.size(8.dp))
                Text(text="Set: ${card.set.name}")
            }

        }
    }
}

@Composable
fun SearchBar(
    onSearch: (Any?) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    var query by remember { mutableStateOf("") }

    val corountine = rememberCoroutineScope()

    TextField(
        value = query,
        onValueChange = {query = it},
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        placeholder = {
            Text(stringResource(R.string.placeholder_search))
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions (onSearch = {
            corountine.launch{
                val result = PokemonApi.getCard(query)

                if (result != null) {
                    onSearch(result.data)
                }
            }
            focusManager.clearFocus()
        }),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}

@Destination
@Composable
fun LoginScreen(destinationsNavigator: DestinationsNavigator) {
    val context = LocalContext.current
    var isLoginSuccessful by remember { mutableStateOf(false)}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        if (isLoginSuccessful){
            // display UI is login success
        }else{
            LoginSection(Modifier.padding(horizontal = 16.dp), destinationsNavigator) { isSuccess ->
                if (isSuccess){
                    isLoginSuccessful = true
                }else
                {
                    Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                }

            }
        }


    }
}

@Composable
fun LoginSection(
    modifier: Modifier = Modifier, destinationsNavigator: DestinationsNavigator, onLoginClick:(Boolean) -> Unit
) {
    var userid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
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

                val db = Firebase.firestore
                val userRef = db.collection("users").document(userid)

                userRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val userEmail = document.data?.get("email")
                            val userPassword = document.data?.get("password")
                            //Toast.makeText(context, "data: ${document.data?.get("password")}", Toast.LENGTH_SHORT).show()
                            if (userPassword == password) {
                                Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show()
//                                destinationsNavigator.navigate(SearchScreenDestination(1, card)
                                destinationsNavigator.navigate(SearchScreenDestination)
                            }else
                            {
                                //Toast.makeText(context, "Login fail: ${document.data}", Toast.LENGTH_SHORT).show()
                                Toast.makeText(context, "Login fail", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Fail to locate the document", Toast.LENGTH_SHORT).show()
                    }
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