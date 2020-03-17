package ru.filit.motiv.app.states.main

import ru.filit.motiv.app.models.main.CategoryQuestions

sealed class FAQState {
    data class QuestionsLoaded (val mutable: MutableList<CategoryQuestions>): FAQState()

    data class InternetState(val active: Boolean): FAQState()

    data class ShowErrorMessage (val message: String): FAQState()

    object Loading : FAQState()
}