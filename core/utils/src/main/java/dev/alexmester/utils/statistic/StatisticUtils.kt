package dev.alexmester.utils.statistic

object StatisticUtils {

    fun xpForLevel(level: Int): Float =
        (10.0 * Math.pow(level.toDouble(), 1.8)).toFloat()
}