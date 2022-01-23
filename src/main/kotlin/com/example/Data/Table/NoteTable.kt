package com.example.Data.Table

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.booleanParam

object NoteTable:Table() {

//    val id = varchar("id",512)
    val id = integer("id").autoIncrement()
    val userEmail = varchar("userEmail",512).references(UserTable.email)
    val noteTitle = text("noteTitle")
    val description = text("description")
    val date = long("date")
    val isOnline = bool("isOnline")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}