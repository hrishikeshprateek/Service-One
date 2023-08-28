package thundersharp.sensivisionhealth.loganalyzer.models;

public class GeneralLogOutput {

    private String NUMBER;
    private long CALL_COUNT;
    private long DURATION;
    private String NAME;
    private long INCOMING;
    private long OUTGOING;

    public GeneralLogOutput() {
    }

    public GeneralLogOutput(String NUMBER, long CALL_COUNT, long DURATION, String NAME, long INCOMING, long OUTGOING) {
        this.NUMBER = NUMBER;
        this.CALL_COUNT = CALL_COUNT;
        this.DURATION = DURATION;
        this.NAME = NAME;
        this.INCOMING = INCOMING;
        this.OUTGOING = OUTGOING;
    }

    public String getNUMBER() {
        return NUMBER;
    }

    public void setNUMBER(String NUMBER) {
        this.NUMBER = NUMBER;
    }

    public long getCALL_COUNT() {
        return CALL_COUNT;
    }

    public void setCALL_COUNT(long CALL_COUNT) {
        this.CALL_COUNT = CALL_COUNT;
    }

    public long getDURATION() {
        return DURATION;
    }

    public void setDURATION(long DURATION) {
        this.DURATION = DURATION;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public long getINCOMING() {
        return INCOMING;
    }

    public void setINCOMING(long INCOMING) {
        this.INCOMING = INCOMING;
    }

    public long getOUTGOING() {
        return OUTGOING;
    }

    public void setOUTGOING(long OUTGOING) {
        this.OUTGOING = OUTGOING;
    }
}
