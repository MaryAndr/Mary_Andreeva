package ru.filit.motiv.app.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText

class PhoneTextWatcher(private val edittext: EditText) : TextWatcher {

    var empty = true
    var editedFlag = false
    var backspacingFlag = false
    var cursorComplement: Int = 0

    override fun afterTextChanged(s: Editable?) {
        var string = s.toString()

        if (!editedFlag) {
            if (backspacingFlag) {
                Log.d("Debug", edittext.selectionEnd.toString())
                if ( edittext.selectionEnd == 3|| edittext.selectionEnd == 8) {
                    string = string.removeRange(edittext.selectionEnd - 2, edittext.selectionEnd)
                    edittext.setSelection(edittext.selectionEnd)
                } else if (edittext.selectionEnd == 2 || edittext.selectionEnd == 7 || edittext.selectionEnd == 12 || edittext.selectionEnd == 15) {
                    string = string.removeRange(edittext.selectionEnd - 1, edittext.selectionEnd)
                    edittext.setSelection(edittext.selectionEnd)
                    Log.d("Debug", string)
                }
            }

            var phone = string.replace("+7", "")
            phone = phone.replace("[^\\d]".toRegex(), "")

            when {
                phone.length == 11 -> {
                    editedFlag = true
                    val ans = "+7 (" + phone.substring(1, 4) + ") " + phone.substring(
                        4,
                        7
                    ) + "-" + phone.substring(7, 9) + "-" + phone.substring(9)
                    edittext.setText(ans)
                    edittext.setSelection(edittext.text.length - cursorComplement)
                }
                phone.length >= 8 -> {
                    editedFlag = true
                    val ans = "+7 (" + phone.substring(0, 3) + ") " + phone.substring(
                        3,
                        6
                    ) + "-" + phone.substring(6, 8) + "-" + phone.substring(8)
                    edittext.setText(ans)
                    edittext.setSelection(edittext.text.length - cursorComplement)
                }
                phone.length >= 6 -> {
                    editedFlag = true
                    val ans = "+7 (" + phone.substring(0, 3) + ") " + phone.substring(
                        3,
                        6
                    ) + "-" + phone.substring(6)
                    edittext.setText(ans)
                    edittext.setSelection(edittext.text.length - cursorComplement)
                }
                phone.length >= 3 -> {
                    editedFlag = true
                    val ans = "+7 (" + phone.substring(0, 3) + ") " + phone.substring(3)
                    edittext.setText(ans)
                    edittext.setSelection(edittext.text.length - cursorComplement)
                }
                phone.length == 2 -> {
                    editedFlag = true
                    val ans = "+7 (" + phone.substring(0, 2)
                    edittext.setText(ans)
                    edittext.setSelection(edittext.text.length - cursorComplement)
                }
                phone.length == 1 -> {
                    editedFlag = true
                    val ans = "+7$phone"
                    edittext.setText(ans)
                    edittext.setSelection(edittext.text.length - cursorComplement)
                }
            }
        } else {
            editedFlag = false
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        val text = edittext.text.toString()
        empty = text.isEmpty()

        cursorComplement = s!!.length - edittext.selectionStart
        backspacingFlag = count > after
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

}