package com.github.abhijitpparate.stackview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * A view which holds 0 to many {@link SwipeView}s and sends callbacks
 * using the listener then they are removed.
 */
public class StackView extends FrameLayout {

    OnCardSwipeListener listener;
    List<SwipeView> views = new ArrayList<>();
    int index = 0;
    private int stackSize;
    private int animationDuration;
    private boolean isUndoing = false;
    private CompositeSubscription subscription = new CompositeSubscription();
    private int yMultiplier = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

    /**
     * Constructor
     * @param context - Context or activity
     */
    public StackView(@NonNull Context context) {
        super(context);
        init();
    }

    /**
     * Constructor, invoked automaticalaly is view is used in layout file
     * @param context Context
     * @param attrs attributes to be set
     */
    public StackView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StackView, 0, 0);
        try {
            stackSize = ta.getInteger(R.styleable.StackView_stackSize, 3);
            animationDuration = ta.getInteger(R.styleable.StackView_animationDuration, 300);
            Log.e(this.getClass().getSimpleName(), stackSize+"");
            Log.e(this.getClass().getSimpleName(), animationDuration+"");
        } finally {
            ta.recycle();
        }
        init();
    }

    public StackView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StackView, 0, 0);
        try {
            stackSize = ta.getInteger(R.styleable.StackView_stackSize, 3);
            animationDuration = ta.getInteger(R.styleable.StackView_animationDuration, 300);
            Log.e(this.getClass().getSimpleName(), stackSize+"");
            Log.e(this.getClass().getSimpleName(), animationDuration+"");
        } finally {
            ta.recycle();
        }
        init();
    }

    public StackView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StackView, 0, 0);
        try {
            stackSize = ta.getInteger(R.styleable.StackView_stackSize, 3);
            animationDuration = ta.getInteger(R.styleable.StackView_animationDuration, 300);
            Log.e(this.getClass().getSimpleName(), stackSize+"");
            Log.e(this.getClass().getSimpleName(), animationDuration+"");
        } finally {
            ta.recycle();
        }
        init();
    }

    /**
     *
     * @return - maximum number of views in the stack
     */
    public int getStackSize() {
        return stackSize;
    }

    /**
     * Set's maximum number of cards in the stack
     * @param stackSize - max number of views
     */
    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
        updateStack();
    }

    /**
     * Invoked when new view is added to stack
     * @param child - view to be added to stack
     */
    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        Log.d("onViewAdded", String.valueOf(index-1));
        if (!isUndoing) updateStack();
    }

    /**
     * Invoked when view is removed from the stack
     * @param child - view to be removed from stack
     */
    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        if (!isUndoing) {
            Log.d("onViewRemoved", String.valueOf(index-getChildCount()-1));
            updateStack();
            rearrangeStack();
        }
    }

    /**
     * Invoked when StackView is detached from parent view
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        subscription.unsubscribe();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    private void init(){
        setClipChildren(false);
        setEventBus();
    }

    private void setEventBus(){
        Subscription rxBus
                = EventBus.getEventBus().asObservable().observeOn(AndroidSchedulers.mainThread()) // UI Thread
                .subscribe(new Action1<CardSwipedEvent>() {

                    @Override
                    public void call(CardSwipedEvent event) {
                        if (event == null) return;

                        if (listener !=  null) {
                            if (event.swipe == Swipe.LEFT) {
                                listener.onCardExitLeft(index - getChildCount());
                            } else if (event.swipe == Swipe.RIGHT) {
                                listener.onCardExitRight(index - getChildCount());
                            }
                        }
                    }
                });

        subscription.add(rxBus);
    }

    private void rearrangeStack(){
        int childCount = getChildCount();

        for(int i=childCount-1; i>=0; i--){
            SwipeView cardView = (SwipeView) getChildAt(i);

            if(cardView != null){
                float scaleValue = 1 - ((childCount-1-i)/50.0f);
                cardView.animate()
                        .x(0)
                        .y((childCount-1-i) * yMultiplier)
                        .scaleX(scaleValue)
                        .rotation(0)
                        .setInterpolator(new AnticipateOvershootInterpolator())
                        .setDuration(animationDuration);
            }
        }

    }

    private void updateStack() {
        int childCount = getChildCount();
        if(childCount < stackSize && index < views.size()) {
            addToStack(views.get(index), 0);
        } else if(childCount == 0) listener.onStackEmpty();
    }

    public void addView(SwipeView swipeView) {
        views.add(swipeView);
        if (getChildCount() < stackSize) {
            addToStack(swipeView, 0);
        }
    }

    private void addToStack(SwipeView swipeView, int i) {
        ViewGroup.LayoutParams layoutParams;
        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ++index;
        int childCount = getChildCount();
        addView(swipeView, i, layoutParams);

        float scaleValue = 1 - (childCount / 50.0f);

        swipeView.animate()
                .x(0)
                .y(childCount * yMultiplier)
                .scaleX(scaleValue)
                .setInterpolator(new AnticipateOvershootInterpolator())
                .setDuration(animationDuration);
    }

    public void setOnCardSwipeListener(OnCardSwipeListener listener) {
        this.listener = listener;
    }

    /**
     * remove top most card to left
     */
    public void removeTopLeft(){
        SwipeView swipeView = (SwipeView) getChildAt(getChildCount()-1);
        swipeView.dismissView(Swipe.LEFT);
    }

    /**
     * Remove topmost card to right
     */
    public void removeTopRight(){
        SwipeView swipeView = (SwipeView) getChildAt(getChildCount()-1);
        swipeView.dismissView(Swipe.RIGHT);
    }

    /**
     * Undo last action
     */
    public void undo() {
        if (index - getChildCount() > 0 && index - getChildCount() <= views.size()) {
            isUndoing = true;
            index = index - getChildCount() -1;
            SwipeView view = views.get(index);
            view.resetCard();
            removeAllViews();
            for (int i = 0; i < stackSize; i++) {
                updateStack();
                rearrangeStack();
            }
        }
        isUndoing = false;
    }

    /**
     * Refresh the stack
     */
    protected void recreateStack(){
        removeAllViews();
        updateStack();
    }

    public enum Swipe { LEFT, RIGHT }

    public interface OnCardSwipeListener {
        void onCardExitRight(int position);

        void onCardExitLeft(int position);

        void onStackEmpty();
    }

    static class CardSwipedEvent {

        private final Swipe swipe;

        CardSwipedEvent(Swipe swipe) {
            this.swipe = swipe;
        }
    }
}