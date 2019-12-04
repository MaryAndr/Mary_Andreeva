package kz.atc.mobapp.utils

class MathUtils {

    fun calculatePercent(rest: Int, total: Int) : Int {
        return when {
            rest >= total -> 100
            else -> {
                rest * 100 / total
            }
        }
    }
}