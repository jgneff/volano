/*
 * Base.java - a base class for VolanoChat Test applications.
 * Copyright (C) 1996-1998 John Neffenger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package COM.volano.chat.test;
import  COM.volano.chat.Build;
import  java.io.*;
import  java.util.*;

public class Base {
    protected static final int    BYTES_PER_KB   = 1024;
    protected static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    protected static void printJVM(PrintStream output) {
        Properties properties = new Properties(System.getProperties());
        output.println("java.vendor        = " + properties.getProperty("java.vendor"));
        output.println("java.vendor.url    = " + properties.getProperty("java.vendor.url"));
        output.println("java.version       = " + properties.getProperty("java.version"));
        output.println("java.class.version = " + properties.getProperty("java.class.version"));
        output.println("java.compiler      = " + properties.getProperty("java.compiler"));
        output.println("os.name            = " + properties.getProperty("os.name"));
        output.println("os.version         = " + properties.getProperty("os.version"));
        output.println("os.arch            = " + properties.getProperty("os.arch"));
    }

    protected static void fullGC(Runtime runtime) {
        long isFree = runtime.freeMemory();
        long wasFree;
        do {
            wasFree = isFree;
            runtime.gc();
            isFree  = runtime.freeMemory();
        } while (isFree > wasFree);
        runtime.runFinalization();
        printUsage(runtime.totalMemory(), runtime.freeMemory());
    }

    protected static void printUsage(long totalBytes, long freeBytes) {
        long totalKB     = Math.round((double) totalBytes / BYTES_PER_KB);
        long freeKB      = Math.round((double) freeBytes  / BYTES_PER_KB);
        long usedKB      = totalKB - freeKB;
        int  percentUsed = Math.round(((float) usedKB / totalKB) * 100.0f);
        System.out.println("Java heap:  " + usedKB + " KB in use, " + totalKB + " KB available (" + percentUsed + "% in use).");
    }
}
