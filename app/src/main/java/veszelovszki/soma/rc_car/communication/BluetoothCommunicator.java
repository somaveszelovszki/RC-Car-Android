package veszelovszki.soma.rc_car.communication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import veszelovszki.soma.rc_car.common.Command;
import veszelovszki.soma.rc_car.utils.ResponseListener;

/**
 * Handles Bluetooth communications. Sends and receives data.
 *
 * This is a Singleton class.
 *
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 01. 11.
 */
public class BluetoothCommunicator {

    private static BluetoothCommunicator instance;

    public static void init(Context context, Handler handler) {
        instance = new BluetoothCommunicator(context, handler);
    }

    public static BluetoothCommunicator getInstance() {
        return instance;
    }

    public enum Constant {
        MESSAGE_READ(1),
        MESSAGE_WRITE(2),
        MESSAGE_ERROR(3);

        private Integer num;

        Constant(Integer num) {
            this.num = num;
        }

        public Integer value() {
            return num;
        }

        public static Constant constantFromInteger(Integer num) {
            for (Constant constant : Constant.values()) {
                if (constant.num.equals(num)) {
                    return constant;
                }
            }

            return null;
        }
    }

    private static final String TAG = BluetoothCommunicator.class.getCanonicalName();

    /**
     * Well-known SPP UUID.
     * @see <a href="https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html</a>
     */
    private final UUID BLUETOOTH_SERIAL_BOARD_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String BLUETOOTH_SERIAL_BOARD_ADDRESS;

    BluetoothAdapter mBluetoothAdapter;

    private ConnectTask mConnectTask;
    private ConnectedThread mConnectedThread;
    private Handler mHandler;
    private Context mContext;


    public static final int REQUEST_ENABLE_BLUETOOTH = 1;
    public static final int REQUEST_DISCOVERABLE = 2;

    private BroadcastReceiver ACTION_FOUND_Receiver;
    private BroadcastReceiver ACTION_BOND_STATE_CHANGED_Receiver;

    private BluetoothCommunicator(Context context, Handler handler) {
        mContext = context;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;

        // initializes and registers ACTION_FOUND receiver
        ACTION_FOUND_Receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address

                    Log.d(TAG, "Detected device: " + deviceName + " (" + deviceHardwareAddress + ")");

                    if ("HC-06".equals(deviceName)) {
                        //mBluetoothService.connectToDevice(device);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(ACTION_FOUND_Receiver, filter);

        // initializes and registers ACTION_BOND_STATE_CHANGED receiver
        ACTION_BOND_STATE_CHANGED_Receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                    if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                        Log.d(TAG, "Paired.");
                    } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                        Log.d(TAG, "Un-Paired.");
                    }

                }
            }
        };

        IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        mContext.registerReceiver(ACTION_BOND_STATE_CHANGED_Receiver, intent);
    }

    public BluetoothDevice getDevice(String address) {
        return mBluetoothAdapter.getRemoteDevice(address);
    }

    public Set<BluetoothDevice> getPairedDevices() {
        this.turnOn();
        return mBluetoothAdapter.getBondedDevices();
    }

    public void connectToDevice(BluetoothDevice device, ResponseListener<Boolean> responseListener) {

        turnOn();

        mConnectTask = new ConnectTask(device, responseListener);
        mConnectTask.execute();

        //mConnectThread = new ConnectThread(device);
        //mConnectThread.start();
    }


    public void connectToHC_06() {
        turnOn();
        //setDiscoverable();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Log.d(TAG, "Detected device: " + deviceName + " (" + deviceHardwareAddress + ")");

                if ("HC-06".equals(deviceName)) {
                    Log.d(TAG, "Connected to HC-06!!!");
                    this.connectToDevice(device, null);
                }
            }
        }
    }

    public Boolean send(Command.CODE code, Object value) {
        return this.send(new Command(code, value));
    }

    public Boolean send(Command command) {
        return this.send(command.toString());
    }

    private Boolean send(String message) {

        Log.d(TAG, "message: " + message);

        return this.send(message != null ? message.getBytes() : null);
    }

    private Boolean send(byte[] bytes) {
        if (mConnectedThread != null) {
            return mConnectedThread.write(bytes);
        }

        return false;
    }

    public void turnOn() {
        setBluetooth(true);
        Log.d(TAG, "Turned on bluetooth.");
    }

    public void turnOff() {
        setBluetooth(false);
        Log.d(TAG, "Turned off bluetooth.");
    }


    public void setDiscoverable() {
        Intent setDiscoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        ((Activity) mContext).startActivityForResult(setDiscoverableIntent, REQUEST_DISCOVERABLE);
    }

    private Boolean setBluetooth(Boolean enable) {

        boolean isEnabled = mBluetoothAdapter.isEnabled();

        if (enable && !isEnabled) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)mContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            return true;
        } else if (!enable && isEnabled) {
            return mBluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return isEnabled;
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Log.d("pairDevice()", "Start Pairing...");
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            Log.d("pairDevice()", "Pairing finished.");
        } catch (Exception e) {
            Log.e("pairDevice()", e.getMessage());
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Log.d("unpairDevice()", "Start Un-Pairing...");
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            Log.d("unpairDevice()", "Un-Pairing finished.");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void destroy() {
        mContext.unregisterReceiver(ACTION_FOUND_Receiver);
        mContext.unregisterReceiver(ACTION_BOND_STATE_CHANGED_Receiver);

        if (mConnectTask != null) {
            mConnectTask.cancel(true);
        }

        if (mConnectedThread != null) {
            mConnectedThread.interrupt();
        }
    }

    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {

        private final BluetoothSocket mmSocket;
        private ResponseListener<Boolean> mResponseListener;

        ConnectTask(BluetoothDevice device, ResponseListener<Boolean> responseListener) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;

            mResponseListener = responseListener;

            try {
                tmp = device.createRfcommSocketToServiceRecord(BLUETOOTH_SERIAL_BOARD_UUID);
                //final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                //tmp = (BluetoothSocket) m.invoke(device, BLUETOOTH_SERIAL_BOARD_UUID);
            } catch (Exception e) {
                mResponseListener.onError(e);
                mmSocket = null;
                return;
            }

            mmSocket = tmp;

            Log.d(TAG, "Created socket!");
        }

        @Override
        public Boolean doInBackground(Void... params) {

            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            if (mmSocket == null) {
                mResponseListener.onError(new Exception("Socket is null."));
                return false;
            }

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }

                if (mResponseListener != null) {
                    mResponseListener.onError(connectException);
                }

                return false;
            }

            Log.d(TAG, "Connected to the device through the socket!");

            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();

            return true;
        }

        @Override
        public void onPostExecute(Boolean result) {
            if (result.equals(true)) {
                if (mResponseListener != null) {
                    mResponseListener.onResponse(true);
                }
            }

            // does not call listener method if result is false,
            // because in this case the error listener has already been called
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception or an interrupt occurs.
            while (!this.isInterrupted()) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = mHandler.obtainMessage(
                            Constant.MESSAGE_READ.value(), numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        private Boolean write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        Constant.MESSAGE_WRITE.value(), -1, -1, bytes);
                writtenMsg.sendToTarget();

                return true;
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(Constant.MESSAGE_ERROR.value());
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }

            return false;
        }
    }
}