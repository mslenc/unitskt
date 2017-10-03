package com.xs0.unitskt

import org.junit.Assert.assertEquals
import org.junit.Test

class UnitsTest {
    @Test
    fun testConversionBasics() {
        val oneMeter = Quantity(1.0, METER)
        val hundredCm = oneMeter.convertTo(CENTIMETER)

        assertEquals(100.0, hundredCm.value, 0.0)
        assertEquals(CENTIMETER, hundredCm.unit)
        assertEquals("100.0 cm", hundredCm.toString())
    }
}