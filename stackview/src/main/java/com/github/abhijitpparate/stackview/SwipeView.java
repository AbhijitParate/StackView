package com.github.abhijitpparate.stackview;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

public abstract class SwipeView extends FrameLayout implements View.OnTouchListener {

    protected final int screenWidth = getResources().getDisplayMetrics().widthPixels;
    protected final int screenHeight = getResources().getDisplayMetrics().heightPixels;
    protected float oldX;
    protected float oldY;
    protected float rightBoundary;
    protected float leftBoundary;
    protected int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
    private int animationDuration = 600;

    /**
     * Constrictor
     * @param context context required to inflate the view
     */
    public SwipeView(@NonNull Context context) {
        super(context);
        init();
    }

    public SwipeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SwipeView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onViewInflated();
    }

    private void init() {
        if (!isInEditMode()) {
            leftBoundary =  screenWidth * (1.0f/6.0f);
            rightBoundary = screenWidth * (5.0f/6.0f);
            setView();
            this.setOnTouchListener(this);
        }
    }

    /**
     * Called when {@link SwipeView} object is instantiated
     */
    public abstract void setView();

    /**
     * Called when {@link SwipeView} object is inflated with given view
     */
    public abstract void onViewInflated();

    /**
     * Called by {@link SwipeView} when its moves out of screen either on right or left
     * @param swipe LEFT OT RIGHT
     */
    @CallSuper
    public void OnStartViewExitEvent(StackView.Swipe swipe){
        dismissView(swipe);
    }

    /**
     * Called when view is dismissed either on left or right
     * @param swipe swipe method, LEFT or RIGHT
     */
    public void onViewDismissed(StackView.Swipe swipe){

    }

    /**
     * Called when view is dismissing either on left or right
     * @param swipe swipe method, LEFT or RIGHT
     */
    public void onViewDismissing(StackView.Swipe swipe){

    }

    /**
     * Called when view is touched and is moving
     * @param view view on which event is dispatched
     * @param event touch event
     */
    public void onViewMoving(View view, MotionEvent event){

    }

    /**
     * Called when view is reset to its original position
     * @param view view
     */
    public void onViewReset(View view){

    }

    /**
     * Invoked when the view is touched or touch event is in progress
     * @param view view on which the touch event is occurring
     * @param event touch event object
     * @param ret return value from default touch handler
     * @return True if the method has consumed the event, false otherwise.
     */
    public boolean onViewTouch(View view, MotionEvent event, boolean ret){
        return ret;
    }

    /**
     * Returns animation duration like swipe right or left, reset view, and undo.
     * @return Duration of the animations in milliseconds
     */
    protected int getAnimationDuration() {
        return animationDuration;
    }

    /**
     * Sets the duration of the animations like swipe right or left, reset view, and undo.
     * @param animationDuration Duration of the animations in milliseconds
     */
    protected void setAnimationDuration(int animationDuration) {
        this.animationDuration = animationDuration;
    }

    /**
     *
     * @param rightBoundary Right boundary in pixels
     */
    protected void setRightBoundary(float rightBoundary) {
        this.rightBoundary = rightBoundary;
    }

    /**
     *
     * @param leftBoundary Left boundary in pixels
     */
    protected void setLeftBoundary(float leftBoundary) {
        this.leftBoundary = leftBoundary;
    }

    /**
     *
     * @return padding of the topmost view (default - 0dp)
     */
    public int getPadding() {
        return padding;
    }

    /**
     * Set padding of the topmost view (default - 0dp)
     * @param padding padding in dp
     */
    public void setPadding(int padding) {
        this.padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding, getResources().getDisplayMetrics());
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean ret = onViewTouch(view, motionEvent, false);
        StackView viewStack = (StackView) view.getParent();
        int position = viewStack.getChildCount()-1;
        SwipeView topCard = (SwipeView) viewStack.getChildAt(position);
        if(topCard.equals(view)){
            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();

                    view.clearAnimation();
                    return true;
                case MotionEvent.ACTION_UP:
                    if(isCardBeyondLeftBoundary(view)){
                        EventBus.getEventBus().onNext(new StackView.CardSwipedEvent(StackView.Swipe.LEFT));
                        OnStartViewExitEvent(StackView.Swipe.LEFT);
                    } else if(isCardBeyondRightBoundary(view)){
                        EventBus.getEventBus().onNext(new StackView.CardSwipedEvent(StackView.Swipe.RIGHT));
                        OnStartViewExitEvent(StackView.Swipe.RIGHT);
                    } else {
                        resetCard();
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float newX = motionEvent.getX();
                    float newY = motionEvent.getY();

                    float dX = newX - oldX;
                    float dY = newY - oldY;

                    view.setX(view.getX() + dX);
                    view.setY(view.getY() + dY);

                    setViewRotation(view, view.getX());

                    onViewMoving(topCard, motionEvent);

                    return true;
            }
        }
        return ret;
    }

    /**
     * Checks if card if beyond the left boundary for swipe to be valid
     * @param view view to check
     * @return  True - valid, False - invalid
     */
    private boolean isCardBeyondLeftBoundary(View view){
        return (view.getX() + (view.getWidth() / 2) < leftBoundary);
    }

    /**
     * Checks if card if beyond the right margin for swipe to be valid
     * @param view view to check
     * @return  True - valid, False - invalid
     */
    private boolean isCardBeyondRightBoundary(View view){
        return (view.getX() + (view.getWidth() / 2) > rightBoundary);
    }

    /**
     * <p>Removed view from top of the screen to provided the side and brings next view on top</p>
     * @param swipe - LEFT or RIGHT
     */
    public void dismissView(final StackView.Swipe swipe){
        this.animate()
            .x((swipe== StackView.Swipe.LEFT?-(screenWidth * 2):(screenWidth * 2)))
            .y(0)
            .setInterpolator(new AccelerateInterpolator())
            .setDuration(animationDuration)
            .setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    onViewDismissing(swipe);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ViewGroup viewGroup = (ViewGroup) SwipeView.this.getParent();
                    if(viewGroup != null) viewGroup.removeView(SwipeView.this);
                    SwipeView.this.animate().setListener(null);
                    onViewDismissed(swipe);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
    }

    /**
     * <p>Reset view to its last position.</p>
     * <p>Used while invalidating the swipe and to reset the view to its position on top of the stack.</p>
     */
    protected void resetCard(){
        this.animate()
                .x(0)
                .y(0)
                .rotation(0)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(animationDuration);
        onViewReset(this);
    }

    /**
     * Set angle of rotation of the view
     * @param view view to be rotated
     * @param posX x-coordinate of the view when touch event is occurring
     */
    private void setViewRotation(View view, float posX){
        float rotation = (40.0f * posX) / screenWidth;
        int halfCardHeight = (view.getHeight() / 2);
        if(oldY < halfCardHeight - (2*padding)){
            view.setRotation(rotation);
        } else {
            view.setRotation(-rotation);
        }
    }
}