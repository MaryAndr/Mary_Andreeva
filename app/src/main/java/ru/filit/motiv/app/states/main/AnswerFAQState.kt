package ru.filit.motiv.app.states.main

import ru.filit.motiv.app.models.main.CategoryQuestions

sealed class AnswerFAQState {
    data class QuestionsLoaded (val questionText:String, val answerText:String): AnswerFAQState()

    data class InternetState(val active: Boolean): AnswerFAQState()

    data class ShowErrorMessage (val message: String): AnswerFAQState()

    object Loading : AnswerFAQState()
}