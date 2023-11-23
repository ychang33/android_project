package com.example.pokemoncards

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import coil.compose.SubcomposeAsyncImage
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
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
    navigator.navigate(LoginScreenDestination)
}

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Destination
@Composable
fun SearchScreen(
    destinationsNavigator: DestinationsNavigator
){
    val viewModel = viewModel{ PokemonViewModel() }

    Scaffold (
        topBar = {SearchBar()},
        floatingActionButton = { FloatingActionButton(onClick = { viewModel.Favourites() })
        {
            Icon(Icons.Filled.Favorite, contentDescription = "Favorites")}
        }
    ){ innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)){
            CardList( destinationsNavigator = destinationsNavigator)}
     }
}

@Composable
fun CardList(destinationsNavigator: DestinationsNavigator){

    val viewModel = viewModel{ PokemonViewModel() }

    if (viewModel.loading)
        {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    else {
        if (viewModel.cards?.isEmpty() == true) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
                contentAlignment = Alignment.Center) {
                Text(text = "No Results",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Black)
            }
        }
        else{
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(10.dp)
        )
        {
            items(viewModel.cards!!) { card ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row() {
                        SubcomposeAsyncImage(
                            model = card.images.small,
                            loading = {
                                CircularProgressIndicator()
                            },
                            modifier = Modifier
                                .weight(0.8f)
                                .clickable() {
                                    destinationsNavigator.navigate(CardDetailDestination(1, card))
                                },
                            contentDescription = card.id
                        )
                        FavoriteIcon(
                            card,
                            modifier = Modifier
                                .weight(0.2f)
                        )
                    }
                    Spacer(Modifier.size(8.dp))
                    Text(text = "${card.set.name}")
                }
            }
        }
    }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val viewModel = viewModel{ PokemonViewModel() }
    var query by remember { mutableStateOf("") }
    val coroutine = rememberCoroutineScope()

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
            viewModel.loading = true
            coroutine.launch{
                val result = PokemonApi.getCard(query)
                if (result != null)
                    viewModel.cards = result.data
                else
                    viewModel.cards = emptyList()
                viewModel.loading = false
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        if (PokemonCardsApp.isLoginSuccessful){
            // display UI is login success
        }else{
            LoginSection(Modifier.padding(horizontal = 16.dp), destinationsNavigator) { isSuccess ->
                if (isSuccess){
                    PokemonCardsApp.isLoginSuccessful = true
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