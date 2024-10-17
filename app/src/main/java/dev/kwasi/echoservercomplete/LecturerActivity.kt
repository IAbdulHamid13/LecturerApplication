package dev.kwasi.echoservercomplete

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.kwasi.echoservercomplete.chat.ChatActivity
import dev.kwasi.echoservercomplete.peerlist.PeerListAdapter
import dev.kwasi.echoservercomplete.peerlist.PeerListAdapterInterface

class LecturerActivity : AppCompatActivity(), PeerListAdapterInterface {

    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var attendeesListView: RecyclerView
    private lateinit var adapter: PeerListAdapter
    private val attendeesList = ArrayList<WifiP2pDevice>()


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecturer)

        // Initialize WifiP2pManager and Channel
        wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager.initialize(this, mainLooper, null)

        // Set up RecyclerView for Attendees List
        attendeesListView = findViewById(R.id.attendeesList)
        attendeesListView.layoutManager = LinearLayoutManager(this)
        adapter = PeerListAdapter(this)
        attendeesListView.adapter = adapter

        // Start peer discovery
        wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(this@LecturerActivity, "Peer discovery started", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(
                    this@LecturerActivity,
                    "Peer discovery failed: $reason",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        // Set up a listener to respond when peers are found
        wifiP2pManager.requestPeers(channel) { peerList ->
            // Clear and update the list of attendees with the newly found peers
            attendeesList.clear()
            attendeesList.addAll(peerList.deviceList)
            adapter.updateList(attendeesList)
        }
    }

    private val wifiP2pReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    // Request the updated peer list
                    wifiP2pManager.requestPeers(channel, object : WifiP2pManager.PeerListListener {
                        override fun onPeersAvailable(peers: WifiP2pDeviceList) {
                            // Update attendeesList with new attendees
                            for (device in peers.deviceList) {
                                if (!attendeesList.contains(device)) {
                                    addAttendee(device)
                                }
                            }
                        }
                    })
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        // Register the BroadcastReceiver
        registerReceiver(
            wifiP2pReceiver,
            IntentFilter(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        )
        // Start peer discovery
        wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.e("LecturerActivity", "Peer discovery started")
            }

            override fun onFailure(reason: Int) {
                Log.e("LecturerActivity", "Peer discovery failed: $reason")
            }
        })
    }

    override fun onPause() {
        super.onPause()
        // Unregister the BroadcastReceiver
        unregisterReceiver(wifiP2pReceiver)
        // Stop peer discovery (optional, you might want to keep it running)
        // wifiP2pManager.stopPeerDiscovery(channel, /* ActionListener */)
    }

    override fun onPeerClicked(peer: WifiP2pDevice) {
        // Open ChatActivity and pass the attendee's device address
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("ATTENDEE_DEVICE_ADDRESS", peer.deviceAddress)
        startActivity(intent)
    }

    fun addAttendee(attendee: WifiP2pDevice) {
        attendeesList.add(attendee)         // Add WifiP2pDevice object to list
        adapter.updateList(attendeesList)   // Update adapter with new list
    }

    fun endClass(view: View) {
        wifiP2pManager.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(
                    this@LecturerActivity, "Class ended successfully.", Toast.LENGTH_SHORT
                ).show()
                attendeesList.clear()
                adapter.updateList(attendeesList)  // Clear the adapter list as well
                finish() // Close LecturerActivity
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(
                    this@LecturerActivity, "Failed to end class: $reason", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
