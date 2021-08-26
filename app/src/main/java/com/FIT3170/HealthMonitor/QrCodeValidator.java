package com.FIT3170.HealthMonitor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QrCodeValidator {
    private String inviteId;
    private String doctorId;
    private boolean matches;

    public QrCodeValidator(String value){
        Pattern p = Pattern.compile("^(\\w*)-(\\w*)$");
        Matcher m = p.matcher(value);
        matches = m.matches();
        if(matches){
            inviteId = m.group(1);
            doctorId = m.group(2);
        }
    }

    public String getInviteId() {
        return inviteId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public boolean isValid() {
        return matches;
    }
}
