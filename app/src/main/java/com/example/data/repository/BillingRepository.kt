package com.example.data.repository

import com.example.data.preferences.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class PurchaseState {
    object Idle : PurchaseState()
    object Processing : PurchaseState()
    data class Success(val orderId: String, val purchaseTime: Long) : PurchaseState()
    data class Error(val message: String) : PurchaseState()
}

class BillingRepository(
    private val userPreferences: UserPreferences
) {
    companion object {
        const val PRODUCT_ID_PRO_LIFETIME = "elenio_connect_pro_lifetime"
        const val PRICE_FORMATTED = "Rp59.000"
    }

    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    val purchaseState: StateFlow<PurchaseState> = _purchaseState.asStateFlow()

    val isProUser: Flow<Boolean> = userPreferences.isProUser

    suspend fun purchaseProLifetime(): Boolean {
        _purchaseState.value = PurchaseState.Processing
        delay(1500) // Simulate Google Play Billing checkout flow

        // Successful purchase
        val orderId = "GPA.3308-4109-8812-${System.currentTimeMillis() % 10000}"
        userPreferences.setProUser(true)
        _purchaseState.value = PurchaseState.Success(orderId, System.currentTimeMillis())
        return true
    }

    suspend fun restorePurchases(): Boolean {
        _purchaseState.value = PurchaseState.Processing
        delay(1200) // Query Google Play Billing for active purchases
        userPreferences.setProUser(true)
        _purchaseState.value = PurchaseState.Success("GPA.RESTORED-${System.currentTimeMillis() % 10000}", System.currentTimeMillis())
        return true
    }

    suspend fun resetProForTesting() {
        userPreferences.setProUser(false)
        _purchaseState.value = PurchaseState.Idle
    }

    fun clearPurchaseState() {
        _purchaseState.value = PurchaseState.Idle
    }
}
