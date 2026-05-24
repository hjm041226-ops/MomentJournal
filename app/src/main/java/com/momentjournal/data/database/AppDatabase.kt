package com.momentjournal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.momentjournal.data.converter.DateConverter
import com.momentjournal.data.dao.*
import com.momentjournal.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [RecordEntity::class, BlockEntity::class, TagEntity::class, RecordTagCrossRef::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun blockDao(): BlockDao
    abstract fun tagDao(): TagDao
    abstract fun recordTagDao(): RecordTagDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "moment_journal.db"
                )
                    .addCallback(PresetTagCallback())
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

class PresetTagCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val presetTags = listOf("工作", "生活", "旅行", "学习", "运动", "美食")
            presetTags.forEachIndexed { index, name ->
                db.execSQL(
                    "INSERT INTO tags (id, name, isPreset) VALUES (?, ?, 1)",
                    arrayOf(index + 1L, name)
                )
            }
        }
    }
}
