import kotlin.test.Test
import kotlin.test.assertEquals
import com.fillmula.qsparser.parse

class ParseTest {

    @Test
    fun testParseDecodesIntIntoString() {
        val result = parse("a=5")
        val expected = mapOf("a" to "5")
        assertEquals(expected, result)
    }

    @Test
    fun testParseDecodesFloatIntoString() {
        val result = parse("a=5.5")
        val expected = mapOf("a" to "5.5")
        assertEquals(expected, result)
    }

    @Test
    fun testParseDecodesTrueIntoString() {
        val result = parse("a=true")
        val expected = mapOf("a" to "true")
        assertEquals(expected, result)
    }

    @Test
    fun testParseDecodesFalseIntoString() {
        val result = parse("a=false")
        val expected = mapOf("a" to "false")
        assertEquals(expected, result)
    }

    @Test
    fun testParseDecodesStringIntoString() {
        val result = parse("a=b")
        val expected = mapOf("a" to "b")
        assertEquals(expected, result)
    }

    @Test
    fun testParseDecodesWhitespaces() {
        val result = parse("a=b c")
        val expected = mapOf("a" to "b c")
        assertEquals(expected, result)
    }

    @Test
    fun testParseDecodesSpecialChars() {
        val result = parse("a=%E4%BF%8A")
        val expected = mapOf("a" to "俊")
        assertEquals(expected, result, "")
    }

    @Test
    fun testParseDecodesMultipleItemsToASingleObject() {
        val result = parse("a=b&c=d")
        val expected = mapOf("a" to "b", "c" to "d")
        assertEquals(expected, result)
    }

    @Test
    fun testParseDecodesEntriesIntoMultipleNestedObjects() {
        val result = parse("a[b]=c&d[e]=f&d[g]=h")
        val expected = mapOf("a" to mapOf("b" to "c"), "d" to mapOf("e" to "f", "g" to "h"))
        assertEquals(expected, result)
    }

    @Test
    fun testParseDecodesListIntoMultipleNestedObject() {
        val result = parse("a[0]=1&a[1]=2&a[2]=3&b[0]=q&b[1]=w&b[2]=e")
        val expected = mapOf("a" to listOf("1", "2", "3"), "b" to listOf("q", "w", "e"))
        assertEquals(expected, result)
    }

    @Test
    fun testParseDecodesDictionaryInLists() {
        val result = parse("a[0][n][0]=John&a[0][n][1]=15&a[1][n][0]=Peter&a[1][n][1]=18&b[0][n][0]=Jack&b[0][n][1]=17")
        val expected = mapOf("a" to listOf(mapOf("n" to listOf("John", "15")), mapOf("n" to listOf("Peter", "18"))),
                              "b" to listOf(mapOf("n" to listOf("Jack", "17"))))
        assertEquals(expected, result)
    }

    @Test
    fun testParseDecodesALongStringIntoNestedItems() {
        val result = parse("_includes[0][favorites][_includes][0]=user")
        val expected = mapOf("_includes" to listOf(mapOf("favorites" to mapOf("_includes" to listOf("user")))))
        assertEquals(expected, result)
    }

    @Test
    fun testParseDecodesEmptyStringIntoEmptyObject() {
        val result = parse("")
        val expected: Map<String, Any> = mapOf()
        assertEquals(expected, result)
    }

    @Test
    fun testParseDecodesNoneIntoNull() {
        assertEquals(mapOf("abc" to null), parse("abc=null"))
        assertEquals(mapOf("abc" to null), parse("abc=nil"))
        assertEquals(mapOf("abc" to null), parse("abc=Null"))
        assertEquals(mapOf("abc" to null), parse("abc=NULL"))
        assertEquals(mapOf("abc" to null), parse("abc=None"))
    }

    @Test
    fun testParseDecodesNullRepresentingStringIntoNullString() {
        assertEquals(mapOf("abc" to "null"), parse("abc=%60null%60"))
        assertEquals(mapOf("abc" to "Null"), parse("abc=%60Null%60"))
        assertEquals(mapOf("abc" to "NULL"), parse("abc=%60NULL%60"))
    }

    @Test
    fun testParseDecodesNilRepresentingStringIntoNilString() {
        assertEquals(mapOf("abc" to "nil"), parse("abc=%60nil%60"))
    }

    @Test
    fun testParseDecodesNoneRepresentingStringIntoNoneString() {
        assertEquals(mapOf("abc" to "None"), parse("abc=%60None%60"))
    }

}
