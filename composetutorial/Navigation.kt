package com.example.composetutorial

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewModel: UserViewModel = viewModel()
    val messages = SampleData.conversationSample
    NavHost(
        navController,
        startDestination = "first"
    ) {
        composable("first") { Conversation(messages, navController, viewModel) }
        composable("second") { SecondScreen(navController, viewModel)}
    }
}
