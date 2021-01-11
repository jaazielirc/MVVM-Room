package com.jaax.edsa.vista

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.snackbar.Snackbar
import com.jaax.edsa.R
import com.jaax.edsa.vista.GestorNotificaciones.Companion.REPLY_ACTION

class NotificationReplyN: AppCompatActivity() {
    private var messageId = 0
    private var notifyId = 0
    private lateinit var mSendButton: ImageButton
    private lateinit var mEditReply: EditText
    private lateinit var relativeLayout: RelativeLayout

    companion object {
        private const val KEY_MESSAGE_ID = "key_message_id"
        private const val KEY_NOTIFY_ID = "key_notify_id"

        fun getReplyMessageIntent(context: Context?, notifyId: Int, messageId: Int): Intent {
            val intent = Intent(context, NotificationReplyN::class.java)
            intent.action = REPLY_ACTION
            intent.putExtra(KEY_MESSAGE_ID, messageId)
            intent.putExtra(KEY_NOTIFY_ID, notifyId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reply)

        if(REPLY_ACTION == intent.action) {
            messageId = intent.getIntExtra(KEY_MESSAGE_ID, 0)
            notifyId = intent.getIntExtra(KEY_NOTIFY_ID, 0)
        }
        mEditReply = findViewById<View>(R.id.edit_reply) as EditText
        mSendButton = findViewById<View>(R.id.button_send) as ImageButton
        mSendButton.setOnClickListener {
            Toast.makeText(this, "SENT", Toast.LENGTH_SHORT).show()
            sendMessage(notifyId)
        }
        relativeLayout = findViewById(R.id.activity_reply)
    }

    private fun sendMessage(messageId: Int) {
        val message = mEditReply.text.toString().trim { it <= ' ' }

        updateNotification(this, notifyId, message)

        Snackbar
            .make(relativeLayout,"SNACK: $messageId -> $message", Snackbar.LENGTH_SHORT )
            .setAction("OK") { finish() }
            .show()
    }

    private fun updateNotification(context: Context, notifyId: Int, message: String) {
        val notificationManager = NotificationManagerCompat.from(context)
        val builder = NotificationCompat
            .Builder(context, GestorNotificaciones.CHANNEL)
            .setSmallIcon(R.drawable.addmail)
            .setContentTitle(message)
            .setContentText("AQUI VA TU CONTRASEÑA")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notifyId, builder.build())
    }
}