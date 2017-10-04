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

    @Test
    fun testETeamUnits() {
        assertEquals(MILLIGRAM, Units.parse("mg"))
        assertEquals(GRAM, Units.parse("g"))
        assertEquals(KILOGRAM, Units.parse("kg"))

        assertEquals(MILLISECOND, Units.parse("ms"))
        assertEquals(SECOND, Units.parse("s"))
        assertEquals(COUNT / SECOND, Units.parse("s^-1"))
        assertEquals(MINUTE, Units.parse("min"))
        assertEquals(COUNT / MINUTE, Units.parse("min^-1"))
        assertEquals(HOUR, Units.parse("h"))
        assertEquals(COUNT / HOUR, Units.parse("h^-1"))
        assertEquals(DAY, Units.parse("d"))
        assertEquals(COUNT / DAY, Units.parse("d^-1"))
        assertEquals(MILLIMETER, Units.parse("mm"))
        assertEquals(CENTIMETER, Units.parse("cm"))
        assertEquals(DECIMETER, Units.parse("dm"))
        assertEquals(METER, Units.parse("m"))
        assertEquals(KILOMETER, Units.parse("km"))
        assertEquals(KELVIN, Units.parse("K"))
        assertEquals(DEG_CELSIUS, Units.parse("degC"))
        assertEquals(DEG_FAHRENHEIT, Units.parse("degF"))
        assertEquals(METER_PER_SECOND, Units.parse("m s^-1"))
        assertEquals(KM_PER_HOUR, Units.parse("km h^-1"))
    }
}
