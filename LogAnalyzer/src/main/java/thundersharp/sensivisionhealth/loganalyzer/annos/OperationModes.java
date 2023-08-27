package thundersharp.sensivisionhealth.loganalyzer.annos;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@IntDef({OperationModes.basicAnalyze,
        OperationModes.queryByNumber})
public @interface OperationModes {
    int basicAnalyze = 0;
    int queryByNumber = 1;
}
