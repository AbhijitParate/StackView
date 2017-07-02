package com.github.abhijitpparate.example;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.abhijitpparate.stackview.StackView;
import com.github.abhijitpparate.stackview.SwipeView;

public class Card1 extends SwipeView {

    private TextView textView;
    private ImageView imageView;
    private String data;

    public Card1(@NonNull Context context) {
        super(context);
    }

    public Card1(@NonNull Context context, String i) {
        super(context);
        this.data = i;
        Log.e(this.getClass().getSimpleName(), "Card added.");
    }

    @Override
    public void setView() {
        inflate(getContext(), R.layout.card_layout_1, this);
    }

    @Override
    public void onViewInflated() {
        textView = (TextView) findViewById(R.id.tv_some_id);
        imageView = (ImageView) findViewById(R.id.image);
        textView.setText(String.valueOf(data));
        setAnimationDuration(200);
    }

    @Override
    public void OnStartViewExitEvent(StackView.Swipe swipe) {
        super.OnStartViewExitEvent(swipe);
    }

    @Override
    public void onViewDismissed(StackView.Swipe swipe) {
        Log.d(this.getClass().getSimpleName(), "onViewDismissed");
    }

    @Override
    public boolean onViewTouch(View view, MotionEvent event, boolean ret) {
        return super.onViewTouch(view, event, ret);
    }

    @Override
    public void onViewMoving(View view, MotionEvent event) {
        super.onViewMoving(view, event);
        float alpha = (event.getRawX() > oldX ? screenWidth - event.getRawX() : event.getRawX()) / (event.getRawX() > oldX ? screenWidth - oldX : oldX);
        imageView.setAlpha(alpha);
    }

    @Override
    public void onViewReset(View view) {
        super.onViewReset(view);
        imageView.setAlpha(1.0f);
    }
}
