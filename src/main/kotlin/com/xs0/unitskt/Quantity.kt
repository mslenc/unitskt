package com.xs0.unitskt

sealed class NumWUnit(val unit: Unit) {
    abstract fun convertTo(newUnit: Unit): NumWUnit

//    abstract operator fun plus(other: NumWUnit): NumWUnit
//    abstract operator fun minus(other: NumWUnit): NumWUnit
//    abstract operator fun times(other: NumWUnit): NumWUnit
//    abstract operator fun div(other: NumWUnit): NumWUnit
}

class Quantity(val value: Double, unit: Unit): NumWUnit(unit) {
    override fun convertTo(newUnit: Unit): Quantity {
        if (unit.kind != newUnit.kind)
            throw IllegalArgumentException("Unit mismatch: $unit vs $newUnit")

        if (unit == newUnit)
            return this

        var toConvert: Quantity = this
        if (unit is CustomUnit) {
            toConvert = unit.toLinearQuantity(value)
        }

        if (newUnit is CustomUnit) {
            return newUnit.fromLinear(toConvert)
        } else {
            val multiplier = unit.toComposite().multiplier / newUnit.toComposite().multiplier
            val newVal = multiplier.doubleApprox * value
            return Quantity(newVal, newUnit)
        }
    }

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other is Quantity -> value == other.value && unit == other.unit
            else -> false
        }
    }

    override fun hashCode(): Int {
        return value.hashCode() * 31 + unit.hashCode()
    }

    override fun toString(): String {
        if (unit == COUNT) {
            return value.toString()
        } else {
            return "$value ${unit.prettySymbols}"
        }
    }
}

class Interval(val difference: Double, unit: Unit): NumWUnit(unit) {
    override fun convertTo(newUnit: Unit): Interval {
        if (unit.kind != newUnit.kind)
            throw IllegalArgumentException("Unit mismatch: $unit vs $newUnit")

        if (unit == newUnit)
            return this

        var toConvert: Interval = this
        if (unit is CustomUnit) {
            toConvert = unit.toLinearInterval(difference)
        }

        if (newUnit is CustomUnit) {
            return newUnit.fromLinear(toConvert)
        } else {
            val multiplier = unit.toComposite().multiplier / newUnit.toComposite().multiplier
            val newVal = multiplier.doubleApprox * difference
            return Interval(newVal, newUnit)
        }
    }
}