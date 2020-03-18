package ru.filit.motiv.app.listeners

interface OnQuestionClickListener {
    fun onClick (questionId: Int, expandableCategoriesId: List<Int>)
}