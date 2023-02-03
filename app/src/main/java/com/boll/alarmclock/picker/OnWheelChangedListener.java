package com.boll.alarmclock.picker;

public interface OnWheelChangedListener {

    /**
     * Invoke when scroll stopped
     * Will return a distance offset which between current scroll position and
     * initial position, this offset is a positive or a negative, positive means
     * scrolling from bottom to top, negative means scrolling from top to bottom
     *
     * @param view   wheel view
     * @param offset Distance offset which between current scroll position and initial position
     */
    void onWheelScrolled(WheelView view, int offset);

    /**
     * Invoke when scroll stopped
     * This method will be called when wheel stop and return current selected item data's
     * position in list
     *
     * @param view     wheel view
     * @param position Current selected item data's position in list
     */
    void onWheelSelected(WheelView view, int position);

    /**
     * Invoke when scroll state changed
     * The state always between idle, dragging, and scrolling, this method will
     * be called when they switch
     *
     * @param view  wheel view
     * @param state {@link ScrollState#IDLE}
     *              {@link ScrollState#DRAGGING}
     *              {@link ScrollState#SCROLLING}
     *              <p>
     *              State only one of the following
     *              {@link ScrollState#IDLE}
     *              Express WheelPicker in state of idle
     *              {@link ScrollState#DRAGGING}
     *              Express WheelPicker in state of dragging
     *              {@link ScrollState#SCROLLING}
     *              Express WheelPicker in state of scrolling
     */
    void onWheelScrollStateChanged(WheelView view, @ScrollState int state);

    /**
     * Invoke when loop finished
     *
     * @param view wheel view
     */
    void onWheelLoopFinished(WheelView view);

}
