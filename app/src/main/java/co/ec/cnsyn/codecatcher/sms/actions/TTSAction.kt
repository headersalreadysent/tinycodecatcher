package co.ec.cnsyn.codecatcher.sms.actions


import android.content.Context
import android.content.res.Resources
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.text.TextUtils.replace
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.ContextCompat.getSystemService
import co.ec.cnsyn.codecatcher.App
import co.ec.cnsyn.codecatcher.R
import co.ec.cnsyn.codecatcher.composables.ParamOptionBox
import co.ec.cnsyn.codecatcher.composables.ParamValueBox
import co.ec.cnsyn.codecatcher.database.relations.ActionDetail
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithActions
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithRegex
import co.ec.cnsyn.codecatcher.helpers.translate
import co.ec.cnsyn.codecatcher.sms.SmsData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.concurrent.thread


class TTSAction : BaseAction {
    private lateinit var textToSpeech: TextToSpeech

    var mode: Int = 0
    var volume: Int = 0

    @OptIn(DelicateCoroutinesApi::class)
    override fun run(catcher: CatcherWithRegex, action: CatcherWithActions, sms: SmsData): Boolean {

        val context = App.context()
        var actionParams = action.params()
        val setupVolume =
            actionParams.keys.contains("adjustVolume") && actionParams["adjustVolume"] == "yes"
        if (setupVolume) {
            println("tts: sound open")
            openSound()
        }
        //get text to speech engine
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Set language for TextToSpeech, if needed
                val locale =
                    if (actionParams.keys.contains("locale")) actionParams["locale"] else ""
                val localeObject = try {
                    Locale.forLanguageTag(locale ?: "")
                } catch (e: Error) {
                    getCurrentLocale(context)
                }
                val result = textToSpeech.setLanguage(localeObject)
                textToSpeech.setPitch(.8F)

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                   /* Toast.makeText(
                        App.context(),
                        translate("action_TTSAction_language_error"),
                        Toast.LENGTH_LONG
                    ).show()*/

                }
                //generate content
                val messageContent = setupTexts(catcher, action, sms)
                textToSpeech.speak(
                    messageContent,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
                if (setupVolume) {
                    GlobalScope.launch {
                        delay(15000)
                        println("tts: sound close")
                        restoreVolume()
                    }
                }

            } else {
                // Handle TextToSpeech initialization fail or not supported

                Toast.makeText(
                    App.context(),
                    translate("action_TTSAction_error"),
                    Toast.LENGTH_LONG
                ).show()

            }
        }

        return true
    }

    /**
     * extract details from sms
     */
    private fun setupTexts(
        catcher: CatcherWithRegex,
        action: CatcherWithActions,
        sms: SmsData
    ): String {
        return try {
            //get values from map
            val map = action.params()
            val matches = extractCode(catcher, sms)
            val messageContent =
                (if (map.keys.contains("ttsContent")) map["ttsContent"] else App.context()
                    .getString(R.string.action_TTSAction_tts_content)) ?: "_sender_   _code_"
            messageContent
                .replace("_code_", matches)
                .replace("_sender_", sms.sender)
                .replace("_body_", sms.body)

        } catch (e: Exception) {
            sms.body
        }
    }

    private fun getCurrentLocale(context: Context): Locale {
        val resources: Resources = context.resources
        val configuration = resources.configuration

        // For Android 24 (Nougat) and above, use getLocales() method
        return configuration.locales[0]
    }

    private fun openSound() {
        val audioManager = App.context().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //store settings
        mode = audioManager.ringerMode
        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        //open sound
        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume * .8).toInt(), 0)
    }


    private fun restoreVolume() {
        val audioManager = App.context().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        //restore
        mode = audioManager.ringerMode
        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        audioManager.ringerMode = mode
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)

    }


    @Composable
    override fun Settings(
        action: ActionDetail, then: (settings: Map<String, String>) -> Unit
    ) {
        var params by remember { mutableStateOf(action.action.params()) }

        Column {
            var labels = stringArrayResource(id = R.array.action_TTSAction_adjust_volume_params)
            ParamOptionBox(
                stringResource(id = R.string.action_TTSAction_adjust_volume),
                params["adjustVolume"] ?: "yes",
                listOf(
                    Pair("yes", labels[0]),
                    Pair("no", labels[1])
                )
            ) {
                val updatable = params.toMutableMap()
                updatable["adjustVolume"] = it
                params = updatable.toMap()
                action.action.updateParam(params)
                then(params)
            }
            val defaultString = stringResource(R.string.action_TTSAction_locale_default)
            var localeList by remember { mutableStateOf(listOf(Pair("", defaultString))) }
            textToSpeech = TextToSpeech(App.context()) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val availableLocales = textToSpeech.availableLanguages
                    val list = mutableListOf(Pair("", defaultString))
                    val appList = availableLocales?.map { locale ->
                        return@map Pair(locale.toLanguageTag(), locale.displayLanguage)
                    } ?: listOf()
                    list.addAll(appList.sortedBy { it.second })
                    localeList = list.toList()
                }
            }
            if (localeList.size > 1) {
                ParamOptionBox(
                    stringResource(id = R.string.action_TTSAction_locale),
                    params["locale"] ?: "",
                    localeList
                ) {
                    val updatable = params.toMutableMap()
                    updatable["locale"] = it
                    params = updatable.toMap()
                    action.action.updateParam(params)
                    then(params)
                }
            }

            val defaultContent = stringResource(id = R.string.action_TTSAction_tts_content)
            ParamValueBox(
                stringResource(id = R.string.action_TTSAction_tts_content_title),
                params["ttsContent"] ?: defaultContent,
                helperText = stringResource(id = R.string.action_TTSAction_tts_content_hint),
            ) {
                val updatable = params.toMutableMap()
                updatable["ttsContent"] = it
                params = updatable.toMap()
                action.action.updateParam(params)
                then(params)
            }


        }
    }
}