package com.xs0.unitskt

// Unit = just base interface
//     NamedUnit = Unit which has its own (simple) symbol
//         LinearUnit = NamedUnit which has a multiplier and some form of UnitKind (e.g. N = 1 * kg m s⁻²)
//         CustomUnit = NamedUnit which is not linearly converted to base units (e.g. °C)
//     CompositeUnit = multiplier + exponents of NamedUnits (e.g. m/s²)
// note: the multiplier always relates to base SI units, not the named unit


sealed class Unit(val encoded: String, val prettySymbols: String, val kind: UnitKind) {
    abstract operator fun div(other: Unit): Unit
    abstract operator fun times(other: Unit): Unit
    abstract fun toComposite(): CompositeUnit

    private val hash: Int by lazy {
        var res: Int

        // so we generally want a unit to equal its composite version,
        // except for custom units, which behave differently..

        when (this) {
            is CustomUnit -> {
                res = 5195278 + this.prettySymbols.hashCode()
            }
            is LinearUnit -> {
                res = multiplier.hashCode()
                res = res xor (kind.hashCode() * 31 + 1)
            }
            is CompositeUnit -> {
                res = multiplier.hashCode()
                for ((unit, exp) in parts) {
                    res = res xor (unit.kind.hashCode() * 31 + exp)
                }
            }
        }

        res
    }

    final override fun hashCode(): Int {
        return hash
    }

    final override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other === null)
            return false

        if (other !is Unit)
            return false

        return equals(other)
    }

    fun equals(other: Unit): Boolean {
        if (this === other)
            return true

        if (this is CustomUnit || other is CustomUnit)
            return false

        val compThis = toComposite()
        val compOther = other.toComposite()

        if (compThis.multiplier != compOther.multiplier)
            return false
        if (compThis.kind != compOther.kind)
            return false

        if (compThis.parts.size != compOther.parts.size)
            return false

        for ((unit, exp) in compThis.parts)
            if (compOther.parts[unit] != exp)
                return false

        return true
    }

    override fun toString(): String {
        return prettySymbols
    }
}

sealed class NamedUnit(encoded: String, prettySymbols: String, kind: UnitKind) : Unit(encoded, prettySymbols, kind) {
    abstract fun toPower(exp: Int): Unit
}

class LinearUnit(val multiplier: Rational, encoded: String, prettySymbols: String, kind: UnitKind): NamedUnit(encoded, prettySymbols, kind) {
    private val asComposite: CompositeUnit
    init {
        asComposite =
            if (multiplier == Rational.ONE && kind == UnitKind.NUMBER) {
                CompositeUnit(emptyMap(), Rational.ONE, encoded, prettySymbols, UnitKind.NUMBER)
            } else {
                CompositeUnit(mapOf(this to 1), multiplier, encoded, prettySymbols, kind)
            }
    }

    override fun div(other: Unit): Unit {
        if (other == NO_UNIT) return this

        return toComposite() / other
    }

    override fun times(other: Unit): Unit {
        if (this == NO_UNIT) return other
        if (other == NO_UNIT) return this

        return toComposite() * other
    }

    override fun toComposite(): CompositeUnit {
        return asComposite
    }

    override fun toPower(exp: Int): Unit {
        if (exp == 1)
            return asComposite

        return CompositeUnit(mapOf(this to exp), multiplier.toPower(exp), "$encoded^$exp", formatPower(prettySymbols, exp), kind.toPower(exp))
    }
}

private fun <K, V> MutableMap<K, V>.mergeAll(other: Map<K, V>, remappingFunction: (V, V) -> V?) {
    for ((otherK, otherV) in other)
        merge(otherK, otherV, remappingFunction)
}

private fun encodeUnits(parts: Map<NamedUnit, Int>): String {
    val sb = StringBuilder()

    for ((unit, exp) in parts) {
        if (sb.isNotEmpty())
            sb.append(' ')

        sb.append(unit.encoded)

        if (exp != 1)
            sb.append('^').append(exp)
    }

    return sb.toString()
}

fun Int.toSuperscriptString(): String {
    val expChars = "⁰¹²³⁴⁵⁶⁷⁸⁹"
    if (this in 0..9)
        return expChars[this].toString()

    val sb = StringBuilder(this.toString())
    for (i in 0 until sb.length) {
        if (sb[i] == '-') {
            sb[i] = '⁻'
        } else {
            sb[i] = expChars[sb[i].toInt() - '0'.toInt()]
        }
    }
    return sb.toString()
}

fun formatPower(prettySymbol: String, exp: Int): String {
    return when {
        exp == 1 -> prettySymbol
        exp == -1 -> "/" + prettySymbol
        exp > 0 -> prettySymbol + exp.toSuperscriptString()
        else -> "/" + prettySymbol + (-exp).toSuperscriptString()
    }
}

private fun prettyPrintUnits(parts: Map<NamedUnit, Int>): String {
    val sb = StringBuilder()

    var first = true

    for ((unit, exp) in parts) {
        if (exp > 0) {
            if (first) {
                first = false
            } else {
                sb.append(' ')
            }

            sb.append(unit.prettySymbols)

            if (exp != 1)
                sb.append(exp.toSuperscriptString())
        }
    }

    first = true

    for ((unit, exp) in parts) {
        if (exp < 0) {
            if (first) {
                sb.append('/')
                first = false
            } else {
                sb.append(' ')
            }

            sb.append(unit.prettySymbols)

            if (exp != -1)
                sb.append((-exp).toSuperscriptString())
        }
    }

    return sb.toString()
}

class CompositeUnit(val parts: Map<NamedUnit, Int>, val multiplier: Rational, encoded: String, prettySymbols: String, kind: UnitKind) : Unit(encoded, prettySymbols, kind) {

    override fun times(other: Unit): Unit {
        when (other) {
            is LinearUnit ->
                return times(other.toComposite())

            is CustomUnit ->
                return times(other.toComposite())

            is CompositeUnit -> {
                val newParts = parts.toMutableMap()
                newParts.mergeAll(other.parts) { a, b -> if (a == -b) null else a + b }

                val newMultiplier = multiplier * other.multiplier
                val newEncoded = encodeUnits(newParts)
                val newPretty = prettyPrintUnits(newParts)
                val newKind = kind * other.kind

                return CompositeUnit(newParts, newMultiplier, newEncoded, newPretty, newKind)
            }
        }
    }

    override fun div(other: Unit): Unit {
        when (other) {
            is LinearUnit ->
                return div(other.toComposite())

            is CustomUnit ->
                return div(other.toComposite())

            is CompositeUnit -> {
                val newParts = parts.toMutableMap()
                for ((unit, exp) in other.parts) {
                    if (unit in newParts) {
                        newParts[unit] = newParts[unit]!! - exp
                    } else {
                        newParts[unit] = -exp
                    }
                }

                val newMultiplier = multiplier / other.multiplier
                val newEncoded = encodeUnits(newParts)
                val newPretty = prettyPrintUnits(newParts)
                val newKind = kind / other.kind

                return CompositeUnit(newParts, newMultiplier, newEncoded, newPretty, newKind)
            }
        }
    }

    override fun toComposite(): CompositeUnit {
        return this
    }
}

sealed class CustomUnit(encoded: String, prettySymbols: String, kind: UnitKind): NamedUnit(encoded, prettySymbols, kind) {
    abstract fun toLinear(quantity: Double, isInterval: Boolean): Quantity
    abstract fun fromLinear(quantity: Quantity): Quantity
}

object DegCelsius : CustomUnit("degC", "°C", UnitKind.TEMPERATURE) {
    private val asComposite = CompositeUnit(mapOf(this to 1), Rational.ONE, encoded, prettySymbols, kind)

    override fun toLinear(quantity: Double, isInterval: Boolean): Quantity {
        return if (isInterval) {
            Quantity(quantity, KELVIN, isInterval)
        } else {
            Quantity(quantity + 273.15, KELVIN, isInterval)
        }
    }

    override fun fromLinear(quantity: Quantity): Quantity {
        val kelvins = quantity.convertTo(KELVIN)
        return if (quantity.isInterval) {
            Quantity(kelvins.value, this, isInterval = true)
        } else {
            Quantity(kelvins.value - 273.15, this, isInterval = false)
        }
    }

    override fun toComposite(): CompositeUnit {
        return asComposite
    }

    override fun div(other: Unit): Unit {
        return toComposite() / other
    }

    override fun times(other: Unit): Unit {
        return toComposite() * other
    }

    override fun toPower(exp: Int): Unit {
        if (exp == 1)
            return asComposite

        return CompositeUnit(mapOf(this to exp), Rational.ONE, "$encoded^$exp", formatPower(prettySymbols, exp), kind.toPower(exp))
    }
}

object DegFahrenheit : CustomUnit("degF", "°F", UnitKind.TEMPERATURE) {
    private val asComposite = CompositeUnit(mapOf(this to 1), Rational.of(5, 9), encoded, prettySymbols, kind)

    override fun toLinear(quantity: Double, isInterval: Boolean): Quantity {
        if (isInterval) {
            return Quantity(quantity * 5 / 9, KELVIN, isInterval = true)
        } else {
            return Quantity((quantity + 459.67) * 5 / 9, KELVIN, isInterval = false)
        }
    }

    override fun fromLinear(quantity: Quantity): Quantity {
        val kelvins = quantity.convertTo(KELVIN)
        return if (quantity.isInterval) {
            Quantity(kelvins.value * 9 / 5, this, isInterval = true)
        } else {
            Quantity(kelvins.value * 9 / 5 - 459.67, this, isInterval = false)
        }
    }

    override fun toComposite(): CompositeUnit {
        return asComposite
    }

    override fun div(other: Unit): Unit {
        return toComposite() / other
    }

    override fun times(other: Unit): Unit {
        return toComposite() * other
    }

    override fun toPower(exp: Int): Unit {
        if (exp == 1)
            return asComposite

        return CompositeUnit(mapOf(this to exp), asComposite.multiplier.toPower(exp), "$encoded^$exp", formatPower(prettySymbols, exp), kind.toPower(exp))
    }
}