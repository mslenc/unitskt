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

        val mps28 = Quantity(28.0, METER / SECOND)
        val kph = mps28.convertTo(KILOMETER / HOUR)
        assertEquals(Quantity(3.6 * 28, KILOMETER / HOUR), kph)

        assertEquals(Quantity(626.0, DEG_FAHRENHEIT), Quantity(603.15, KELVIN).convertTo(DEG_FAHRENHEIT))
        assertEquals(Quantity(626.0, DEG_FAHRENHEIT), Quantity(626.0 , DEG_FAHRENHEIT).convertTo(DEG_FAHRENHEIT))
        assertEquals(Quantity(626.0, DEG_FAHRENHEIT), Quantity(330.0, DEG_CELSIUS).convertTo(DEG_FAHRENHEIT))

        assertEquals(Quantity(626.0, DEG_CELSIUS), Quantity(899.15, KELVIN).convertTo(DEG_CELSIUS))
        assertEquals(Quantity(626.0, DEG_CELSIUS), Quantity(1158.8, DEG_FAHRENHEIT).convertTo(DEG_CELSIUS))
        assertEquals(Quantity(626.0, DEG_CELSIUS), Quantity(626.0, DEG_CELSIUS).convertTo(DEG_CELSIUS))

        assertEquals(Quantity(626.0, KELVIN), Quantity(626.0 , KELVIN).convertTo(KELVIN))
        assertEquals(Quantity(626.0, KELVIN), Quantity(667.13, DEG_FAHRENHEIT).convertTo(KELVIN))
        assertEquals(Quantity(626.0, KELVIN), Quantity(352.85, DEG_CELSIUS).convertTo(KELVIN))
    }

    @Test
    fun testPlus() {
        assertEquals(Quantity(64.0, METER), Quantity(22.0, METER) + Quantity(42.0, METER))
        assertEquals(Quantity(3.141592, KILOMETER), Quantity(3.0, KILOMETER) + Quantity(141.0, METER) + Quantity(592.0, MILLIMETER))
    }

    @Test
    fun testMinus() {
        assertEquals(Quantity(-20.0, METER, true), Quantity(22.0, METER) - Quantity(42.0, METER))
        assertEquals(Quantity(3.141, KILOMETER, true), Quantity(4.0, KILOMETER) - Quantity(859.0, METER))
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun assertEquals(a: Quantity, b: Quantity) {
        assertEquals(a.isInterval, b.isInterval)
        assertEquals(a.unit, b.unit)
        assertEquals(a.value, b.value, 0.0000005) // we'll be rounding to 6 decimals in db..
    }

    @Test
    fun testPlusTemperature() {
        val celsQty37 = Quantity(37.0, DEG_CELSIUS, isInterval = false) // 98,6 fahrenheit
        val celsInt25 = Quantity(25.0, DEG_CELSIUS, isInterval = true) // 45 degrees fahrenheit
        val fahrQty86 = Quantity(86.0, DEG_FAHRENHEIT, isInterval = false) // 30 degrees celsius
        val fahrInt27 = Quantity(27.0, DEG_FAHRENHEIT, isInterval = true) // 15 degrees celsius
        val kelvQty300 = Quantity(300.0, KELVIN, isInterval = false)
        val kelvInt45 = Quantity(45.0, KELVIN, isInterval = true)

        val all = arrayOf(celsQty37, celsInt25, fahrQty86, fahrInt27, kelvQty300, kelvInt45)

        for (a in all) {
            for (b in all) {
                assertEquals(a + b, b + a)
            }
        }

        assertEquals(Quantity( 74.0 , DEG_CELSIUS), celsQty37 + celsQty37)
        assertEquals(Quantity( 62.0 , DEG_CELSIUS), celsQty37 + celsInt25)
        assertEquals(Quantity(340.15, DEG_CELSIUS), celsQty37 + fahrQty86) // in kelvins: 273.15 + 37 + 273.15 + 30 - 273.15
        assertEquals(Quantity( 52.0 , DEG_CELSIUS), celsQty37 + fahrInt27)
        assertEquals(Quantity(337.0 , DEG_CELSIUS), celsQty37 + kelvQty300)
        assertEquals(Quantity( 82.0 , DEG_CELSIUS), celsQty37 + kelvInt45)

        assertEquals(Quantity( 50.0 , DEG_CELSIUS, true), celsInt25 + celsInt25)
        assertEquals(Quantity(131.0 , DEG_FAHRENHEIT   ), celsInt25 + fahrQty86)
        assertEquals(Quantity( 40.0 , DEG_CELSIUS, true), celsInt25 + fahrInt27)
        assertEquals(Quantity(325.0 , KELVIN           ), celsInt25 + kelvQty300)
        assertEquals(Quantity( 70.0 , DEG_CELSIUS, true), celsInt25 + kelvInt45)

        assertEquals(Quantity(172.0 , DEG_FAHRENHEIT), fahrQty86 + fahrQty86)
        assertEquals(Quantity(113.0 , DEG_FAHRENHEIT), fahrQty86 + fahrInt27)
        assertEquals(Quantity(626.0 , DEG_FAHRENHEIT), fahrQty86 + kelvQty300)
        assertEquals(Quantity(167.0 , DEG_FAHRENHEIT), fahrQty86 + kelvInt45)

        assertEquals(Quantity( 54.0 , DEG_FAHRENHEIT, true), fahrInt27 + fahrInt27)
        assertEquals(Quantity(315.0 , KELVIN              ), fahrInt27 + kelvQty300)
        assertEquals(Quantity(108.0 , DEG_FAHRENHEIT, true), fahrInt27 + kelvInt45)

        assertEquals(Quantity(600.0 , KELVIN), kelvQty300 + kelvQty300)
        assertEquals(Quantity(345.0 , KELVIN), kelvQty300 + kelvInt45)

        assertEquals(Quantity(90.0 , KELVIN, true), kelvInt45 + kelvInt45)
    }

    @Test
    fun testMinusTemperature() {
        val celsQty37 = Quantity(37.0, DEG_CELSIUS, isInterval = false) // 98,6 fahrenheit
        val celsInt25 = Quantity(25.0, DEG_CELSIUS, isInterval = true) // 45 degrees fahrenheit
        val fahrQty86 = Quantity(86.0, DEG_FAHRENHEIT, isInterval = false) // 30 degrees celsius
        val fahrInt27 = Quantity(27.0, DEG_FAHRENHEIT, isInterval = true) // 15 degrees celsius
        val kelvQty300 = Quantity(300.0, KELVIN, isInterval = false)
        val kelvInt45 = Quantity(45.0, KELVIN, isInterval = true)

        val all = arrayOf(celsQty37, celsInt25, fahrQty86, fahrInt27, kelvQty300, kelvInt45)

        assertEquals(Quantity(7.0, DEG_CELSIUS, true), celsQty37 - fahrQty86)

        for (a in all) {
            for (b in all) {
                assertEquals(a - b, -(b - a))
            }
        }

        assertEquals(Quantity(  0.0 , DEG_CELSIUS, true), celsQty37 - celsQty37)
        assertEquals(Quantity( 12.0 , DEG_CELSIUS      ), celsQty37 - celsInt25)
        assertEquals(Quantity(  7.0 , DEG_CELSIUS, true), celsQty37 - fahrQty86) // 37 C - 30 C
        assertEquals(Quantity( 22.0 , DEG_CELSIUS      ), celsQty37 - fahrInt27) // 37 C - 15 C
        assertEquals(Quantity(10.15 , DEG_CELSIUS, true), celsQty37 - kelvQty300) // 37 C - 26.85
        assertEquals(Quantity( -8.0 , DEG_CELSIUS      ), celsQty37 - kelvInt45)

        assertEquals(Quantity(   0.0 , DEG_CELSIUS, true), celsInt25 - celsInt25)
        assertEquals(Quantity( -41.0 , DEG_FAHRENHEIT   ), celsInt25 - fahrQty86) // -86 F + 45 F
        assertEquals(Quantity(  10.0 , DEG_CELSIUS, true), celsInt25 - fahrInt27) // 25 C - 15 C
        assertEquals(Quantity(-275.0 , KELVIN           ), celsInt25 - kelvQty300) // -300 K + 25 K
        assertEquals(Quantity( -20.0 , DEG_CELSIUS, true), celsInt25 - kelvInt45)

        assertEquals(Quantity(  0.0  , DEG_FAHRENHEIT, true), fahrQty86 - fahrQty86)
        assertEquals(Quantity( 59.0  , DEG_FAHRENHEIT      ), fahrQty86 - fahrInt27)
        assertEquals(Quantity(  5.67 , DEG_FAHRENHEIT, true), fahrQty86 - kelvQty300) // 86 F - 80.33 F
        assertEquals(Quantity(  5.0  , DEG_FAHRENHEIT      ), fahrQty86 - kelvInt45) // 86 F - 81 F

        assertEquals(Quantity(   0.0 , DEG_FAHRENHEIT, true), fahrInt27 - fahrInt27)
        assertEquals(Quantity(-285.0 , KELVIN              ), fahrInt27 - kelvQty300) // -300 K + 15 K
        assertEquals(Quantity( -54.0 , DEG_FAHRENHEIT, true), fahrInt27 - kelvInt45)

        assertEquals(Quantity(   0.0 , KELVIN, true), kelvQty300 - kelvQty300)
        assertEquals(Quantity( 255.0 , KELVIN      ), kelvQty300 - kelvInt45)

        assertEquals(Quantity(   0.0 , KELVIN, true), kelvInt45 - kelvInt45)
    }

    @Test
    fun testUnitFormatting() {
        assertEquals("m", (COUNT * METER).toString())
        assertEquals("%", PERCENT.toString())
        assertEquals("m/s", (METER / SECOND).toString())
        assertEquals("kg m/s²", (KILOGRAM * METER / SECOND / SECOND).toString())
    }
}