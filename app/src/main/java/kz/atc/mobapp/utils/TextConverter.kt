package kz.atc.mobapp.utils

class TextConverter {

    fun getOnlyDigits(text: String) : String {
        var out = text.replace("+7", "")
        out = out.replace("[^\\d]".toRegex(), "")
        return out
    }


    fun getFormattedPhone(phoneNumber: String) : String {
        return if (phoneNumber.length != 10) {
            "Некорректный номер"
        } else {
            "+7 ${phoneNumber.substring(0,3)} ${phoneNumber.substring(3,6)}-${phoneNumber.substring(6,8)}-${phoneNumber.substring(8,10)}"
        }
    }
}