package com.xs0.unitskt

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class UnitParsingTest {
    @Test
    fun testBasicSanity() {
        for (unit in Units.allNamed()) {
            val fromEncoded = Units.parse(unit.encoded)
            val fromPretty = Units.parse(unit.prettySymbols)

            assertSame(fromEncoded, unit)
            assertSame(fromPretty, unit)
        }
    }

    @Test
    fun testBasics() {
        val expect = METER / SECOND / SECOND

        assertEquals(expect, Units.parse("m s^-2"))
        assertEquals(expect, Units.parse("m/s^2"))
        assertEquals(expect, Units.parse("m^1/s^2"))
        assertEquals(expect, Units.parse("s^-2 / m^-1"))
        assertEquals(expect, Units.parse("s^-2/m^-1"))
        assertEquals(expect, Units.parse("m m m s / m m s s s"))
    }

    // TODO: more..
}
