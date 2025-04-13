package com.example.myrestaurant.database

import android.content.ContentValues
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

        val createOrderTable = """
            CREATE TABLE orders (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customerName TEXT NOT NULL,
                itemList TEXT NOT NULL, 
                totalAmount REAL NOT NULL,
                orderDate TEXT NOT NULL
            );
        """.trimIndent()

        val createInvoicesTable = """
            CREATE TABLE invoices (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                staffId INTEGER NOT NULL,
                month TEXT NOT NULL,
                totalShifts INTEGER NOT NULL,
                totalPay REAL NOT NULL,
                FOREIGN KEY (staffId) REFERENCES staff(id)
            );
        """.trimIndent()

        db?.execSQL(createStaffTable)
        db?.execSQL(createMenuTable)
        db?.execSQL(createShiftTable)
        db?.execSQL(createOrderTable)
        db?.execSQL(createInvoicesTable)

        Log.d("DBHelper", "Tables created")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS staff")
        db?.execSQL("DROP TABLE IF EXISTS menu")
        db?.execSQL("DROP TABLE IF EXISTS shift")
        db?.execSQL("DROP TABLE IF EXISTS orders")
        db?.execSQL("DROP TABLE IF EXISTS invoices")
        onCreate(db)
    }

    // Insert a new staff member
    fun addStaff(name: String, role: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("role", role)
        }
        val result = db.insert("staff", null, values)
        db.close()
        return result
    }

    // Update staff details
    fun updateStaff(id: Int, name: String, role: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("role", role)
        }
        val result = db.update("staff", values, "id = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    // Delete staff by ID
    fun deleteStaff(id: Int): Int {
        val db = writableDatabase
        val result = db.delete("staff", "id = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    // Query all staff members
    fun getAllStaff(): List<String> {
        val staffList = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.query("staff", arrayOf("id", "name", "role"), null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                val staffName = getString(getColumnIndexOrThrow("name"))
                staffList.add(staffName)
            }
        }
        cursor.close()
        db.close()
        return staffList
    }
}


// Other functions for menu, orders, etc., can be added similarly...




