package com.xs0.unitskt

val COUNT = LinearUnit(Rational.ONE, "", "", UnitKind.COUNT)
val PERCENT = LinearUnit(Rational.of(1, 100), "%", "%", UnitKind.COUNT)

val METER = LinearUnit(Rational.ONE, "m", "m", UnitKind.LENGTH)
val SECOND = LinearUnit(Rational.ONE, "s", "s", UnitKind.TIME)
val KELVIN = LinearUnit(Rational.ONE, "K", "K", UnitKind.TEMPERATURE)
val KILOGRAM = LinearUnit(Rational.ONE, "kg", "kg", UnitKind.WEIGHT)

val GRAM = LinearUnit(Rational.of(1, 1000), "g", "g", UnitKind.WEIGHT)
val MILLIGRAM = LinearUnit(Rational.of(1, 1000000), "mg", "mg", UnitKind.WEIGHT)

val MINUTE = LinearUnit(Rational.of(60), "min", "min", UnitKind.TIME)
val HOUR = LinearUnit(Rational.of(60 * 60), "h", "h", UnitKind.TIME)
val DAY = LinearUnit(Rational.of(24 * 60 * 60), "d", "d", UnitKind.TIME)
val MILLISECOND = LinearUnit(Rational.of(1, 1000), "ms", "ms", UnitKind.TIME)

val MILLIMETER = LinearUnit(Rational.of(1, 1000), "mm", "mm", UnitKind.LENGTH)
val CENTIMETER = LinearUnit(Rational.of(1, 100), "cm", "cm", UnitKind.LENGTH)
val DECIMETER = LinearUnit(Rational.of(1, 100), "dm", "dm", UnitKind.LENGTH)
val KILOMETER = LinearUnit(Rational.of(1000), "km", "km", UnitKind.LENGTH)
val INCH = LinearUnit(Rational.of( 254, 10000), "in", "in", UnitKind.LENGTH)
val FOOT = LinearUnit(Rational.of(3048, 10000), "ft", "ft", UnitKind.LENGTH)
val YARD = LinearUnit(Rational.of(9144, 10000), "yd", "yd", UnitKind.LENGTH)

val METER_PER_SECOND = METER / SECOND

val DEG_CELSIUS = DegCelsius
val DEG_FAHRENHEIT = DegFahrenheit
