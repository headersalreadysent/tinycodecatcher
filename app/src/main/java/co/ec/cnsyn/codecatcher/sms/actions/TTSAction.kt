package co.ec.cnsyn.codecatcher.sms.actions


import android.content.Context
import android.content.res.Resources
import android.speech.tts.TextToSpeech
import android.widget.Toast
import co.ec.cnsyn.codecatcher.App
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithActions
import co.ec.cnsyn.codecatcher.database.relations.CatcherWithRegex
import co.ec.cnsyn.codecatcher.helpers.translate
import co.ec.cnsyn.codecatcher.sms.SmsData
import java.util.Locale


class TTSAction : BaseAction {
    private lateinit var textToSpeech: TextToSpeech


    override fun run(catcher: CatcherWithRegex, action: CatcherWithActions, sms: SmsData): Boolean {
        val context = App.context()
        //get text to speech engine
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Set language for TextToSpeech, if needed
                val result = textToSpeech.setLanguage(getCurrentLocale(context))
                textToSpeech.setPitch(.8F)

                if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Toast.makeText(
                        App.context(),
                        translate("action_TTSAction_language_error"),
                        Toast.LENGTH_LONG
                    ).show()
                    // Handle language data missing or not supported
                    error("Cant setup lang")

                }
                //generate content
                val messageContent = setupTexts(catcher, action, sms)
                textToSpeech.speak(
                    messageContent,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )

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

            val messageContent = if (map["content"] == null || map["content"] == "") {
                "_sender_ _code_"
            } else ({
                map["content"]
            }).toString()

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
}