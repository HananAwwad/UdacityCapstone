package com.hananawwad.ketabi.acra;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hananawwad.ketabi.firebase.ACRAFirebaseHelper;

import org.acra.ACRAConstants;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hananawwad
 */
public class ACRAReportSender implements ReportSender {

    public ACRAReportSender(){
        /** Empty constructor required **/
    }


    @Override
    public void send(final Context context, CrashReportData errorContent) throws ReportSenderException {

        final Map<String, String> finalReport = remap(errorContent);

        ACRAFirebaseHelper firebaseHelper = new ACRAFirebaseHelper(context);
        firebaseHelper.sendAcraMap(finalReport);
    }

    @NonNull
    private Map<String, String> remap(@NonNull Map<ReportField, String> report) {

        ReportField[] fields = ACRAConstants.DEFAULT_REPORT_FIELDS;

        final Map<String, String> finalReport = new HashMap<String, String>(report.size());
        for (ReportField field : fields) {
            if(field !=null && report.get(field) != null) {
                finalReport.put(field.toString(), report.get(field));
            }
        }
        return finalReport;
    }
}
