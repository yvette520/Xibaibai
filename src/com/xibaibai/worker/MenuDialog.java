package com.xibaibai.worker;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MenuDialog extends Dialog implements  android.view.View.OnClickListener {
    LinearLayout layout;
    Animation ai; 
    Button menu_share;
    Button menu_del;
    Button menu_cancel;

    public MenuDialog(Context context) {
        super(context, R.style.dialog);
        setContentView(R.layout.menudialog);
        layout = (LinearLayout) findViewById(R.id.menu_layout);
         
        menu_share = (Button) findViewById(R.id.menu_share);
        menu_del = (Button) findViewById(R.id.menu_del);
        menu_cancel = (Button) findViewById(R.id.menu_cancel);

         
        menu_share.setOnClickListener(this);
        menu_del.setOnClickListener(this);
        menu_cancel.setOnClickListener(this);
     

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
//    @Override
//    public void dismiss() {
//        layout.setAnimation(ai);
//    }

    @Override
    public void show() {
        layout.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in));
        super.show();
    }
 
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.menu_cancel:
            dismiss();
            break;

        default:
            break;
        }
    }
}
