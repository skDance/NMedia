package ru.netology

import android.app.Application
import ru.netology.auth.AppAuth

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppAuth.init(this)
    }
}