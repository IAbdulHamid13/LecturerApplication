package dev.kwasi.echoservercomplete

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LecturerActivity : AppCompatActivity() {

    // Define a hardcoded set of valid student IDs
    private val validStudentIDs = setOf("ID001", "ID002", "ID003")

    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var attendeesListView: ListView
    private val attendeesList = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecturer)

        // Initialize WifiP2pManager and Channel
        wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager.initialize(this, mainLooper, null)

        attendeesListView = findViewById(R.id.attendeesList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, attendeesList)
        attendeesListView.adapter = adapter

        // Set up the End Class button functionality
        findViewById<Button>(R.id.endClassButton).setOnClickListener {
            endClass()
        }
    }

    // Function to handle student connections
    private fun onStudentConnect(studentID: String) {
        if (validStudentIDs.contains(studentID)) {
            // Add student to the list if ID is valid
            attendeesList.add(studentID)
            adapter.notifyDataSetChanged()
        } else {
            // Handle invalid student ID
            Toast.makeText(this, "Unauthorized student", Toast.LENGTH_SHORT).show()
        }
    }

    private fun endClass() {
        // Remove the group
        wifiP2pManager.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(
                    this@LecturerActivity,
                    "Class ended successfully.",
                    Toast.LENGTH_SHORT
                ).show()
                attendeesList.clear() // Clear the list of attendees
                adapter.notifyDataSetChanged() // Update the list view
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(
                    this@LecturerActivity,
                    "Failed to end class: $reason",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}