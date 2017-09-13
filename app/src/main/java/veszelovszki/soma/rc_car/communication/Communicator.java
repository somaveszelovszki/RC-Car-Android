package veszelovszki.soma.rc_car.communication;

import veszelovszki.soma.rc_car.common.Message;

/**
 * Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017.07.19.
 */

public abstract class Communicator {

    public interface Listener{
        void onCommunicatorConnected();
        void onCommunicationError(Exception e);
        void onNewMessage(String message);
    }

    public abstract void connect();

    public synchronized Boolean send(Message.CODE code, Object value) {
        return this.send(new Message(code, value));
    }

    public abstract Boolean send(Message msg);

    public abstract void cancel();
}
