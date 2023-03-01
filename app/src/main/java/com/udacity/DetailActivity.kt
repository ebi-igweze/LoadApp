package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var option: DownloadOption

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val downloadOptionName = intent.getStringExtra(DOWNLOAD_OPTION_KEY)
            ?: throw IllegalArgumentException("No value passed for download option")
        option = DownloadOption.valueOf(downloadOptionName)

        setSupportActionBar(toolbar)
        cancelNotification()
        showFileDownloadStatus()

        binding.detailContent.okButton.setOnClickListener {
            finish()
        }
    }


    private fun showFileDownloadStatus() {
        binding.detailContent.apply {
            fileName.text = option.simpleName
            fileStatus.text = "SUCCESS"
            fileStatus.setTextColor(getColor(R.color.colorPrimary))
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
    }

}
