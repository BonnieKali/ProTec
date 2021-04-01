package com.example.map.direction;

/**
 * James Hanratty
 * Defines the interface that other classes should use
 * so they can get directions
 */

public interface TaskLoadedCallback {
    void onTaskDone(Object... values);
}
