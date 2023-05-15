package ru.netology.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.R
import ru.netology.auth.AppAuth
import ru.netology.dto.PushContent
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val pushTokenChannelId = "pushToken"
    private val gson = Gson()

    @Inject
    lateinit var appAuth: AppAuth


    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val pushChannel = NotificationChannel(pushTokenChannelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            manager.createNotificationChannel(pushChannel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val pushContent = gson.fromJson(message.data["content"], PushContent::class.java)
        val authId = appAuth.authStateFlow.value.id.toString()
        with(pushContent) {
            when (recipientId) {
                authId, null -> handlePush(pushContent)
                else -> appAuth.sendPushToken()
            }
        }
//        message.data[action]?.let {
//
//            when (it) {
//                Action.LIKE.name -> handleLike(
//                    gson.fromJson(
//                        message.data[content],
//                        Like::class.java
//                    )
//                )
//                Action.NEW_POST.name -> handleNewPost(
//                    gson.fromJson(
//                        message.data[content],
//                        NewPost::class.java
//                    )
//                )
//                else -> return
//            }
//
//        }
    }

    override fun onNewToken(token: String) {
        appAuth.sendPushToken(token)
    }

    private fun handlePush(pushContent: PushContent) {
        val notification = NotificationCompat.Builder(this, pushTokenChannelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(pushContent.content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

//    private fun handleLike(content: Like) {
//        val notification = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.ic_notification)
//            .setContentTitle(
//                getString(
//                    R.string.notification_user_liked,
//                    content.userName,
//                    content.postAuthor,
//                )
//            )
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .build()
//        NotificationManagerCompat.from(this)
//            .notify(Random.nextInt(100_000), notification)
//    }


//    private fun handleNewPost(content: NewPost) {
//        val notification = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.ic_notification)
//            .setContentTitle(
//                getString(
//                    R.string.notification_new_post,
//                    content.userName
//                )
//            )
//            .setStyle(
//                NotificationCompat.BigTextStyle()
//                    .bigText(content.postText)
//            )
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .build()
//        NotificationManagerCompat.from(this)
//            .notify(Random.nextInt(100_000), notification)
//    }
}

enum class Action {
    LIKE,
    NEW_POST,
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)

data class NewPost(
    val userName: String,
    val postText: String,
)