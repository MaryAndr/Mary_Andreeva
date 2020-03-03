package ru.filit.motiv.app.utils

class TextConverter {

    val declensionOfAttempt = mapOf(
        1 to "попытка",
        2 to "попытки",
        3 to "попытки",
        4 to "попытки"
    )

    fun getOnlyDigits(text: String) : String {
        var out = text.replace("+7", "")
        out = out.replace("[^\\d]".toRegex(), "")
        return out
    }


    fun descriptionBuilder(min: String? = "0", data: String?, sms: String?) : String {
        var minExist: String? = ""
        var dataExist: String? = ""
        var smsExist: String?  = ""
        if (min != null) {
            minExist = ", $min минут"
        }
        if (data != null) {
            dataExist = "$data ГБ"
        }
        if (sms != null) {
            smsExist = ", $sms SMS"
        }

        return "Свой тариф: $dataExist$minExist$smsExist"
    }


    fun getFormattedPhone(phoneNumber: String) : String {
        return if (phoneNumber.length != 10) {
            "Некорректный номер"
        } else {
            "+7 ${phoneNumber.substring(0,3)} ${phoneNumber.substring(3,6)}-${phoneNumber.substring(6,8)}-${phoneNumber.substring(8,10)}"
        }
    }

    fun getFormattedDate(date:String):String{
        return  if (date.length==10){
            "${date.substring(8,10)}.${date.substring(5,7)}.${date.substring(0,4)}"
        }else{
            ""
        }
    }

    fun getDeclensionOfAttempt (count:Int?):String?{
       return if (count!=null&&count<=4){
            declensionOfAttempt[count]
        }else "попыток"
    }
}