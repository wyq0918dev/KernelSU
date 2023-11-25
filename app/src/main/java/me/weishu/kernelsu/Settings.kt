package me.weishu.kernelsu

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object Settings {

    private const val LANGUAGE = "language"
    private const val NIGHT_MODE = "night_mode"

    private lateinit var mSharedPreferences: SharedPreferences

    fun getPreferences(): SharedPreferences {
        return mSharedPreferences
    }

    fun getPreferencesEditor(): SharedPreferences.Editor {
        return mSharedPreferences.edit()
    }

    fun initialize(context: Context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }
}