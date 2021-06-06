package moc.nahba.dynamic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

const val ON_DEMAND_MODULE = "demand"
const val INSTALL_MODULE = "install"
const val SPECIFIED_FEATURE_MODULE = "specified"

class BaseModuleActivity : AppCompatActivity() {
    private val TAG = "NAHBA"
    private var sessionId = 0
    private val splitInstallManager = SplitInstallManagerFactory.create(application)
    private val listener = SplitInstallStateUpdatedListener { state ->
        if (state.sessionId() == sessionId) {
            when (state.status()) {
                SplitInstallSessionStatus.FAILED -> {
                    Log.w(TAG, "InstallationFailed ${state.moduleNames()}, $sessionId")
                }
                SplitInstallSessionStatus.INSTALLED -> {
                    Log.d(TAG, "Installed ${state.moduleNames()}, $sessionId")
                }
                else -> {
                    Log.d(TAG, "Status: ${state.status()}")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base_module)
        splitInstallManager.registerListener(listener)
    }

    private fun startOnDemand() {}

    private fun startInstall() {}

    private fun startSpecified() {}

    private fun hasFeatureModuleInstalled(module: String) : Boolean
        = splitInstallManager.installedModules.contains(module)

    private fun installModule(module: String) {
        val request =
                SplitInstallRequest
                        .newBuilder()
                        .addModule(module)
                        .build()
        sessionId = 0
        splitInstallManager
                .startInstall(request)
                .addOnSuccessListener { id -> sessionId = id }
                .addOnFailureListener { exception ->
                    Log.w(TAG, exception.toString())
                }
    }

    override fun onDestroy() {
        splitInstallManager.unregisterListener(listener)
        super.onDestroy()
    }
}
