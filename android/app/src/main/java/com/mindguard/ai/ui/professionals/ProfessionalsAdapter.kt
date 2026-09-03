package com.mindguard.ai.ui.professionals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mindguard.ai.R
import com.mindguard.ai.data.model.Professional

class ProfessionalsAdapter(
    private var items: List<Professional> = emptyList(),
    private val onProfessionalClick: (Professional) -> Unit
) : RecyclerView.Adapter<ProfessionalsAdapter.ViewHolder>() {

    fun submitList(newItems: List<Professional>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_professional_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvProfName)
        private val ivVerified: ImageView = itemView.findViewById(R.id.ivVerifiedBadge)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvProfTitle)
        private val tvRating: TextView = itemView.findViewById(R.id.tvProfRating)
        private val tvQualifications: TextView = itemView.findViewById(R.id.tvProfQualifications)
        private val tvLanguages: TextView = itemView.findViewById(R.id.tvProfLanguages)
        private val tvFee: TextView = itemView.findViewById(R.id.tvProfFee)
        private val btnBook: MaterialButton = itemView.findViewById(R.id.btnBookSession)

        fun bind(prof: Professional) {
            tvName.text = prof.name
            ivVerified.visibility = if (prof.isVerified) View.VISIBLE else View.GONE
            tvTitle.text = "${prof.title} • ${prof.experienceYears} yrs exp"
            tvRating.text = "${prof.rating} (${prof.reviewCount})"
            tvQualifications.text = "Specialty: ${prof.specialty} • ${prof.qualifications}"
            tvLanguages.text = "🗣 ${prof.languages.joinToString(", ")}"
            tvFee.text = "₹${prof.consultationFee.toInt()} / session"

            val clickListener = View.OnClickListener {
                onProfessionalClick(prof)
            }

            itemView.setOnClickListener(clickListener)
            btnBook.setOnClickListener(clickListener)
        }
    }
}
