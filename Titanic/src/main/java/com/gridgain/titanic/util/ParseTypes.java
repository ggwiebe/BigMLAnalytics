package com.gridgain.titanic.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class ParseTypes {
    public static Double ParseDouble(String strNumber) {
        Double retVal = null;
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch(Exception e) {
                return Double.NEGATIVE_INFINITY;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }
    public static Float ParseFloat(String strNumber) {
        Float retVal = null;
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Float.parseFloat(strNumber);
            } catch(Exception e) {
                return Float.NEGATIVE_INFINITY;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }
    public static BigDecimal ParseBigDecimal(String strNumber) {
    	BigDecimal retVal = null;
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return BigDecimal.valueOf(new Double(strNumber));
            } catch(Exception e) {
	            System.out.println("ParseBigDecimal - value: " + strNumber + "; Parsing ERROR: " + e );
                return retVal;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }
    public static Integer ParseInteger(String strNumber) {
        Integer retVal = null;
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Integer.valueOf(strNumber);
            } catch(Exception e) {
                return retVal;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }
    public static Short ParseShort(String strNumber) {
        Short retVal = null;
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Short.valueOf(strNumber);
            } catch(Exception e) {
                return retVal;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }
    public static Date ParseDate(String strDate) {
    	Date retVal = null;
        if (strDate != null && strDate.length() > 0) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date uDate = formatter.parse(strDate);
                return new java.sql.Date(uDate.getTime());
            } catch(Exception e) {
                return retVal;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }
    public static Timestamp ParseTimestamp(String strDate) {
    	Timestamp retVal = null;
        if (strDate != null && strDate.length() > 0) {
            try {
            	Instant instant = Instant.parse(strDate);
            	retVal = java.sql.Timestamp.from(instant);
                return retVal;
            } catch(Exception e) {
                return retVal;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }
    static Date ParseTimestampToDate(String strDate) {
    	Date retVal = null;
        if (strDate != null && strDate.length() > 0) {
            try {
            	DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            	TemporalAccessor ta = dtf.parse(strDate);
            	retVal = java.sql.Date.valueOf(LocalDate.from(ta));
            	return retVal;
            } catch(Exception e) {
                return retVal;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return retVal;
    }

}
