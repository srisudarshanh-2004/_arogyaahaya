package com.arogyasahaya.ui.asha

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arogyasahaya.R
import com.arogyasahaya.data.entity.AshaEventEntity
import com.arogyasahaya.data.entity.AshaEventType
import com.arogyasahaya.databinding.ItemAshaEventBinding
import java.text.SimpleDateFormat
import java.util.*

class AshaEventAdapter : ListAdapter<AshaEventEntity, AshaEventAdapter.EventViewHolder>(DiffCallback) {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, EEEE", Locale.getDefault())

    inner class EventViewHolder(private val binding: ItemAshaEventBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: AshaEventEntity) {
            val context = binding.root.context
            
            // If titles are standard, we can translate them. 
            // If they are custom from DB, they stay as is.
            binding.tvEventTitle.text    = when(event.title) {
                "Health Camp" -> context.getString(R.string.health_camp)
                "ASHA Visit" -> context.getString(R.string.asha_visit)
                "Vaccination" -> context.getString(R.string.vaccination)
                "Wellness Check" -> context.getString(R.string.wellness_check)
                else -> event.title
            }
            
            binding.tvEventDate.text     = dateFormat.format(Date(event.eventDate))
            binding.tvEventLocation.text = event.location
            binding.tvEventDesc.text     = event.description

            // Set icon based on event type
            val emoji = when (event.eventType) {
                AshaEventType.HEALTH_CAMP    -> "🏥"
                AshaEventType.ASHA_VISIT     -> "👩‍⚕️"
                AshaEventType.VACCINATION    -> "💉"
                AshaEventType.WELLNESS_CHECK -> "❤️"
            }
            binding.tvEventIcon.text = emoji
            
            // Use theme-aware background color
            binding.cardEvent.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.background_secondary)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemAshaEventBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<AshaEventEntity>() {
        override fun areItemsTheSame(a: AshaEventEntity, b: AshaEventEntity) = a.id == b.id
        override fun areContentsTheSame(a: AshaEventEntity, b: AshaEventEntity) = a == b
    }
}
