package kz.atc.mobapp.utils

import kz.atc.mobapp.models.main.UnitValue

class StringUtils {

    fun unitValueConverter(value: Int) : UnitValue{
        val unitValue = UnitValue(null,null)
        if(value < 1024) {
            unitValue.value = value
            unitValue.unit = "Мб"
        } else {
            unitValue.value = value/1024
            unitValue.unit = "Гб"
        }
        return unitValue
    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}