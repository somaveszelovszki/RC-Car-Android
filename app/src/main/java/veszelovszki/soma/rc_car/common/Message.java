package veszelovszki.soma.rc_car.common;

import android.util.Log;

/**
 * Message object describes msg data - code and value.
 * Codes match Arduino project's msg codes.
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 09.
 */

public class Message {

    public enum CODE {
        Speed(1, -55, 55),           // in [cm/sec] (>0 means FORWARD)
        SteeringAngle(2, (int) Math.toRadians(-60), (int) Math.toRadians(60)),  // in radians, positive means clockwise
        DriveMode(3);    // sets drive mode to one of the values in Utils.DriveMode

        private Integer mCode;

        private Integer mCommMinValue;
        private Integer mCommMaxValue;

        CODE(Integer code) {
            this(code, null, null);
        }

        CODE(Integer code, Integer commMinValue, Integer commMaxValue) {
            mCode = code;
            mCommMinValue = commMinValue;
            mCommMaxValue = commMaxValue;
        }

        public Integer getCode() {
            return mCode;
        }

        public Integer getMinValue() {
            return mCommMinValue;
        }

        public Integer getMaxValue() {
            return mCommMaxValue;
        }

        public static CODE getByCode(Integer code) {
            for (CODE codeObj : CODE.values()) {
                if (codeObj.getCode().equals(code)) {
                    return codeObj;
                }
            }

            return null;
        }
    };

    private CODE mCode;
    private String mValue;

    public static final Character END_CHAR = ';';
    public static final Character SEPARATOR_CHAR = ':';


    public Message(CODE code, Object value) {
        mCode = code;
        mValue = String.valueOf(value);
    }

    CODE getCode() {
        return mCode;
    }

    String getValue() {
        return mValue;
    }

    Integer getValueAsInt() {
        return Integer.parseInt(mValue);
    }

    /**
     * Parses Message from String object.
     * e.g. incoming String is "1:10"
     * output will be:
     *    Message { code=1 and value=10 }
     */
    public Message fromString(String msgString) {

        // finds separator in string (separates key from value)
        Integer separatorIndex = msgString.indexOf(SEPARATOR_CHAR);

        // code is before separator
        Integer code = Integer.parseInt(msgString.substring(0, separatorIndex));

        // value is after separator
        String value = msgString.substring(separatorIndex + 1);

        return new Message(CODE.getByCode(code), value);
    }

    /**
     * Generates String from Message object.
     * e.g. code=1 and value=10
     * output will be "1:10;"
     */
    @Override
    public String toString() {
        return mCode.getCode().toString() + SEPARATOR_CHAR + mValue + END_CHAR;
    }


}
