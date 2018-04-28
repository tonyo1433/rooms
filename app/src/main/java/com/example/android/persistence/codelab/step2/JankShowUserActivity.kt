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

package com.example.android.persistence.codelab.step2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView

import com.example.android.codelabs.persistence.R
import com.example.android.persistence.codelab.db.AppDatabase
import com.example.android.persistence.codelab.db.Book
import com.example.android.persistence.codelab.db.utils.DatabaseInitializer

class JankShowUserActivity : AppCompatActivity() {

    private var mDb: AppDatabase? = null

    private var mBooksTextView: TextView? = null

    private fun showListInUI(books: List<Book>,
                             booksTextView: TextView?) {
        val sb = StringBuilder()
        for (book in books) {
            sb.append(book.title)
            sb.append("\n")
        }
        booksTextView!!.text = sb.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.db_activity)

        mBooksTextView = findViewById(R.id.books_tv)

        // Note: Db references should not be in an activity.
        mDb = AppDatabase.getInMemoryDatabase(applicationContext)

        populateDb()

        fetchData()
    }

    override fun onDestroy() {
        AppDatabase.destroyInstance()
        super.onDestroy()
    }

    private fun populateDb() {
        DatabaseInitializer.populateSync(mDb!!)
    }

    private fun fetchData() {
        // This activity is executing a query on the main thread, making the UI perform badly.
        val books = mDb!!.bookModel().findBooksBorrowedByNameSync("Mike")
        showListInUI(books, mBooksTextView)
    }

    fun onRefreshBtClicked(view: View) {
        mBooksTextView!!.text = ""
        fetchData()
    }
}
