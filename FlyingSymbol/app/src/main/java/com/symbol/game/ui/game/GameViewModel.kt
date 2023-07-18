package com.symbol.game.ui.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private var gameScope = CoroutineScope(Dispatchers.Default)
    var gameState = false
    var playerSize = 0
    private val _scores = MutableLiveData(0)
    val scores: LiveData<Int> = _scores

    private val _lives = MutableLiveData(3)
    val lives: LiveData<Int> = _lives

    private val _playerXY = MutableLiveData(0f to 0f)
    val playerXY: LiveData<Pair<Float, Float>> = _playerXY

    private val _ringList = MutableLiveData<List<Pair<Float, Float>>>(listOf())
    val ringList: LiveData<List<Pair<Float, Float>>> = _ringList

    private val _starsList = MutableLiveData<List<Pair<Float, Float>>>(listOf())
    val starsList: LiveData<List<Pair<Float, Float>>> = _starsList

    private val _livesList = MutableLiveData<List<Pair<Float, Float>>>(listOf())
    val livesList: LiveData<List<Pair<Float, Float>>> = _livesList

    var starsCallback: (() -> Unit)? = null


    fun initPlayer(x: Float, y: Float) {
        _playerXY.postValue(x to y)
    }

    fun movePlayerUp() {
        val xy = _playerXY.value!!
        if (xy.second - 2 > 0) {
            _playerXY.postValue(xy.first to xy.second - 2)
        }
    }

    fun movePlayerDown(limit: Int) {
        val xy = _playerXY.value!!
        if (xy.second + 2 < limit) {
            _playerXY.postValue(xy.first to xy.second + 2)
        }
    }

    fun generateRings(size: Int, parentX: Float, minY: Float, maxY: Float) {
        gameScope.launch {
            while (true) {
                delay(3000)
                spawnRing(parentX, minY, maxY - size)
            }
        }
        moveRings(size)
    }

    private fun moveRings(size: Int) {
        gameScope.launch {
            while (true) {
                val newList = _ringList.value!!.toMutableList()
                val listToReturn = mutableListOf<Pair<Float, Float>>()
                if (newList.isNotEmpty()) {
                    for (i in newList) {
                        val item = i.first - 10f to i.second
                        if (i.first + (size * 2) - 10 > 0) {
                            listToReturn.add(item)
                        } else {
                            removeLife()
                        }
                        if ((item.first.toInt()..(item.first.toInt() + playerSize * 2)).any {
                                it in (_playerXY.value!!.first.toInt()..(_playerXY.value!!.first + playerSize).toInt())
                            }
                            && (item.second.toInt()..(item.second.toInt() + playerSize)).any {
                                it in (_playerXY.value!!.second.toInt()..(_playerXY.value!!.second + playerSize).toInt())
                            }
                        ) {
                            listToReturn.remove(item)
                            addScores()
                        }
                    }
                }
                _ringList.postValue(listToReturn)
                delay(16)
            }
        }
    }

    private fun addScores() {
        _scores.postValue(_scores.value!! + 10)
    }

    private fun addLife() {
        val currentLives = _lives.value!!
        if (currentLives < 3) {
            _lives.postValue(currentLives + 1)
        }
    }

    private fun removeLife() {
        val currentLives = _lives.value!!
        if (currentLives > 0) {
            _lives.postValue(currentLives - 1)
        }
    }

    fun startGame() {
        gameScope = CoroutineScope(Dispatchers.Default)
    }

    fun stopGame() {
        gameScope.cancel()
    }

    private fun spawnRing(parentX: Float, minY: Float, maxY: Float) {
        val newList = _ringList.value!!.toMutableList()
        newList.add(parentX to (minY.toInt()..maxY.toInt()).random().toFloat())
        _ringList.postValue(newList)
    }

    fun generateStars(size: Int, parentX: Float, minY: Float, maxY: Float) {
        gameScope.launch {
            while (true) {
                delay(3000)
                spawnStar(parentX, minY, maxY - size)
            }
        }
        moveStars(size)
    }

    private fun moveStars(size: Int) {
        gameScope.launch {
            while (true) {
                val newList = _starsList.value!!.toMutableList()
                val listToReturn = mutableListOf<Pair<Float, Float>>()
                if (newList.isNotEmpty()) {
                    for (i in newList) {
                        val item = i.first - 5f to i.second
                        if (i.first + (size * 2) - 5 > 0) {
                            listToReturn.add(item)
                        }
                        if ((item.first.toInt()..(item.first.toInt() + playerSize / 2)).any {
                                it in (_playerXY.value!!.first.toInt()..(_playerXY.value!!.first + playerSize).toInt())
                            }
                            && (item.second.toInt()..(item.second.toInt() + playerSize / 2)).any {
                                it in (_playerXY.value!!.second.toInt()..(_playerXY.value!!.second + playerSize).toInt())
                            }
                        ) {
                            listToReturn.remove(item)
                            starsCallback?.invoke()
                        }
                    }
                }
                _starsList.postValue(listToReturn)
                delay(16)
            }
        }
    }

    private fun spawnStar(parentX: Float, minY: Float, maxY: Float) {
        val newList = _starsList.value!!.toMutableList()
        newList.add(parentX to (minY.toInt()..maxY.toInt()).random().toFloat())
        _starsList.postValue(newList)
    }

    fun generateLives(size: Int, parentX: Float, minY: Float, maxY: Float) {
        gameScope.launch {
            while (true) {
                delay(20000)
                spawnLive(parentX, minY, maxY - size)
            }
        }
        moveLives(size)
    }

    private fun moveLives(size: Int) {
        gameScope.launch {
            while (true) {
                val newList = _livesList.value!!.toMutableList()
                val listToReturn = mutableListOf<Pair<Float, Float>>()
                if (newList.isNotEmpty()) {
                    for (i in newList) {
                        val item = i.first - 5f to i.second
                        if (i.first + (size * 2) - 5 > 0) {
                            listToReturn.add(item)
                        }
                        if ((item.first.toInt()..(item.first.toInt() + playerSize / 2)).any {
                                it in (_playerXY.value!!.first.toInt()..(_playerXY.value!!.first + playerSize).toInt())
                            }
                            && (item.second.toInt()..(item.second.toInt() + playerSize / 2)).any {
                                it in (_playerXY.value!!.second.toInt()..(_playerXY.value!!.second + playerSize).toInt())
                            }
                        ) {
                            listToReturn.remove(item)
                            addLife()
                        }
                    }
                }
                _livesList.postValue(listToReturn)
                delay(16)
            }
        }
    }

    private fun spawnLive(parentX: Float, minY: Float, maxY: Float) {
        val newList = _livesList.value!!.toMutableList()
        newList.add(parentX to (minY.toInt()..maxY.toInt()).random().toFloat())
        _livesList.postValue(newList)
    }
}