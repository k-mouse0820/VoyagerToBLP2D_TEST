package jp.daisen_solution.voyagertoblp2d_test

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import app.akexorcist.bluetotohspp.library.BluetoothSPP
import app.akexorcist.bluetotohspp.library.BluetoothState
import app.akexorcist.bluetotohspp.library.DeviceList
import jp.daisen_solution.voyagertoblp2d_test.adapter.ScanAdapter
import jp.daisen_solution.voyagertoblp2d_test.databinding.ActivityMainBinding
import jp.daisen_solution.voyagertoblp2d_test.model.Code
import jp.daisen_solution.voyagertoblp2d_test.databinding.ActivityMainBinding.*
import java.time.Instant
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity(),
    BluetoothSPP.OnDataReceivedListener,
    BluetoothSPP.BluetoothConnectionListener,
    BluetoothSPP.BluetoothStateListener {

    private lateinit var binding:ActivityMainBinding
    lateinit var scanAdapter: ScanAdapter
    var bt = BluetoothSPP(this)
    var scannedCodes: MutableList<Code> = mutableListOf()
    var devices: MutableList<BluetoothDevice> = mutableListOf()
    var isBtConnected = false

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let {
                val mac = it.getStringExtra("device_address")
                mac?.let { bt.connect(it) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bt.setupService()
        bt.startService(BluetoothState.DEVICE_OTHER)

        if(!bt.isBluetoothAvailable) {
            Toast.makeText(this,getString(R.string.msg_bt_not_available), Toast.LENGTH_LONG).show()
            finish()
        }

        scanAdapter = ScanAdapter(this)

        binding.rvScan.layoutManager = LinearLayoutManager(this)
        binding.rvScan.adapter = scanAdapter

        binding.fabPrint.setOnClickListener { fabClicked() }

        bt.setOnDataReceivedListener(this)
        bt.setBluetoothConnectionListener(this)
        bt.setBluetoothStateListener(this)

    }


    private fun fabClicked() {
        // 印刷処理に置き換える予定
        if(isBtConnected){
            bt.disconnect()
        } else {
            val intent = Intent(this, DeviceList::class.java)

            resultLauncher.launch(intent)
        }
    }


    override fun onStart() {
        super.onStart()
        if(!bt.isBluetoothEnabled) bt.enable()
    }

    override fun onStop() {
        super.onStop()
        bt.stopService()
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDataReceived(data: ByteArray, message: String) {
        scannedCodes.add(Code(message, DateTimeFormatter.ISO_INSTANT.format(Instant.now())))
        scanAdapter.setData(scannedCodes)
        binding.rvScan.scrollToPosition(scannedCodes.size - 1)
    }

    override fun onDeviceConnected(name: String?, address: String?) {
        Toast.makeText(this, getText(R.string.msg_scanner_connected), Toast.LENGTH_SHORT).show()
        binding.fabPrint.setImageResource(R.drawable.baseline_print_24)
        binding.progressMessage.text = getText(R.string.msg_connecting)
        binding.progress.visibility = View.VISIBLE
        binding.progressAction.visibility = View.GONE
    }

    override fun onDeviceDisconnected() {
        Toast.makeText(this, getText(R.string.msg_scanner_disconnected), Toast.LENGTH_SHORT).show()
        binding.fabPrint.setImageResource(R.drawable.baseline_stop_24)
        binding.progress.visibility = View.GONE
        binding.progressAction.visibility = View.GONE
    }

    override fun onDeviceConnectionFailed() {
        Toast.makeText(this, getText(R.string.msg_scanner_connection_failed), Toast.LENGTH_SHORT).show()
        binding.fabPrint.setImageResource(R.drawable.baseline_stop_24)
        binding.progress.visibility = View.GONE
        binding.progressAction.visibility = View.GONE
    }

    override fun onServiceStateChanged(state: Int) {
        if(state == BluetoothState.STATE_CONNECTING) {
            binding.progressMessage.text = getText(R.string.msg_connecting)
            binding.progressAction.visibility = View.VISIBLE
        }
    }
}