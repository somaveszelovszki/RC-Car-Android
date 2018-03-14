package veszelovszki.soma.rc_car.communication;


import veszelovszki.soma.rc_car.utils.ByteArray;
import veszelovszki.soma.rc_car.utils.Utils;

/**
 * Message object describes msg data - code and data.
 * Codes match Arduino project's message codes.
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 09.
 */

public class Message {

    public static final ByteArray BOOL_TRUE = ByteArray.fromInteger(1);
    public static final ByteArray BOOL_FALSE = ByteArray.fromInteger(0);

    public enum CODE {
        ACK_(           0b00000000                                                          ),  // for acknowledgements
        Speed(          0b00000001,     -55.0f,             55.0f                           ),  // [cm/sec] (>0 means FORWARD)
        SteeringAngle(  0b00000010,     -60.0f,             60.0f                           ),  // [degree] (>0 means LEFT)
        DriveMode(      0b00000011                                                          ),  // values in Utils.DriveMode
        CarPos(         0b00000101                                                          ),  // Absolute position of the car
        CarAngle(       0b00000110,     0.0f,               2 * (float)Math.PI              ),  // Forward angle of the car (X axis is ZERO).
        RelEnvEn(       0b00000111,     BOOL_FALSE,         BOOL_TRUE                       ),  // enable/disable relative environment sending
        RelEnvPoint(    0b00001000,     Utils.INT8_MIN,     Utils.INT8_MAX,     0b11111000  ),  // group sensed points (index of the point-pair needs to be added to this number)
        EnvGridEn(      0b00000100,     BOOL_FALSE,         BOOL_TRUE                       ),  // enable/disable environment grid sending
        EnvGrid(        0b01000000,                                             0b11000000  );  // group for environment grid points (container's X (2) and Y (4) coordinates need to be added to this number)

        private byte mCodeByte;

        private Object mMinDataValue;
        private Object mMaxDataValue;
        private byte mMatchPattern;

        CODE(int code) {
            this(code, 0b11111111);
        }

        CODE(int code, int matchPattern) {
            this(code, null, null, matchPattern);
        }

        <T> CODE(int code, T minDataValue, T maxDataValue) {
            this(code, minDataValue, maxDataValue, 0b11111111);
        }

        <T> CODE(int code, T minDataValue, T maxDataValue, int matchPattern) {
            mCodeByte = (byte)code;
            mMinDataValue = minDataValue;
            mMaxDataValue = maxDataValue;
            mMatchPattern = (byte)matchPattern;
        }

        public byte getCodeValue() {
            return mCodeByte;
        }

        public Object getMinDataValue() {
            return mMinDataValue;
        }

        public Object getMaxDataValue() {
            return mMaxDataValue;
        }

        public static CODE apply(byte code) {
            for (CODE codeObj : CODE.values())
                if (codeObj.getCodeValue() == (code & codeObj.mMatchPattern))
                    return codeObj;
            throw new IllegalArgumentException("No CODE exists for '" + code + "'.");
        }
    };

    public static final Message ACK = new Message(CODE.ACK_, 0);

    public static final Integer SEPARATOR_LENGTH = 4;
    public static final Integer CODE_LENGTH = 1;
    public static final Integer DATA_LENGTH = 4;
    public static final Integer LENGTH = SEPARATOR_LENGTH + CODE_LENGTH + DATA_LENGTH;

    // equals Integer.MAX and float NaN       (length = 4)
    // forbidden as data value
    public static final ByteArray SEPARATOR = ByteArray.fromInteger(0x7fffffff);

    private CODE mCode;
    private byte mCodeByte;
    private ByteArray mData;      // length = 4

    public Message(){
        mData = new ByteArray(DATA_LENGTH);
    }

    public Message(CODE code, int data) {
        mCode = code;
        mCodeByte = mCode.getCodeValue();
        mData = ByteArray.fromInteger(data);
    }

    public Message(CODE code, float data) {
        mCode = code;
        mCodeByte = mCode.getCodeValue();
        mData = ByteArray.fromFloat(data);
    }

    public Message(CODE code, boolean data) {
        mCode = code;
        mData = data ? BOOL_TRUE : BOOL_FALSE;
    }

    public Message(byte codeByte, int data) {
        mCodeByte = codeByte;
        mCode = CODE.apply(codeByte);
        mData = ByteArray.fromInteger(data);
    }

    public Message(byte codeByte, float data) {
        mCodeByte = codeByte;
        mCode = CODE.apply(codeByte);
        mData = ByteArray.fromFloat(data);
    }

    public Message(byte codeByte, boolean data) {
        mCodeByte = codeByte;
        mCode = CODE.apply(codeByte);
        mData = data ? BOOL_TRUE : BOOL_FALSE;
    }

    public CODE getCode() {
        return mCode;
    }

    public ByteArray getData() {
        return mData;
    }

    public byte[] getBytes(){
        byte[] bytes = new byte[LENGTH];

        // adds separator
        System.arraycopy(SEPARATOR.getValue(), 0, bytes, 0, SEPARATOR_LENGTH);

        // adds code
        bytes[SEPARATOR_LENGTH] = mCode.getCodeValue();

        // adds data
        System.arraycopy(mData.getValue(), 0, bytes, SEPARATOR_LENGTH + CODE_LENGTH, DATA_LENGTH);

        return bytes;
    }

    public static Message fromBytes(byte[] bytes){
        Message message = new Message();
        // skips SEPARATOR

        message.mCodeByte = bytes[SEPARATOR_LENGTH];
        message.mCode = CODE.apply(bytes[SEPARATOR_LENGTH]);
        System.arraycopy(bytes, SEPARATOR_LENGTH + CODE_LENGTH, message.mData.getValue(), 0, Message.DATA_LENGTH);
        return message;
    }

    @Override
    public String toString() {
        return String.valueOf((int)mCode.getCodeValue()) + ": " + mData.toString();
    }
}
