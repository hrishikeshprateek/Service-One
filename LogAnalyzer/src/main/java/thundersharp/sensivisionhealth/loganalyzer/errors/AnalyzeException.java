package thundersharp.sensivisionhealth.loganalyzer.errors;

public class AnalyzeException extends Exception{
    public AnalyzeException(String message) {
        super(message);
    }

    public AnalyzeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnalyzeException(Throwable cause) {
        super(cause);
    }

    protected AnalyzeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
