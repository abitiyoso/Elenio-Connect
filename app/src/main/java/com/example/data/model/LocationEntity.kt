package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey
    val name: String,
    val iconName: String = "home",
    val orderIndex: Int = 0
)
