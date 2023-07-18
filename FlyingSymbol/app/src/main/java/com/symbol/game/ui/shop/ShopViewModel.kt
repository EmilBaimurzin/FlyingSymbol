package com.symbol.game.ui.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShopViewModel: ViewModel() {
    private val _isPage1 = MutableLiveData(true)
    val isPage1: LiveData<Boolean> = _isPage1

    fun changePage() = _isPage1.postValue(!_isPage1.value!!)
}