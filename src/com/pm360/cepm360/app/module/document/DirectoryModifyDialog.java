package com.pm360.cepm360.app.module.document;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.entity.Document;

/**
 * 目录 添加、修改，关联 对话框
 *
 */
public class DirectoryModifyDialog extends Dialog {
    private Context mContext;
    private TextView mTitle;
    private Button mSave;//, mCancelRelevance;
    private ImageView mClose;
    private RadioGroup mRadioGroup;
    // 添加和修改
    private LinearLayout mDirModifyLayout;
    private EditText mDirName;
    private String mRadioSelected;
    public enum Operation {
        ADD, MODIFY, RELEVANCE
    }
    
    public DirectoryModifyDialog(Context context, String title, Operation operation, Document document) {
        super(context, R.style.MyDialogStyle);
        mContext = context;
        initLayout(title, operation, document);
    }

    public Button getSaveButton() {
        return mSave;
    }
    
    public ImageView getCloseButton() {
        return mClose;
    }
    
//    public Button getCancelRelevanceButton() {
//        return mCancelRelevance;
//    }
    
    public void setRelevanceData(String[] datas, String defaultValue) {
        for (int i = 0; i < datas.length; i++) {
            RadioButton subRadio = new RadioButton(mContext);
            subRadio.setId(i);
            subRadio.setText(datas[i]);
            subRadio.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.sp20_s));
            subRadio.setTextColor(Color.BLACK);
            subRadio.setPadding(30, 10, 0, 10);
            subRadio.setButtonDrawable(R.drawable.radiobutton);
            if (!defaultValue.equals("") && defaultValue.equals(datas[i])) {
                subRadio.setChecked(true);
            } else {
                subRadio.setChecked(false);
            }
            mRadioGroup.addView(subRadio);
        }
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int index) {
                int id = group.getCheckedRadioButtonId();
                mRadioSelected = ((RadioButton) findViewById(id)).getText().toString();
            }
        });        
    }
    
    public String getRelevanceData() {
        return mRadioSelected;
    }
    
    public String getDirName() {
        return mDirName.getText().toString();
    }
    
    @SuppressLint("InflateParams")
    public void initLayout(String title, Operation operation, Document document) {
        View dialogLayout = LayoutInflater.from(mContext).inflate(
                R.layout.document_directory_modify_dialog, null);
        
        mTitle = (TextView) dialogLayout.findViewById(R.id.dialog_title);
        mSave = (Button) dialogLayout.findViewById(R.id.btn_save);        
        mClose = (ImageView) dialogLayout.findViewById(R.id.btn_close);
        
        mTitle.setText(title);
        
        if (operation == Operation.RELEVANCE) {
            mRadioGroup = (RadioGroup) dialogLayout.findViewById(R.id.radioGroup);
            mRadioGroup.setVisibility(View.VISIBLE);
            //mCancelRelevance = (Button) dialogLayout.findViewById(R.id.btn_cancel_relevance);
            //mCancelRelevance.setVisibility(View.VISIBLE);
        }
        
        if (operation == Operation.ADD || operation == Operation.MODIFY) {
            mDirModifyLayout = (LinearLayout) dialogLayout.findViewById(R.id.dir_layout);
            mDirModifyLayout.setVisibility(View.VISIBLE);
            mDirName = (EditText) dialogLayout.findViewById(R.id.dir_name);
            if (operation == Operation.MODIFY) {
                mDirName.setText(document.getName());
            }
            mSave.setText(mContext.getResources().getString(R.string.save));
        }

        setContentView(dialogLayout);
        setCancelable(false);
    }
}