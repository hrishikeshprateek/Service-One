package thundersharp.sensivisionhealth.loganalyzer.models;

public class RawLogHolder {

    private String NUMBER;
    private String CALL_DATE;
    private String DURATION;
    private String NAME;
    private String CALL_TYPE;

    public RawLogHolder(String NUMBER, String CALL_DATE, String DURATION, String NAME, String CALL_TYPE) {
        this.NUMBER = NUMBER;
        this.CALL_DATE = CALL_DATE;
        this.DURATION = DURATION;
        this.NAME = NAME;
        this.CALL_TYPE = CALL_TYPE;
    }

    public String getCALL_TYPE() {
        return CALL_TYPE;
    }

    public void setCALL_TYPE(String CALL_TYPE) {
        this.CALL_TYPE = CALL_TYPE;
    }

    public RawLogHolder() {}

    public String getNUMBER() {
        return NUMBER;
    }

    public void setNUMBER(String NUMBER) {
        this.NUMBER = NUMBER;
    }

    public String getCALL_DATE() {
        return CALL_DATE;
    }

    public void setCALL_DATE(String CALL_DATE) {
        this.CALL_DATE = CALL_DATE;
    }

    public String getDURATION() {
        return DURATION;
    }

    public void setDURATION(String DURATION) {
        this.DURATION = DURATION;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }
}
