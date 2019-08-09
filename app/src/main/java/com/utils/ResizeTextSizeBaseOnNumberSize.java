package com.utils;

import android.util.TypedValue;
import android.widget.TextView;

public class ResizeTextSizeBaseOnNumberSize {
    TextView mtextView;
    public ResizeTextSizeBaseOnNumberSize(TextView textView, double number) {
        mtextView=textView;
        if(number>99){
            mtextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
        }
        if(number>999){
            mtextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
        }

    }

    public TextView getTextView(){
        return mtextView;
    }

}
