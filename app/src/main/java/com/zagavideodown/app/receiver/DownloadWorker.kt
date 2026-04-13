/*
 * *
 *  * Created by Syed Usama Ahmad on 3/14/23, 5:04 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 3/14/23, 5:01 PM
 *
 */

package com.zagavideodown.app.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.zagavideodown.app.GlobalConstant
import com.zagavideodown.app.R
import com.zagavideodown.app.utils.Utils
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import java.io.File

class DownloadWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager?

    override suspend fun doWork(): Result {

        val url = inputData.getString(urlKey)!!
        val name = Utils.createFilenameWithJapneseAndOthers(inputData.getString(nameKey)!!)
        println("myfilepath nameeAFTER = $name")
        val formatId = inputData.getString(formatIdKey)!!
        val acodec = inputData.getString(acodecKey)
        val vcodec = inputData.getString(vcodecKey)
        val taskId = inputData.getString(taskIdKey)!!
        val ext = inputData.getString(ext)!!

        createNotificationChannel()
        val notificationId = id.hashCode()
        val notification = NotificationCompat.Builder(
            applicationContext,
            GlobalConstant.myNotificationChannel
        )
            .setSmallIcon(R.drawable.ic_download_yellow)
            .setContentTitle(name)
            .setSound(null)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setContentText(applicationContext.getString(R.string.don_start))
            .setOngoing(true)
            .build()

//todo

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setForegroundAsync(
                ForegroundInfo(
                    notificationId,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            )

        } else {
            setForegroundAsync(ForegroundInfo(notificationId, notification))
        }

        val request = YoutubeDLRequest(url)


        try {
            val file_v = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + GlobalConstant.directoryMediaVideos
            )
            if (!file_v.exists()) {
                file_v.mkdir()
            }
            val file_i = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + GlobalConstant.directoryMediaImages
            )
            if (!file_i.exists()) {
                file_i.mkdir()
            }
            val file_a = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + GlobalConstant.directoryMediaAudio
            )
            if (!file_a.exists()) {
                file_a.mkdir()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

        var mypath: String = ""
        println("myfilepath ext = $ext")

        when (ext) {
            "png", "jpg", "gif", "jpeg" -> {
                mypath =
                    Environment.getExternalStorageDirectory().absolutePath + File.separator + Environment.DIRECTORY_DOWNLOADS + GlobalConstant.directoryMediaImages
            }

            "mp4", "webm" -> {
                mypath =
                    Environment.getExternalStorageDirectory().absolutePath + File.separator + Environment.DIRECTORY_DOWNLOADS + GlobalConstant.directoryMediaVideos

            }

            "mp3", "m4a", "wav" -> {
                mypath =
                    Environment.getExternalStorageDirectory().absolutePath + File.separator + Environment.DIRECTORY_DOWNLOADS + GlobalConstant.directoryMediaAudio
            }
        }


        val tmpFile = withContext(Dispatchers.IO) {
            File.createTempFile("allvideodd", null, applicationContext.externalCacheDir)
        }
        tmpFile.delete()
        tmpFile.mkdir()
        tmpFile.deleteOnExit()
        val destFileDir = File(mypath)
        println("myfilepath folder = " + tmpFile.absolutePath)
        request.addOption("-o", "${tmpFile.absolutePath}/${name}.%(ext)s")
        val videoOnly = vcodec != "none" && acodec == "none"
        if (videoOnly) {
            request.addOption("-f", "${formatId}+bestaudio")
        } else {
            request.addOption("-f", formatId)
        }


        try {
            YoutubeDL.getInstance()
                .execute(request, taskId) { progress, _, line ->
                    showProgress(id.hashCode(), taskId, name, progress.toInt(), line, tmpFile)
                }

            tmpFile.listFiles()!!.forEach {
                FileUtils.moveFile(
                    it.absoluteFile,
                    File(
                        destFileDir.absolutePath + File.separator + FilenameUtils.removeExtension(it.name) + System.currentTimeMillis() + GlobalConstant.MY_ANDROID_IDENTIFIER_OF_FILE_DL + it.extension
                    )
                )
            }

            Utils.sendOreoNotification(
                applicationContext,
                applicationContext.resources.getString(R.string.app_name),
                applicationContext.resources.getString(R.string.yourdoncomple)
            )
        } catch (e: Throwable) {
            Log.e("allvideodd error ", e.message.toString())
            e.printStackTrace()
        } finally {
            tmpFile.deleteRecursively()
        }


        return Result.success()
    }

    private fun showProgress(
        id: Int,
        taskId: String,
        name: String,
        progress: Int,
        line: String,
        tmpFile: File
    ) {
        val text = line.replace(tmpFile.toString(), "")
        val intent = Intent(applicationContext, CancelReceiver::class.java)
            .putExtra("taskId", taskId)
            .putExtra("notificationId", id)

        val pendingIntent =
            PendingIntent.getBroadcast(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        val notification = NotificationCompat.Builder(
            applicationContext,
            GlobalConstant.myNotificationChannel
        )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSmallIcon(R.drawable.ic_download_yellow)
            .setContentTitle(name)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(text)
            )
            .setSound(null)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setProgress(100, progress, progress == -1)
            .addAction(
                R.drawable.ic_close_24dp,
                applicationContext.getString(R.string.cancel),
                pendingIntent
            )
            .setOngoing(true)
            .build()
        notificationManager?.notify(id, notification)
    }


    private fun createNotificationChannel() {
        var notificationChannel =
            notificationManager?.getNotificationChannel(GlobalConstant.myNotificationChannel)
        if (notificationChannel == null) {
            val channelName = applicationContext.getString(R.string.app_name)
            notificationChannel = NotificationChannel(
                GlobalConstant.myNotificationChannel,
                channelName, NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description =
                channelName
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        const val urlKey = "url"
        const val nameKey = "name"
        const val formatIdKey = "formatId"
        const val acodecKey = "acodec"
        const val vcodecKey = "vcodec"
        const val taskIdKey = "taskId"
        const val ext = "ext"
    }
}
