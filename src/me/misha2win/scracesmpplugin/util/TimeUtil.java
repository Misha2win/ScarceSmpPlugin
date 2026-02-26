package me.misha2win.scracesmpplugin.util;

public class TimeUtil {
	
	public static long getNowMillis() {
		return System.currentTimeMillis();
	}
	
	public static long getFutureTimeFromTicks(long ticks) {
		return getNowMillis() + ticks * 50l;
	}
	
	public static long getDeltaMilliseconds(long future) {
		return future - getNowMillis();
	}
	
	public static double getDeltaSeconds(long future) {
		return (future - getNowMillis()) / 1000d;
	}
	
	public static double getDeltaMinutes(long future) {
		return (future - getNowMillis()) / 1000d / 60d;
	}
	
	public static double getDeltaHours(long future) {
		return (future - getNowMillis()) / 1000d / 60d / 60d;
	}
	
	public static long hoursToTicks(int hours) {
		return hours * 20l * 60l * 60l;
	}
	
	public static long minutesToTicks(int minutes) {
		return minutes * 20l * 60l;
	}
	
	public static long secondsToTicks(int seconds) {
		return seconds * 20l;
	}
	
	public static long millisecondsToTicks(int millis) {
		return millis / 50l;
	}

}