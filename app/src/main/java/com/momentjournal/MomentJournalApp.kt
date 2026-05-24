package com.momentjournal

import android.app.Application
import com.momentjournal.data.database.AppDatabase

class MomentJournalApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
}
