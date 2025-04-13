package com.example.myresturant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myresturant.database.MyDatabaseHelper
import com.example.myresturant.ui.theme.MyresturantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyresturantTheme {
                val dbHelper = remember { MyDatabaseHelper(this) }

                // Add dummy staff only if not already added (optional: avoid duplicates on recomposition)
                dbHelper.addStaff("John Doe", "Chef")

                val staffList = dbHelper.getAllStaff()

                Scaffold { paddingValues ->
                    StaffListScreen(
                        staffList = staffList,
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun StaffListScreen(staffList: List<String>, modifier: Modifier = Modifier) {
    Text(
        text = "Staff List:\n" + staffList.joinToString("\n"),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyresturantTheme {
        StaffListScreen(staffList = listOf("John Doe", "Jane Smith"))
    }
}
