package veszelovszki.soma.rc_car.communication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

import veszelovszki.soma.rc_car.utils.Utils;


/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017.07.19.
 */


// TODO finish the implementation of this class -> i.e.: NOT WORKING AT ALL
public class WiFiCommunicator implements Communicator {

    private static final String TAG = WiFiCommunicator.class.getCanonicalName();

    private static final String IP_ADDR = "192.168.4.1"; // TODO
    private static final Integer PORT = 80;      // TODO

    private static final String SSID = "RC_CAR";
    private static final String PASSWORD = "zoldmokus";
    private Context mContext;

    private BufferedInputStream mInStream;
    private BufferedOutputStream mOutStream;

    HttpURLConnection mConnection;
    BufferedWriter mOut;

    private Utils.SynchronizedValue<Boolean> mCancelFlag = new Utils.SynchronizedValue<>(false);

    private ReadThread mReadThread;

    private Communicator.EventListener mListener;

    @Override
    public void updateContext(Context context) {

    }

    @Override
    public void connect(Object device) {

//        WifiConfiguration conf = new WifiConfiguration();
//        conf.SSID = "\"" + SSID + "\"";
//        conf.preSharedKey = "\""+ PASSWORD +"\"";
//
//        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//        wifiManager.addNetwork(conf);
//
//        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
//        for( WifiConfiguration i : list ) {
//            if(i.SSID != null && i.SSID.equals("\"" + SSID + "\"")) {
//                wifiManager.disconnect();
//                wifiManager.enableNetwork(i.networkId, true);
//                wifiManager.reconnect();
//
//                break;
//            }
//        }
//
//        try {
//            Socket socket = new Socket(IP_ADDR, PORT);
//            mInStream = new BufferedInputStream(socket.getInputStream());
//            mOutStream = new BufferedOutputStream(socket.getOutputStream());
//
//            mReadThread = new ReadThread();
//            mReadThread.start();
//
//            mListener.onCommunicatorConnected();
//
//        } catch (IOException e){
//            mInStream = null;
//            mOutStream = null;
//            mListener.onCommunicationError(e);
//        }

//        try {
//            //URL url = new URL(IP_ADDR);
//            //mConnection = (HttpURLConnection) url.openConnection();
//
//            //mOut = new BufferedWriter(new OutputStreamWriter(mConnection.getOutputStream()));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        mListener.onCommunicatorConnected();
    }

    @Override
    public Boolean isConnected() {
        return false;
    }

    @Override
    public synchronized void send(Message msg) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket clientSocket = null;
                DataOutputStream dos;
                DataInputStream dis;

                try {
                    InetAddress serverAddr = InetAddress.getByName(IP_ADDR);
                    clientSocket = new Socket(serverAddr, 80);
                    dos = new DataOutputStream(clientSocket.getOutputStream());
                    dis = new DataInputStream(clientSocket.getInputStream());

                    // now you can write data to stream
                    dos.writeUTF("Hello");

                    // you can also read data from stream
                    String strResponseData = dis.readUTF();


                } catch (Exception e) {
                    e.printStackTrace();
                } finally{
                    if (clientSocket != null){
                        try {
                            clientSocket.close();
                        }
                        catch (IOException ignore) {
                        }
                    }
                }

            }
        }).start();



        try {
            //mOut.write(msg.toString());
            //mOutStream.write(msg.toString().getBytes());
            //TaskEsp taskEsp = new TaskEsp(IP_ADDR);
            //taskEsp.execute();


            Log.d(TAG, "sent: " + msg.toString());
        } catch (NullPointerException e){
            mListener.onCommunicationError(e);
        }
    }

    @Override
    public void sendAndWaitACK(Message msg) {

    }

    @Override
    public void cancel() {
        mCancelFlag.setValue(true);
    }

    private class ReadThread extends Thread {

        private byte[] mBuffer = new byte[1024];
        private Integer mNumBytes;
        private StringBuilder mMessageBuilder = new StringBuilder();
        private String mStrTempBuffer;

        @Override
        public void run() {
            while(!mCancelFlag.value) {
                try {
                    mNumBytes = mInStream.read(mBuffer);
//                    mStrTempBuffer = new String(mBuffer, 0, mNumBytes);
//
//                    for (Integer i = 0; i < mStrTempBuffer.length(); ++i){
//                        Character c = mStrTempBuffer.charAt(i);
//
//                        if (c.equals(Message.END_CHAR)){
//                            mListener.onNewMessage(mMessageBuilder.toString());
//                            mMessageBuilder.setLength(0);
//                        } else
//                            mMessageBuilder.append(c);
//                    }

                } catch (IOException e) {
                    Log.d(TAG, "Input stream has been disconnected", e);
                    break;
                }
            }
        }
    }

    private class TaskEsp extends AsyncTask<Void, Void, String> {

        String server;

        TaskEsp(String server){
            this.server = server;
        }

        @Override
        protected String doInBackground(Void... params) {

            final String p = "http://"+server;

            String serverResponse = "";

            //Using java.net.HttpURLConnection
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection)(new URL(p).openConnection());

                if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream inputStream = null;
                    inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader =
                            new BufferedReader(new InputStreamReader(inputStream));
                    serverResponse = bufferedReader.readLine();

                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                serverResponse = e.getMessage();
            }

            return serverResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "from ESP: " + s);
        }
    }
}
