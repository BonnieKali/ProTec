package com.example.threads;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * Interface representing a callback which executes on the UI thread, when the corresponding task
 * is finished. It can be created as shown below:
 *
 *   OnTaskCompleteCallback callback -> taskResult {
 *      Object result = (Object) taskResult.getData();
 *      updateUI(result);
 *   }
 */


public interface OnTaskCompleteCallback {

    /**
     * Called when the associated task completes in another thread.
     *
     * @param taskResult TaskResult (?) returned from associated task
     */
    void onComplete(TaskResult<?> taskResult);
}
