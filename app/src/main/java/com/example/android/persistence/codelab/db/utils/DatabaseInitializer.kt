/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.persistence.codelab.db.utils

import android.os.AsyncTask
import android.util.Log

import com.example.android.persistence.codelab.db.AppDatabase
import com.example.android.persistence.codelab.db.Book
import com.example.android.persistence.codelab.db.Loan
import com.example.android.persistence.codelab.db.User

import java.util.Calendar
import java.util.Date

object DatabaseInitializer {

    // Simulate a blocking operation delaying each Loan insertion with a delay:
    private val DELAY_MILLIS = 500

    fun populateAsync(db: AppDatabase) {

        val task = PopulateDbAsync(db)
        task.execute()
    }

    fun populateSync(db: AppDatabase) {
        populateWithTestData(db)
    }

    private fun addLoan(db: AppDatabase, id: String,
                        user: User, book: Book, from: Date, to: Date) {
        val loan = Loan()
        loan.id = id
        loan.bookId = book.id
        loan.userId = user.id
        loan.startTime = from
        loan.endTime = to
        db.loanModel().insertLoan(loan)
    }

    private fun addBook(db: AppDatabase, id: String, title: String): Book {
        val book = Book()
        book.id = id
        book.title = title
        db.bookModel().insertBook(book)
        return book
    }

    private fun addUser(db: AppDatabase, id: String, name: String,
                        lastName: String, age: Int): User {
        val user = User()
        user.id = id
        user.age = age
        user.name = name
        user.lastName = lastName
        db.userModel().insertUser(user)
        return user
    }

    private fun populateWithTestData(db: AppDatabase) {
        db.loanModel().deleteAll()
        db.userModel().deleteAll()
        db.bookModel().deleteAll()

        val user1 = addUser(db, "1", "Jason", "Seaver", 40)
        val user2 = addUser(db, "2", "Mike", "Seaver", 12)
        addUser(db, "3", "Carol", "Seaver", 15)

        val book1 = addBook(db, "1", "Dune")
        val book2 = addBook(db, "2", "1984")
        val book3 = addBook(db, "3", "The War of the Worlds")
        val book4 = addBook(db, "4", "Brave New World")
        addBook(db, "5", "Foundation")
        try {
            // Loans are added with a delay, to have time for the UI to react to changes.

            val today = getTodayPlusDays(0)
            val yesterday = getTodayPlusDays(-1)
            val twoDaysAgo = getTodayPlusDays(-2)
            val lastWeek = getTodayPlusDays(-7)
            val twoWeeksAgo = getTodayPlusDays(-14)

            addLoan(db, "1", user1, book1, twoWeeksAgo, lastWeek)
            Thread.sleep(DELAY_MILLIS.toLong())
            addLoan(db, "2", user2, book1, lastWeek, yesterday)
            Thread.sleep(DELAY_MILLIS.toLong())
            addLoan(db, "3", user2, book2, lastWeek, today)
            Thread.sleep(DELAY_MILLIS.toLong())
            addLoan(db, "4", user2, book3, lastWeek, twoDaysAgo)
            Thread.sleep(DELAY_MILLIS.toLong())
            addLoan(db, "5", user2, book4, lastWeek, today)
            Log.d("DB", "Added loans")

        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    private fun getTodayPlusDays(daysAgo: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, daysAgo)
        return calendar.time
    }

    private class PopulateDbAsync internal constructor(private val mDb: AppDatabase) : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void): Void? {
            populateWithTestData(mDb)
            return null
        }

    }
}
