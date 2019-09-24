package com.gison.win.constant;

/***
 * @author <a href="mailto:wenjx@linose.com">Gisonwin</a>
 */
public enum TSTATE {
    START(1), PAUSE(2), STOP(3), RESUME(4), FINISH(5);
    private int val;

    TSTATE(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
