package org.insight.centre.feds2.feds2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintHeapUsage {

    private static final Logger logger = LoggerFactory.getLogger(PrintHeapUsage.class.getName());

    public static void gcAndprintMB() {
        gc();
        logger.debug(getInfoMB());
    }

    public static void gcAndprintGB() {
        gc();
        logger.debug(getInfoGB());
    }

    public static void printMB() {
        logger.debug(getInfoMB());
    }

    public static void printGB() {
    	System.out.println(getInfoGB());
        logger.debug(getInfoGB());
    }

    public static String getInfoMB() {
        long mb = 1024 * 1024;
        Runtime r = Runtime.getRuntime();
        // Get current size of heap in bytes
        long total = r.totalMemory();
        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
        long max = r.maxMemory();
        // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
        long free = r.freeMemory();

        long usedmb = (total - free) / mb;
        long totalmb = total / mb;
        long maxmb = max / mb;
        long freedmb = free / mb;

        //TODO: also for GB 
        return String.format("Heap:\tused: %dMB\tfree: %dMB\ttotal: %dMB\tmax: %dMB", usedmb, freedmb, totalmb, maxmb);

    }

    public static long getUsedMB() {
        long mb = 1024 * 1024;
        Runtime r = Runtime.getRuntime();
        // Get current size of heap in bytes
        long total = r.totalMemory();
        // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
        long free = r.freeMemory();

        return (total - free) / mb;
    }

    public static String getInfoGB() {
        float gb = 1024 * 1024 * 1024;
        Runtime r = Runtime.getRuntime();
        // Get current size of heap in bytes
        float total = r.totalMemory();
        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
        float max = r.maxMemory();
        // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
        float free = r.freeMemory();

        float usedgb = (total - free) / gb;
        float totalgb = total / gb;
        float maxgb = max / gb;
        float freedgb = free / gb;

        return String.format("Heap:\tused: %.3fGB\tfree: %.3fGB\ttotal: %.3fGB\tmax: %.3fGB", usedgb, freedgb, totalgb, maxgb);
    }

    public static void gc() {
        System.gc();
    }

}
