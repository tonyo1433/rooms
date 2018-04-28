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

package com.example.android.persistence.codelab.step1

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

import com.example.android.codelabs.persistence.R
import com.example.android.persistence.codelab.db.AppDatabase
import com.example.android.persistence.codelab.db.User
import com.example.android.persistence.codelab.db.utils.DatabaseInitializer
import java.util.Locale

class UsersActivity : AppCompatActivity() {

    private var mDb: AppDatabase? = null

    private var mYoungUsersTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.db_activity1)

        mYoungUsersTextView = findViewById(R.id.young_users_tv)

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
        // Note: this kind of logic should not be in an activity.
        val sb = StringBuilder()
        val youngUsers = mDb!!.userModel().findUsersYoungerThan(35)
        for (youngUser in youngUsers) {
            sb.append(String.format(Locale.US,
                    "%s, %s (%d)\n", youngUser.lastName, youngUser.name, youngUser.age))
        }
        mYoungUsersTextView!!.text = sb
    }
}
