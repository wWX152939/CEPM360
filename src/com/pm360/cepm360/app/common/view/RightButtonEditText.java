
package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pm360.cepm360.R;

public class RightButtonEditText extends LinearLayout {
    private EditText mEditText;
    private ImageView mButton;

    public RightButtonEditText(Context context) {
        super(context);
    }

    public RightButtonEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CepmView);
        int textColor = a.getColor(R.styleable.CepmView_textColor, Color.WHITE);
        Drawable buttonIcon = a.getDrawable(R.styleable.CepmView_buttonIcon);
        a.recycle();
        LayoutInflater.from(context).inflate(R.layout.edit_and_rightbtn, this);
        mEditText = (EditText) findViewById(R.id.text);
        mEditText.setTextColor(textColor);
        mButton = (ImageView) findViewById(R.id.btn);
        if (buttonIcon != null)
            mButton.setImageDrawable(buttonIcon);
    }

    public ImageView getRightButton() {
        return mButton;
    }

    public EditText getEditText() {
        return mEditText;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mEditText.setEnabled(enabled);
        mButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

}
