package com.example.Data.Model

import org.jetbrains.exposed.dao.id.EntityID


data class NoteResponse(
    val id: Int,
    val noteTitle:String,
    val description:String,
    val date:Long,
    val isOnline:Boolean

)