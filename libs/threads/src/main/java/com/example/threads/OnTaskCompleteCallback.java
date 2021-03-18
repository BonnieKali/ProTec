package com.example.threads;

public interface OnTaskCompleteCallback {
    void onComplete(TaskResult<?> taskResult);
}
