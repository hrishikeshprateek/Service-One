package thundersharp.sensivisionhealth.loganalyzer.asyncs;

import android.os.AsyncTask;
import android.telecom.Call;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import thundersharp.sensivisionhealth.loganalyzer.annos.ArrangeBy;
import thundersharp.sensivisionhealth.loganalyzer.annos.OperationModes;
import thundersharp.sensivisionhealth.loganalyzer.errors.AnalyzeException;
import thundersharp.sensivisionhealth.loganalyzer.interfaces.OnCallLogsAnalyzed;
import thundersharp.sensivisionhealth.loganalyzer.models.RawLogHolder;

public class CallLogsAnalyzer extends AsyncTask<String,Void, String> {

    private static CallLogsAnalyzer callLogsAnalyzer;
    private OnCallLogsAnalyzed onCallLogsAnalyzed;
    private @ArrangeBy int arrangeBy;
    private String queryPhoneNo;
    private @OperationModes Integer operationMode;
    private List<RawLogHolder> callLogEntity;

    public static CallLogsAnalyzer getCallLogsAnalyzer(){
        if (callLogsAnalyzer == null) new CallLogsAnalyzer();
        return callLogsAnalyzer;
    }

    public CallLogsAnalyzer() {
        callLogEntity = new ArrayList<>();
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
        String data = strings[0];
        if (onCallLogsAnalyzed == null || data.isEmpty())
            throw new IllegalArgumentException("onCallLogsAnalyzedListener() not set or data invalid !!");
        onCallLogsAnalyzed.onProgress("Started to analyze the received logs...");
        try {
            JSONArray jsonObject = new JSONArray(data);
            onCallLogsAnalyzed.onProgress("Parsing logs...");
            for (int i = 0; i <=jsonObject.length(); i++){
                JSONObject individualCallRecord = jsonObject.getJSONObject(i);
                callLogEntity.add(new RawLogHolder(individualCallRecord.getString("NUMBER"),
                        individualCallRecord.getString("CALL_DATE"),
                        individualCallRecord.getString("DURATION"),
                        individualCallRecord.getString("NAME"),
                        individualCallRecord.getString("CALL_TYPE")));
            }
            onCallLogsAnalyzed.onProgress("Logs parsed successfully...");

        }catch (Exception e){
            onCallLogsAnalyzed.onFailedToAnalyze(new AnalyzeException(e));
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
