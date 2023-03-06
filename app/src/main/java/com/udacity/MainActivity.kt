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
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding


class MainActivity: AppCompatActivity() {

    private var downloadID: Long = -1
    private lateinit var binding: ActivityMainBinding
    private var selectedOptionId: Int = -1


    private val notificationManager by lazy {
        ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
    }

    private val receiver = object: BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            // set download as complete
            binding.mainContent.customButton.completeDownload()
            showNotification()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createNotificationChannel()
        setupDownloadOptions()
    }

    private fun createNotificationChannel() {
        val channelId = getString(R.string.channel_id)
        val channelName = getString(R.string.channel_name)

        NotificationUtils.createChannel(
            notificationManager,
            channelId,
            channelName
        )
    }

    private fun createPendingIntent(): PendingIntent {
        val detailIntent = Intent(
            applicationContext,
            DetailActivity::class.java
        ).putExtra(
            DetailActivity.DOWNLOAD_OPTION_KEY,
            selectedOptionId.asDownloadOption().name
        ).putExtra(
            DetailActivity.DOWNLOAD_ID,
            downloadID
        )

        return PendingIntent.getActivity(
            applicationContext,
            NotificationUtils.NOTIFICATION_ID,
            detailIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
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

        binding.mainContent.customButton.setOnClickListener {
            download()
        }
    }


    private fun download() {
        if (selectedOptionId == -1) {
            val hintMessage = "Please select an option above, to download the file"
            Toast.makeText(this, hintMessage, Toast.LENGTH_SHORT).show()
            return
        }

        // change button state
        binding.mainContent.customButton.buttonState = ButtonState.Loading

        val selectedOption = selectedOptionId.asDownloadOption()
        val request =
            DownloadManager.Request(Uri.parse(selectedOption.url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        // put the download request in the queue.
        downloadID = downloadManager.enqueue(request)
    }

    private fun showNotification() {
        val pendingIntent = createPendingIntent()

        NotificationUtils.sendNotification(
            applicationContext,
            notificationManager,
            pendingIntent
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        // remove broadcast receiver
        unregisterReceiver(receiver)
    }
}
