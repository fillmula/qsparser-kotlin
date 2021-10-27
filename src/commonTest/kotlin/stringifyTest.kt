import kotlin.test.Test
import kotlin.test.assertEquals
import com.fillmula.qsparser.stringify

class StringifyTest {
    @Test
    fun testStringifyEncodesIntIntoInt() {
        val result = stringify(mapOf("a" to 5))
        val expected = "a=5"
        assertEquals(expected, result)
    }

    @Test
    fun testStringifyEncodesFloatIntoFloat() {
        val result = stringify(mapOf("a" to 5.5))
        val expected = "a=5.5"
        assertEquals(expected, result)
    }

    @Test
    fun testStringifyEncodesTrueIntoTrue() {
        val result = stringify(mapOf("a" to true))
        val expected = "a=true"
        assertEquals(expected, result)
    }

    @Test
    fun testStringifyEncodesTrueIntoFalse() {
        val result = stringify(mapOf("a" to false))
        val expected = "a=false"
        assertEquals(expected, result)
    }

    @Test
    fun testStringifyEncodesStringIntoString() {
        val result = stringify(mapOf("a" to "b"))
        val expected = "a=b"
        assertEquals(expected, result)
    }

    @Test
    fun testStringifyEncodesWhitespaces() {
        val result = stringify(mapOf("a" to "b c"))
        val expected = "a=b%20c"
        assertEquals(expected, result)
    }

    @Test
    fun testStringifyEncodesSpecialChars() {
        val result = stringify(mapOf("a" to "俊"))
        val expected = "a=%E4%BF%8A"
        assertEquals(expected, result)
    }

    @Test
    fun testStringifyConcatsMultipleItemsWithTheAmpersand() {
        val result = listOf("a=b&c=d", "c=d&a=b").contains(stringify(mapOf("a" to "b", "c" to "d")))
        assertEquals(true, result)
    }

    @Test
    fun testStringifyEncodesDictIntoMultipleEntries() {
        val stringifyStr = stringify(mapOf("a" to mapOf("b" to "c"), "d" to mapOf("e" to "f", "g" to "h")))
        val result = listOf("a[b]=c&d[e]=f&d[g]=h",
                             "d[e]=f&d[g]=h&a[b]=c",
                             "d[g]=h&d[e]=f&a[b]=c",
                             "a[b]=c&d[g]=h&d[e]=f").contains(stringifyStr)
        assertEquals(true, result)
    }

    @Test
    fun testStringifyEncodesListIntoMultipleEntries() {
        val stringifyStr = stringify(mapOf("a" to listOf(1, 2, 3), "b" to listOf("q", "w", "e")))
        val result = listOf("a[0]=1&a[1]=2&a[2]=3&b[0]=q&b[1]=w&b[2]=e",
                             "b[0]=q&b[1]=w&b[2]=e&a[0]=1&a[1]=2&a[2]=3").contains(stringifyStr)
        assertEquals(true, result)
    }

    @Test
    fun testStringifyEncodesNestedItemsIntoALongString() {
        val result = stringify(mapOf("_includes" to listOf(mapOf("favorites" to mapOf("_includes" to listOf("user"))))))
        val expected = "_includes[0][favorites][_includes][0]=user"
        assertEquals(expected, result)
    }
}
