

public fun parser(qs: String): Map<String, Any> {
    var result: Map<String, Any> = mutableMapOf()
    if (qs == "") {
        return  result
    }
    var tokens = qs.split('&')
    for (token in tokens) {
        var (k, v) = token.split('=')
        var items = k.removeSuffix("]").split(Regex("""\]?\["""))
        result += mapOf(items[0] to v)
    }

    return result
}

public fun stringify(obj: Map<String, Any>): String {
    var tokens: List<String> = listOf()
    for ((k, v) in obj) {
        tokens += genTokens(listOf(k), v)
    }
    return tokens.joinToString("&")
}

private fun genTokens(items: List<String>, value: Any?): List<String> {
    var result: List<String> = listOf()
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
                result += genTokens(items + i.toString(), v)
            }
            return result
        }
        is Map<*,*> -> {
            for ((k, v) in value) {
                result += genTokens(items + k.toString(), v)
            }
            return result
        }
        else -> {
            return listOf("${genKey(items)}=${value.toString()}")
        }
    }
}

private fun genKey(items: List<String>): String {
    return "${items[0]}[${items.takeLast(items.size-1).joinToString("][")}]".removeSuffix("[]")
}


private fun assignToResult(result: MutableMap<String, Any>, items: List<String>, value: String): Any {
    if (items.size == 1) {
        result[items[0]] = value
        return result
    }
    if (!result.containsKey(items[0])) {
        if (items.size > 1 && items[1] == "0") {
            result[items[0]] = listOf<String>()
        }else {
            result[items[0]] = mapOf<String, Any>()
        }
    }
    return result
}


