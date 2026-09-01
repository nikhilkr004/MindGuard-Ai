package com.mindguard.ai.ui.assessment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindguard.ai.data.model.Question
import com.mindguard.ai.databinding.ItemQuestionLikertBinding

class QuestionnaireAdapter(
    private val onAnswerSelected: (questionId: String, value: Float) -> Unit
) : RecyclerView.Adapter<QuestionnaireAdapter.QuestionViewHolder>() {

    private var questions: List<Question> = emptyList()
    private var answers: MutableMap<String, Float> = mutableMapOf()
    private var baseQuestionIndex: Int = 1

    fun submitQuestions(newQuestions: List<Question>, currentAnswers: Map<String, Float>, startIndex: Int = 1) {
        questions = newQuestions
        answers = currentAnswers.toMutableMap()
        baseQuestionIndex = startIndex
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemQuestionLikertBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position], baseQuestionIndex + position)
    }

    override fun getItemCount(): Int = questions.size

    inner class QuestionViewHolder(
        private val binding: ItemQuestionLikertBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(question: Question, displayIndex: Int) {
            binding.tvQuestionNumber.text = "Question $displayIndex"
            binding.tvQuestionText.text = question.text

            // Clear previous listener to avoid recycling side-effects
            binding.rgLikertOptions.setOnCheckedChangeListener(null)

            val selectedScore = answers[question.id]
            when (selectedScore?.toInt()) {
                0 -> binding.rbOption0.isChecked = true
                1 -> binding.rbOption1.isChecked = true
                2 -> binding.rbOption2.isChecked = true
                3 -> binding.rbOption3.isChecked = true
                4 -> binding.rbOption4.isChecked = true
                else -> binding.rgLikertOptions.clearCheck()
            }

            binding.rgLikertOptions.setOnCheckedChangeListener { _, checkedId ->
                val score = when (checkedId) {
                    binding.rbOption0.id -> 0f
                    binding.rbOption1.id -> 1f
                    binding.rbOption2.id -> 2f
                    binding.rbOption3.id -> 3f
                    binding.rbOption4.id -> 4f
                    else -> return@setOnCheckedChangeListener
                }
                answers[question.id] = score
                onAnswerSelected(question.id, score)
            }
        }
    }
}
