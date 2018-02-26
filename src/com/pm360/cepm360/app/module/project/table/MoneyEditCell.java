
package com.pm360.cepm360.app.module.project.table;

import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.utils.UtilTools;

public class MoneyEditCell extends TextCell {

    private boolean mEnabled = true;
    private String mCoinSymbol;
    private double mMoneyNum;

    public MoneyEditCell(String coinSymbol, double moneyNum, String head, int width) {
        super("", head, width);
        mCoinSymbol = coinSymbol;
        mMoneyNum = moneyNum;
    }

    @Override
    public View createView(ViewGroup parent, int rowId, boolean isFolder, int textColor) {
        final EditText editText = (EditText) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_table_money_edit_text, parent, false);
        editText.setTextColor(textColor);

        String cellValue = getCellValue();
        if (isFolder) {
            String text = "<b>" + cellValue + "</b>";
            editText.setText(Html.fromHtml(text));
        }
        else {
            editText.setText(cellValue);
        }
        editText.getLayoutParams().width = UtilTools.dp2pxW(parent.getContext(), getWidth());
        editText.setTag(getHead());
        editText.setGravity(getGravity());
        editText.setEnabled(mEnabled);

        InputFilter moneyFilter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                    Spanned dest, int dstart, int dend) {
                if ("".equals(source.toString())) {
                    return source;
                }
                String dValue = dest.toString();
                int dResult = dValue.indexOf(".");
                String[] splitArray = dValue.split("\\.");
                if (splitArray.length > 1 && dstart > dResult) {
                    String dotValue = splitArray[1];
                    int diff = dotValue.length() + 1 - 2;
                    if (diff > 0) {
                        return source.subSequence(start, end - diff);
                    }
                }
                return source;
            }
        };
        editText.setFilters(new InputFilter[] {
                moneyFilter
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // editText.setText(mMoneyNum != 0 ? UtilTools.doubleToMoneyString(mMoneyNum) : "");
                } else {
                    String text = editText.getText().toString();
                    mMoneyNum = text.equals("") ? 0 : UtilTools.backFormatMoney("", text);
                    // editText.setText(getCellValue());
                }
            }
        });

        TextPaint textPaint = editText.getPaint();
        textPaint.setFakeBoldText(isFakeBoldText());
        return editText;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public String getCellValue() {
        return UtilTools.formatMoney(mCoinSymbol, mMoneyNum, 2);
    }

}
