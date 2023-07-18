package com.symbol.game.domain

import android.content.Context
import com.symbol.game.R

class AppPrefs(context: Context) {
    private val sharedPrefs by lazy {
        context.getSharedPreferences(
            "SHARED_PREFS",
            Context.MODE_PRIVATE
        )
    }

    fun getStarsAmount(): Int = sharedPrefs.getInt("STARS", 0)
    fun addStar() {
        sharedPrefs.edit().putInt("STARS", getStarsAmount() + 1).apply()
    }

    fun setVolume(volume: Int) {
        sharedPrefs.edit().putInt("VOLUME", volume).apply()
    }

    fun getVolume(): Int = sharedPrefs.getInt("VOLUME", 50)

    fun getCurrentSymbol(): Int = sharedPrefs.getInt("CURRENT_SYMBOL", 1)
    fun setCurrentSymbol(value: Int) = sharedPrefs.edit().putInt("CURRENT_SYMBOL", value).apply()

    fun getImageBySymbol(value: Int): Int {
        return when (value) {
            1 -> R.drawable.img_symbol_1
            2 -> R.drawable.img_symbol_2
            3 -> R.drawable.img_symbol_3
            4 -> R.drawable.img_symbol_4
            5 -> R.drawable.img_symbol_5
            6 -> R.drawable.img_symbol_6
            7 -> R.drawable.img_symbol_7
            8 -> R.drawable.img_symbol_8
            else -> R.drawable.img_symbol_9
        }
    }

    fun isSymbolBought(value: Int): Boolean = sharedPrefs.getBoolean(value.toString(), false)
    fun buySymbol(value: Int) {
        sharedPrefs.edit().putBoolean(value.toString(), true).apply()
        sharedPrefs.edit().putInt("STARS", getStarsAmount() - 1000).apply()
    }
}