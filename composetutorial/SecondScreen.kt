package com.example.composetutorial

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import kotlinx.coroutines.Dispatchers

import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.material3.* // Tai androidx.compose.material.* Material Design 2:lle
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.navigation.*
import androidx.navigation.compose.*
import androidx.lifecycle.*
import androidx.room.*
import kotlinx.coroutines.*
import android.content.*
import androidx.activity.result.PickVisualMediaRequest

import androidx.compose.runtime.livedata.observeAsState


import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras


@Composable
fun SecondScreen(navController: NavController, viewModel: UserViewModel) {

    val user by viewModel.user.observeAsState()

    var username by remember { mutableStateOf(user?.username ?: "") }
    var selectedImageUri by remember { mutableStateOf(user?.selectedImageUri ?: "") }

    val context = LocalContext.current
    val openDocumentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        uri: Uri? -> uri?.let {
            try {
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                val contentResolver = context.contentResolver
                contentResolver.takePersistableUriPermission(uri, takeFlags)
                selectedImageUri = uri.toString()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error granting persistable permission")
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Button(
            onClick = { navController.popBackStack() }
        ) {
            Text("Conversation")
        }
        //val contactsSample = listOf(
        Text("User:")

        AsyncImage(
            model = selectedImageUri,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .border(1.6.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                .clickable {
                    openDocumentLauncher.launch(arrayOf("image/*"))
                }
            )

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Enter name..") }
        )
        
        Button(onClick = {
            if (username.isNotEmpty() && selectedImageUri.isNotEmpty()) {
                viewModel.saveUser(username, selectedImageUri)
            }
        }) {
            Text("Save")
        }
    }
}


class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase by lazy {
        Room.databaseBuilder(application, AppDatabase::class.java, "user_profile_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    val user = MutableLiveData<User?>()

    init {
        loadUser()
    }

    fun saveUser(username: String, selectedImageUri: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val newUser = User(username = username, selectedImageUri = selectedImageUri ?: "")
            db.userDao().insertOrUpdateUser(newUser)
            user.postValue(newUser)
        }
    }

    private fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val loadedUser = db.userDao().getLastUser()
            user.postValue(loadedUser)
        }
    }
}
