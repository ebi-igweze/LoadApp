package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var option: DownloadOption
    private var downloadId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val downloadOptionName = intent.getStringExtra(DOWNLOAD_OPTION_KEY)
            ?: throw IllegalArgumentException("No value passed for download option")
        option = DownloadOption.valueOf(downloadOptionName)
        downloadId = intent.getLongExtra(DOWNLOAD_ID, -1L)

        setSupportActionBar(toolbar)
        cancelNotification()
        showFileDownloadStatus()

        binding.detailContent.okButton.setOnClickListener {
            finish()
        }
    }


    @SuppressLint("Range")
    private fun showFileDownloadStatus() {
        // get the download status
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        var isSuccessful = false
        with(cursor) {
            if (moveToFirst()) {
                val status: Int = getInt(getColumnIndex(DownloadManager.COLUMN_STATUS))
                isSuccessful = (status == DownloadManager.STATUS_SUCCESSFUL)
            }
        }

        binding.detailContent.apply {
            fileName.text = option.simpleName
            fileStatus.text =  if (isSuccessful) "SUCCESS" else "FAILED"
            val color = if (isSuccessful) R.color.colorPrimary else android.R.color.holo_red_dark
            fileStatus.setTextColor(getColor(color))
        }

    }

    private fun cancelNotification() {
        val notificationManager =
            ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
            ) as NotificationManager

        NotificationUtils.cancelNotification(notificationManager)
    }

    companion object {
        const val DOWNLOAD_OPTION_KEY = "download_option_key"
        const val DOWNLOAD_ID = "download_option_id"

    }

}
