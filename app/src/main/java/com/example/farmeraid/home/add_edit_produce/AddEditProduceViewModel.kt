package com.example.farmeraid.home.add_edit_produce

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.home.add_edit_produce.model.AddEditProduceModel
import com.example.farmeraid.home.add_edit_produce.model.getSubmitButton
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditProduceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val inventoryRepository: InventoryRepository,
    private val snackbarDelegate: SnackbarDelegate,
    private val appNavigator: AppNavigator,
) : ViewModel() {
    private val initialProduceName : String? = savedStateHandle["produceName"]
    private val initialProduceAmount : String? = savedStateHandle["produceAmount"]

    private val _state = MutableStateFlow(AddEditProduceModel.AddEditProduceViewState(
        produceName = initialProduceName,
        produceAmount = initialProduceAmount?.toIntOrNull(),
        submitButtonUiState = getSubmitButton(),
        isAddProduce = initialProduceName == null,
    ))
    val state: StateFlow<AddEditProduceModel.AddEditProduceViewState>
        get() = _state

    private val produceName : MutableStateFlow<String?> = MutableStateFlow(_state.value.produceName)
    private val produceAmount : MutableStateFlow<Int?> = MutableStateFlow(_state.value.produceAmount)
    private val submitButtonUiState : MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.submitButtonUiState)

    init {
        viewModelScope.launch {
            combine(produceName, produceAmount, submitButtonUiState) {
                    produceName: String?, produceAmount: Int?, submitButtonUiState : UiComponentModel.ButtonUiState ->
                AddEditProduceModel.AddEditProduceViewState(
                    produceName = produceName,
                    produceAmount = produceAmount,
                    submitButtonUiState = submitButtonUiState,
                    isAddProduce = initialProduceName == null,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun setProduceName(name : String?) {
        produceName.value = name
    }

    fun setProduceAmount(amount : Int?) {
        produceAmount.value = amount
    }

    fun confirmDeleteProduce() {
        snackbarDelegate.showSnackbar(
            message = "Are you sure you want to delete ${produceName.value}?",
            actionLabel = "Yes",
            onAction = { deleteProduce() }
        )
    }

    private fun deleteProduce() {
        viewModelScope.launch {
            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = true)
            val prodName = produceName.value
            if (prodName == null) {
                snackbarDelegate.showSnackbar("Invalid produce name")
            } else if (initialProduceName != null) {
                when (val deleteResult = inventoryRepository.deleteProduce(prodName)) {
                    is ResponseModel.FAResponse.Success -> {
                        appNavigator.navigateBack()
                    }
                    is ResponseModel.FAResponse.Error -> {
                        snackbarDelegate.showSnackbar(deleteResult.error ?: "Unknown error")
                    }
                }
            }
            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = false)
        }
    }

    fun submitProduce() {
        viewModelScope.launch {
            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = true)
            val prodName = produceName.value
            val prodAmount = produceAmount.value
            if (prodName == null) {
                snackbarDelegate.showSnackbar("Invalid produce name")
            } else if (prodAmount == null) {
                snackbarDelegate.showSnackbar("Invalid produce amount")
            } else {
                when (val addResult = if (initialProduceName == null) inventoryRepository.addNewProduce(prodName, prodAmount) else inventoryRepository.editProduce(prodName, prodAmount)) {
                    is ResponseModel.FAResponse.Error -> {
                        snackbarDelegate.showSnackbar(addResult.error ?: "Unknown error")
                    }
                    else -> {}
                }
            }
            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = false)
            navigateBack()
        }
    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }
}