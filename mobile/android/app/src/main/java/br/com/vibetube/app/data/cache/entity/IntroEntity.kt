package br.com.vibetube.app.data.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "intro_cache")
data class IntroEntity(
    @PrimaryKey val id: String = "current",
    val title: String,
    val subtitle: String,
    val body: String,
    val sourceUrl: String,
    val updatedAt: String?,
    val cachedAt: Long
)
