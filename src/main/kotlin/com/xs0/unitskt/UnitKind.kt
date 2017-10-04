package com.xs0.unitskt

/**
 * Represents a unit kind, which means exponents of base units without actual units.
 * For example, both ft/h and m/s have the same kind, because they're both length^1/time^1,
 * so it's possible to convert between them. We don't currently support current (amperes),
 * amount of substance (mol) or luminous intensity (candela), because they're unlikely to
 * be used in sports-related contexts..
 */
class UnitKind(val weightExp: Int = 0, val timeExp: Int = 0, val lengthExp: Int = 0, val tempExp: Int = 0) {
    operator fun times(other: UnitKind): UnitKind {
        return UnitKind(
            weightExp = weightExp + other.weightExp,
            timeExp   = timeExp   + other.timeExp,
            lengthExp = lengthExp + other.lengthExp,
            tempExp   = tempExp   + other.tempExp
        )
    }

    operator fun div(other: UnitKind): UnitKind {
        return UnitKind(
            weightExp = weightExp - other.weightExp,
            timeExp   = timeExp   - other.timeExp,
            lengthExp = lengthExp - other.lengthExp,
            tempExp   = tempExp   - other.tempExp
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other is UnitKind) {
            return weightExp == other.weightExp &&
                   timeExp   == other.timeExp   &&
                   lengthExp == other.lengthExp &&
                   tempExp   == other.tempExp
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        return weightExp * (31 * 31 * 31) +
               timeExp   * (31 * 31) +
               lengthExp *  31 +
               tempExp
    }


    companion object {
        val NUMBER = UnitKind()

        val WEIGHT = UnitKind(weightExp = 1)
        val TIME = UnitKind(timeExp = 1)
        val LENGTH = UnitKind(lengthExp = 1)
        val TEMPERATURE = UnitKind(tempExp = 1)

        val SPEED = LENGTH / TIME
        val FREQUENCY = NUMBER / TIME
        val ACCELERATION = SPEED / TIME
    }

    fun toPower(exp: Int): UnitKind {
        return UnitKind(
                weightExp = weightExp * exp,
                timeExp   = timeExp   * exp,
                lengthExp = lengthExp * exp,
                tempExp   = tempExp   * exp
        )
    }
}