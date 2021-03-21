package com.example.threads;

public interface OnTaskCompleteCallback {

    /**
     * Called when the associated task completes in another thread.
     *
     * @param taskResult TaskResult (?) returned from associated task
     */
    void onComplete(TaskResult<?> taskResult);
}
