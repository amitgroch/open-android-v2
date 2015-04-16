package com.citrus.sdk.ui.listeners;

/**
 * Listener for various fragment events for which activity should respond.
 * <p/>
 * Created by salil on 25/3/15.
 */
public interface FragmentEventsListeners {

    void onActivityTitleChanged(String title);

    void onNewCardAdded(boolean cardAdded);
}
