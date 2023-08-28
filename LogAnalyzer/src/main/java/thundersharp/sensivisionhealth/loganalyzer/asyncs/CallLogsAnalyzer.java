package thundersharp.sensivisionhealth.loganalyzer.asyncs;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

import thundersharp.sensivisionhealth.loganalyzer.annos.ArrangeBy;
import thundersharp.sensivisionhealth.loganalyzer.annos.OperationModes;
import thundersharp.sensivisionhealth.loganalyzer.errors.AnalyzeException;
import thundersharp.sensivisionhealth.loganalyzer.interfaces.OnCallLogsAnalyzed;
import thundersharp.sensivisionhealth.loganalyzer.models.GeneralLogOutput;

public class CallLogsAnalyzer extends AsyncTask<String,Void, String> {

    private static CallLogsAnalyzer callLogsAnalyzer;
    private OnCallLogsAnalyzed onCallLogsAnalyzed;
    private @ArrangeBy int arrangeBy;
    private String queryPhoneNo;
    private @OperationModes Integer operationMode;
    private Map<String, GeneralLogOutput> callLogEntity;

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
        String data = strings[0];
        if (onCallLogsAnalyzed == null || data.isEmpty())
            throw new IllegalArgumentException("onCallLogsAnalyzedListener() not set or data invalid !!");
        onCallLogsAnalyzed.onProgress("Started to analyze the received logs...");
        try {
            //updates call string JSON to Objects
            JSONArray jsonObject = new JSONArray(data);

            onCallLogsAnalyzed.onProgress("Started to parse logs...");
            callLogEntity = new TreeMap<>((key1, key2) -> {
                long duration1;
                long duration2;
                if (arrangeBy == ArrangeBy.duration) {
                    duration1 = callLogEntity.get(key1).getDURATION();
                    duration2 = callLogEntity.get(key2).getDURATION();
                }else {
                    duration1 = callLogEntity.get(key1).getCALL_COUNT();
                    duration2 = callLogEntity.get(key2).getCALL_COUNT();
                }
                return Long.compare(duration1, duration2);
            });

            for (int i = 0; i < jsonObject.length(); i++){
                onCallLogsAnalyzed.onProgress("Parsing logs "+i+" of "+jsonObject.length());
                JSONObject individualCallRecord = jsonObject.getJSONObject(i);
                if (individualCallRecord.has("NUMBER") && individualCallRecord.has("DURATION")) {
                    String phoneWithoutCountryCode = individualCallRecord.getString("NUMBER").replace("+91","");
                    boolean isIncoming = individualCallRecord.getString("CALL_TYPE").equalsIgnoreCase("INCOMING");

                    GeneralLogOutput output = callLogEntity.get(phoneWithoutCountryCode);

                    if (output != null) {
                        //Update entry
                        callLogEntity.replace(phoneWithoutCountryCode,
                                new GeneralLogOutput(output.getNUMBER(),
                                output.getCALL_COUNT()+1,
                                output.getDURATION() + Long.parseLong(individualCallRecord.getString("DURATION")),
                                output.getNAME(),
                                isIncoming ? (output.getINCOMING()+1) : output.getINCOMING(),
                                isIncoming ? output.getOUTGOING() : (output.getOUTGOING() + 1)));

                    }else {
                        //new Entry
                        callLogEntity.put(phoneWithoutCountryCode,
                                new GeneralLogOutput(phoneWithoutCountryCode,
                                        1,
                                        Long.parseLong(individualCallRecord.getString("DURATION")),
                                        individualCallRecord.getString("NAME"),
                                        isIncoming ? 1 : 0,
                                        isIncoming ? 0 : 1));
                    }
                }
            }
            onCallLogsAnalyzed.onProgress("Logs parsed successfully...");

            long totalTalkTime = 0;
            String mostCalledNumber = null;
            long mostCalledValue = 0;

            JSONArray output = new JSONArray();
            for (Map.Entry<String, GeneralLogOutput> entry : callLogEntity.entrySet()) {
                GeneralLogOutput logOutput = entry.getValue();
                totalTalkTime += logOutput.getCALL_COUNT();

                long currentValue = (arrangeBy == ArrangeBy.duration) ?
                        logOutput.getDURATION() :
                        logOutput.getCALL_COUNT();

                if (currentValue > mostCalledValue) {
                    mostCalledNumber = logOutput.getNUMBER();
                    mostCalledValue = currentValue;
                }

                JSONObject jsonEntry = new JSONObject();
                jsonEntry.put("NUMBER", entry.getKey());
                jsonEntry.put("DURATION", logOutput.getDURATION());
                jsonEntry.put("NAME", logOutput.getNAME());
                jsonEntry.put("CALL_COUNT", logOutput.getCALL_COUNT());
                jsonEntry.put("INCOMING", logOutput.getINCOMING());
                jsonEntry.put("OUTGOING", logOutput.getOUTGOING());

                output.put(jsonEntry);
            }

            // Now sortedJsonArray contains the sorted log entries in JSON format
            JSONObject jsonObjectFinal = new JSONObject();
            jsonObjectFinal.put("MOST_CONTACTED",mostCalledNumber);
            jsonObjectFinal.put("TOTAL_TALK_TIME",totalTalkTime);
            jsonObjectFinal.put("RECORDS",output);
            jsonObjectFinal.put("QUERY_TYPE",arrangeBy == ArrangeBy.duration? "DURATION" : "COUNT");
            onCallLogsAnalyzed.onExtractionSuccessFull(jsonObjectFinal);

        }catch (Exception e){
            onCallLogsAnalyzed.onFailedToAnalyze(new AnalyzeException(e));
        }
        return "com";
    }
}
