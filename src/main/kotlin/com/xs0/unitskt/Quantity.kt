package com.xs0.unitskt

private fun chooseBigger(a: Unit, b: Unit): Unit {
    return if (a.toComposite().multiplier > b.toComposite().multiplier) {
        a
    } else {
        b
    }
}

fun Double.of(unit: Unit) = Quantity(this, unit)


class Quantity(val value: Double, val unit: Unit, val isInterval: Boolean = false) {
    fun convertTo(newUnit: Unit): Quantity {
        if (unit == newUnit)
            return this

        if (unit.kind != newUnit.kind)
            throw IllegalArgumentException("Unit mismatch: $unit vs $newUnit")

        var toConvert: Quantity = this
        if (unit is CustomUnit) {
            toConvert = unit.toLinear(value, isInterval)
            if (toConvert.unit == newUnit)
                return toConvert
        }

        if (newUnit is CustomUnit) {
            return newUnit.fromLinear(toConvert)
        } else {
            val multiplier = toConvert.unit.toComposite().multiplier / newUnit.toComposite().multiplier
            val newVal = multiplier.doubleApprox * toConvert.value
            return Quantity(newVal, newUnit, isInterval)
        }
    }

    operator fun unaryMinus(): Quantity {
        return Quantity(-value, unit, isInterval)
    }

    operator fun minus(other: Quantity): Quantity {
        // interval - interval -> interval
        // interval - quantity -> quantity (same as -quantity + interval)
        // quantity - interval -> quantity
        // quantity - quantity -> interval

        val wantInterval = isInterval == other.isInterval
        if (unit == other.unit)
            return Quantity(value - other.value, unit, wantInterval)

        if (other.isInterval)
            return this + -other

        if (isInterval)
            return -other + this

        // so we have quantity - quantity with different units.. as with plus, we'll work
        // in linear scale, to resolve ambiguity between:
        // 37 °C - 9 °F = 32 °C (with 9 °F = 5 °C being the amount to subtract)
        // 37 °C - 9 °F = 49.78 °C (with 9 °F = -12.78 °C temperature)
        // (we choose the second one; to have the first one, you need to have an explicit interval)

        val targetUnit: Unit

        if (unit is CustomUnit) {
            if (other.unit is CustomUnit) {
                targetUnit = chooseBigger(unit, other.unit)
            } else {
                targetUnit = unit
            }
        } else {
            if (other.unit is CustomUnit) {
                targetUnit = other.unit
            } else {
                targetUnit = chooseBigger(unit, other.unit)
            }
        }

        val left = if (unit is CustomUnit) {
            unit.toLinear(value, false)
        } else {
            this
        }

        val right = if (other.unit is CustomUnit) {
            other.unit.toLinear(other.value, false)
        } else {
            other
        }

        val sum = if (left.unit == right.unit) {
            Quantity(left.value - right.value, left.unit, true)
        } else {
            val sumUnit = chooseBigger(left.unit, right.unit)
            Quantity(left.convertTo(sumUnit).value - right.convertTo(sumUnit).value, sumUnit, true)
        }

        return if (targetUnit is CustomUnit) {
            targetUnit.fromLinear(sum)
        } else {
            sum.convertTo(targetUnit)
        }
    }

    operator fun plus(other: Quantity): Quantity {
        // Legend: L = linear, C = custom, qty = quantity, int = interval
        //         left = choose the unit from left, top = choose the unit from above, bigger = choose bigger unit
        //
        //           C qty     C int    L qty    L int
        // C qty     bigger    left     left     left
        // C int      top      bigger   top      left
        // L qty      top      left     bigger   left
        // L int      top      top      top      bigger
        //
        // in words: - if units are the same, just sum the values together
        //           - if one is interval and the other is a quantity, prefer the unit from quantity
        //           - else, if one unit is custom and the other isn't, prefer the custom unit
        //           - else, choose the bigger unit
        //
        //           in all cases, we get an interval if both were intervals, otherwise we get a quantity
        //           in all cases (with different units), we sum in linear space

        if (unit.kind != other.unit.kind)
            throw IllegalArgumentException("Units not compatible")

        val wantInterval = isInterval && other.isInterval

        if (unit == other.unit)
            return Quantity(value + other.value, unit, wantInterval)

        val targetUnit: Unit

        if (isInterval && !other.isInterval) {
            targetUnit = other.unit
        } else
        if (!isInterval && other.isInterval) {
            targetUnit = unit
        } else {
            if (unit is CustomUnit) {
                if (other.unit is CustomUnit) {
                    targetUnit = chooseBigger(unit, other.unit)
                } else {
                    targetUnit = unit
                }
            } else {
                if (other.unit is CustomUnit) {
                    targetUnit = other.unit
                } else {
                    targetUnit = chooseBigger(unit, other.unit)
                }
            }
        }

        val left = if (unit is CustomUnit) {
            unit.toLinear(value, isInterval)
        } else {
            this
        }

        val right = if (other.unit is CustomUnit) {
            other.unit.toLinear(other.value, other.isInterval)
        } else {
            other
        }

        val sum = if (left.unit == right.unit) {
            Quantity(left.value + right.value, left.unit, wantInterval)
        } else {
            val sumUnit = chooseBigger(left.unit, right.unit)
            Quantity(left.convertTo(sumUnit).value + right.convertTo(sumUnit).value, sumUnit, wantInterval)
        }

        if (targetUnit is CustomUnit) {
            return targetUnit.fromLinear(sum)
        } else {
            return sum.convertTo(targetUnit)
        }
    }

    operator fun times(other: Quantity): Quantity {
        val left = if (unit is CustomUnit) {
            unit.toLinear(value, isInterval)
        } else {
            this
        }

        val right = if (other.unit is CustomUnit) {
            other.unit.toLinear(other.value, other.isInterval)
        } else {
            other
        }

        return Quantity(left.value * right.value, left.unit * right.unit, isInterval && other.isInterval)
    }

    operator fun div(other: Quantity): Quantity {
        if (Math.abs(other.value) < 1E-30)
            throw IllegalArgumentException("Division by zero")

        val left = if (unit is CustomUnit) {
            unit.toLinear(value, isInterval)
        } else {
            this
        }

        val right = if (other.unit is CustomUnit) {
            other.unit.toLinear(other.value, other.isInterval)
        } else {
            other
        }

        return Quantity(left.value / right.value, left.unit / right.unit, isInterval && other.isInterval)
    }

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other is Quantity -> value == other.value && unit == other.unit && isInterval == other.isInterval
            else -> false
        }
    }

    override fun hashCode(): Int {
        return (value.hashCode() * 31 + unit.hashCode()) * if (isInterval) 107 else 1
    }

    override fun toString(): String {
        if (unit == NO_UNIT) {
            return value.toString()
        } else {
            return "$value ${unit.prettySymbols}"
        }
    }
}
