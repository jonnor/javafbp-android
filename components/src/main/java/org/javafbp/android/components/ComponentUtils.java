package org.javafbp.android.components;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by jon on 8/28/14.
 */
public class ComponentUtils {

    static private final int defaultTimeoutMs = 2000;

    static public void runInMainThreadWaiting(Runnable runnable) throws InterruptedException {
        runInMainThreadWaiting(runnable, defaultTimeoutMs);
    }

    static public void runInMainThreadWaiting(Runnable runnable, int timeoutMs)
            throws InterruptedException {

        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(runnable);
        synchronized (runnable) {
            runnable.wait(timeoutMs);
        }
    }
}
