package co.ec.cnsyn.codecatcher.values

import co.ec.cnsyn.codecatcher.database.regex.Regex
import co.ec.cnsyn.codecatcher.helpers.translate


private var regexes = listOf(
    Regex(
        id = 1,
        regex = "[0-9]{4}",
        key = "4digit",
    ),
    Regex(
        id = 2,
        regex = "[0-9]{5}",
        key = "5digit",
    ),
    Regex(
        id = 3,
        regex = "[0-9]{6}",
        key = "6digit",
    ),
    Regex(
        id = 4,
        regex = """^https?://([a-zA-Z0-9_-]+\.)+[a-zA-Z]{2,}(/[a-zA-Z0-9#%&?=._-]*)?$""",
        key = "url"
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
    )
)

fun regexList(): List<Regex> {
    return regexes.map {
        it.name = translate("regexlist_${it.key}")
        it.description = translate("regexlist_${it.key}_desc")
        return@map it
    }

}