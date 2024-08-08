package co.ec.cnsyn.codecatcher.values

import co.ec.cnsyn.codecatcher.database.regex.Regex
import java.util.Locale


private var regexes = listOf(
    Regex(
        id = 1,
        regex = "[0-9]{4}",
        key = "4digit",
        name = "",
        description = "",
        catchCount = 1,
        status = 1
    ),
    Regex(
        id = 2,
        regex = "[0-9]{5}",
        key = "5digit",
        name = "",
        description = "",
        catchCount = 1,
        status = 1
    ),
    Regex(
        id = 3,
        regex = "[0-9]{6}",
        key = "6digit",
        name = "",
        description = "",
        catchCount = 3,
        status = 1
    )
)

var regexLang = mapOf(
    "tr_TR" to mapOf(
        "4digit" to "4 karakter sayısal ifade",
        "5digit" to "5 karakter sayısal ifade",
        "6digit" to "6 karakter sayısal ifade",
        "4digit-desc" to "4 karakterli sayısal ifadeleri yakalar.",
        "5digit-desc" to "5 karakterli sayısal ifadeleri yakalar",
        "6digit-desc" to "6 karakterli sayısal ifadeleri yakalar",
    ),
    "*" to mapOf(
        "4digit" to "4 digit numeric",
        "5digit" to "5 digit numeric",
        "6digit" to "6 digit numeric",
        "4digit-desc" to "Catches 4 digit numeric codes.",
        "5digit-desc" to "Catches 5 digit numeric codes.",
        "6digit-desc" to "Catches 6 digit numeric codes.",
    ),
)

fun regexList(): List<Regex> {
    val locale = Locale.getDefault().toString()
    val regexLangDetails = regexLang[locale] ?: regexLang["*"]
    if (regexLangDetails == null) {
        return regexes
    }
    return regexes.map {
        it.name = regexLangDetails[it.key] ?: ""
        it.description = regexLangDetails[it.key + "-desc"] ?: ""
        return@map it
    }

}