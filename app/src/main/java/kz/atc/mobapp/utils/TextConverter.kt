package kz.atc.mobapp.utils

class TextConverter {

    fun getOnlyDigits(text: String) : String {
        var out = text.replace("+7", "")
        out = out.replace("[^\\d]".toRegex(), "")
        return out
    }
}