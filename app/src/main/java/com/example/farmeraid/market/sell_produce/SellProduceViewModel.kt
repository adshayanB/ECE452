package com.example.farmeraid.market.sell_produce

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cesarferreira.pluralize.pluralize
import com.example.farmeraid.data.InventoryRepository
import com.example.farmeraid.data.MarketRepository
import com.example.farmeraid.data.QuotasRepository
import com.example.farmeraid.data.TransactionRepository
import com.example.farmeraid.data.UserRepository
import com.example.farmeraid.data.model.InventoryModel
import com.example.farmeraid.data.model.MarketModel
import com.example.farmeraid.data.model.QuotaModel
import com.example.farmeraid.data.model.ResponseModel
import com.example.farmeraid.data.model.TransactionModel
import com.example.farmeraid.farm.model.getMicButton
import com.example.farmeraid.farm.model.getStopButton
import com.example.farmeraid.market.sell_produce.model.SellProduceModel
import com.example.farmeraid.navigation.AppNavigator
import com.example.farmeraid.snackbar.SnackbarDelegate
import com.example.farmeraid.speech_recognition.SpeechRecognizerUtility
import com.example.farmeraid.uicomponents.models.UiComponentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellProduceViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val marketRepository: MarketRepository,
    private val quotasRepository: QuotasRepository,
    private val transactionRepository: TransactionRepository,
    private val speechRecognizerUtility: SpeechRecognizerUtility,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle,
    private val appNavigator: AppNavigator,
    private val snackbarDelegate: SnackbarDelegate,
): ViewModel() {

    private val marketId : String? = savedStateHandle["marketId"]

    private val _state = MutableStateFlow(
        SellProduceModel.SellProduceViewState(
            submitButtonUiState = UiComponentModel.ButtonUiState(text = "Submit"),
            micFabUiState = getMicButton(),
            micFabUiEvent = getMicButtonEvent()
        )
    )

    val state: StateFlow<SellProduceModel.SellProduceViewState>
        get() = _state

    private val marketName : MutableStateFlow<String> = MutableStateFlow("")
    private val marketIdFlow : MutableStateFlow<String> = MutableStateFlow("")
    private val produceSellList : MutableStateFlow<List<SellProduceModel.ProduceSell>> = MutableStateFlow(listOf())
    private val submitButtonUiState: MutableStateFlow<UiComponentModel.ButtonUiState> = MutableStateFlow(_state.value.submitButtonUiState)
    private val micFabUiState: MutableStateFlow<UiComponentModel.FabUiState> = MutableStateFlow(_state.value.micFabUiState)
    private val micFabUiEvent: MutableStateFlow<UiComponentModel.FabUiEvent> = MutableStateFlow(_state.value.micFabUiEvent)
    private val isLoading : MutableStateFlow<Boolean> = MutableStateFlow(_state.value.isLoading)
    private val speechRes: MutableStateFlow<String> = MutableStateFlow("")

    inline fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        flow5: Flow<T5>,
        flow6: Flow<T6>,
        flow7: Flow<T7>,
        crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R
    ): Flow<R> {
        return kotlinx.coroutines.flow.combine(flow, flow2, flow3, flow4, flow5, flow6, flow7) { args: Array<*> ->
            @Suppress("UNCHECKED_CAST")
            transform(
                args[0] as T1,
                args[1] as T2,
                args[2] as T3,
                args[3] as T4,
                args[4] as T5,
                args[5] as T6,
                args[6] as T7,
            )
        }
    }

    init {
        viewModelScope.launch {
            combine(marketName, marketIdFlow, produceSellList, submitButtonUiState, micFabUiState, micFabUiEvent, isLoading) {
                    marketName: String,
                    marketIdFlow: String,
                    produceSellList: List<SellProduceModel.ProduceSell>,
                    submitButtonUiState: UiComponentModel.ButtonUiState,
                    micFabUiState: UiComponentModel.FabUiState,
                    micFabUiEvent: UiComponentModel.FabUiEvent,
                    isLoading: Boolean ->
                SellProduceModel.SellProduceViewState(
                    marketName = marketName,
                    marketId = marketIdFlow,
                    produceSellList = produceSellList,
                    submitButtonUiState = submitButtonUiState,
                    isLoading = isLoading,
                    micFabUiEvent = micFabUiEvent,
                    micFabUiState = micFabUiState,
                )
            }.collect {
                _state.value = it
            }
        }
    }

    fun fetchData() {
        viewModelScope.launch {
            isLoading.value = true
            val market: MarketModel.Market = marketRepository.getMarket(marketId!!).data!!
            val inventory: MutableMap<String, Int> = inventoryRepository.getInventory().single().data!!
            val quotaRes: ResponseModel.FAResponseWithData<QuotaModel.Quota?> = quotasRepository.getQuota(marketId, market.name)

            val quota: QuotaModel.Quota? = quotaRes.data

            marketName.value = market.name
            marketIdFlow.value = market.id
            produceSellList.value = market.prices.map {(produceName, price) ->
                val produceQuota: QuotaModel.ProduceQuota? = quota?.produceQuotaList?.find { it.produceName == produceName}

                SellProduceModel.ProduceSell(
                    produceName = produceName,
                    produceCount = 0,
                    produceInventory = inventory.getOrDefault(produceName, 0),
                    producePrice = price,
                    produceTotalPrice = 0.0,
                    produceQuotaCurrentProgress = produceQuota?.saleAmount ?: 0,
                    produceQuotaTotalGoal = produceQuota?.produceGoalAmount ?: -1
                )
            }
            isLoading.value = false
        }
    }

    init{ fetchData() }

    init{
        viewModelScope.launch {
            speechRecognizerUtility.speechRecognizerResult.collect {speechText ->
                produceSellList.value = updateCount(speechText)
            }
        }
    }
    private fun updateCount(speechResult: String): List<SellProduceModel.ProduceSell>{

        speechRes.value = speechResult


        return produceSellList.value.map { produce ->
            val soldCount = parseSpeech(speechResult,"(sold|sell)( )*([0-9]+)( )*([a-z]+)(( )*([0-9]+)( )*([a-z]+))*(( )?(and)( )*([0-9]+)( )*([a-z]+)( )*)*", produce)

            val removeCount = parseSpeech(speechResult,"(removed|take away|returned)( )*([0-9]+)( )*([a-z]+)(( )*([0-9]+)( )*([a-z]+))*(( )?(and)( )*([0-9]+)( )*([a-z]+)( )*)*", produce)

            var total = 0
            if((produce.produceCount?:0) == 0 && (soldCount-removeCount) < 0){
                snackbarDelegate.showSnackbar(message = "Do not have sell any ${produce.produceName} to remove!")
            } else if((produce.produceCount?:0) + (soldCount-removeCount) < 0){
                snackbarDelegate.showSnackbar(message = "Do not sell that many ${produce.produceName} to remove!")
            } else{
                total = soldCount-removeCount
            }
            SellProduceModel.ProduceSell(
                produceName = produce.produceName,
                produceCount = total,
                produceInventory = produce.produceInventory,
                producePrice = produce.producePrice,
                produceTotalPrice =  total * produce.producePrice,
                produceQuotaCurrentProgress = produce.produceQuotaCurrentProgress,
                produceQuotaTotalGoal = produce.produceQuotaTotalGoal
            )

        }

    }
    private fun parseSpeech(speechResult:String, regexPattern: String, produce: SellProduceModel.ProduceSell): Int{
        var reAdd = Regex(regexPattern)
        var addMatches = reAdd.findAll(speechResult.lowercase())
        var count = 0
        //Get every add phrase in our speech Result
        addMatches.forEach { addM ->
            val addCommand:String = addM.value
            val prod = produce.produceName.lowercase()
            var reAddAmount = Regex("([0-9]+)( )*(${prod}|${prod.pluralize()})")
            var amountMatches = reAddAmount.findAll(addCommand)
            //Get every instance of the current produce being incremented in the add phrase
            amountMatches.forEach { amountM ->
                val quantity = amountM.groupValues[1].toIntOrNull()
                if (quantity != null){
                    count += quantity
                }
            }

        }
        return count
    }

    private fun getMicButtonEvent(): UiComponentModel.FabUiEvent {
        return UiComponentModel.FabUiEvent(onClick = {startListening()})
    }

    private fun getStopButtonEvent(): UiComponentModel.FabUiEvent {
        return UiComponentModel.FabUiEvent(onClick = {stopListening()})
    }

    fun startListening(){
        if(speechRecognizerUtility.isPermissionGranted()){
            micFabUiState.value = getStopButton()
            micFabUiEvent.value = getStopButtonEvent()
        }
        speechRecognizerUtility.startSpeechRecognition()
    }

    fun stopListening(){
        micFabUiState.value = getMicButton()
        micFabUiEvent.value = getMicButtonEvent()
        speechRecognizerUtility.stopSpeechRecognition()
    }

    fun incrementProduceCount(produceName : String) {
        produceSellList.value = produceSellList.value.map { produceSell ->
            SellProduceModel.ProduceSell(
                produceName = produceSell.produceName,
                produceCount = produceSell.produceCount + if (produceName == produceSell.produceName) 1 else 0,
                produceInventory = produceSell.produceInventory,
                producePrice = produceSell.producePrice,
                produceTotalPrice = produceSell.produceTotalPrice + if (produceName == produceSell.produceName) produceSell.producePrice else 0.0,
                produceQuotaCurrentProgress = produceSell.produceQuotaCurrentProgress,
                produceQuotaTotalGoal = produceSell.produceQuotaTotalGoal
            )
        }
    }

    fun decrementProduceCount(produceName : String) {
        produceSellList.value = produceSellList.value.map { produceSell ->
            SellProduceModel.ProduceSell(
                produceName = produceSell.produceName,
                produceCount = produceSell.produceCount - if (produceName == produceSell.produceName) 1 else 0,
                produceInventory = produceSell.produceInventory,
                producePrice = produceSell.producePrice,
                produceTotalPrice = produceSell.produceTotalPrice - if (produceName == produceSell.produceName) produceSell.producePrice else 0.0,
                produceQuotaCurrentProgress = produceSell.produceQuotaCurrentProgress,
                produceQuotaTotalGoal = produceSell.produceQuotaTotalGoal
            )
        }
    }

    fun setProduceCount(produceName: String, produceCount: Int) {
        produceSellList.value = produceSellList.value.map { produceSell ->
            SellProduceModel.ProduceSell(
                produceName = produceSell.produceName,
                produceCount = if (produceName == produceSell.produceName) produceCount else produceSell.produceCount,
                produceInventory = produceSell.produceInventory,
                producePrice = produceSell.producePrice,
                produceTotalPrice = if (produceName == produceSell.produceName) produceCount * produceSell.producePrice else produceSell.produceTotalPrice,
                produceQuotaCurrentProgress = produceSell.produceQuotaCurrentProgress,
                produceQuotaTotalGoal = produceSell.produceQuotaTotalGoal
            )
        }
    }

    fun submitSell() {
        viewModelScope.launch {
            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = true)

            val produceSellMap: MutableMap<String, Int> = produceSellList.value.associate {
                Pair(it.produceName, it.produceCount)
            }.toMutableMap()

            inventoryRepository.sell(produceSellMap)

            val market: MarketModel.Market = marketRepository.getMarket(marketId!!).data!!

            marketRepository.updateSaleCount(marketId, getTotalEarnings())

            quotasRepository.getQuota(market.id, market.name).let { quota ->
                quota.data?.let { quotaData ->
                    quotasRepository.updateSaleCounts(
                        market.id,
                        produceSellMap.filter { sell -> sell.key in quotaData.produceQuotaList.map { prod -> prod.produceName } }
                    ).let { res ->
                        when (res) {
                            is ResponseModel.FAResponse.Error -> snackbarDelegate.showSnackbar(res.error ?: "Unknown error")
                            else -> {}
                        }
                    }
                } ?: run {
                    if (quota.error != "Quota does not exist") {
                        snackbarDelegate.showSnackbar(quota.error ?: "Unknown error")
                    }
                }
            }

            produceSellList.value.forEach { produceSell ->
                if (produceSell.produceCount > 0) {
                    when(val res = transactionRepository.addTransaction(
                        TransactionModel.Transaction(
                            transactionType = TransactionModel.TransactionType.SELL,
                            produce = InventoryModel.Produce(produceSell.produceName, produceSell.produceCount),
                            pricePerProduce = produceSell.producePrice,
                            location = market.name,
                        )
                    )) {
                        is ResponseModel.FAResponse.Error -> {
                            snackbarDelegate.showSnackbar(res.error ?: "Unknown error")
                        }
                        else -> {}
                    }
                }
            }

            fetchData()

            submitButtonUiState.value = submitButtonUiState.value.copy(isLoading = false)
        }
    }

    fun getTotalEarnings(): Double {
        return produceSellList.value.sumOf { produceSell -> produceSell.produceTotalPrice }
    }

    fun userIsAdmin() : Boolean {
        return userRepository.isAdmin()
    }

    fun navigateToTransactions() {
        appNavigator.navigateToTransactions(TransactionModel.TransactionType.SELL.stringValue)
    }

    fun navigateBack() {
        appNavigator.navigateBack()
    }

    fun navigateToEditMarket(marketId: String) {
        appNavigator.navigateToEditMarket(marketId)
    }
}