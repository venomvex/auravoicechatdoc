package com.aura.voicechat.ui.medals

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aura.voicechat.data.model.UpdateMedalDisplayRequest
import com.aura.voicechat.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Medals ViewModel (Live API Connected)
 * Developer: Hawkaye Visions LTD — Pakistan
 * 
 * Fetches medals from the backend API.
 */
@HiltViewModel
class MedalsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {
    
    companion object {
        private const val TAG = "MedalsViewModel"
    }
    
    private val _uiState = MutableStateFlow(MedalsUiState())
    val uiState: StateFlow<MedalsUiState> = _uiState.asStateFlow()
    
    init {
        loadMedals()
    }
    
    private fun loadMedals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = apiService.getUserMedals()
                if (response.isSuccessful) {
                    val data = response.body()
                    val medals = data?.medals?.map { dto ->
                        Medal(
                            id = dto.id,
                            name = dto.name,
                            description = dto.description,
                            category = dto.category,
                            progress = if (dto.earnedAt != null) 100L else 0L,
                            target = 100L,
                            isDisplayed = dto.isDisplayed ?: false
                        )
                    } ?: emptyList()
                    
                    val earned = medals.count { it.progress >= it.target }
                    val displayed = medals.filter { it.isDisplayed }.map { it.id }
                    
                    _uiState.value = MedalsUiState(
                        isLoading = false,
                        medals = medals,
                        totalMedals = medals.size,
                        earnedMedals = earned,
                        displayedMedals = displayed
                    )
                    Log.d(TAG, "Loaded ${medals.size} medals from API")
                } else {
                    Log.e(TAG, "Failed to load medals: ${response.code()}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load medals"
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading medals", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun toggleMedalDisplay(medalId: String) {
        viewModelScope.launch {
            val currentDisplayed = _uiState.value.displayedMedals.toMutableList()
            val hiddenList = mutableListOf<String>()
            val medals = _uiState.value.medals.map { medal ->
                if (medal.id == medalId && medal.progress >= medal.target) {
                    val newDisplayed = !medal.isDisplayed
                    if (newDisplayed && currentDisplayed.size >= 10) {
                        // Max 10 displayed
                        medal
                    } else {
                        if (newDisplayed) {
                            currentDisplayed.add(medalId)
                        } else {
                            currentDisplayed.remove(medalId)
                            hiddenList.add(medalId)
                        }
                        medal.copy(isDisplayed = newDisplayed)
                    }
                } else {
                    if (!medal.isDisplayed) hiddenList.add(medal.id)
                    medal
                }
            }
            
            _uiState.value = _uiState.value.copy(
                medals = medals,
                displayedMedals = currentDisplayed
            )
            
            // Call API to update medal display status
            try {
                val response = apiService.updateMedalDisplay(
                    UpdateMedalDisplayRequest(
                        displayedMedals = currentDisplayed,
                        hiddenMedals = hiddenList
                    )
                )
                if (response.isSuccessful) {
                    Log.d(TAG, "Medal display updated successfully")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating medal display", e)
            }
        }
    }
    
    fun refresh() {
        loadMedals()
    }
    
    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class MedalsUiState(
    val isLoading: Boolean = false,
    val medals: List<Medal> = emptyList(),
    val totalMedals: Int = 0,
    val earnedMedals: Int = 0,
    val displayedMedals: List<String> = emptyList(),
    val error: String? = null
)
