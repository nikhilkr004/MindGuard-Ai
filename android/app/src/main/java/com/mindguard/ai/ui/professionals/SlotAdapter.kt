package com.mindguard.ai.ui.professionals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mindguard.ai.R
import com.mindguard.ai.data.model.Slot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SlotAdapter(
    private var slots: List<Slot> = emptyList(),
    private val onSlotSelected: (Slot) -> Unit
) : RecyclerView.Adapter<SlotAdapter.ViewHolder>() {

    private var selectedPosition = -1

    fun submitList(newSlots: List<Slot>) {
        slots = newSlots
        selectedPosition = if (newSlots.isNotEmpty()) 0 else -1
        notifyDataSetChanged()
        if (selectedPosition != -1 && slots.isNotEmpty()) {
            onSlotSelected(slots[0])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slot_chip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(slots[position], position == selectedPosition, position)
    }

    override fun getItemCount(): Int = slots.size

    fun getSelectedSlot(): Slot? {
        return if (selectedPosition in slots.indices) slots[selectedPosition] else null
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val container: LinearLayout = itemView.findViewById(R.id.llSlotContainer)
        private val tvSlotTime: TextView = itemView.findViewById(R.id.tvSlotTime)
        private val tvSlotDate: TextView = itemView.findViewById(R.id.tvSlotDate)

        private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        private val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

        fun bind(slot: Slot, isSelected: Boolean, pos: Int) {
            val start = Date(slot.startTime)
            val end = Date(slot.endTime)

            tvSlotTime.text = "${timeFormat.format(start)} - ${timeFormat.format(end)}"
            tvSlotDate.text = "${dateFormat.format(start)} • Available"

            if (isSelected) {
                container.setBackgroundResource(R.drawable.bg_slot_selected)
                tvSlotTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.primary))
            } else {
                container.setBackgroundResource(R.drawable.bg_slot_unselected)
                tvSlotTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_primary))
            }

            itemView.setOnClickListener {
                val oldPos = selectedPosition
                selectedPosition = pos
                notifyItemChanged(oldPos)
                notifyItemChanged(selectedPosition)
                onSlotSelected(slot)
            }
        }
    }
}
