package thundersharp.sensivisionhealth.loganalyzer.interfaces;

import org.json.JSONArray;
import org.json.JSONObject;

import thundersharp.sensivisionhealth.loganalyzer.errors.AnalyzeException;

public interface OnCallLogsAnalyzed {
    void onExtractionSuccessFull(JSONObject data);
    void onFailedToAnalyze(AnalyzeException analyzeException);
    void onProgress(String status);
}
