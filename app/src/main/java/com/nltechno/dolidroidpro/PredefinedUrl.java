package com.nltechno.dolidroidpro;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class of a given predefined URL entry
 */
public class PredefinedUrl {
    public String   url;
    public String   logo;
    public String   basicauthlogin;
    public String   basicauthpass;
    public int position = 100;

    public int getPosition() {
        return position;
    }

    public String getSortOrder() {
        return this.url.replaceAll("^https?:\\/\\/([^:]+:[^@]+@)?", "");
    }

    public String getScheme() {
        Pattern p = Pattern.compile("^(https?):");
        Matcher m = p.matcher(this.url);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    public String getBasicAuthLogin() {
        Pattern p = Pattern.compile("^https?:\\/\\/([^:]+):");
        Matcher m = p.matcher(this.url);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    public String getBasicAuthPass() {
        Pattern p = Pattern.compile("^https?:\\/\\/[^:]+:([^@]+)@");
        Matcher m = p.matcher(this.url);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    public String getDomainUrl() {
        return this.url.replaceAll("^https?:\\/\\/([^:]+:[^@]+@)?", "");
    }
}
