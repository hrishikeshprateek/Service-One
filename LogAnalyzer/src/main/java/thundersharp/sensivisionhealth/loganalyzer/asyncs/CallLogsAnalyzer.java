package thundersharp.sensivisionhealth.loganalyzer.asyncs;

import android.os.AsyncTask;
import android.telecom.Call;

import thundersharp.sensivisionhealth.loganalyzer.annos.ArrangeBy;
import thundersharp.sensivisionhealth.loganalyzer.annos.OperationModes;
import thundersharp.sensivisionhealth.loganalyzer.interfaces.OnCallLogsAnalyzed;

public class CallLogsAnalyzer extends AsyncTask<String,Void, String> {

    private static CallLogsAnalyzer callLogsAnalyzer;
    private OnCallLogsAnalyzed onCallLogsAnalyzed;
    private @ArrangeBy int arrangeBy;
    private String queryPhoneNo;
    private @OperationModes Integer operationMode;

    public static CallLogsAnalyzer getCallLogsAnalyzer(){
        if (callLogsAnalyzer == null) new CallLogsAnalyzer();
        return callLogsAnalyzer;
    }

    public CallLogsAnalyzer setOnCallLogsAnalyzedListener(OnCallLogsAnalyzed onCallLogsAnalyzed){
        this.onCallLogsAnalyzed = onCallLogsAnalyzed;
        return this;
    }

    public CallLogsAnalyzer setOperationMode(int operationMode) {
        this.operationMode = operationMode;
        return this;
    }

    public CallLogsAnalyzer setArrangeBy(@ArrangeBy int arrangeBy){
        this.arrangeBy = arrangeBy;
        return this;
    }

    public CallLogsAnalyzer setQueryPhoneNo(String phoneNo){
        if (operationMode == null || operationMode == OperationModes.basicAnalyze){
            throw new IllegalArgumentException("Either operationMode() is not called or is not set to OperationModes.queryByNumber !");
        }
        this.queryPhoneNo = phoneNo;
        return this;
    }

    @Override
    protected String doInBackground(String... strings) {
        if (callLogsAnalyzer == null){
            throw new IllegalArgumentException("onCallLogsAnalyzedListener() not set !!");
        }
        String data = strings[0];
        String arrangeBy = strings[1];

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
