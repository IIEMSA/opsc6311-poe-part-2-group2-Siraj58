package com.example.moneyv1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyv1.R


class CategoryAdapter(private val categories: List<Category>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    // ViewHolder that binds the layout items
    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
    }

    // Called when RecyclerView needs a new ViewHolder to represent an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    // Called to bind data to the ViewHolder
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        // Set category name
        holder.nameTextView.text = category.name

        // Load the category image (use a placeholder for now)
        val imageResId = if (category.picture.isNotEmpty()) {
            // Load the image dynamically (e.g., use Glide, Picasso, etc.)
            // In this case, we're assuming it's an image resource name
            val resId = holder.itemView.context.resources.getIdentifier(category.picture, "drawable", holder.itemView.context.packageName)
            if (resId != 0) resId else R.drawable.ic_more // Fallback to default if not found
        } else {
            // Default image if no picture is provided
            R.drawable.ic_more
        }

        holder.iconImageView.setImageResource(imageResId)
    }

    // Return the number of categories
    override fun getItemCount(): Int {
        return categories.size
    }
}
