package veszelovszki.soma.rc_car;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Set;

import veszelovszki.soma.rc_car.communication.Message;
import veszelovszki.soma.rc_car.communication.Communicator;
import veszelovszki.soma.rc_car.fragment.ControlFragment;
import veszelovszki.soma.rc_car.fragment.DeviceListFragment;
import veszelovszki.soma.rc_car.fragment.SteeringWheelControlFragment;
import veszelovszki.soma.rc_car.communication.BluetoothCommunicator;
import veszelovszki.soma.rc_car.utils.ByteArray;
import veszelovszki.soma.rc_car.utils.Config;
import veszelovszki.soma.rc_car.utils.PrefManager;
import veszelovszki.soma.rc_car.utils.PreferenceAdaptActivity;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2016. 11. 13.
 */
public class ControlActivity extends PreferenceAdaptActivity
        implements ControlFragment.EventListener,
        Communicator.EventListener,
        DeviceListFragment.DeviceListFragmentListener {

    public static final String TAG = ControlActivity.class.getCanonicalName();

    /**
     * Time period of sending drive data (speed, rotation) to the micro-controller.
     */
    private static final Integer DRIVE_DATA_SEND_PERIOD = 100;

    private Communicator mCommunicator;

    private SteeringWheelControlFragment mControlFragment;
    private DeviceListFragment mDeviceListFragment;

    private BluetoothDevice mDevice;

    private PrefManager mPrefManager;

    private static final int PERMISSION_REQUEST_BLUETOOTH = 1;

    private Point gridPoint = new Point(0, 0);

    /**
     * Initializes message send handler. Sends drive messages periodically with the Communicator.
     */
    final Handler mSendHandler = new Handler();

    final Runnable mSendMessage = new Runnable() {
        public void run() {

            float speed = mControlFragment.getSpeed();
            float steeringAngle = mControlFragment.getSteeringAngle();

            // sends messages
            mCommunicator.send(new Message(Message.CODE.Speed, speed));
            mCommunicator.send(new Message(Message.CODE.SteeringAngle, steeringAngle));

            mSendHandler.postDelayed(this, DRIVE_DATA_SEND_PERIOD);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setNavigationDrawerEnabled(true);

        setContentView(R.layout.activity_control);

        mPrefManager = new PrefManager(this);

        if (savedInstanceState != null) {
            mControlFragment = (SteeringWheelControlFragment) getSupportFragmentManager().getFragment(savedInstanceState, ControlFragment.TAG);
            mDeviceListFragment = (DeviceListFragment) getSupportFragmentManager().getFragment(savedInstanceState, DeviceListFragment.TAG);
        } else {
            mControlFragment = SteeringWheelControlFragment.newInstance();
            mDeviceListFragment = DeviceListFragment.newInstance();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //WiFiCommunicator.initialize();
        //mCommunicator = WiFiCommunicator.getInstance(this);
        //mCommunicator.connect();
        mCommunicator = BluetoothCommunicator.getInstance(this);

        if (mCommunicator.isConnected()) {
            openControlFragment();
        } else {
            String carMacAddress = (String) mPrefManager.readPref(PrefManager.PREFERENCE.CAR_MAC_ADDRESS);

            // If car address is known, connects to it and opens control fragment.
            // If it is not known yet, opens list of paired devices.
            if (/*mCommunicator.isConnected() && */!carMacAddress.equals("")) {
                mDevice = ((BluetoothCommunicator)mCommunicator).getDevice(carMacAddress);
                mCommunicator.connect(mDevice);
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, mDeviceListFragment, DeviceListFragment.TAG)
                        .commit();
            }
        }
    }

    @Override
    protected void onPause() {
        cancelMessageSending();
        super.onPause();
    }

    @Override
    public void searchDevices() {
        Set<BluetoothDevice> pairedDevices = ((BluetoothCommunicator)mCommunicator).getPairedDevices();
        mDeviceListFragment.setList(pairedDevices);
    }

    @Override
    public void onDeviceSelected(BluetoothDevice device) {
        mDevice = device;
        mCommunicator.connect(device);
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Snackbar.make(getContentView(), R.string.bluetooth_connection_setup_error, Snackbar.LENGTH_LONG).show();
//
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, mDeviceListFragment, DeviceListFragment.TAG)
//                        .commit();
//            }
//        });
    }

    private void startMessageSending() {
        mCommunicator.sendAndWaitACK(new Message(Message.CODE.DriveMode, Integer.valueOf((String) mPrefManager.readPref(PrefManager.PREFERENCE.DRIVE_MODE))));
        mSendHandler.post(mSendMessage);
    }

    private void cancelMessageSending() {
        mSendHandler.removeCallbacks(mSendMessage);
        //mCommunicator.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();

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
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else
                    finish();
                return;

            default:
                // other 'case' lines to check for other
                // permissions this app might request
        }
    }

    @Override
    public PrefManager.PREFERENCE getFirstStartPreference() {
        return PrefManager.PREFERENCE.FIRST_START_CONTROL;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case BluetoothCommunicator.REQUEST_ENABLE_BLUETOOTH:
                switch (resultCode){
                    case RESULT_OK:
                        // Request granted - bluetooth is turning on...
                    case RESULT_CANCELED:
                        // Request denied by user, or an error was encountered while
                        // attempting to enable bluetooth
                }
        }
    }

    private void openControlFragment() {
        // opens control fragment - adds this fragment to the back stack, so that user can navigate back
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mControlFragment, ControlFragment.TAG)
                .commit();

        startMessageSending();
    }

    @Override
    public void onCommunicatorConnected() {
        // saves address in shared preferences
        mPrefManager.writePref(PrefManager.PREFERENCE.CAR_MAC_ADDRESS, mDevice.getAddress());
        openControlFragment();
    }

    @Override
    public void onCommunicationError(Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onNewMessage(Message message) {
        switch (message.getCode()) {
            case ACK_:
                Log.d(TAG, "ACK received");
                break;
            case Speed:
                break;
            case SteeringAngle:
                break;
            case DriveMode:
                break;
            case Car:
                handleMsg_Car(message);
                break;
            case RelEnvPoint:
                //handleMsg_EnvironmentPoint(message);
                break;
            case RelEnvEn:
                break;
            case EnvGridEn:
                break;
            case EnvGrid:
                handleMsg_EnvironmentGridPoint(message);
                break;
        }
    }

//    private void handleMsg_EnvironmentPoint(Message message) {
//        // 1 message stores 2 points (measured by 2 ultrasonic sensors)
//        final int pos1 = 2 * ((int)message.getCode().getCodeValue() - (int)Message.CODE.RelEnvPoint.getCodeValue()),
//                pos2 = pos1 + 1;
//
//        final Pointf p1 = Pointf.fromByteArray(message.getData().subArray(0, 2)),
//                p2 = Pointf.fromByteArray(message.getData().subArray(2, 2));
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mControlFragment.updateCarEnvironmentPoint(pos1, p1);
//                mControlFragment.updateCarEnvironmentPoint(pos2, p2);
//            }
//        });
//    }

    private void handleMsg_Car(Message message) {
        ByteArray data = message.getData();
        final Point carGridPoint = new Point(((int)data.get(0) + 256) % 256, (data.get(1) + 256) % 256);
        final float angleDeg = 360.0f * ((data.get(2) & 0x000000ff) | ((data.get(3) << 8) & 0x0000ff00)) / 65535;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mControlFragment.updateEnvironment_Car(carGridPoint, angleDeg);
            }
        });
    }

    private void handleMsg_EnvironmentGridPoint(Message message) {
        // message stores X and Y coordinates of the
        final int codeValue = message.getCodeByte() - Message.CODE.EnvGrid.getCodeValue();
        final int data = message.getData().asInteger();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int y = codeValue & 0b00111111;

                if (y != gridPoint.y) {    // resynchronization (should not happen under normal circumstances)
                    gridPoint.y = y;
                    gridPoint.x = 0;
                }

                int mask = 0;

                // creates mask (e.g. 00000000000000000000000000000011 if point bit depth is 2)
                for (int i = 0; i < Config.ENV_ABS_POINTS_BIT_DEPTH; ++i)
                    mask |= (1 << i);

                for (int i = 0; i < 32 / Config.ENV_ABS_POINTS_BIT_DEPTH; ++i) {
                    int currentMask = mask << (i * Config.ENV_ABS_POINTS_BIT_DEPTH);
                    int point = ((data & currentMask) >> (i * Config.ENV_ABS_POINTS_BIT_DEPTH)) & mask;

                    mControlFragment.updateEnvironment_Point(gridPoint, point);

                    gridPoint.x = (gridPoint.x + 1) % Config.ENV_ABS_AXIS_POINTS_NUM;
                    if (gridPoint.x == 0) {
                        y = gridPoint.y = (y + 1) % Config.ENV_ABS_AXIS_POINTS_NUM;
                    }
                }
            }
        });
    }

    @Override
    public void onCommunicatorDisconnected() {
        cancelMessageSending();
    }

    @Override
    protected void onDestroy() {
        cancelMessageSending();
        //mCommunicator.cancel();
        super.onDestroy();
    }

    @Override
    public void onCarEnvironmentEnabled() {
        if (mCommunicator.isConnected())
            //mCommunicator.sendAndWaitACK(new Message(Message.CODE.RelEnvEn, true));
            mCommunicator.sendAndWaitACK(new Message(Message.CODE.EnvGridEn, true));
        else
            onError(new Exception("Communicator is not connected!"));
    }

    @Override
    public void onCarEnvironmentDisabled() {
        if (mCommunicator.isConnected())
            //mCommunicator.sendAndWaitACK(new Message(Message.CODE.RelEnvEn, false));
            mCommunicator.sendAndWaitACK(new Message(Message.CODE.EnvGridEn, false));
        else
            onError(new Exception("Communicator is not connected!"));
    }
}
