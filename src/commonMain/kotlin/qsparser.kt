package com.fillmula.qsparser

import java.net.URLDecoder
import java.net.URLEncoder

fun stringify(obj: Map<String, Any>): String {
    val tokens: MutableList<String> = mutableListOf()
    for ((k, v) in obj) {
        tokens.addAll(genTokens(listOf(k), v))
    }
    return tokens.joinToString("&")
}

private fun encode(str: String): String {
    return URLEncoder.encode(str, "utf-8").replace("+", "%20")
}

private fun decode(str: String): String {
    return URLDecoder.decode(str, "utf-8")
}

private fun escapeNullString(value: String): String {
    when (value) {
        "nil" -> {
            return "`nil`"
        }
        "null" -> {
            return "`null`"
        }
        "Null" -> {
            return "`Null`"
        }
        "NULL" -> {
            return "`NULL`"
        }
        "None" -> {
            return "`None`"
        }
        else -> {
            return value
        }
    }
}

private fun unescapeNullString(value: String): String? {
    when (value) {
        "`nil`" -> {
            return "nil"
        }
        "`null`" -> {
            return "null"
        }
        "`Null`" -> {
            return "Null"
        }
        "`NULL`" -> {
            return "NULL"
        }
        "`None`" -> {
            return "None"
        }
        "nil" -> {
            return null
        }
        "nil" -> {
            return null
        }
        "null" -> {
            return null
        }
        "Null" -> {
            return null
        }
        "NULL" -> {
            return null
        }
        "None" -> {
            return null
        }
        else -> {
            return value
        }
    }
}

private fun genTokens(items: List<String>, value: Any?): List<String> {
    val result: MutableList<String> = mutableListOf()
    when (value) {
        true -> {
            return listOf("${genKey(items)}=true")
        }
        false -> {
            return listOf("${genKey(items)}=false")
        }
        null -> {
            return listOf("${genKey(items)}=null")
        }
        is List<*> -> {
            value.forEachIndexed { i, v ->
                result.addAll(genTokens(items + i.toString(), v))
            }
            return result
        }
        is Map<*,*> -> {
            for ((k, v) in value) {
                result.addAll(genTokens(items + k.toString(), v))
            }
            return result
        }
        else -> {
            return mutableListOf("${genKey(items)}=${escapeNullString(encode(value.toString()))}")
        }
    }
}

private fun genKey(items: List<String>): String {
    return "${items[0]}[${items.takeLast(items.size-1).joinToString("][")}]".removeSuffix("[]")
}

fun parse(qs: String): Map<String, Any> {
    val result: MutableMap<String, Any> = mutableMapOf()
    if (qs == "") {
        return result
    }
    val tokens = qs.split('&')
    for (token in tokens) {
        val (key, value) = token.split('=')
        val items = key.removeSuffix("]").split(Regex("""]?\["""))
        assignToMap(result, items, value)
    }
    return result
}

private fun assignToMap(result: MutableMap<String, Any>, items: List<String>, value: String): Any {
    if (items.size == 1) {
        result[items[0]] = unescapeNullString(decode(value))
        return result
    }
    if (!result.containsKey(items[0])) {
        if ((items.size > 1) && (items[1] == "0")) {
            result[items[0]] = mutableListOf<Any>()
        } else {
            result[items[0]] = mutableMapOf<String, Any>()
        }
    }
    val firstResult = result[items[0]]
    if (firstResult is MutableMap<*, *>) {
        @Suppress("UNCHECKED_CAST")
        assignToMap(firstResult as MutableMap<String, Any>, items.subList(1, items.size), value)
    } else if (firstResult is MutableList<*>) {
        @Suppress("UNCHECKED_CAST")
        assignToList(firstResult as MutableList<Any>, items.subList(1, items.size), value)
    }
    return result
}

private fun assignToList(result: MutableList<Any>, items: List<String>, value: String): Any {
    if (items.size == 1) {
        result.add(unescapeNullString(decode(value)) )
        return result
    }
    if (items[0].toInt() >= result.size) {
        if ((items.size > 1) && (items[1] == "0")) {
            result.add(mutableListOf<Any>())
        } else {
            result.add(mutableMapOf<String, Any>())
        }
    }
    val index = items[0].toInt()
    if (result[index] is MutableMap<*, *>) {
        @Suppress("UNCHECKED_CAST")
        assignToMap(result[index] as MutableMap<String, Any>, items.subList(1, items.size), value)
    } else if (result[index] is MutableList<*>) {
        @Suppress("UNCHECKED_CAST")
        assignToList(result[index] as MutableList<Any>, items.subList(1, items.size), value)
    }
    return result
}
