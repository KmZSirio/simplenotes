package com.bustasirio.simplenotes.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bustasirio.simplenotes.ui.theme.*

@Entity
data class Note(
    val title: String,
    val content: String,
    val timestamp: Long,
    val color: Int,
    @PrimaryKey val id: Int? = null
) {
    companion object {
        val noteColors = listOf(GreenPastel, RedPastel, BluePastel, OrangePastel, PurplePastel)
    }
}

class InvalidNoteException(message: String): Exception(message)