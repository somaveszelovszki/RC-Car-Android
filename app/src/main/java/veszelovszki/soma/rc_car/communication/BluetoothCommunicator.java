package veszelovszki.soma.rc_car.communication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import veszelovszki.soma.rc_car.common.Message;
import veszelovszki.soma.rc_car.utils.Utils.*;

/**
 * Handles Bluetooth communications. Sends and receives data.
 *
 * This is a Singleton class.
 *
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 01. 11.
 */
public class BluetoothCommunicator implements Communicator {

    private static final String TAG = BluetoothCommunicator.class.getCanonicalName();

    /**
     * Well-known SPP UUID.
     * @see <a href="https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html</a>
     */
    private final UUID BLUETOOTH_SERIAL_BOARD_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter mBluetoothAdapter;

    private ConnectTask mConnectTask;
    private ConnectedThread mConnectedThread;
    private Context mContext;

    private BluetoothDevice mDevice;

    public static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private Communicator.EventListener mListener;

    public BluetoothCommunicator(Context context) {
        mContext = context;

        mListener = (EventListener) context;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    }

    public BluetoothDevice getDevice(String address) {
        return mBluetoothAdapter.getRemoteDevice(address);
    }

    public Set<BluetoothDevice> getPairedDevices() {
        setBluetooth(true);
        return mBluetoothAdapter.getBondedDevices();
    }

    @Override
    public void connect(Object device) {
        String name = ((BluetoothDevice) device).getName();
        Log.d(TAG, "Connecting to: " + (name != null ? name : "null"));
        setBluetooth(true);
        mDevice = (BluetoothDevice) device;
        mConnectTask = new ConnectTask();
        mConnectTask.execute();
    }

    @Override
    public void send(Message msg) {
        Log.d(TAG, "Sent: " + msg.toString());
        mConnectedThread.write(msg.getBytes());
    }

    private Boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    private Boolean setBluetooth(Boolean enable) {
        Boolean enabled = isEnabled();
        if (enable && !enabled) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) mContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
            return true;
        } else if (!enable && enabled)
            return mBluetoothAdapter.disable();
        return enabled;
    }

    @Override
    public void cancel() {
        setBluetooth(false);
        mConnectTask.cancel(true);
        mConnectedThread.interrupt();
    }

    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {

        private BluetoothSocket mmSocket;

        ConnectTask() {
            try {
                mmSocket = mDevice.createRfcommSocketToServiceRecord(BLUETOOTH_SERIAL_BOARD_UUID);
                Log.d(TAG, "Created socket!");
            } catch (Exception e) {
                mmSocket = null;
                mListener.onCommunicationError(e);
            }
        }

        @Override
        public Boolean doInBackground(Void... params) {

            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            if (mmSocket == null) {
                mListener.onCommunicationError(new Exception("Socket is null."));
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

                mListener.onCommunicationError(connectException);
                return false;
            }

            Log.d(TAG, "Connected to the device through the socket!");

            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();

            mListener.onCommunicatorConnected();

            return true;
        }

        @Override
        public void onPostExecute(Boolean result) {
            // does nothing, connection broadcast listener will handle new connection
        }
    }

    private class ConnectedThread extends Thread {
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private byte[] mmBuffer;

        public ConnectedThread(BluetoothSocket socket) {
            try {
                mmInStream = socket.getInputStream();
                mmOutStream = socket.getOutputStream();
            } catch (IOException e) {
                mmInStream = null;
                mmOutStream = null;
                mListener.onCommunicationError(e);
            }
        }

        public void run() {
            mmBuffer = new byte[Message.LENGTH];

            // Keep listening to the InputStream until an exception or an interrupt occurs.
            while (!this.isInterrupted()) {
                try {
                    // Read from the InputStream.
                    if (mmInStream.available() >= Message.LENGTH){
                        mmInStream.read(mmBuffer, 0, Message.LENGTH);

                        Message message = Message.fromBytes(mmBuffer);
                        Log.d(TAG, "new message:" + message.toString());
                        //mListener.onNewMessage(Message.fromBytes(mmBuffer));
                    }
                } catch (Exception e) {
                    mListener.onCommunicationError(e);
                }
            }
        }

        private void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                mListener.onCommunicationError(e);
            }
        }
    }
}