package veszelovszki.soma.rc_car.common;

import android.util.Log;

/**
 * Command object describes command data - code and value.
 * Codes match Arduino project's command codes.
 * Created by Created by Soma Veszelovszki {soma.veszelovszki@gmail.com} on 2017. 02. 09.
 */

public class Command {

    public enum CODE {
        Speed(1, 0, 100),           // [0 100] contains direction as well (>50 means FORWARD)
        SteeringAngle(2, -100, 100),  // [-100 100] positive means steering to the right
        ServoRecalibrate(3, -100, 100),    // [-100 100] same as steering angle
        DriveMode(4);    // sets drive mode to one of the values in Utils.DriveMode

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

        public Integer getCommMinValue() {
            return mCommMinValue;
        }

        public Integer getCommMaxValue() {
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


    public Command(CODE code, Object value) {
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
     * Parses Command from String object.
     * e.g. incoming String is "1:10"
     * output will be:
     *    Command { code=1 and value=10 }
     */
    public Command fromString(String commandString) {

        // finds separator in string (separates key from value)
        Integer separatorIndex = commandString.indexOf(SEPARATOR_CHAR);

        // code is before separator
        Integer code = Integer.parseInt(commandString.substring(0, separatorIndex));

        // value is after separator
        String value = commandString.substring(separatorIndex + 1);

        return new Command(CODE.getByCode(code), value);
    }

    /**
     * Generates String from Command object.
     * e.g. code=1 and value=10
     * output will be "1:10;"
     */
    @Override
    public String toString() {
        return mCode.getCode().toString() + SEPARATOR_CHAR + mValue + END_CHAR;
    }


}
