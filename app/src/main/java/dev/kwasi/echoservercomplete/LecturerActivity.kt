import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.kwasi.echoservercomplete.CommunicationActivity
import dev.kwasi.echoservercomplete.R
import dev.kwasi.echoservercomplete.chat.ChatActivity
import dev.kwasi.echoservercomplete.peerlist.PeerListAdapter
import dev.kwasi.echoservercomplete.peerlist.PeerListAdapterInterface

class LecturerActivity : AppCompatActivity(), PeerListAdapterInterface {

    private lateinit var wifiP2pManager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var attendeesListView: RecyclerView
    private lateinit var adapter: PeerListAdapter
    private val attendeesList = ArrayList<WifiP2pDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecturer)

        // Initialize WifiP2pManager and Channel
        wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager.initialize(this, mainLooper, null)

        // Set up RecyclerView for Attendees List
        attendeesListView = findViewById(R.id.attendeesList)
        attendeesListView.layoutManager = LinearLayoutManager(this)
        adapter = PeerListAdapter(this)  // No need to pass the list here; it’s managed internally
        attendeesListView.adapter = adapter
    }

    override fun onPeerClicked(peer: WifiP2pDevice) {
        // Open ChatActivity and pass the attendee's device address
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("ATTENDEE_DEVICE_ADDRESS", peer.deviceAddress)
        startActivity(intent)
    }

    private fun addAttendee(attendee: WifiP2pDevice) {
        attendeesList.add(attendee)         // Add WifiP2pDevice object to list
        adapter.updateList(attendeesList)   // Update adapter with new list
    }

    private fun endClass() {
        wifiP2pManager.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(
                    this@LecturerActivity, "Class ended successfully.", Toast.LENGTH_SHORT
                ).show()
                attendeesList.clear()
                adapter.updateList(attendeesList)  // Clear the adapter list as well

                // Navigate back to CommunicationActivity
                val intent = Intent(this@LecturerActivity, CommunicationActivity::class.java)
                startActivity(intent)
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
