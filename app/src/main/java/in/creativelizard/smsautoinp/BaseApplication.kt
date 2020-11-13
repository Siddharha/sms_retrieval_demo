package `in`.creativelizard.smsautoinp

import android.app.Application

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        var appSignature = AppSignatureHelper(this)
        appSignature.appSignatures
    }
}