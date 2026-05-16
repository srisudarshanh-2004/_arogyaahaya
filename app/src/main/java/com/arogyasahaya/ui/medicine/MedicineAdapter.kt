package com.arogyasahaya.ui.medicine

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arogyasahaya.R
import com.arogyasahaya.data.entity.MedicineEntity
import com.arogyasahaya.databinding.ItemMedicineBinding

class MedicineAdapter(
    private val onEdit: (MedicineEntity) -> Unit,
    private val onDelete: (MedicineEntity) -> Unit
) : ListAdapter<MedicineEntity, MedicineAdapter.MedicineViewHolder>(DiffCallback) {

    inner class MedicineViewHolder(private val binding: ItemMedicineBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(medicine: MedicineEntity) {
            val context = binding.root.context
            binding.tvMedicineName.text = medicine.name
            binding.tvDosage.text       = context.getString(R.string.dosage_format, medicine.dosage)

            // Build schedule text e.g. "🌅 Morning  ☀️ Afternoon"
            val slots = buildList {
                if (medicine.isMorning)   add(context.getString(R.string.morning_time_format, medicine.morningTime))
                if (medicine.isAfternoon) add(context.getString(R.string.afternoon_time_format, medicine.afternoonTime))
                if (medicine.isNight)     add(context.getString(R.string.night_time_format, medicine.nightTime))
            }
            binding.tvSchedule.text = slots.joinToString("  •  ")

            binding.btnEdit.setOnClickListener   { onEdit(medicine) }
            binding.btnDelete.setOnClickListener { onDelete(medicine) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val binding = ItemMedicineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MedicineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MedicineEntity>() {
        override fun areItemsTheSame(a: MedicineEntity, b: MedicineEntity) = a.id == b.id
        override fun areContentsTheSame(a: MedicineEntity, b: MedicineEntity) = a == b
    }
}
