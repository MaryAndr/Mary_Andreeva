package ru.filit.motiv.app.models.main

 data class FAQResponse (
     val category_id: Int,
     val category_name: String,
     val category_rank: Int,
     val questions: List<QuestionModel>
 ) {

     fun toCategoryQuestionList():CategoryQuestions{
         return CategoryQuestions(category_id, category_rank, category_name, questions )
     }

 }