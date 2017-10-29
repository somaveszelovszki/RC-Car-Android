package veszelovszki.soma.rc_car.communication;

import android.content.Context;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017.07.19.
 */

public interface Communicator {

    interface EventListener{
        void onCommunicatorConnected();
        void onCommunicationError(Exception e);
        void onNewMessage(Message message);
        void onCommunicatorDisconnected();
    }

    void updateContext(Context context);

    void connect(Object device);

    Boolean isConnected();

    void send(Message msg);

    void cancel();
}
