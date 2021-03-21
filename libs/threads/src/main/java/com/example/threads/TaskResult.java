package com.example.threads;

public class TaskResult<T> {

    // Generic-Type data holder
    private T data;

    /**
     * Default constructor
     */
    public TaskResult() {}

    /**
     * Constructor used when the result is successful. The passed object is saved as the generic
     * data instance.
     *
     * @param data Generic T object
     */
    public TaskResult(T data) {
        this.data = data;
    }

    /**
     * Retrieves the generic object passed to the Result constructor
     *
     * @return Generic T object
     */
    public T getData(){
        return this.data;
    }

    /**
     * TaskResult subclass representing a task execution Error and holding the corresponding
     * exception. The exception can be retrieved by TaskResult.Error.exception
     *
     * @param <T>
     */
    public static final class Error<T> extends TaskResult<T> {
        // Exception which caused the error
        public Exception exception;

        public Error(Exception exception) {
            this.exception = exception;
        }
    }
}
