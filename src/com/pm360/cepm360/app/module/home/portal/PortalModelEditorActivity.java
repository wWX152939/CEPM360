package com.pm360.cepm360.app.module.home.portal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.common.GLOBAL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PortalModelEditorActivity extends Activity implements PortalGridAdpater.OnListItemClickListener {
    
    private DragGridView mEditorGridView;
    private PortalGridAdpater mPortalGridAdpater;
    
    private SharedPreferences mPrefs;
    
    private ArrayList<PortalModelBean> mModelLists = new ArrayList<PortalModelBean>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, ArrayList<PortailListItem>> mDatalListMap = new HashMap<Integer, ArrayList<PortailListItem>>();
    private String[] mModelTitles;
    
    private ArrayList<String> mSelectedModels = new ArrayList<String>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portal_model_editor_activity);
        
        String sequence = getIntent().getStringExtra("sequence");
        String[] strings = sequence.split(",");
        for (String s : strings) {
            mSelectedModels.add(s);
        }
        
        mPrefs = getSharedPreferences(GLOBAL.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);        
        mModelTitles = getResources().getStringArray(
                R.array.home_portal_model_titles);       
        
        mModelLists.clear();
        for (int i = 0; i < GLOBAL.INDEX_TYPE.length; i++) {            
            mDatalListMap.put(i, new ArrayList<PortailListItem>());            
            PortalModelBean bean = new PortalModelBean();
            bean.setIndex(i);
            bean.setTitle(mModelTitles[i]);
            bean.setDataList(mDatalListMap.get(i));
            mModelLists.add(bean);
        }
        
        for (int i = mSelectedModels.size() - 1; i >= 0; i--) {
            PortalModelBean bean = findModelByIndex(Integer.parseInt(mSelectedModels.get(i)));            
            if (bean != null) {
                mModelLists.remove(bean);
                bean.setChecked(true);
                mModelLists.add(0, bean);
            }
            
        }

        mEditorGridView = (DragGridView) findViewById(R.id.editor_grid_view);
        mPortalGridAdpater = new PortalGridAdpater(this, R.layout.portal_model_editor_layout, mModelLists);
        mPortalGridAdpater.setOnListItemClickListener(this);
        mEditorGridView.setAdapter(mPortalGridAdpater);
        
        mEditorGridView.setOnChangeListener(new DragGridView.OnChanageListener() {

            @Override
            public void onChange(int from, int to) {
              if (from < 0 || from > mModelLists.size() || to < 0 || to > mModelLists.size()) {
                  return;
              }
              PortalModelBean temp = mModelLists.get(from);
              //直接交互item
//            dataSourceList.set(from, dataSourceList.get(to));
//            dataSourceList.set(to, temp);
              
              
              //这里的处理需要注意下
              if(from < to){
                  for(int i = from; i< to; i++){
                      Collections.swap(mModelLists, i, i+1);
                  }
              } else if(from > to){
                  for(int i = from; i > to; i--){
                      Collections.swap(mModelLists, i, i-1);
                  }
              }
              
              mModelLists.set(to, temp);              
              mPortalGridAdpater.notifyDataSetChanged();
              
              mSelectedModels.clear();
              for (PortalModelBean bean : mModelLists) {
                  if (bean.isChecked()) {
                      mSelectedModels.add(String.valueOf(bean.getIndex()));
                  }
              }
              
              updateModelSequence(mSelectedModels);
            }            
        });
    }
    
    @Override
    public void onCheckBoxClick(int viewTag, boolean isChecked) {
        for (int i = 0; i < mModelLists.size() - 1; i++) {
            if (!isChecked && mModelLists.get(i).getIndex() == viewTag) {
                if (mModelLists.get(i+1).isChecked()) {
                    Collections.swap(mModelLists, i, i+1);
                    mPortalGridAdpater.notifyDataSetChanged();
                }
            }
        }
        
        if (isChecked) {
            mSelectedModels.add(String.valueOf(viewTag));
        } else {
            mSelectedModels.remove(String.valueOf(viewTag));
        }
        
        updateModelSequence(mSelectedModels);    
    }
    
    private void updateModelSequence(ArrayList<String> selectedModels) {
        String sequence = "";
        if (selectedModels.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (String s : selectedModels) {
                builder.append(s + ",");
            }
            sequence = builder.toString();
            if (sequence.endsWith(",")) {
                sequence = sequence.substring(0,sequence.length() - 1);
            }
        }
        
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("portal_model_sequence", sequence);
        editor.commit(); 
    }
    
    private PortalModelBean findModelByIndex(int index) {
        PortalModelBean res = null;
        for (PortalModelBean bean : mModelLists) {
            if (bean.getIndex() == index) {
                res = bean;
                break;
            }
        }
        return res;
    }

    @Override
    public void onListViewItemClick(int viewTag, AdapterView<?> parent,
            View view, int position, long id) {
        
    }

    @Override
    public void onMoreButtonClick(int viewTag, View view) {
        
    }

    @Override
    public void onItemLongClick(View view) {
        
    }

    @Override
    public void onImageClick(int viewTag, View view) {
        
    }

}
