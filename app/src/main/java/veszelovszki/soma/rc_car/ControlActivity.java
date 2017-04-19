package veszelovszki.soma.rc_car;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;

import java.util.Set;

import veszelovszki.soma.rc_car.fragment.SteeringWheelControlFragment;
import veszelovszki.soma.rc_car.fragment.DeviceListFragment;
import veszelovszki.soma.rc_car.communication.BluetoothCommunicator;
import veszelovszki.soma.rc_car.common.Command;
import veszelovszki.soma.rc_car.utils.PrefManager;
import veszelovszki.soma.rc_car.utils.PreferenceAdaptActivity;
import veszelovszki.soma.rc_car.utils.Utils.*;
import veszelovszki.soma.rc_car.utils.Utils;
import veszelovszki.soma.rc_car.view.SteeringWheelView;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2016. 11. 13.
 */
public class ControlActivity extends PreferenceAdaptActivity
        implements SteeringWheelControlFragment.ControlFragmentListener, DeviceListFragment.DeviceListFragmentListener,
        BluetoothCommunicator.EventListener {

    public static final String TAG = ControlActivity.class.getCanonicalName();

    /**
     * Determines time period of sending data (speed, rotation) to Arduino.
     */
    private static final Integer DATA_SEND_PERIOD_MILLISECONDS = 100;

    private BluetoothCommunicator mBluetoothCommunicator;
    private SteeringWheelControlFragment mControlFragment;
    private DeviceListFragment mDeviceListFragment;

    private PrefManager mPrefManager;

    private static final int PERMISSION_REQUEST_BLUETOOTH = 1;

    private final AdvancedBroadcastReceiver mConnectionStateReceiver = new AdvancedBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Device found - not handled
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Device is now connected
                Log.d(TAG, "ACTION_ACL_CONNECTED");
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching - not handled

            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect - not handled
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                Log.d(TAG, "ACTION_ACL_DISCONNECTED");

                cancelCommandSending();

            }
        }

        @Override
        public void onError(Exception e) {
            e.printStackTrace();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(getContentView(), R.string.bluetooth_connection_setup_error, Snackbar.LENGTH_LONG).show();

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, mDeviceListFragment, DeviceListFragment.TAG)
                            .commit();
                }
            });
        }
    };

    /**
     * Initializes data send handler. Sends data periodically through the BluetoothCommunicator.
     */
    final Handler sendHandler = new Handler();
    final Runnable sendData = new Runnable() {
        public void run() {

            // maps AccelerationSeekBar's value to communicator speed value
            Integer speed = Utils.map(mControlFragment.getSpeed(), 0, 100,
                    Command.CODE.Speed.getCommMinValue(), Command.CODE.Speed.getCommMaxValue());

            // maps SteeringWheel's value to communicator steering angle
            Integer steeringAngle = Utils.map(mControlFragment.getSteeringAngle(),
                    (-1) * SteeringWheelView.STEERING_WHEEL_MAX_ROTATION.intValue(),
                    SteeringWheelView.STEERING_WHEEL_MAX_ROTATION.intValue(),
                    Command.CODE.SteeringAngle.getCommMinValue(), Command.CODE.SteeringAngle.getCommMaxValue());

            Log.d(TAG, "speed: " + speed);
            Log.d(TAG, "angle: " + steeringAngle);

            // sends commands
            mBluetoothCommunicator.send(Command.CODE.Speed, speed);
            mBluetoothCommunicator.send(Command.CODE.SteeringAngle, steeringAngle);

            sendHandler.postDelayed(sendData, DATA_SEND_PERIOD_MILLISECONDS);
        }
    };

    /**
     * The Handler that gets information back from the BluetoothCommunicator
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            BluetoothCommunicator.Constant constant = BluetoothCommunicator.Constant.constantFromInteger(msg.what);
            if (constant == null) {
                return;
            }

            switch (constant) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    //Log.d(TAG, "Me: " + writeMessage);

                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    //Log.d(TAG, "\tPartner: " + readMessage);

                    break;
                /*case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;*/
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setNavigationDrawerEnabled(true);

        setContentView(R.layout.activity_control);

        mPrefManager = new PrefManager(this);

        if (savedInstanceState == null) {
            mControlFragment = SteeringWheelControlFragment.newInstance();
            mDeviceListFragment = DeviceListFragment.newInstance();

            try {
                mBluetoothCommunicator = new BluetoothCommunicator(this, mConnectionStateReceiver, mHandler);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            String carMacAddress = (String) mPrefManager.readPref(PrefManager.PREFERENCE.CAR_MAC_ADDRESS);

            /*
            If car address is not known yet, opens list of paired devices.
            If it is known, connects to it and opens control fragment.
             */
            if (carMacAddress.equals("")) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mDeviceListFragment, DeviceListFragment.TAG)
                        .commit();
            } else {
                connectToDevice(mBluetoothCommunicator.getDevice(carMacAddress));
            }
        }

        //mBluetoothCommunicator.connectToHC_06();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBluetoothCommunicator.send(Command.CODE.DriveMode, mPrefManager.readPref(PrefManager.PREFERENCE.DRIVE_MODE));
    }

    private void initializeCommandSending() {
        sendHandler.post(sendData);
    }

    private void cancelCommandSending() {
        sendHandler.removeCallbacks(sendData);
    }

    @Override
    public void searchDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothCommunicator.getPairedDevices();

        mDeviceListFragment.setList(pairedDevices);
    }

    @Override
    public void onDeviceSelected(BluetoothDevice device) {
        connectToDevice(device);
    }

    private void connectToDevice(final BluetoothDevice device) {
        mBluetoothCommunicator.connectToDevice(device);
    }


    /**
     * Checks for permissions - called during onCreate().
     */
    protected void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.BLUETOOTH)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH},
                        PERMISSION_REQUEST_BLUETOOTH);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_BLUETOOTH:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    finish();
                }
                return;

            default:
                // other 'case' lines to check for other
                // permissions this app might request
        }
    }

    /**
     * Method is called when activity runs for the first time - must be overwritten by every descendant.
     */
    public void onFirstRun() {
        // TODO
    }

    public PrefManager.PREFERENCE getFirstRunPreference() {
        return PrefManager.PREFERENCE.FIRST_START_CONTROL;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothCommunicator.REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                // Request granted - bluetooth is turning on...
            }
            if (resultCode == RESULT_CANCELED) {
                // Request denied by user, or an error was encountered while
                // attempting to enable bluetooth
            }
        }
    }

    @Override
    protected void onDestroy() {

        cancelCommandSending();

        if (mBluetoothCommunicator != null) {
            mBluetoothCommunicator.destroy();
        }

        super.onDestroy();
    }

    @Override
    public void onBluetoothConnected(BluetoothDevice device) {
        // saves address in shared preferences
        mPrefManager.writePref(PrefManager.PREFERENCE.CAR_MAC_ADDRESS, device.getAddress());

        openControlFragment();
    }

    private void openControlFragment() {

        // opens control fragment - adds this fragment to the back stack, so that user can navigate back
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mControlFragment, SteeringWheelControlFragment.TAG)
                .commit();

        mBluetoothCommunicator.send(Command.CODE.DriveMode, mPrefManager.readPref(PrefManager.PREFERENCE.DRIVE_MODE));

        initializeCommandSending();
    }
}
