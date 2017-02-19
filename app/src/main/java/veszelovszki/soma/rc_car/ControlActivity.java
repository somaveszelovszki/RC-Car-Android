package veszelovszki.soma.rc_car;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
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
import android.util.Log;

import java.util.Set;

import veszelovszki.soma.rc_car.fragment.SteeringWheelControlFragment;
import veszelovszki.soma.rc_car.fragment.DeviceListFragment;
import veszelovszki.soma.rc_car.utils.BluetoothCommunicator;
import veszelovszki.soma.rc_car.common.Command;
import veszelovszki.soma.rc_car.utils.PrefManager;
import veszelovszki.soma.rc_car.utils.PreferenceAdaptActivity;
import veszelovszki.soma.rc_car.utils.ResponseListener;
import veszelovszki.soma.rc_car.utils.Utils;
import veszelovszki.soma.rc_car.view.SteeringWheelView;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2016. 11. 13.
 */
public class ControlActivity extends PreferenceAdaptActivity
        implements SteeringWheelControlFragment.ControlFragmentListener, DeviceListFragment.DeviceListFragmentListener {

    /**
     * Determines time period of sending data (speed, rotation) to Arduino.
     */
    private static final Integer DATA_SEND_PERIOD_MILLISECONDS = 50;

    BluetoothCommunicator mBluetoothCommunicator;
    SteeringWheelControlFragment mControlFragment;
    DeviceListFragment mDeviceListFragment;

    private static final int PERMISSION_REQUEST_BLUETOOTH = 1;

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

            // sends commands
            mBluetoothCommunicator.send(Command.CODE.Speed, speed);
            mBluetoothCommunicator.send(Command.CODE.SteeringAngle, steeringAngle);

            sendHandler.postDelayed(sendData, DATA_SEND_PERIOD_MILLISECONDS);
        }
    };

    /**
     * The Handler that gets information back from the BluetoothChatService
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

        this.isDrawerEnabled = true;

        setContentView(R.layout.activity_control);

        if (savedInstanceState == null) {
            mControlFragment = SteeringWheelControlFragment.newInstance();
            mDeviceListFragment = DeviceListFragment.newInstance();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mDeviceListFragment, DeviceListFragment.TAG).commit();

            try {
                mBluetoothCommunicator = new BluetoothCommunicator(this, mHandler);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        Log.d(TAG, savedInstanceState == null ? "savedInstance is null" : "savedInstance is not null");

        //mBluetoothCommunicator.connectToHC_06();
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
    public void deviceSelected(BluetoothDevice device) {
        mBluetoothCommunicator.connectToDevice(device, new ResponseListener<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                if (response.equals(true)) {
                    // opens control fragment - adds this fragment to the back stack, so that user can navigate back
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, mControlFragment, SteeringWheelControlFragment.TAG);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    initializeCommandSending();
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Snackbar.make(getContentView(), R.string.error, Snackbar.LENGTH_LONG);
            }
        });
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
}
