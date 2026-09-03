package com.mindguard.ai.ui.wellbeing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mindguard.ai.R
import com.mindguard.ai.data.model.Recommendation

class RecommendationAdapter(
    private var items: List<Recommendation> = emptyList(),
    private val onRecommendationClick: (Recommendation) -> Unit
) : RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    fun submitList(newItems: List<Recommendation>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recommendation_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivRecIcon)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvRecCategory)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvRecTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvRecDescription)
        private val tvActionHint: TextView = itemView.findViewById(R.id.tvRecActionHint)

        fun bind(item: Recommendation) {
            tvCategory.text = item.category.uppercase()
            tvTitle.text = item.title
            tvDescription.text = item.description

            val iconRes = when (item.actionType) {
                "BREATHING" -> R.drawable.ic_wind
                "GROUNDING" -> R.drawable.ic_sparkle
                "DIGITAL_BREAK" -> R.drawable.ic_phone_off
                "SLEEP_TIPS" -> R.drawable.ic_moon
                "BOOKING" -> R.drawable.ic_doctor
                else -> R.drawable.ic_shield_check
            }
            ivIcon.setImageResource(iconRes)

            tvActionHint.text = when (item.actionType) {
                "BREATHING" -> "Start Breathing Exercise →"
                "GROUNDING" -> "Start Grounding Exercise →"
                "DIGITAL_BREAK" -> "View Screen Usage Tips →"
                "BOOKING" -> "Find a Professional →"
                else -> "Learn More →"
            }

            itemView.setOnClickListener {
                onRecommendationClick(item)
            }
        }
    }
}
