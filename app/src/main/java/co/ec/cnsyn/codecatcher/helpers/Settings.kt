package co.ec.cnsyn.codecatcher.helpers

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class Settings(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        private lateinit var instance: Settings
        private const val PREFERENCES_NAME = "AppSettings"

        fun get() : Settings{
            return instance
        }
    }

    init {
        instance=this
    }



    @OptIn(DelicateCoroutinesApi::class)
    private fun publish(key: String, value: Any?) {
        GlobalScope.launch {
            EventBus.publish(SettingsChange(key, value))
        }
    }

    // Store String value
    open fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
        publish(key, value)
    }

    // Retrieve String value
    open fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    // Store Int value
    open fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
        publish(key, value)
    }

    // Retrieve Int value
    open fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    // Store Boolean value
    open fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
        publish(key, value)
    }

    // Retrieve Boolean value
    open fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    // Store Float value
    open fun putFloat(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
        publish(key, value)
    }

    // Retrieve Float value
    open fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    // Remove a setting
    open fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
        publish(key, null)
    }

    // Remove a setting
    open fun purge(key: String) {
        sharedPreferences.all.forEach {
            if (it.key.contains(key)) {
                remove(it.key)
            }
        }
    }

    // Clear all settings
    open fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}