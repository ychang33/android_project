package com.example.pokemoncards.navigation

import com.example.pokemoncards.core.Constants.SIGN_IN_SCREEN

sealed class Screen(val route: String) {
    object SignInScreen: Screen(SIGN_IN_SCREEN)
//    object ForgotPasswordScreen: Screen(FORGOT_PASSWORD_SCREEN)
//    object SignUpScreen: Screen(SIGN_UP_SCREEN)
//    object VerifyEmailScreen: Screen(VERIFY_EMAIL_SCREEN)
//    object ProfileScreen: Screen(PROFILE_SCREEN)
}