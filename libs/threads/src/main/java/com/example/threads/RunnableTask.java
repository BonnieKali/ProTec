package com.example.threads;

public interface RunnableTask {
    /**
     * Task which runs in another thread and returns a TaskResult upon completion.
     *
     * @return TaskResult (?)
     */
    TaskResult<?> run();
}
