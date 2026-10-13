package com.example.groupprojectapp.tasks.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.groupprojectapp.documentation.data.DocumentationDao
import com.example.groupprojectapp.documentation.data.DocumentationEntry

/**
 * MODEL (data layer): the Room database for the Tasks feature. It holds the
 * `tasks` and `members` tables and hands out their DAOs.
 * As well as store documentation tables for each tasks.
 *
 * [getInstance] is a singleton: one shared database for the whole app, so
 * every caller reads and writes the same data. `@Volatile` and `synchronized`
 * make sure two threads can't create it twice.
 */

@Database(entities = [Task::class, Member::class, DocumentationEntry::class], version = 4, exportSchema = false)
abstract class TasksDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun memberDao(): MemberDao
    abstract fun documentationDao(): DocumentationDao

    companion object {
        @Volatile
        private var INSTANCE: TasksDatabase? = null

        fun getInstance(context: Context): TasksDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TasksDatabase::class.java,
                    "tasks.db"
                )
                    .fallbackToDestructiveMigration(true)
                    // !! Room will wipe and recreate database tables automatically
                    // when version changes !!
                    .build()
                    .also { INSTANCE = it }
            }
    }
}

// TODO (future): replace with real migrations before real users rely on the app.
//  This wipes all tables on every version change. Fine for development.
//  Fix: set exportSchema = true, add AutoMigration / Migration objects for each
//  version step, register them with .addMigrations(...), then remove this fallback.
