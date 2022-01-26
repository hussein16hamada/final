package com.example.Rebository

import com.example.Data.Model.Note
import com.example.Data.Model.NoteRequest
import com.example.Data.Model.NoteResponse
import com.example.Data.Model.User
import com.example.Data.Table.NoteTable
import com.example.Data.Table.UserTable
import com.example.Rebository.DatabaseFactory.dbQuery
import io.ktor.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class Repo {

    suspend fun addUser(user: User) {
        dbQuery {
            UserTable.insert { ut ->
                ut[UserTable.email] = user.email
                ut[UserTable.hashPassword] = user.hashPassword
                ut[UserTable.name] = user.userName
            }
        }
    }

    suspend fun findUserByEmail(email: String) = dbQuery {
        UserTable.select { UserTable.email.eq(email) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }

        return User(
            email = row[UserTable.email],
            hashPassword = row[UserTable.hashPassword],
            userName = row[UserTable.name]
        )
    }

    suspend fun getAllUsers(): List<User> = dbQuery {

        UserTable.selectAll().mapNotNull { rowToUser(it) }

    }

    //    ============== NOTES ==============


    suspend fun addNote(note: NoteRequest, email: String) : Int {
       try {
           return transaction {
               val  id =  NoteTable.insert { nt ->
//                nt[NoteTable.id] = note.id
                   nt[NoteTable.userEmail] = email
                   nt[NoteTable.noteTitle] = note.noteTitle
                   nt[NoteTable.description] = note.description
                   nt[NoteTable.date] = note.date
                   nt[NoteTable.isOnline] = note.isOnline

               } get NoteTable.id

               transaction {
                   NoteTable.update ({ NoteTable.id eq id }){ nt->
                       nt[NoteTable.userEmail] = email
                       nt[NoteTable.noteTitle] = note.noteTitle
                       nt[NoteTable.description] = note.description
                       nt[NoteTable.date] = note.date
                       nt[NoteTable.isOnline] = note.isOnline
                   }
               }

               return@transaction id.value
           }

       }catch (e:Exception){
           return -1
       }

    }


    suspend fun getAllNotes(email: String): List<NoteResponse> = dbQuery {

        NoteTable.select {
            NoteTable.userEmail.eq(email)
        }.mapNotNull { rowToNote(it) }

    }


    suspend fun updateNote(note: NoteResponse, email: String, id: Int) {

        dbQuery {

            NoteTable.update(
                where = {
                    NoteTable.userEmail.eq(email) and NoteTable.id.eq(id)
                }
            ) { nt ->
                nt[NoteTable.id] = note.id
                nt[NoteTable.noteTitle] = note.noteTitle
                nt[NoteTable.description] = note.description
                nt[NoteTable.date] = note.date
                nt[NoteTable.isOnline] = note.isOnline

            }

        }

    }

    //
    suspend fun deleteNote(id: Int, email: String) {
        dbQuery {
            NoteTable.deleteWhere { NoteTable.userEmail.eq(email) and NoteTable.id.eq(id) }
        }
    }


    private fun rowToNote(row: ResultRow?): NoteResponse? {

        if (row == null) {
            return null
        }

        return NoteResponse(
            id = row[NoteTable.id],
            noteTitle = row[NoteTable.noteTitle],
            description = row[NoteTable.description],
            date = row[NoteTable.date],
            isOnline = row[NoteTable.isOnline]
        )

    }

}