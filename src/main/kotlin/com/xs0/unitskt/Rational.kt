package com.xs0.unitskt

import java.math.BigDecimal
import java.math.BigInteger
import java.math.BigInteger.ZERO
import java.math.MathContext

class Rational private constructor(val mul: BigInteger, val div: BigInteger) {
    val doubleApprox: Double by lazy {
        BigDecimal(mul).divide(BigDecimal(div), MathContext.DECIMAL128).toDouble()
    }

    init {
        if (div == ZERO)
            throw IllegalArgumentException("denominator can't be zero")
    }

    operator fun plus(other: Rational): Rational {
        return when {
            div == other.div ->
                Rational.of(mul + other.mul, div)

            else ->
                Rational.of(mul * other.div + other.mul * div, div * other.div)
        }
    }

    operator fun minus(other: Rational): Rational {
        return when {
            div == other.div ->
                Rational.of(mul - other.mul, div)

            else ->
                Rational.of(mul * other.div - other.mul * div, div * other.div)
        }
    }

    operator fun times(other: Rational): Rational {
        return when {
            this == ONE -> other
            other == ONE -> this
            else -> of(mul * other.mul, div * other.div)
        }
    }

    operator fun div(other: Rational): Rational {
        return when {
            other == ONE -> this
            this == ONE -> Rational(other.div, other.mul)
            else -> of(mul * other.div, div * other.mul)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true

        return if (other is Rational) {
            mul == other.mul && div == other.div
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return 31 * mul.hashCode() + div.hashCode()
    }

    override fun toString(): String {
        return "$mul/$div"
    }

    companion object {
        val ONE = Rational(BigInteger.ONE, BigInteger.ONE)

        fun of(value: Int): Rational {
            if (value <= 0)
                throw IllegalArgumentException("$value <= 0")

            return Rational(BigInteger.valueOf(value.toLong()), BigInteger.ONE)
        }

        fun of(mul: Int, div: Int): Rational {
            if (mul <= 0 || div <= 0)
                throw IllegalArgumentException("$mul <= 0 || $div <= 0")

            return of(BigInteger.valueOf(mul.toLong()), BigInteger.valueOf(div.toLong()))
        }

        fun of(mul: BigInteger, div: BigInteger = BigInteger.ONE): Rational {
            if (mul <= ZERO || div <= ZERO)
                throw IllegalArgumentException("negative or zero")

            return when {
                mul == BigInteger.ONE || div == BigInteger.ONE ->
                    Rational(mul, div)

                mul == div ->
                    Rational(BigInteger.ONE, BigInteger.ONE)

                else -> {
                    val gcd = mul.gcd(div)
                    if (gcd == BigInteger.ONE) {
                        Rational(mul, div)
                    } else {
                        Rational(mul / gcd, div / gcd)
                    }
                }
            }
        }
    }

    operator fun compareTo(other: Rational): Int {
        // a/b  <=>  c/d
        // ad   <=>  cb
        return (mul * other.div).compareTo(other.mul * div)
    }

    fun toPower(exp: Int): Rational {
        return when {
            exp == 1 -> this
            exp == -1 -> Rational(div, mul)
            exp > 0 -> Rational(mul.pow(exp), div.pow(exp))
            exp < 0 -> Rational(div.pow(-exp), mul.pow(-exp))
            else -> ONE
        }
    }
}