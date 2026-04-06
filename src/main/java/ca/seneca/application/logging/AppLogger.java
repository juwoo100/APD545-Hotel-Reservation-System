package com.hotel.logging;

import java.io.IOException;
import java.util.logging.*;

/**
 * Cross-cutting concern: rotating file logger.
 * 1 MB per file, up to 10 rotated files, kept across restarts.
 */
public class AppLogger {

    private static final Logger logger = Logger.getLogger("com.hotel");
    private static boolean initialised = false;

    private AppLogger() {}

    public static synchronized void init() {
        if (initialised) return;
        try {
            // Remove default console handlers to avoid duplicate output
            Logger root = Logger.getLogger("");
            for (Handler h : root.getHandlers()) root.removeHandler(h);

            // Rolling file: hotel_app_%g.log, 1 MB each, 10 files
            FileHandler fh = new FileHandler("hotel_app_%g.log", 1_048_576, 10, true);
            fh.setFormatter(new SimpleFormatter());
            fh.setLevel(Level.ALL);

            ConsoleHandler ch = new ConsoleHandler();
            ch.setLevel(Level.WARNING);
            ch.setFormatter(new SimpleFormatter());

            logger.addHandler(fh);
            logger.addHandler(ch);
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);

            initialised = true;
            logger.info("Logger initialised — rotating file handler active.");
        } catch (IOException e) {
            System.err.println("[AppLogger] Failed to init file handler: " + e.getMessage());
        }
    }

    public static Logger get() { return logger; }
    public static Logger get(Class<?> cls) { return Logger.getLogger(cls.getName()); }
}
