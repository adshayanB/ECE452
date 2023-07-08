package com.example.farmeraid.farm_selection

import androidx.lifecycle.ViewModel
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
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
