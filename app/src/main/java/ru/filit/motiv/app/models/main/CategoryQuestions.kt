package ru.filit.motiv.app.models.main

class CategoryQuestions (
    val questionId: Int,
    val questionRank: Int,
    val questionText: String,
    val questions: List<QuestionModel>
)