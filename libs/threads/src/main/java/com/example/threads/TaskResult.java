package com.example.threads;

public class TaskResult<T> {

    private T data;

    /**
     * Default constructor
     */
    public TaskResult() {}

    /**
     * Constructor used when the result is successful. The passed object is saved as the result
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
     * @return
     */
    public T getData(){
        return this.data;
    }

    public static final class Error<T> extends TaskResult<T> {
        public Exception exception;

        public Error(Exception exception) {
            this.exception = exception;
        }
    }
}
