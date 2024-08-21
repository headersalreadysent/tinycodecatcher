package co.ec.cnsyn.codecatcher.helpers


import android.content.Context
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MockSettings(context: Context) : Settings(context) {

    private val mockPreferences = mutableMapOf<String, Any?>()

    companion object {
        private lateinit var instance: MockSettings

        fun get(): MockSettings {
            return instance
        }
    }

    init {
        instance = this
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun publish(key: String, value: Any?) {
        GlobalScope.launch {
            EventBus.publish(SettingsChange(key, value))
        }
    }

    // Store String value
    override fun putString(key: String, value: String) {
        mockPreferences[key] = value
        publish(key, value)
    }

    // Retrieve String value
    override fun getString(key: String, defaultValue: String?): String? {
        return mockPreferences[key] as? String ?: defaultValue
    }

    // Store Int value
    override fun putInt(key: String, value: Int) {
        mockPreferences[key] = value
        publish(key, value)
    }

    // Retrieve Int value
    override fun getInt(key: String, defaultValue: Int): Int {
        return mockPreferences[key] as? Int ?: defaultValue
    }

    // Store Boolean value
    override fun putBoolean(key: String, value: Boolean) {
        mockPreferences[key] = value
        publish(key, value)
    }

    // Retrieve Boolean value
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return mockPreferences[key] as? Boolean ?: defaultValue
    }

    // Store Float value
    override fun putFloat(key: String, value: Float) {
        mockPreferences[key] = value
        publish(key, value)
    }

    // Retrieve Float value
    override fun getFloat(key: String, defaultValue: Float): Float {
        return mockPreferences[key] as? Float ?: defaultValue
    }

    // Remove a setting
    override fun remove(key: String) {
        mockPreferences.remove(key)
        publish(key, null)
    }

    // Remove settings matching a key prefix
    override fun purge(key: String) {
        mockPreferences.keys.filter { it.contains(key) }.forEach {
            remove(it)
        }
    }

    // Clear all settings
    override fun clear() {
        mockPreferences.clear()
    }
}
