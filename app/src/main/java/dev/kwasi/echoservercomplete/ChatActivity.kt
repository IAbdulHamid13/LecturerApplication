package dev.kwasi.echoservercomplete.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import dev.kwasi.echoservercomplete.R

class ChatActivity : AppCompatActivity() {

    private lateinit var chatTextView: TextView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private var attendeeDeviceAddress: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatTextView = findViewById(R.id.chatTextView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)

        // Get the attendee's device address from the intent
        attendeeDeviceAddress = intent.getStringExtra("ATTENDEE_DEVICE_ADDRESS")

        // Set the title or use the device address as a title
        title = "Chat with $attendeeDeviceAddress"

        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotEmpty()) {
                // Display the sent message in the chat
                chatTextView.append("Me: $message\n")
                messageEditText.text.clear()

                // TODO: Send the message to the attendee
            }
        }
    }
}
