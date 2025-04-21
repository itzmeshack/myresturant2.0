package com.example.myresturant

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myrestaurant.database.MyDatabaseHelper
import com.example.myresturant.ui.theme.MyresturantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dbHelper = MyDatabaseHelper(this)

        // Avoid duplicates by checking existing staff (optional improvement)
        dbHelper.addStaff("John Doe", "Chef")

        val staffId = dbHelper.addStaff("Alice", "Waiter").toInt()
        dbHelper.assignShift("2025-04-15", staffId)
        dbHelper.updateShiftStatus(1, "Accepted")

        val staffList = dbHelper.getAllStaff()
        val todayStaffShifts = dbHelper.getShiftsByDate("2025-04-15")


        val staffStats = dbHelper.getStaffShiftStats()
        val todayShifts = dbHelper.getShiftsByDate("2025-04-15")


        setContent {
            MyresturantTheme {

                //main content below please be careful

                AdminApp()




               /*
                Scaffold { paddingValues ->
                    StaffListScreen(
                        staffList = staffStats + listOf("\nToday's Shifts:") + todayShifts,
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()

                    )
                   /* PendingScreen(
                        staffPend = staffList + listOf("\n Pending Shifts:") + todayStaffShifts,
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    )*/


                }

                */
            }
        }
    }
}


//for admin panel screen below


@Composable

fun AdminApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(navController) }
        composable("staff") { StaffManagementScreen() }
        composable("shifts") { ShiftAssignmentScreen() }
        composable("payroll") { PayrollScreen() }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Admin Dashboard") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { navController.navigate("staff") }) {
                Text("Manage Staff")
            }
            Button(onClick = { navController.navigate("shifts") }) {
                Text("Assign Shifts")
            }
            Button(onClick = { navController.navigate("payroll") }) {
                Text("View Payroll")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffManagementScreen(context: Context = LocalContext.current) {
    val dbHelper = remember { MyDatabaseHelper(context) }

    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var staffList by remember { mutableStateOf(dbHelper.getAllStaffDetailed()) }
    var selectedId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Staff Management") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Staff Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = role,
                onValueChange = { role = it },
                label = { Text("Role (e.g., Chef, Waiter)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    if (name.isNotBlank() && role.isNotBlank()) {
                        if (selectedId == null) {
                            dbHelper.addStaff(name, role)
                        } else {
                            dbHelper.updateStaff(selectedId!!, name, role)
                            selectedId = null
                        }
                        name = ""
                        role = ""
                        staffList = dbHelper.getAllStaffDetailed()
                    }
                }) {
                    Text(if (selectedId == null) "Add Staff" else "Update Staff")
                }

                Button(onClick = {
                    name = ""
                    role = ""
                    selectedId = null
                }) {
                    Text("Clear")
                }
            }

            Divider()

            LazyColumn {
                items(staffList) { staff ->
                    val parts = staff.split(" - ")
                    val id = parts[0].toInt()
                    val staffName = parts[1]
                    val staffRole = parts[2]

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Name: $staffName")
                            Text("Role: $staffRole")
                        }
                        Row {
                            IconButton(onClick = {
                                selectedId = id
                                name = staffName
                                role = staffRole
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = {
                                dbHelper.deleteStaff(id)
                                staffList = dbHelper.getAllStaffDetailed()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                    Divider()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftAssignmentScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Shift Assignment") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Assign shifts to staff (UI coming soon)")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayrollScreen() {



    @OptIn(ExperimentalMaterial3Api::class)





    @Composable
    fun DashboardScreen(navController: NavHostController) {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Admin Dashboard") }) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = { navController.navigate("staff") }) {
                    Text("Manage Staff")
                }
                Button(onClick = { navController.navigate("shifts") }) {
                    Text("Assign Shifts")
                }
                Button(onClick = { navController.navigate("payroll") }) {
                    Text("View Payroll")
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun StaffManagementScreen() {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Staff Management") }) }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("Add, Update, Delete, View Staff (UI coming soon)")
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ShiftAssignmentScreen() {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Shift Assignment") }) }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("Assign shifts to staff (UI coming soon)")
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PayrollScreen() {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Payroll") }) }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("Monthly invoice summary (UI coming soon)")
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Payroll") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Monthly invoice summary (UI coming soon)")
        }
    }
}



/*

@Composable
fun StaffListScreen(staffList: List<String>, modifier: Modifier = Modifier) {
    Text(
        text = "Staff List:\n" + staffList.joinToString("\n"),
        modifier = modifier
    )
}

*/

/*
@Composable
fun PendingScreen(staffPend: List<String>, modifier: Modifier = Modifier){
    Text(
        text = "Pending List:\n" + staffPend.joinToString("\n"),
        modifier = modifier
    )
}
*/
/*

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyresturantTheme {
        StaffListScreen(staffList = listOf("John Doe", "Jane Smith"))
    }
}
*/



