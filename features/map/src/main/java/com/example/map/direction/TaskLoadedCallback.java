package com.example.map.direction;

/**
 * James Hanratty (s1645821)
 * Defines the interface that other classes should use
 * so they can get directions
 */

public interface TaskLoadedCallback {
    void onTaskDone(Object... values);
}
