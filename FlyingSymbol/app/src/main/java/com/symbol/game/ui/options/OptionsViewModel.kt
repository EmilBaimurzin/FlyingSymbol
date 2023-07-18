package com.symbol.game.ui.options

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class OptionsViewModel(private val currentSkin: Int): ViewModel() {
    private val _list = MutableLiveData<MutableList<Int>>(mutableListOf())
    val list: LiveData<MutableList<Int>> = _list

    private val _current = MutableLiveData(currentSkin)
    val current: LiveData<Int> = _current

    fun initList(list: MutableList<Int>) {
        _list.postValue(list)
    }

    fun left(callback: (Int) -> Unit) {
        val index = _list.value!!.indexOf(_current.value!!)
        if (index - 1 >= 0) {
            _current.postValue(_list.value!![index - 1])
            callback.invoke(_list.value!![index - 1])
        }
    }

    fun right(callback: (Int) -> Unit) {
        val index = _list.value!!.indexOf(_current.value!!)
        if (index + 1 <= _list.value!!.size - 1) {
            _current.postValue(_list.value!![index + 1])
            callback.invoke(_list.value!![index + 1])
        }
    }
}

class OptionsViewModelFactory(private val currentSkin: Int): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OptionsViewModel(currentSkin) as T
    }
}