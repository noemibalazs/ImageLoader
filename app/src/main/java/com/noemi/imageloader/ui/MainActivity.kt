package com.noemi.imageloader.ui

import android.Manifest
import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.noemi.imagecache.ImageDiskCache
import com.noemi.imagecache.ImageLoader
import com.noemi.imagecache.ImageMemoryCache
import com.noemi.imagecache.PeriodicClearCacheListener
import com.noemi.imageloader.R
import com.noemi.imageloader.databinding.ActivityMainBinding
import com.noemi.imageloader.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ManualClearCacheListener, PeriodicClearCacheListener {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var viewBinding: ActivityMainBinding
    private val notificationPermission = listOf(Manifest.permission.POST_NOTIFICATIONS)

    @Inject
    lateinit var memoryCache: ImageMemoryCache

    @Inject
    lateinit var diskCache: ImageDiskCache

    private val placeHolder by lazy { BitmapFactory.decodeResource(resources, R.drawable.a3) }
    private lateinit var adapter: ZipoImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (intent.action == SCHEDULED_CACHE_CLEAR_ACTION) {
            cacheClear()
        }

        memoryCache.initializeCache()
        diskCache.initialize(this)

        val loader = ImageLoader.getInstance(memoryCache, diskCache, placeHolder, this)
        adapter = ZipoImageAdapter(emptyList(), loader)

        with(viewBinding) {
            listener = this@MainActivity
            model = viewModel
            lifecycleOwner = this@MainActivity
            imageRecycleView.adapter = adapter
        }

        with(viewModel) {
            loadImages()
            zipoImages.observe(this@MainActivity) { images ->
                adapter.updateImages(images)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !permissionIsGranted()) {
            ActivityCompat.requestPermissions(this, notificationPermission.toTypedArray(), NOTIFICATION_CODE)
        }
    }

    private fun permissionIsGranted() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permission: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults)
        when (requestCode) {
            NOTIFICATION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(this, notificationPermission.toTypedArray(), NOTIFICATION_CODE)
            }
            else -> Unit
        }
    }

    override fun periodicCacheClear() {
        scheduleCacheClear()
    }

    private fun scheduleCacheClear() {
        val time: Long = System.currentTimeMillis().plus(4 * 1000 * 3600)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, CacheCleanerReceiver::class.java)
        intent.action = CACHE_CLEAR_ACTION
        val pendingIntent =
            PendingIntent.getBroadcast(this, 12, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setAndAllowWhileIdle(RTC_WAKEUP, time, pendingIntent)

        Toast.makeText(this, R.string.label_cache_clear, Toast.LENGTH_LONG).show()
    }

    override fun manualCacheClear() {
        cacheClear()
    }

    private fun cacheClear() {
        memoryCache.clearCache()
        diskCache.clearCache()
        Toast.makeText(this, getString(R.string.label_notification_content), Toast.LENGTH_LONG).show()
    }
}