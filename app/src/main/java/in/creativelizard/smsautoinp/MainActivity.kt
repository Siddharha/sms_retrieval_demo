package `in`.creativelizard.smsautoinp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsApi
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, OTPReceiveListener {
     val mCredentialsApiClient:GoogleApiClient by lazy { GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .enableAutoManage(this, this)
        .addApi(Auth.CREDENTIALS_API)
        .build() }
    lateinit var smsBroadcastReceiver:SMSBroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialize()
        requestHint()
        startSmsListener()
    }
    fun initialize(){

        smsBroadcastReceiver = SMSBroadcastReceiver()
        val filter =  IntentFilter("com.google.android.gms.auth.api.phone.SMS_RETRIEVED")
        registerReceiver(smsBroadcastReceiver,filter)
    }


    private fun startSmsListener() {
        val client = SmsRetriever.getClient(this /* context */)
        val task = client.startSmsRetriever()
        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener {
            // Successfully started retriever, expect broadcast intent
            // ...
            smsBroadcastReceiver.smsInti(this)
            otp_txt.text = "Waiting for the OTP"
        }

        task.addOnFailureListener {
            // Failed to start retriever, inspect Exception for more details
            // ...
            otp_txt.text = "Cannot Start SMS Retriever"
        }
    }

    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


    private val RC_HINT = 2
    @SuppressLint("LongLogTag")
    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val intent = Auth.CredentialsApi.getHintPickerIntent(
            mCredentialsApiClient, hintRequest)

        //val intent = Credentials.getClient(activity.baseContext).getHintPickerIntent(hintRequest)

        try {
            startIntentSenderForResult(
                intent.intentSender,
                RC_HINT, null, 0, 0, 0
            )
        } catch (e: Exception) {
            e.message?.let { Log.e("Error In getting Message", it) }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_HINT && resultCode == Activity.RESULT_OK) {

            /*You will receive user selected phone number here if selected and send it to the server for request the otp*/
            var credential: Credential = data?.getParcelableExtra(Credential.EXTRA_KEY)!!



        }
    }

    override fun onOTPReceived(otp: String) {
        etOtp.setText(otp)
    }

    override fun onOTPTimeOut() {

    }


}





