package thundersharp.sensivisionhealth.loganalyzer.interfaces;

import org.json.JSONArray;

import thundersharp.sensivisionhealth.loganalyzer.errors.AnalyzeException;

public interface OnCallLogsAnalyzed {
    void onExtractionSuccessFull(JSONArray data);
    void onFailedToAnalyze(AnalyzeException analyzeException);
    void onProgress(String status);
}
