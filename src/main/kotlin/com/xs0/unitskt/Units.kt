package com.xs0.unitskt

import java.util.regex.Pattern

object Units {
    private val allNamed = ArrayList<NamedUnit>()
    private val indexByEncoded = HashMap<String, NamedUnit>()
    private val indexByPretty = HashMap<String, NamedUnit>()

    internal fun <T : NamedUnit> add(unit: T): T {
        if (indexByEncoded.put(unit.encoded, unit) != null)
            throw IllegalArgumentException("A unit with encoding ${unit.encoded} is already defined")

        if (indexByPretty.put(unit.prettySymbols, unit) != null)
            throw IllegalArgumentException("A unit with pretty form ${unit.encoded} is already defined")

        allNamed.add(unit)

        return unit
    }

    fun parse(units: String): Unit {
        // so we support something like
        // units := part* "/"? part*       (in other words, there can be 0 or 1 slashes, everything else must be parts)
        // part := name exponent?          (a missing exponent is equal to 1)
        // name := <encoded name or pretty name>
        // exponent := "^" "-"? [0-9]*
        //          |  [⁻]?[⁰¹²³⁴⁵⁶⁷⁸⁹]+

        // the exponents can't be 0
        // the parts must be separated by at least one whitespace char
        // the slash just makes all exponents that follow it negated, so
        // parse("m s^-1") == parse("m/s") == parse("s^-1/m⁻¹")

        // anyway, for now just something simple/inefficient.. we'll make something better if there's a need to do so

        val halves = units.split("/".toRegex())
        when (halves.size) {
            1 -> return parseHalf(halves[0])
            2 -> return parseHalf(halves[0]) / parseHalf(halves[1])
            else -> throw IllegalArgumentException("Invalid unit spec")
        }
    }

    fun allNamed(): List<Unit> {
        return allNamed
    }

    private fun parseHalf(units: String): Unit {
        val trimmed = units.trim()
        if (trimmed.isEmpty())
            return NO_UNIT

        val parts = trimmed.split("\\s+".toRegex())
        var result: Unit = NO_UNIT
        for (part in parts)
            result *= parsePart(part)
        return result
    }

    private fun parsePart(part: String): Unit {
        val unit: String
        val exp: String

        if (part.contains('^')) {
            val tmp = part.split(Pattern.quote("^").toRegex())
            if (tmp.size != 2)
                throw IllegalArgumentException("Illegal unit \"$part\"")
            unit = tmp[0]
            exp = tmp[1]
        } else {
            exp = extractSuperscriptSuffix(part)
            unit = part.substring(0 until part.length - exp.length)
        }

        val expNum = when {
            exp == "" ->
                1
            else ->
                exp.toIntOrNull() ?: throw IllegalArgumentException("Illegal unit \"$part\"")
        }

        if (expNum == 0 || expNum < -99 || expNum > 99)
            throw IllegalArgumentException("$expNum is not a valid exponent in unit \"$part\"")

        val baseUnit = indexByEncoded[unit] ?: indexByPretty[unit] ?: throw IllegalArgumentException("Unknown unit \"$unit\"")
        return if (expNum == 1) {
            baseUnit
        } else {
            baseUnit.toPower(expNum)
        }
    }

    private fun extractSuperscriptSuffix(part: String): String {
        var len = 0

        while (len < part.length && isSuperscript(part[part.length - 1 - len]))
            len++

        if (len == 0)
            return ""

        val sb = StringBuilder()
        for (i in part.length - len until part.length) {
            val n: Int = "⁰¹²³⁴⁵⁶⁷⁸⁹⁻".indexOf(part[i])
            if (n > 9) {
                if (sb.isEmpty()) {
                    sb.append('-')
                } else {
                    throw IllegalArgumentException("Not a valid exponent: " + part.substring(part.length - len))
                }
            } else {
                sb.append(n)
            }
        }

        val res = sb.toString()
        if (res == "-")
            throw IllegalArgumentException("Not a valid exponent: ⁻")

        return res
    }

    private fun isSuperscript(c: Char): Boolean {
        return "⁰¹²³⁴⁵⁶⁷⁸⁹⁻".contains(c)
    }
}

val NO_UNIT = Units.add(LinearUnit(Rational.ONE, "", "", UnitKind.NUMBER))
val COUNT = NO_UNIT
val PERCENT = Units.add(LinearUnit(Rational.of(1, 100), "%", "%", UnitKind.NUMBER))

val METER = Units.add(LinearUnit(Rational.ONE, "m", "m", UnitKind.LENGTH))
val SECOND = Units.add(LinearUnit(Rational.ONE, "s", "s", UnitKind.TIME))
val KELVIN = Units.add(LinearUnit(Rational.ONE, "K", "K", UnitKind.TEMPERATURE))
val KILOGRAM = Units.add(LinearUnit(Rational.ONE, "kg", "kg", UnitKind.WEIGHT))

val GRAM = Units.add(LinearUnit(Rational.of(1, 1000), "g", "g", UnitKind.WEIGHT))
val MILLIGRAM = Units.add(LinearUnit(Rational.of(1, 1000000), "mg", "mg", UnitKind.WEIGHT))

val MINUTE = Units.add(LinearUnit(Rational.of(60), "min", "min", UnitKind.TIME))
val HOUR = Units.add(LinearUnit(Rational.of(60 * 60), "h", "h", UnitKind.TIME))
val DAY = Units.add(LinearUnit(Rational.of(24 * 60 * 60), "d", "d", UnitKind.TIME))
val MILLISECOND = Units.add(LinearUnit(Rational.of(1, 1000), "ms", "ms", UnitKind.TIME))

val MILLIMETER = Units.add(LinearUnit(Rational.of(1, 1000), "mm", "mm", UnitKind.LENGTH))
val CENTIMETER = Units.add(LinearUnit(Rational.of(1, 100), "cm", "cm", UnitKind.LENGTH))
val DECIMETER = Units.add(LinearUnit(Rational.of(1, 100), "dm", "dm", UnitKind.LENGTH))
val KILOMETER = Units.add(LinearUnit(Rational.of(1000), "km", "km", UnitKind.LENGTH))
val INCH = Units.add(LinearUnit(Rational.of( 254, 10000), "in", "in", UnitKind.LENGTH))
val FOOT = Units.add(LinearUnit(Rational.of(3048, 10000), "ft", "ft", UnitKind.LENGTH))
val YARD = Units.add(LinearUnit(Rational.of(9144, 10000), "yd", "yd", UnitKind.LENGTH))

val DEG_CELSIUS = Units.add(DegCelsius)
val DEG_FAHRENHEIT = Units.add(DegFahrenheit)

val METER_PER_SECOND = METER / SECOND
val KM_PER_HOUR = KILOMETER / HOUR