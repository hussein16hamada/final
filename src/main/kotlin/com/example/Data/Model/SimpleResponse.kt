package com.example.Data.Model

data class SimpleResponse(
    val success:Boolean,
    val message:String,
    val noteId:Int=-1
)