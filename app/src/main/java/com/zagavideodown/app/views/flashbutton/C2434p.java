package com.zagavideodown.app.views.flashbutton;


import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class C2434p {

    public Paint f4939a;

    public Path f4940b;

    public int f4941c;

    public int f4942d = 0;

    public boolean f4943e = false;

    public boolean f4944f = true;

    public boolean f4945g = true;

    public View f4946h;

    public final Runnable f4947i = new C2435a();

    public class C2435a implements Runnable {
        public C2435a() {
        }

        public void run() {
            C2434p pVar = C2434p.this;
            pVar.f4943e = false;
            View view = pVar.f4946h;
            if (view != null) {
                view.postInvalidate();
            }
        }
    }
}

