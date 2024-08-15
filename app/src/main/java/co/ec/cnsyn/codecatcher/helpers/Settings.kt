package co.ec.cnsyn.codecatcher.helpers

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class Settings(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFERENCES_NAME = "AppSettings"
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun publish(key:String, value:Any?){
        GlobalScope.launch {
            EventBus.publish(SettingsChange(key,value))
        }
    }

    // Store String value
    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
        publish(key,value)
    }

    // Retrieve String value
    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    // Store Int value
    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
        publish(key,value)
    }

    // Retrieve Int value
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    // Store Boolean value
    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
        publish(key,value)
    }

    // Retrieve Boolean value
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    // Store Float value
    fun putFloat(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
        publish(key,value)
    }

    // Retrieve Float value
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    // Remove a setting
    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
        publish(key,null)
    }

    // Remove a setting
    fun purge(key: String) {
        sharedPreferences.all.forEach {
            if (it.key.contains(key)) {
                remove(it.key)
            }
        }
    }

    // Clear all settings
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}