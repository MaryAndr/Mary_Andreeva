package ru.filit.motiv.app.models.main

class CategoryQuestions (
    val categoryId: Int,
    val categoryRank: Int,
    val categoryName: String,
    val questions: List<QuestionModel>
)