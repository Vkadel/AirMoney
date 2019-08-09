package com.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.cardview.widget.CardView;

import com.vkreated.airmoney.R;

public class SetCardBackgroundColorOnClick {
    CardView mcardView;
    public SetCardBackgroundColorOnClick(CardView cardView, Boolean wasClicked, Context context) {
        mcardView=cardView;
        if(wasClicked){
            //Highlight cell
            ObjectAnimator animator = ObjectAnimator.ofFloat(mcardView, "cardElevation", 8f, 16f);
            animator.start();

            mcardView.setCardBackgroundColor(context.getColor(R.color.colorAccent));

        }else{
            mcardView.setCardElevation(8f);
            mcardView.setCardBackgroundColor(context.getColor(R.color.colorPrimary));
        }
    }
}
