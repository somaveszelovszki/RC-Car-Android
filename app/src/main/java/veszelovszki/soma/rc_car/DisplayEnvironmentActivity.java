package veszelovszki.soma.rc_car;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import veszelovszki.soma.rc_car.communication.BluetoothCommunicator;
import veszelovszki.soma.rc_car.communication.Communicator;
import veszelovszki.soma.rc_car.communication.Message;
import veszelovszki.soma.rc_car.fragment.DisplayEnvironmentFragment;
import veszelovszki.soma.rc_car.utils.Pointf;
import veszelovszki.soma.rc_car.utils.PrefManager;
import veszelovszki.soma.rc_car.utils.PreferenceAdaptActivity;

public class DisplayEnvironmentActivity extends PreferenceAdaptActivity
        implements Communicator.EventListener {

    private static final String TAG = DisplayEnvironmentFragment.class.getCanonicalName();

    DisplayEnvironmentFragment mDisplayEnvironmentFragment;

    Communicator mCommunicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_environment);

        if (savedInstanceState != null) {
            mDisplayEnvironmentFragment = (DisplayEnvironmentFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, DisplayEnvironmentFragment.TAG);
        } else {
            mDisplayEnvironmentFragment = DisplayEnvironmentFragment.newInstance();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mDisplayEnvironmentFragment, DisplayEnvironmentFragment.TAG).commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.car_environment);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCommunicator = BluetoothCommunicator.getInstance(this);

        if (mCommunicator.isConnected()) {
            mCommunicator.send(new Message(Message.CODE.EnableEnvironment, true));
        } else
            onError(new Exception("Communicator is not connected!"));
    }

    @Override
    protected void onPause() {
        if (mCommunicator.isConnected()) {
            mCommunicator.send(new Message(Message.CODE.EnableEnvironment, false));
        } else
            onError(new Exception("Communicator is not connected!"));

        super.onPause();
    }

    @Override
    public PrefManager.PREFERENCE getFirstStartPreference() {
        return PrefManager.PREFERENCE.FIRST_START_DISPLAY_ENVIRONMENT;
    }

    @Override
    public void onCommunicatorConnected() {

    }

    @Override
    public void onCommunicationError(Exception e) {
        onError(e);
    }

    @Override
    public void onNewMessage(Message message) {
        switch (message.getCode()) {
            case ACK:
                break;
            case Speed:
                break;
            case SteeringAngle:
                break;
            case DriveMode:
                break;
            case Ultra0_1_EnvPoint:
            case Ultra2_3_EnvPoint:
            case Ultra4_5_EnvPoint:
            case Ultra6_7_EnvPoint:
            case Ultra8_9_EnvPoint:
            case Ultra10_11_EnvPoint:
            case Ultra12_13_EnvPoint:
            case Ultra14_15_EnvPoint:
                handleMsg_EnvironmentPoint(message);
                break;
            case EnableEnvironment:
                break;
        }
    }

    @Override
    public void onCommunicatorDisconnected() {
        Log.d(TAG, "Communicator disconnected.");
    }

    private void handleMsg_EnvironmentPoint(Message message) {
        // 1 message stores 2 points (measured by 2 ultrasonic sensors)
        final int pos1 = 2 * (message.getCode().getCodeValue() - Message.CODE.Ultra0_1_EnvPoint.getCodeValue()),
                pos2 = pos1 + 1;

        final Pointf p1 = Pointf.fromByteArray(message.getData().subArray(0, 2)),
                p2 = Pointf.fromByteArray(message.getData().subArray(2, 2));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDisplayEnvironmentFragment.updatePoint(pos1, p1);
                mDisplayEnvironmentFragment.updatePoint(pos2, p2);
            }
        });
    }
}
