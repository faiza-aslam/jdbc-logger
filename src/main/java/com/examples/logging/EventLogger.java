package com.examples.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EventLogger {

	public enum EventLevel {
		INFO, WARN, ERROR
	}

	public enum EventType {
		LOGIN, LOGOUT, SIGNUP;
	}

	private static Logger eventLogger = Logger.getLogger("CustomEventLogger");

	public static void logEvent(EventLevel level, String principalName, EventType eventType, String message) {
		logMessage(level, message, eventType, principalName);
	}

    private static void logMessage(EventLevel level, String message, EventType eventType, String principalName) {

        if(level == EventLevel.INFO) {
            eventLogger.log(Level.INFO, message, new Object[]{eventType.toString(), principalName});

        } else if(level == EventLevel.WARN) {
            eventLogger.log(Level.WARNING, message, new Object[]{eventType.toString(), principalName});

        } else if(level == EventLevel.ERROR) {
            eventLogger.log(Level.SEVERE, message, new Object[]{eventType.toString(), principalName});
        }
    }
}
