package com.horizen.economy.economy;
import java.text.NumberFormat;import java.util.Locale;
public final class Money {
 private Money(){}
 public static String format(long value,boolean compact,String symbol){if(!compact)return symbol+NumberFormat.getIntegerInstance(Locale.US).format(value);double n=value;String[] u={"","k","m","b","t"};int i=0;while(n>=1000&&i<u.length-1){n/=1000;i++;}String s=(n==Math.floor(n))?String.format(Locale.US,"%.0f",n):String.format(Locale.US,"%.1f",n);return symbol+s+u[i];}
 public static long parse(String raw){if(raw==null||!raw.matches("[0-9]+"))throw new IllegalArgumentException("Amount must be a whole number.");return Long.parseLong(raw);}
}
