package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.udacity.databinding.ActivityMainBinding


class MainActivity: AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var selectedOptionId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupDownloadOptions()

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.mainContent.customButton.setOnClickListener {
            download()
        }
    }

    private fun setupDownloadOptions() {
        val radioGroup = binding.mainContent.downloadOptions
        // create radio button for each download option
        for (option in DownloadOption.values()) {
            val radioOption = layoutInflater
                .inflate(R.layout.download_option_radio, radioGroup, false) as RadioButton

            radioOption.tag = option.name
            radioOption.text = option.simpleName
            radioOption.id = option.ordinal
            radioOption.isChecked = option.ordinal == selectedOptionId
            radioGroup.addView(radioOption)
        }

        radioGroup.setOnCheckedChangeListener { _, id -> selectedOptionId = id }
    }

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download() {
        if (selectedOptionId == -1) {
            val hintMessage = "Please select an option above, to download the file"
            Toast.makeText(this, hintMessage, Toast.LENGTH_SHORT).show()
            return
        }
        
        val selectedOption = selectedOptionId.asDownloadOption()
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
