package com.example.myresturant.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "restaurant.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createStaffTable = """
            CREATE TABLE staff (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                role TEXT NOT NULL,
                shifts_completed INTEGER DEFAULT 0
            );
        """.trimIndent()


        val createShiftTable = """
            CREATE TABLE shift (
               id INTEGER PRIMARY KEY AUTOINCREMENT,
               date TEXT NOT NULL,
               staff_id INTEGER NOT NULL,
               status TEXT NOT NULL CHECK(status IN ('Accepted', 'Declined', 'Pending')),
               FOREIGN KEY (staff_id) REFERENCES staff(id)
            );
        """.trimIndent()

        val createMenuTable = """
            CREATE TABLE menu (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                description TEXT
            );
        """.trimIndent()

        db?.execSQL(createStaffTable)
        db?.execSQL(createMenuTable)
        db?.execSQL(createShiftTable)


        Log.d("DBHelper", "Tables created")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS staff")
        db?.execSQL("DROP TABLE IF EXISTS menu")
        db?.execSQL("DROP TABLE IF EXISTS shift")
        onCreate(db)
    }
}
