package com.example.map;

import java.util.Observable;

/**
 * James Hanratty (s1645821)
 * This class is an observer object that when implamented allows any feature to send it data via
 * the updateValue method. Make sure to start listening onResume and stop on onPause.
 */
public class ObservableObject extends Observable {
    private static ObservableObject instance = new ObservableObject();

    public static ObservableObject getInstance() {
        return instance;
    }

    private ObservableObject() {
    }

    public void updateValue(Object data) {
        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }
}