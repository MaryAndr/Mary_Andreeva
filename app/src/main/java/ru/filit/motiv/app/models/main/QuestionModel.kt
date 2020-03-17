package ru.filit.motiv.app.models.main

data class QuestionModel (
    val question_id: Int,
    val question_rank: Int,
    val question_text: String,
    val answer_text: String?
)