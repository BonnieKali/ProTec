package com.example.threads;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BackgroundPool {
    private static final String TAG = "BackgroundPool";

    /*
     * Gets the number of available cores
     * (not always the same as the maximum number of cores)
     */
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    // Thread pool service
    private static ExecutorService executor;

    // UI thread handler for executing onTaskCompleteCallbacks on the UI thread
    private static Handler mainThreadHandler;

    /**
     * This initializes the background pool thread and should be called upon starting the
     * application. If it is not called, the first attaching of a task/process will initialize the
     * pool, possibly affecting the UI thread.
     */
    public static void initialize(){
        executor = initializePoolExecutor();
        mainThreadHandler = initializeUIHandler();
    }

    /**
     * Attaches a BackgroundProcess to the application thread pool and executes until finished.
     *
     * @param runnableProcess Implementation of BackgroundProcess interface.
     */
    public static void attachProcess(RunnableProcess runnableProcess){
        getExecutor().execute(runnableProcess::run);
    }

    /**
     * Attaches a RunnableTask to the application thread pool. When the task finishes executing
     * its result is passed to the onTaskCompleteCallback which is executed on the UI thread.
     *
     * If the RunnableTask fails with an exception, a TaskResult.Error is created and passed to the
     * onTaskCompleteCallback. TaskResult.Error includes a public Exception variable which should be
     * accessed and thrown/handled.
     *
     * @param task RunnableTask returning a TaskResult<?> object.
     * @param callback onTaskCompleteCallback accepting a TaskResult<?> object.
     */
    public static void attachTask(RunnableTask task, OnTaskCompleteCallback callback){
        getExecutor().execute(() -> {
            Log.d(TAG, "attachTask:working");
            try {
                TaskResult<?> taskResult = task.run();
                Log.d(TAG, "attachTask:returned result");
                notifyUI(taskResult, callback);
            } catch (Exception e) {
                TaskResult<?> errorTaskResult = new TaskResult.Error<>(e);
                notifyUI(errorTaskResult, callback);
            }
        });
    }

    /**
     * This is called from the background thread pool when the RunnableTask is finished, with the
     * onTaskCompleteCallback and TaskResult as arguments. It then sends the callback to the UI
     * thread to deal with the result of the background task.
     *
     * @param taskResult TaskResult object
     * @param callback onTaskCompleteCallback object (executed on UI thread)
     */
    private static void notifyUI(TaskResult<?> taskResult, OnTaskCompleteCallback callback){
        Log.d(TAG, "notifyUI:sending callback to UIHandler");
        getUIHandler().post(() -> callback.onComplete(taskResult));
    }

    /**
     * Returns background system executor. If the executor has been destroyed by the system, it
     * will reinitialize it.
     *
     * @return Executor object
     */
    private static Executor getExecutor(){
        if(executor == null){
            executor = initializePoolExecutor();
        }
        return executor;
    }

    /**
     * Returns UI thread handler for callbacks that run on the main thread. If the handler has been
     * destroyed by the system, it will reinitialize it.
     *
     * @return Handler object
     */
    private static Handler getUIHandler(){
        if(mainThreadHandler == null){
            mainThreadHandler = initializeUIHandler();
        }
        return mainThreadHandler;
    }

    /**
     *  Initializes the background ThreadPool
     *
     * @return Initialized executor object
     */
    private static ExecutorService initializePoolExecutor() {
        // Instantiates the queue of Runnables as a LinkedBlockingQueue
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        return new ThreadPoolExecutor(
                NUMBER_OF_CORES,       // Initial pool size
                NUMBER_OF_CORES,       // Max pool size
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                workQueue
        );
    }

    /**
     * Initializes UI thread handler
     *
     * @return Initialized Handler object
     */
    private static Handler initializeUIHandler(){
        return HandlerCompat.createAsync(Looper.getMainLooper());
    }


}
