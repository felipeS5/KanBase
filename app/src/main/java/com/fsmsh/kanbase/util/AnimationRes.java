package com.fsmsh.kanbase.util;

import com.fsmsh.kanbase.R;

public class AnimationRes {
    boolean leftToRight;
    boolean highSpeed;

    public AnimationRes(boolean leftToRight, boolean highSpeed) {
        this.leftToRight = leftToRight;
        this.highSpeed = highSpeed;
    }

    public int getAnimResEnter() {

        if (!highSpeed) {
            if (leftToRight) return R.anim.enter_right_left;
            else return R.anim.enter_left_right;
        } else {
            if (leftToRight) return R.anim.enter_speed_right_left;
            else return R.anim.enter_speed_left_right;
        }
    }
    public int getAnimResExit() {

        if (!highSpeed) {
            if (leftToRight) return R.anim.exit_right_left;
            else return R.anim.exit_left_right;
        } else {
            if (leftToRight) return R.anim.exit_speed_right_left;
            else return R.anim.exit_speed_left_right;
        }
    }
}
