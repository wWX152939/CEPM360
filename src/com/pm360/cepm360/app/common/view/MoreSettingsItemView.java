package com.pm360.cepm360.app.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pm360.cepm360.R;

public class MoreSettingsItemView extends RelativeLayout {
    
    private ImageView mIcon;
    private TextView mContent;
    private TextView mNote;
    private ToggleButton mSwitch;
    private ImageView mArrowsIcon;
    private View mBottomDivider;

    public MoreSettingsItemView(Context context) {
        super(context);
    }
    public MoreSettingsItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public MoreSettingsItemView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public MoreSettingsItemView(Context context, int resId, String content, 
            String note, boolean enableSwitch, boolean enableArrow, boolean enableDivider) {
        super(context);
        init(resId, content, note, enableSwitch, enableArrow, enableDivider);
    }
    
    public void setFeatureIcon(int resId) {
        mIcon.setImageResource(resId);
    }
    
    public void setFeatureContent(String text) {
        mContent.setText(text);
    }
    
    public void setFeatureNote(String text) {
        mNote.setText(text);
    }
    
    public void setFeatureSwitch(boolean visibility) {
        mSwitch.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }
    
    public void setFeatureArrowRight(boolean visibility) {
        mArrowsIcon.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }
    
    public void setFeatureBottomDivider(boolean visibility) {
        mBottomDivider.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }
    
    public void init(int resId, String content, String note, 
            boolean enableSwitch, boolean enableArrow, boolean enableDivider) {
        mIcon = (ImageView) this.findViewById(R.id.more_item_feature_icon);
        mContent = (TextView) this.findViewById(R.id.more_item_feature_content);
        mSwitch = (ToggleButton) this.findViewById(R.id.more_item_feature_switch);
        mNote = (TextView) this.findViewById(R.id.more_item_feature_note);
        mArrowsIcon = (ImageView) this.findViewById(R.id.more_item_feature_arrow);
        mBottomDivider = this.findViewById(R.id.more_item_feature_divider);
        
        mIcon.setImageResource(resId);
        mContent.setText(content);
        mNote.setText(note);
        mSwitch.setVisibility(enableSwitch ? View.VISIBLE : View.GONE);
        mArrowsIcon.setVisibility(enableArrow ? View.VISIBLE : View.GONE);
        mBottomDivider.setVisibility(enableDivider ? View.VISIBLE : View.GONE);
        
    }

}
