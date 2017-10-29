package veszelovszki.soma.rc_car.communication;


import veszelovszki.soma.rc_car.utils.ByteArray;
import veszelovszki.soma.rc_car.utils.Utils;

/**
 * Message object describes msg data - code and data.
 * Codes match Arduino project's msg codes.
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 09.
 */

public class Message {

    public static final ByteArray BOOL_VALUE_TRUE = ByteArray.fromInteger(1);
    public static final ByteArray BOOL_VALUE_FALSE = ByteArray.fromInteger(0);

    public enum CODE {
        ACK(                    0b00000000                                                                  ),  // for acknowledgements
        Speed(                  0b00000001,     -55.0f,                         55.0f                       ),  // [cm/sec] (>0 means FORWARD)
        SteeringAngle(          0b00000010,     (float) Math.toRadians(-60.0),  (float) Math.toRadians(60.0)),  // [rad] (>0 means LEFT)
        DriveMode(              0b00000011),   // datas in Utils.DriveMode

        Ultra0_1_EnvPoint(      0b00001000,     Utils.SIGNED_BYTE_MIN_VALUE,    Utils.SIGNED_BYTE_MAX_VALUE ),
        Ultra2_3_EnvPoint(      0b00001001,     Utils.SIGNED_BYTE_MIN_VALUE,    Utils.SIGNED_BYTE_MAX_VALUE ),
        Ultra4_5_EnvPoint(      0b00001010,     Utils.SIGNED_BYTE_MIN_VALUE,    Utils.SIGNED_BYTE_MAX_VALUE ),
        Ultra6_7_EnvPoint(      0b00001011,     Utils.SIGNED_BYTE_MIN_VALUE,    Utils.SIGNED_BYTE_MAX_VALUE ),
        Ultra8_9_EnvPoint(      0b00001100,     Utils.SIGNED_BYTE_MIN_VALUE,    Utils.SIGNED_BYTE_MAX_VALUE ),
        Ultra10_11_EnvPoint(    0b00001101,     Utils.SIGNED_BYTE_MIN_VALUE,    Utils.SIGNED_BYTE_MAX_VALUE ),
        Ultra12_13_EnvPoint(    0b00001110,     Utils.SIGNED_BYTE_MIN_VALUE,    Utils.SIGNED_BYTE_MAX_VALUE ),
        Ultra14_15_EnvPoint(    0b00001111,     Utils.SIGNED_BYTE_MIN_VALUE,    Utils.SIGNED_BYTE_MAX_VALUE ),
        EnableEnvironment(      0b00010000,     BOOL_VALUE_FALSE,               BOOL_VALUE_TRUE             );

        private Integer mCode;

        private Object mMinDataValue;
        private Object mMaxDataValue;

        CODE(Integer code) {
            this(code, null, null);
        }

        <T> CODE(Integer code, T minDataValue, T maxDataValue) {
            mCode = code;
            mMinDataValue = minDataValue;
            mMaxDataValue = maxDataValue;
        }

        public Integer getCodeValue() {
            return mCode;
        }

        public Object getMinDataValue() {
            return mMinDataValue;
        }

        public Object getMaxDataValue() {
            return mMaxDataValue;
        }

        public static CODE getByCode(Integer code) {
            for (CODE codeObj : CODE.values())
                if (codeObj.getCodeValue().equals(code))
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

    public Message(CODE code, Integer data) {
        mCode = code;
        mData = ByteArray.fromInteger(data);
    }

    public Message(CODE code, Float data) {
        mCode = code;
        mData = ByteArray.fromFloat(data);
    }

    public Message(CODE code, Boolean data) {
        mCode = code;
        mData = data ? BOOL_VALUE_TRUE : BOOL_VALUE_FALSE;
    }

    public CODE getCode() {
        return mCode;
    }

    public ByteArray getData() {
        return mData;
    }

    public Integer getDataAsInt() {
        return mData.asInteger();
    }

    public Float getDataAsFloat() {
        return mData.asFloat();
    }

    public Boolean getDataAsBool() {
        return mData == BOOL_VALUE_TRUE;
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
        bytes[SEPARATOR_LENGTH] = (byte) (int) mCode.getCodeValue();

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
        return String.valueOf(mCode.getCodeValue()) + ": " + mData.toString();
    }


}
