package com.example.farmeraid.farm_selection

import androidx.lifecycle.ViewModel
import com.example.farmeraid.data.TransactionRepository
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.farm_selection.model.FarmSelectionModel
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FarmSelectionViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
) : ViewModel() {
    fun navigateToCreateFarm(){
        appNavigator.navigateToCreateFarm()
    }

    fun navigateToJoinFarm(){
        appNavigator.navigateToJoinFarm()
    }
}
