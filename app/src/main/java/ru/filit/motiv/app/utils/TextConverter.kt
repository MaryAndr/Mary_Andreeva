package ru.filit.motiv.app.utils

class TextConverter {

    fun getOnlyDigits(text: String) : String {
        var out = text.replace("+7", "")
        out = out.replace("[^\\d]".toRegex(), "")
        return out
    }


    fun descriptionBuilder(min: String? = "0", data: String? = "0.00", sms: String? = "0") : String {
        var minExist: String? = "0.00"
        var dataExist: String? = "0.00"
        var smsExist: String?  = "0.00"
        if (min != null) {
            minExist = min
        }
        if (data != null) {
            dataExist = data
        }
        if (sms != null) {
            smsExist = sms
        }

        return "Свой тариф: $minExist минут, $dataExist ГБ, $smsExist SMS"
    }


    fun getFormattedPhone(phoneNumber: String) : String {
        return if (phoneNumber.length != 10) {
            "Некорректный номер"
        } else {
            "+7 ${phoneNumber.substring(0,3)} ${phoneNumber.substring(3,6)}-${phoneNumber.substring(6,8)}-${phoneNumber.substring(8,10)}"
        }
    }
}