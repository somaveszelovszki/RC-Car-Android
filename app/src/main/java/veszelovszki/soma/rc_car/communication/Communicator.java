package veszelovszki.soma.rc_car.communication;

import veszelovszki.soma.rc_car.common.Message;

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

    void connect(Object device);

    void send(Message msg);

    void cancel();
}
