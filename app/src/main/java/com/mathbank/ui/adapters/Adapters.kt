package com.mathbank.ui.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.mathbank.R
import com.mathbank.data.model.Difficulty
import com.mathbank.data.model.Question
import com.mathbank.data.model.Test
import java.text.SimpleDateFormat
import java.util.*

// ──────────────────────────────────────────────
// QUESTION ADAPTER
// ──────────────────────────────────────────────
class QuestionAdapter(
    private val onQuestionClick: (Question) -> Unit,
    private val onDeleteClick: (Question) -> Unit
) : ListAdapter<Question, QuestionAdapter.ViewHolder>(QuestionDiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view.findViewById(R.id.card)
        val tvQuestionText: TextView = view.findViewById(R.id.tvQuestionText)
        val tvTopic: TextView = view.findViewById(R.id.tvTopic)
        val tvSubtopic: TextView = view.findViewById(R.id.tvSubtopic)
        val chipDifficulty: Chip = view.findViewById(R.id.chipDifficulty)
        val ivQuestionImage: ImageView = view.findViewById(R.id.ivQuestionImage)
        val tvPage: TextView = view.findViewById(R.id.tvPage)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
        val tvOptionCount: TextView = view.findViewById(R.id.tvOptionCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = getItem(position)

        holder.tvQuestionText.text = question.text.take(150) +
            if (question.text.length > 150) "..." else ""
        holder.tvTopic.text = question.topic
        holder.tvSubtopic.text = question.subtopic
        holder.tvPage.text = "S.${question.pageNumber}"

        // Zorluk chip rengi
        holder.chipDifficulty.text = "${question.difficulty.emoji} ${question.difficulty.displayName}"
        val chipColor = when (question.difficulty) {
            Difficulty.EASY -> 0xFF4CAF50.toInt()
            Difficulty.MEDIUM -> 0xFFFFC107.toInt()
            Difficulty.HARD -> 0xFFF44336.toInt()
        }
        holder.chipDifficulty.setChipBackgroundColorResource(
            when (question.difficulty) {
                Difficulty.EASY -> R.color.difficulty_easy
                Difficulty.MEDIUM -> R.color.difficulty_medium
                Difficulty.HARD -> R.color.difficulty_hard
            }
        )

        // Şık sayısı
        holder.tvOptionCount.text = if (question.options.isNotEmpty())
            "${question.options.size} şık" else "Açık uçlu"

        // Görsel
        if (question.imageData != null) {
            val bmp = BitmapFactory.decodeByteArray(question.imageData, 0, question.imageData.size)
            holder.ivQuestionImage.setImageBitmap(bmp)
            holder.ivQuestionImage.visibility = View.VISIBLE
        } else {
            holder.ivQuestionImage.visibility = View.GONE
        }

        holder.card.setOnClickListener { onQuestionClick(question) }
        holder.ivDelete.setOnClickListener { onDeleteClick(question) }
    }

    class QuestionDiffCallback : DiffUtil.ItemCallback<Question>() {
        override fun areItemsTheSame(oldItem: Question, newItem: Question) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Question, newItem: Question) = oldItem == newItem
    }
}

// ──────────────────────────────────────────────
// TEST ADAPTER
// ──────────────────────────────────────────────
class TestAdapter(
    private val onTestClick: (Test) -> Unit,
    private val onDeleteClick: (Test) -> Unit
) : ListAdapter<Test, TestAdapter.ViewHolder>(TestDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: MaterialCardView = view.findViewById(R.id.card)
        val tvTestName: TextView = view.findViewById(R.id.tvTestName)
        val tvQuestionCount: TextView = view.findViewById(R.id.tvQuestionCount)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvScore: TextView = view.findViewById(R.id.tvScore)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_test, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val test = getItem(position)

        holder.tvTestName.text = test.name
        holder.tvQuestionCount.text = "${test.questionIds.size} soru"
        holder.tvDate.text = dateFormat.format(Date(test.createdAt))

        if (test.isCompleted) {
            holder.tvStatus.text = "✅ Tamamlandı"
            holder.tvScore.text = "%${test.score}"
            holder.tvScore.visibility = View.VISIBLE
        } else {
            holder.tvStatus.text = "▶ Başla"
            holder.tvScore.visibility = View.GONE
        }

        holder.card.setOnClickListener { onTestClick(test) }
        holder.ivDelete.setOnClickListener { onDeleteClick(test) }
    }

    class TestDiffCallback : DiffUtil.ItemCallback<Test>() {
        override fun areItemsTheSame(oldItem: Test, newItem: Test) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Test, newItem: Test) = oldItem == newItem
    }
}
