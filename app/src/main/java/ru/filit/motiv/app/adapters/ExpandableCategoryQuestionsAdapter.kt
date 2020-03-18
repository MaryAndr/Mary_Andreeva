package ru.filit.motiv.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_question_faq.view.*
import kotlinx.android.synthetic.main.item_category_question.view.*
import ru.filit.motiv.app.R
import ru.filit.motiv.app.listeners.OnQuestionClickListener
import ru.filit.motiv.app.models.main.CategoryQuestions
import ru.filit.motiv.app.models.main.QuestionModel

class ExpandableCategoryQuestionsAdapter(private val onQuestionClickListener: OnQuestionClickListener): BaseExpandableListAdapter() {

    private var titleList: MutableList<String> = mutableListOf()
    private var dataList: MutableMap<String, List<QuestionModel>> = mutableMapOf()
    private var expandableCategoriesId: MutableList<Int> = mutableListOf()


    fun setData (items: MutableList<CategoryQuestions>, expandableCategoriesId: List<Int> = listOf()){
        titleList.clear()
        dataList.clear()
        titleList = items.distinctBy { it.categoryRank}.map { it.categoryName }.toMutableList()
        titleList.forEach{title ->
            dataList[title] = items.first{pred:CategoryQuestions -> pred.categoryName == title }.questions
        }
        this.expandableCategoriesId.addAll(expandableCategoriesId)
        notifyDataSetChanged()
    }


    override fun getGroup(groupPosition: Int): String {
        return titleList[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val viewHolder: CategoryQuestionsViewHolder
        val rowView: View
        val groupTitle = getGroup(groupPosition)
        if (convertView == null) {
            val layoutInflater = parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = layoutInflater.inflate(R.layout.item_category_question, null)
            viewHolder = CategoryQuestionsViewHolder(rowView)
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = rowView.tag as CategoryQuestionsViewHolder
        }

        if (isExpanded) {
            viewHolder.imgIndicator?.setImageResource(R.drawable.ic_arrowup)
            expandableCategoriesId.add(groupPosition)
        } else {
            viewHolder.imgIndicator?.setImageResource(R.drawable.ic_arrowdown)
            expandableCategoriesId.remove(groupPosition)
        }

        viewHolder.categoryName?.text = groupTitle
        return rowView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return dataList[titleList[groupPosition]]!!.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): QuestionModel {
        return dataList[titleList[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val viewHolder: QuestionsFAQHolder
        val rowView: View
        val child = getChild(groupPosition, childPosition)

        if (convertView == null) {
            val layoutInflater = parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = layoutInflater.inflate(R.layout.item_question_faq, null)
            viewHolder = QuestionsFAQHolder(rowView)
            rowView.tag = viewHolder
        }else {
            rowView = convertView
            viewHolder = rowView.tag as QuestionsFAQHolder
        }


        viewHolder.questionName.text = child.question_text
        viewHolder.itemView.setOnClickListener{
            onQuestionClickListener.onClick(child.question_id, expandableCategoriesId)
        }
        return rowView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return titleList.size
    }

    private class CategoryQuestionsViewHolder (view:View?){
        val categoryName = view?.category_questions_name
        val imgIndicator = view?.category_expandImg
    }

    private class QuestionsFAQHolder(view: View): RecyclerView.ViewHolder(view){
        val questionName = view.questions_name
    }
}