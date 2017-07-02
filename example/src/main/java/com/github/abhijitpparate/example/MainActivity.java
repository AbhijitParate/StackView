package com.github.abhijitpparate.example;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.github.abhijitpparate.stackview.StackView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab_left)
    FloatingActionButton fabLeft;

    @BindView(R.id.fab_undo)
    FloatingActionButton fabUndo;

    @BindView(R.id.fab_right)
    FloatingActionButton fabRight;

    @BindView(R.id.stack_view)
    StackView stackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        populateStackView();
        stackView.setOnCardSwipeListener(new StackView.OnCardSwipeListener() {
            @Override
            public void onCardExitRight(int position) {
                Toast.makeText(MainActivity.this, "Card " + position + " removed on right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCardExitLeft(int position) {
                Toast.makeText(MainActivity.this, "Card " + position + " removed on left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStackEmpty() {
                Toast.makeText(MainActivity.this, "Stack is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateStackView() {
        stackView.addView(new Card1(this, "A"));
        stackView.addView(new Card2(this, "B"));
        stackView.addView(new Card1(this, "C"));
        stackView.addView(new Card2(this, "D"));
        stackView.addView(new Card1(this, "E"));
        stackView.addView(new Card2(this, "F"));
//        stackView.addView(new Card1(this, "G"));
//        stackView.addView(new Card2(this, "H"));
//        stackView.addView(new Card1(this, "I"));
//        stackView.addView(new Card2(this, "J"));
//        stackView.addView(new Card1(this, "K"));
//        stackView.addView(new Card2(this, "L"));
//        stackView.addView(new Card1(this, "M"));
//        stackView.addView(new Card2(this, "N"));
//        stackView.addView(new Card1(this, "O"));
//        stackView.addView(new Card2(this, "P"));
//        stackView.addView(new Card1(this, "Q"));
//        stackView.addView(new Card2(this, "R"));
//        stackView.addView(new Card1(this, "S"));
//        stackView.addView(new Card2(this, "T"));
//        stackView.addView(new Card1(this, "U"));
//        stackView.addView(new Card2(this, "V"));
//        stackView.addView(new Card1(this, "W"));
//        stackView.addView(new Card2(this, "X"));
//        stackView.addView(new Card1(this, "Y"));
//        stackView.addView(new Card2(this, "Z"));
    }

    @OnClick(R.id.fab_left)
    public void onClickLeftFab(View view) {
        stackView.removeTopLeft();
        Toast.makeText(MainActivity.this, "Left", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.fab_right)
    public void onClickRightFab(View view) {
        stackView.removeTopRight();
        Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.fab_undo)
    public void onClickUndoFab(View view) {
        stackView.undo();
        Toast.makeText(MainActivity.this, "Undo", Toast.LENGTH_SHORT).show();
    }
}
