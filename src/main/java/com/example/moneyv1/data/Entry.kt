// Entry.kt
package com.example.moneyv1

import android.icu.text.CaseMap.Title
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.sql.Time

@Entity
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val expense: Boolean,
    val amount : Float,
    val date: String,
    val title: String,
    val startTime: String,
    val endTime: String,
    val description: String,
    val category: String,
    val minGoal: Int,
    val maxGoal: Int,
    val imagepath: String,
    val imageUri: String?
)
