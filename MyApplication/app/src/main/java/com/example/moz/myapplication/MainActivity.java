package com.example.moz.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.View;



public class MainActivity extends Activity {
    public Button button;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.imageView);
        button.setOnClickListener(new ButtonListerner());
    }

    private class ButtonListerner implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            // 新建一个AlphaAnimation，构造器参数代表从1到0的淡化
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            // 淡化时长为2秒
            alphaAnimation.setDuration(2000);
            // 重复两次，加一次是三次
            alphaAnimation.setRepeatCount(2);
            // 设置一个监听器
            alphaAnimation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation arg0) {
                    // TODO Auto-generated method stub
                    System.out.println("--->start");
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                    // TODO Auto-generated method stub
                    System.out.println("--->repeat");
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    // TODO Auto-generated method stub
                    System.out.println("--->end");
                }
            });
            // 让图片产生淡化动画
            imageView.startAnimation(alphaAnimation);
            // 也可以让其他控件产生动画
            button.startAnimation(alphaAnimation);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
