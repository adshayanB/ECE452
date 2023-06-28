package com.example.farmeraid.transactions

import com.example.farmeraid.snackbar.SnackbarDelegate
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.sign_in.model.SignInModel
import com.example.farmeraid.sign_in.model.getSignInButton

import androidx.lifecycle.viewModelScope
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.navigation.NavRoute
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class TransactionsViewModel {

}