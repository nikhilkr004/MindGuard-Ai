package com.mindguard.ai.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mindguard.ai.data.model.CategoryType
import com.mindguard.ai.data.model.Question
import com.mindguard.ai.data.model.QuestionCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuestionnaireLocalDataSource(private val context: Context) {
    private val gson = Gson()
    private var cachedQuestions: List<Question>? = null

    suspend fun getQuestions(): List<Question> = withContext(Dispatchers.IO) {
        cachedQuestions?.let { return@withContext it }
        
        try {
            val jsonString = context.assets.open("questions.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Question>>() {}.type
            val questions: List<Question> = gson.fromJson(jsonString, listType)
            cachedQuestions = questions
            questions
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCategories(): List<QuestionCategory> = withContext(Dispatchers.IO) {
        val questions = getQuestions()
        CategoryType.entries.map { categoryType ->
            val categoryQuestions = questions.filter { it.category.equals(categoryType.id, ignoreCase = true) }
            val description = when (categoryType) {
                CategoryType.MOOD -> "Assess persistent low mood, interest levels, and emotional balance."
                CategoryType.ANXIETY -> "Assess nervousness, tension, anticipatory worries, and physical unease."
                CategoryType.STRESS -> "Assess workload pressures, coping capacity, and feeling overwhelmed."
                CategoryType.SLEEP -> "Assess sleep quality, nighttime awakenings, and daytime fatigue."
                CategoryType.COGNITIVE -> "Assess mental focus, decision making, racing thoughts, and clarity."
                CategoryType.SOCIAL -> "Assess routine engagement, social connection, and daily motivation."
            }
            QuestionCategory(
                type = categoryType,
                title = categoryType.displayName,
                description = description,
                questions = categoryQuestions
            )
        }
    }
}
