package com.xs0.unitskt

import org.junit.Assert.assertEquals
import org.junit.Test


class RationalTest {
    @Test
    fun testBasics() {
        val twoThirds = Rational.of(2, 3)
        val fourSixths = Rational.of(4, 6)
        assertEquals(twoThirds, fourSixths)
        assertEquals("2/3", twoThirds.toString())
        assertEquals("2/3", fourSixths.toString())
        assertEquals(2.0/3.0, twoThirds.doubleApprox, 1E-15)

        assertEquals("1/1", (twoThirds / fourSixths).toString())

        assertEquals("7/12", (Rational.of(1, 3) + Rational.of(1, 4)).toString())
        assertEquals("1/12", (Rational.of(1, 3) - Rational.of(1, 4)).toString())

        assertEquals("${17*19}/${13*25}", (Rational.of(17, 13) * Rational.of(19, 25)).toString())
        assertEquals("${17*25}/${13*19}", (Rational.of(17, 13) / Rational.of(19, 25)).toString())

    }
}