package co.ec.cnsyn.codecatcher.values

import co.ec.cnsyn.codecatcher.database.regex.Regex
import co.ec.cnsyn.codecatcher.helpers.translate


private var regexes = listOf(
    Regex(
        regex = "\\b[0-9]{4}\\b",
        key = "4digit",
    ),
    Regex(
        regex = "\\b[0-9]{5}\\b",
        key = "5digit",
    ),
    Regex(
        regex = "\\b[0-9]{6}\\b",
        key = "6digit",
    ),
    Regex(
        regex = "\\b[0-9]{3}-[0-9]{3}\\b",
        key = "6digit_with_dash",
    ),
    Regex(
        regex = "\\bhttps?://([a-zA-Z0-9_-]+\\.)+[a-zA-Z]{2,}(/[a-zA-Z0-9#%&?=._-]*)?\\b",
        key = "url"
    ),
    Regex(
        regex = ".*",
        key = "any"
    )

)

fun regexList(): List<Regex> {
    return regexes.map {
        it.name = translate("regexlist_${it.key}")
        it.description = translate("regexlist_${it.key}_desc")
        return@map it
    }

}