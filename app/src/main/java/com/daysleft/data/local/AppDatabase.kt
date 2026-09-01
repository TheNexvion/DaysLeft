package com.daysleft.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE events ADD COLUMN remindersEnabled INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE events ADD COLUMN remindSevenDaysBefore INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE events ADD COLUMN remindOneDayBefore INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE events ADD COLUMN remindOnDay INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE events ADD COLUMN reminderHour INTEGER NOT NULL DEFAULT 9")
        db.execSQL("ALTER TABLE events ADD COLUMN reminderMinute INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(entities = [Event::class], version = 2, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "days_left_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
