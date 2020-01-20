package com.t3k.mobile.fams.stocktake.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateIPAddress {
    public boolean isIPAddress(String ipAddress) {
        Pattern pattern;
        Matcher matcher;
        final String IP_ADDRESS = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$";
        pattern = Pattern.compile(IP_ADDRESS);
        matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    public boolean isDomainName(String domainName) {
        Pattern pattern;
        Matcher matcher;
        final String DOMIAN_NAME = "^([a-z0-9]+(-[a-z0-9]+)*\\.)+[a-z]{2,}$";
        pattern = Pattern.compile(DOMIAN_NAME);
        matcher = pattern.matcher(domainName);
        return matcher.matches();
    }
}
