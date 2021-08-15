/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Threads;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author https://stackoverflow.com/questions/53089402/javafx-running-parallel-threads-and-update-overall-progress-to-a-progressbar
 */
//class retirada da internet pelo link referido, permite o update da progressbar por varias threads
public class ProgressReporter {
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private int progress = 0;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void accumulateProgress(int progress){
        this.propertyChangeSupport.firePropertyChange("progress", this.progress, this.progress + progress);
        this.progress += progress;
    }

    public int getProgress() {
        return progress;
    }
}

