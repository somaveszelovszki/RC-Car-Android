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

    private CODE mCode;
    private ByteArray mValue;      // length = 4

//    public static final Character END_CHAR = ';';
//    public static final Character SEPARATOR_CHAR = ':';


    public Message(CODE code, int value) {
        mCode = code;
        mValue = ByteArray.fromInteger(value);
    }

    public Message(CODE code, float value) {
        mCode = code;
        mValue = ByteArray.fromFloat(value);
    }

    CODE getCode() {
        return mCode;
    }

    public Integer getValueAsInt() {
        return mValue.asInteger();
    }

    public Float getValueAsFloat() {
        return mValue.asFloat();
    }

    public void setValue(Integer value){
        mValue = ByteArray.fromInteger(value);
    }

    public void setValue(Float value){
        mValue = ByteArray.fromFloat(value);
    }

    public byte[] getBytes(){
        byte[] bytes = new byte[5];
        bytes[0] = (byte) (int) mCode.getCode();
        for (int i = 0; i < 4; ++i)
            bytes[i + 1] = mValue.getValue()[i];
        return bytes;
    }

//    /**
//     * Parses Message from String object.
//     * e.g. incoming String is "1:10"
//     * output will be:
//     *    Message { code=1 and value=10 }
//     */
//    public Message fromString(String msgString) {
//
//        // finds separator in string (separates key from value)
//        Integer separatorIndex = msgString.indexOf(SEPARATOR_CHAR);
//
//        // code is before separator
//        Integer code = Integer.parseInt(msgString.substring(0, separatorIndex));
//
//        // value is after separator
//        String value = msgString.substring(separatorIndex + 1);
//
//        return new Message(CODE.getByCode(code), value);
//    }

    /**
     * Only for DEBUGGING!
     */
    @Override
    public String toString() {
        return mCode.getCode().toString() + ": " + mValue.toString();
    }


}
