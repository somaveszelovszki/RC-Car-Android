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

import veszelovszki.soma.rc_car.utils.ByteArray;

import static java.lang.Math.min;

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

    private static BluetoothCommunicator __instance;

    private BluetoothCommunicator() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //IntentFilter filter = new IntentFilter();
        //filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        //filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    }

    public static BluetoothCommunicator getInstance(Context context) {
        if (__instance == null) __instance = new BluetoothCommunicator();

        __instance.updateContext(context);
        return __instance;
    }

    public BluetoothDevice getDevice(String address) {
        return mBluetoothAdapter.getRemoteDevice(address);
    }

    public Set<BluetoothDevice> getPairedDevices() {
        setBluetooth(true);
        return mBluetoothAdapter.getBondedDevices();
    }

    @Override
    public void updateContext(Context context) {
        mContext = context;
        mListener = (EventListener) context;
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

            mConnectedThread = new ConnectedThread(mmSocket, mListener);
            mConnectedThread.start();

            mListener.onCommunicatorConnected();

            return true;
        }

        @Override
        public void onPostExecute(Boolean result) {
            // does nothing, connection broadcast listener will handle new connection
        }
    }

    private static class ConnectedThread extends Thread {
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private ByteArray mmRecvBuffer = new ByteArray(Message.LENGTH);
        private int mmRecvByteIdx = 0;

        private Communicator.EventListener mListener;

        private enum RecvState {
            READ_SEPARATOR, READ_CODE, READ_DATA
        }

        private RecvState mmRecvState = RecvState.READ_SEPARATOR;

        public ConnectedThread(BluetoothSocket socket, Communicator.EventListener listener) {
            mListener = listener;
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
            // Keep listening to the InputStream until an exception or an interrupt occurs.
            while (!this.isInterrupted()) {
                try {
                    Integer availableBytesNum = mmInStream.available();
                    if (availableBytesNum > 0) {
                        Integer bytesNum = min(availableBytesNum, Message.LENGTH - mmRecvByteIdx);


//                        mmInStream.read(mmRecvBuffer.getValue(), mmRecvByteIdx, bytesNum);
//                        mmRecvByteIdx += bytesNum;
//
//                        // if SEPARATOR is not at the beginning of the byte array, shifts it
//                        int sepIndex = mmRecvBuffer.indexOf(Message.SEPARATOR);
//                        Log.d(TAG, "separator:" + Message.SEPARATOR + " -> as byte array: " + ByteArray.fromInteger(Message.SEPARATOR).toString());
//                        Log.d(TAG, "buffer:" + mmRecvBuffer.toString());
//                        Log.d(TAG, "sepIndex:" + sepIndex);
//                        if (sepIndex > 0) {
//                            mmRecvBuffer.shiftBytesLeft(sepIndex);
//                            mmRecvByteIdx -= sepIndex;
//                        } else if (sepIndex == -1)
//                            mmRecvByteIdx = 0;

                        for (int i = 0; i < bytesNum; ++i) {
                            byte b = (byte) mmInStream.read();
                            switch (mmRecvState) {
                                case READ_SEPARATOR:
                                    if (b == Message.SEPARATOR.get(mmRecvByteIdx)) {
                                    mmRecvBuffer.set(mmRecvByteIdx++, b);
                                    if (mmRecvByteIdx == Message.SEPARATOR_LENGTH)
                                        mmRecvState = RecvState.READ_CODE;
                                } else
                                mmRecvByteIdx = 0;
                                break;
                                case READ_CODE:
                                    mmRecvBuffer.set(mmRecvByteIdx++, b);
                                    if (mmRecvByteIdx == Message.SEPARATOR_LENGTH + Message.CODE_LENGTH)
                                        mmRecvState = RecvState.READ_DATA;
                                    break;
                                case READ_DATA:
                                    mmRecvBuffer.set(mmRecvByteIdx++, b);
                                    if (mmRecvByteIdx == Message.LENGTH) {
                                        mmRecvByteIdx = 0;
                                        mmRecvState = RecvState.READ_SEPARATOR;

                                        // message received
                                        Message message = Message.fromBytes(mmRecvBuffer.getValue());
                                        Log.d(TAG, "new message:" + message.toString());
                                        mListener.onNewMessage(message);
                                    }
                                    break;
                            }
                        }
                    }

//                    if (mmRecvByteIdx == Message.LENGTH) {
//                        Message message = Message.fromBytes(mmRecvBuffer.getValue());
//                        Log.d(TAG, "new message:" + message.toString());
//                        mListener.onNewMessage(message);
//                    }

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