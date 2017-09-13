package veszelovszki.soma.rc_car;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import veszelovszki.soma.rc_car.communication.Communicator;
import veszelovszki.soma.rc_car.communication.WiFiCommunicator;
import veszelovszki.soma.rc_car.fragment.ControlFragment;
import veszelovszki.soma.rc_car.fragment.SteeringWheelControlFragment;
import veszelovszki.soma.rc_car.fragment.DeviceListFragment;
import veszelovszki.soma.rc_car.communication.BluetoothCommunicator;
import veszelovszki.soma.rc_car.common.Message;
import veszelovszki.soma.rc_car.utils.PrefManager;
import veszelovszki.soma.rc_car.utils.PreferenceAdaptActivity;
import veszelovszki.soma.rc_car.utils.Utils.*;
import veszelovszki.soma.rc_car.utils.Utils;
import veszelovszki.soma.rc_car.view.AccelerationSeekBar;
import veszelovszki.soma.rc_car.view.SteeringWheelView;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2016. 11. 13.
 */
public class ControlActivity extends PreferenceAdaptActivity
        implements SteeringWheelControlFragment.ControlFragmentListener,
        Communicator.Listener {

    public static final String TAG = ControlActivity.class.getCanonicalName();

    /**
     * Time period of sending drive data (speed, rotation) to the micro-controller.
     */
    private static final Integer DRIVE_DATA_SEND_PERIOD_MS = 200;

    private Communicator mCommunicator;
    private SteeringWheelControlFragment mControlFragment;

    private PrefManager mPrefManager;

    private static final int PERMISSION_REQUEST_BLUETOOTH = 1;

    // TODO alter for WiFi state listener
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

                cancelMessageSending();

            }
        }

        @Override
        public void onError(Exception e) {
            e.printStackTrace();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(getContentView(), R.string.bluetooth_connection_setup_error, Snackbar.LENGTH_LONG).show();
                }
            });
        }
    };

    /**
     * Initializes message send handler. Sends drive messages periodically with the Communicator.
     */
    final Handler mSendHandler = new Handler();
    final Runnable mSendMessage = new Runnable() {
        public void run() {

            // converts AccelerationSeekBar's value to communicator speed -> [cm/sec]
            Integer speed = Utils.map(mControlFragment.getSpeed(),
                    AccelerationSeekBar.MIN_POS,
                    AccelerationSeekBar.MAX_POS,
                    Message.CODE.Speed.getMinValue(), Message.CODE.Speed.getMaxValue());

            // maps SteeringWheel's value to communicator steering angle
            Integer steeringAngle = Utils.map(mControlFragment.getSteeringAngle(),
                    (-1) * SteeringWheelView.STEERING_WHEEL_MAX_ROTATION.intValue(),
                    SteeringWheelView.STEERING_WHEEL_MAX_ROTATION.intValue(),
                    Message.CODE.SteeringAngle.getMinValue(), Message.CODE.SteeringAngle.getMaxValue());

            Log.d(TAG, "speed: " + speed);
            Log.d(TAG, "angle: " + steeringAngle);

            // sends messages
            mCommunicator.send(Message.CODE.Speed, speed);
            mCommunicator.send(Message.CODE.SteeringAngle, steeringAngle);

            mSendHandler.postDelayed(mSendMessage, DRIVE_DATA_SEND_PERIOD_MS);
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

            try {
                WiFiCommunicator.initialize();
                mCommunicator = WiFiCommunicator.getInstance(this);
                mCommunicator.connect();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mControlFragment, ControlFragment.TAG)
                    .commit();
        }
    }

    private void startMessageSending() {
        mCommunicator.send(Message.CODE.DriveMode, mPrefManager.readPref(PrefManager.PREFERENCE.DRIVE_MODE));

        mSendHandler.post(mSendMessage);
    }

    private void cancelMessageSending() {
        mSendHandler.removeCallbacks(mSendMessage);
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

        cancelMessageSending();

        if (mCommunicator != null) {
            mCommunicator.cancel();
        }

        super.onDestroy();
    }

    @Override
    public void onCommunicatorConnected() {
        startMessageSending();
    }

    @Override
    public void onCommunicationError(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onNewMessage(String message) {
        Log.d(TAG, "received: " + message);
    }
}
