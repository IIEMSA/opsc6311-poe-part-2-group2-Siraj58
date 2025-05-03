package com.example.moneyv1

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EntryAdapter(private val entries: List<Entry>) : RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_entry, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemCount(): Int = entries.size

    inner class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvTimeRange: TextView = itemView.findViewById(R.id.tvTimeRange)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val ivEntryImage: ImageView = itemView.findViewById(R.id.ivEntryImage)

        fun bind(entry: Entry) {
            tvTitle.text = entry.title
            tvAmount.text = if (entry.expense) "-R${entry.amount}" else "+R${entry.amount}"
            tvCategory.text = "Category: ${entry.category}"
            tvDate.text = "Date: ${entry.date}"
            tvTimeRange.text = "Time: ${entry.startTime} - ${entry.endTime}"
            tvDescription.text = "Note: ${entry.description}"

            // Show image if imageUri exists
            if (!entry.imageUri.isNullOrEmpty()) {
                ivEntryImage.visibility = View.VISIBLE
                ivEntryImage.setImageURI(Uri.parse(entry.imageUri))
            } else {
                ivEntryImage.visibility = View.GONE
            }
        }
    }
}
