/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bas
 */
public abstract class DataProvider {

    private List<IListener> listeners;

    protected DataProvider() {
        listeners = new ArrayList();
    }

    public void addListener(IListener listener) {
        listeners.add(listener);
    }

    public void triggerEvent(int event) {
        for (IListener listener : listeners) {
            if (event == 0) {
                listener.onReady();
            } else if (event == 1) {
                listener.onKillPlayer();
            }
        }
    }
}
