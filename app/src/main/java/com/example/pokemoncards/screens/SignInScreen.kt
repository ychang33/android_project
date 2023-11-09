package com.example.pokemoncards.screens

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pokemoncards.R
import com.example.pokemoncards.core.Utils.Companion.showMessage


@Composable
@ExperimentalComposeUiApi
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel()
/*    ,
    navigateToForgotPasswordScreen: () -> Unit,
    navigateToSignUpScreen: () -> Unit,*/
) {
    val context = LocalContext.current

    Scaffold(
/*        topBar = {
            SignInTopBar()
        },*/
        bottomBar = {
                        BottomNavigation()
                    },
        content = { padding ->
            SignInContent(
                padding = padding,
                signIn = { email, password ->
                    viewModel.signInWithEmailAndPassword(email, password)
                }
/*                ,
                navigateToForgotPasswordScreen = navigateToForgotPasswordScreen,
                navigateToSignUpScreen = navigateToSignUpScreen*/
            )
        }
    )

    SignIn(
        showErrorMessage = { errorMessage ->
            showMessage(context, errorMessage)
        }
    )
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