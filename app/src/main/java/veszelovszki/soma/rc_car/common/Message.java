package veszelovszki.soma.rc_car.common;


import java.util.ArrayList;
import java.util.List;

/**
 * Message object describes msg data - code and value.
 * Codes match Arduino project's msg codes.
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 09.
 */

public class Message {

    public enum CODE {
        ACK(0),     // for acknowledgements
        Speed(1, -55.0f, 55.0f),           // [cm/sec] (>0 means FORWARD)
        SteeringAngle(2, (float) Math.toRadians(-60.0), (float) Math.toRadians(60.0)),  // [rad] (>0 means LEFT)
        DriveMode(3);    // values in Utils.DriveMode

        private Integer mCode;

        private Object mMinValue;
        private Object mMaxValue;

        CODE(Integer code) {
            this(code, null, null);
        }

        <T> CODE(Integer code, T minValue, T maxValue) {
            mCode = code;
            mMinValue = minValue;
            mMaxValue = maxValue;
        }

        public Integer getCode() {
            return mCode;
        }

        public Object getMinValue() {
            return mMinValue;
        }

        public Object getMaxValue() {
            return mMaxValue;
        }

        public static CODE getByCode(Integer code) {
            for (CODE codeObj : CODE.values())
                if (codeObj.getCode().equals(code))
                    return codeObj;
            throw new IllegalArgumentException("No CODE exists for '" + code + "'.");
        }
    };

    public static final Integer SEPARATOR_LENGTH = 4;
    public static final Integer CODE_LENGTH = 1;
    public static final Integer DATA_LENGTH = 4;
    public static final Integer LENGTH = SEPARATOR_LENGTH + CODE_LENGTH + DATA_LENGTH;

    // equals Integer.MAX_VALUE and float NaN       (length = 4)
    // forbidden as data value
    public static final ByteArray SEPARATOR = ByteArray.fromInteger(0x7fffffff);

    private CODE mCode;
    private ByteArray mData;      // length = 4

    public Message(){
        mData = new ByteArray(DATA_LENGTH);
    }

    public Message(CODE code, int value) {
        mCode = code;
        mData = ByteArray.fromInteger(value);
    }

    public Message(CODE code, float value) {
        mCode = code;
        mData = ByteArray.fromFloat(value);
    }

    CODE getCode() {
        return mCode;
    }

    public Integer getDataAsInt() {
        return mData.asInteger();
    }

    public Float getDataAsFloat() {
        return mData.asFloat();
    }

    public void setData(Integer data){
        mData = ByteArray.fromInteger(data);
    }

    public void setData(Float data){
        mData = ByteArray.fromFloat(data);
    }

    public byte[] getBytes(){
        byte[] bytes = new byte[LENGTH];

        // adds separator
        System.arraycopy(SEPARATOR.getValue(), 0, bytes, 0, SEPARATOR_LENGTH);

        // adds code
        bytes[SEPARATOR_LENGTH] = (byte) (int) mCode.getCode();

        // adds data
        System.arraycopy(mData.getValue(), 0, bytes, SEPARATOR_LENGTH + CODE_LENGTH, DATA_LENGTH);

        return bytes;
    }

    public static Message fromBytes(byte[] bytes){
        Message message = new Message();
        // skips SEPARATOR
        message.mCode = CODE.getByCode((int) bytes[SEPARATOR_LENGTH]);
        System.arraycopy(bytes, SEPARATOR_LENGTH + CODE_LENGTH, message.mData.getValue(), 0, Message.DATA_LENGTH);
        return message;
    }

    @Override
    public String toString() {
        return String.valueOf(mCode.getCode()) + ": " + mData.toString();
    }


}
