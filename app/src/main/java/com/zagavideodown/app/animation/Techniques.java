
package com.zagavideodown.app.animation;


import com.zagavideodown.app.animation.effect.LandingAnimator;
import com.zagavideodown.app.animation.effect.SlideInLeftAnimator;
import com.zagavideodown.app.animation.effect.ZoomInLeftAnimator;

public enum Techniques {
    Landing(LandingAnimator.class),

    SlideInLeft(SlideInLeftAnimator.class),

    ZoomInLeft(ZoomInLeftAnimator.class);


    private final Class animatorClazz;

    private Techniques(Class clazz) {
        animatorClazz = clazz;
    }

    public BaseViewAnimator getAnimator() {
        try {
            return (BaseViewAnimator) animatorClazz.newInstance();
        } catch (Exception e) {
            throw new Error("Can not init animatorClazz instance");
        }
    }
}

