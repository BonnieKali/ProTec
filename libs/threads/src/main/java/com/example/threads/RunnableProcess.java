package com.example.threads;


/**
 * Evangelos Dimitriou (s1657192)
 *
 * Interface representing a process which runs in the background. A custom RunnableProcess can be
 * created using a lambda expression. Example is below:
 *
 *   RunnableProcess process -> (){
 *      doSomething();
 *   }
 */


public interface RunnableProcess {
    /**
     * Runs in another thread.
     */
    void run();
}
