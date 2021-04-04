package com.example.threads;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * Interface representing a task which runs in the background. A custom RunnableTask can be created
 * using a lambda expression. Example is below:
 *
 *   RunnableTask task -> (){
 *      Object result = doSomething();
 *      return new TaskResult<Object>(result);
 *   }
 */


public interface RunnableTask {
    /**
     * Task which runs in another thread and returns a TaskResult upon completion.
     *
     * @return TaskResult (?)
     */
    TaskResult<?> run();
}
