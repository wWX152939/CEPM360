/*
 * Copyright (c) 2015 PM360, Inc.
 * All Rights Reserved.
 * PM360 Proprietary and Confidential.
 */
package com.pm360.cepm360.app.module.combination;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.OwnerSelectActivity;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.entity.ZH_group;

import java.util.ArrayList;

/**
 * 
 * 组合管理 项目节点添加、修改 弹出界面
 * 
 * 作者： Administrator 日期： 2016-1-6
 * 
 */
public class NodeCreatorActivity extends Activity {

    public static final int NODE_ADD = 1;    // 添加
    public static final int NODE_MODIFY = 2; // 修改
    public static final String NODE_OPERATION_KEY = "operation";
    public static final String NODE_DATA_KEY = "group";
    public static final String NODE_TYPE = "type";//节点究竟是管理方专用还是通用的节点

    private TextView mTitle;
    private ImageView mCancel;
    private EditText mNodeNameEdit;
    private TextView mSafetyEdit;
    private TextView mQualityEdit;
    private CheckBox mCheckBox1;
    private CheckBox mCheckBox2;
    private CheckBox mCheckBox3;
    private CheckBox mCheckBox4;
    private CheckBox mCheckBox5;
    private CheckBox mCheckBox6;    
    private Button mPreviousButton;
    private Button mNextButton;
    private Button mFinishButton;
    private LinearLayout mCheckBoxLinearLayout; 
    private LinearLayout mNodeSafetyLinearLayout; 
    private LinearLayout mNodeQualityLinearLayout; 

    private int mOperation;
    private int mNodeType;
    private int mGroupPages;
    private int mCurrentPage;
    private ZH_group mGroup;

    private ArrayList<ZH_group> mGroupList = new ArrayList<ZH_group>();
    private User mSafetyUser;
    private User mQualityUser;
    private Project mProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.combination_node_creator_activity);
        this.setFinishOnTouchOutside(true);
        Intent intent = getIntent();
        mOperation = intent.getIntExtra(NODE_OPERATION_KEY, 1);
        mNodeType = intent.getIntExtra(NODE_TYPE, 1);
        mProject = (Project) intent.getSerializableExtra("project");
        mTitle = (TextView) findViewById(R.id.title);
        mCancel = (ImageView) findViewById(R.id.close);
        mCancel.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        mNodeNameEdit = (EditText) findViewById(R.id.node_name_edit);
        mNodeSafetyLinearLayout = (LinearLayout) findViewById(R.id.node_safety_layout);
        mNodeQualityLinearLayout = (LinearLayout) findViewById(R.id.node_quality_layout);
        if (mNodeType == 2) {
        	//计划模式 去除checkbox 显示安全员和质量员
        	TextView nodeNametv = (TextView) findViewById(R.id.node_name_text);
        	nodeNametv.setText(R.string.combination_new_node_plan_name);
            mCheckBoxLinearLayout = (LinearLayout) findViewById(R.id.checkbox_layout);
            mSafetyEdit = (TextView) findViewById(R.id.node_safety_edit);
            mQualityEdit = (TextView) findViewById(R.id.node_quality_edit);
        	mCheckBoxLinearLayout.setVisibility(View.GONE);	
            mSafetyEdit.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				importUser(0);	
    			}
    		});
            
            mQualityEdit.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View arg0) {
    				importUser(1);	
    			}
    		});        	
        } else {
        	//组合模式 去除安全员和质量员
        	mNodeSafetyLinearLayout.setVisibility(View.GONE);
        	mNodeQualityLinearLayout.setVisibility(View.GONE);
	        mCheckBox1 = (CheckBox) findViewById(R.id.cb1);
	        mCheckBox2 = (CheckBox) findViewById(R.id.cb2);
	        mCheckBox3 = (CheckBox) findViewById(R.id.cb3);
	        mCheckBox4 = (CheckBox) findViewById(R.id.cb4);
	        mCheckBox5 = (CheckBox) findViewById(R.id.cb5);
	        mCheckBox6 = (CheckBox) findViewById(R.id.cb6);
	        mCheckBox1.setText(GLOBAL.COMBINATION_NODE_TYPE[0][1]);
	        mCheckBox2.setText(GLOBAL.COMBINATION_NODE_TYPE[1][1]);
	        mCheckBox3.setText(GLOBAL.COMBINATION_NODE_TYPE[2][1]);
	        mCheckBox4.setText(GLOBAL.COMBINATION_NODE_TYPE[3][1]);
	        mCheckBox5.setVisibility(View.GONE);  
	        mCheckBox6.setVisibility(View.GONE);
        }
        mPreviousButton = (Button) findViewById(R.id.button_previous);
        mNextButton = (Button) findViewById(R.id.button_next);
        mFinishButton = (Button) findViewById(R.id.button_finish);
               

        if (mOperation == NODE_ADD) {
            mCurrentPage = 1;
            mGroupPages = 1;
            createNewNode();
            addNodeToList();
            setupCreateButtons(mGroupPages);
            updateCurrentTitle(mCurrentPage, mGroupPages);
        } else if (mOperation == NODE_MODIFY) {
            mGroup = (ZH_group) intent.getSerializableExtra(NODE_DATA_KEY);
            updateCurrentTitle(1, 1);
            displayNodeContent(mGroup);
            setupModifyButtons();
        }
    }
    
    private void importUser(int type) {   	
    	Intent intent = new Intent();
		intent.setClass(this, OwnerSelectActivity.class);
		intent.putExtra("title", getString(type == 0 ? R.string.combination_safety : R.string.combination_quality));					
		intent.putExtra("project", mProject);
		startActivityForResult(intent, type);    	
    }    

    private void createNewNode() {
        mNodeNameEdit.setText("");
        if (mNodeType == 2) {
            mSafetyEdit.setText("");
            mQualityEdit.setText("");     	
        } else {
        	setCheckBoxs(true);
        }
    }

    private void displayNodeContent(ZH_group group) {
        mNodeNameEdit.setText(group.getNode_name());
        if (mNodeType == 2) {
        	mSafetyEdit.setText(UserCache.getNameById(group.getSafety_user()));
        	mQualityEdit.setText(UserCache.getNameById(group.getQuality_user()));
        	mSafetyUser = UserCache.getUserById(group.getSafety_user()+"");
        	mQualityUser = UserCache.getUserById(group.getQuality_user()+"");
        } else {
	        setCheckBoxs(false);
	        String[] nodeAttributes = group.getNode_module().split(",");
	        for (int i = 0; i < nodeAttributes.length; i++) {
	            if (nodeAttributes[i].equals(GLOBAL.COMBINATION_NODE_TYPE[0][0])) {
	                mCheckBox1.setChecked(true);
	            } else if (nodeAttributes[i]
	                    .equals(GLOBAL.COMBINATION_NODE_TYPE[1][0])) {
	                mCheckBox2.setChecked(true);
	            } else if (nodeAttributes[i]
	                    .equals(GLOBAL.COMBINATION_NODE_TYPE[2][0])) {
	                mCheckBox3.setChecked(true);
	            } else if (nodeAttributes[i]
	                    .equals(GLOBAL.COMBINATION_NODE_TYPE[3][0])) {
	                mCheckBox4.setChecked(true);
	            } else if (nodeAttributes[i]
	                    .equals(GLOBAL.COMBINATION_NODE_TYPE[4][0])) {
	                mCheckBox5.setChecked(false);
	            } else if (nodeAttributes[i]
	                    .equals(GLOBAL.COMBINATION_NODE_TYPE[5][0])) {
	                mCheckBox6.setChecked(false);
	            }
	        }
        }
    }

    private void setCheckBoxs(boolean checked) {
        mCheckBox1.setChecked(checked);
        mCheckBox2.setChecked(checked);
        mCheckBox3.setChecked(checked);
        mCheckBox4.setChecked(checked);
        mCheckBox5.setChecked(false);
        mCheckBox6.setChecked(false);        
    }

    private void setupCreateButtons(int pages) {
        mPreviousButton.setText(getString(mNodeType == 2 ? 
        		R.string.combination_previous_node_plan : R.string.previous));
        mNextButton.setText(getString(mNodeType == 2 ?
        		R.string.combination_next_node_plan : R.string.next));
        mFinishButton.setText(getString(R.string.finish));
        mPreviousButton.setVisibility(pages == 1 ? View.GONE : View.VISIBLE);

        mPreviousButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mCurrentPage == mGroupPages) {
                    if (!mNodeNameEdit.getText().toString().isEmpty()) {
                        if (mGroupPages > mGroupList.size()) {
                            if (!validationNodeModule() || !validationNodeUser()) {
                                return;
                            }
                            addNodeToList();
                            saveCurrentPage(mCurrentPage);
                        }
                    } else {
                        mGroupPages--;
                    }
                } else {
                    if (!validationNodeModule() || !validationNodeUser()) {
                        return;
                    }
                    saveCurrentPage(mCurrentPage);
                }
             
                mCurrentPage--;
                updateCurrentTitle(mCurrentPage, mGroupPages);
                displayNodeContent(mGroupList.get(mCurrentPage - 1));
                if (mCurrentPage == 1) {
                    mPreviousButton.setVisibility(View.GONE);
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!validationNodeName() || !validationNodeModule() || !validationNodeUser()) {
                    return;
                }

                saveCurrentPage(mCurrentPage);
                if (mCurrentPage == mGroupPages) {
                    mCurrentPage++;
                    mGroupPages++;
                    createNewNode();
                } else {
                    mCurrentPage++;
                    displayNodeContent(mGroupList.get(mCurrentPage - 1));
                }
                updateCurrentTitle(mCurrentPage, mGroupPages);

                if (mCurrentPage > 1) {
                    mPreviousButton.setVisibility(View.VISIBLE);
                }
            }
        });

        mFinishButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            	if (mCurrentPage == mGroupPages) {
            	    if (mGroupPages > mGroupList.size()) {
                        if (!validationNodeName() || !validationNodeModule() || !validationNodeUser()) {
                            return;
                        }
                        addNodeToList();
            	    } else {
            	    	if (!validationNodeName() || !validationNodeModule() || !validationNodeUser()) {
                            return;
                        }
            	        saveCurrentPage(mCurrentPage);
            	    }
            	}
            	
            	if (mGroupList.get(mGroupPages - 1).getNode_name().isEmpty()) {
                    Toast.makeText(NodeCreatorActivity.this,
                            getString(R.string.combination_empty_node_name),
                            Toast.LENGTH_SHORT).show();
            	    return;
            	}
            	
            	setResult();
            }
        });
    }

    private void addNodeToList() {
        ZH_group group = new ZH_group();
        group.setNode_name(mNodeNameEdit.getText().toString());
        if (mNodeType == 2) {
        	if (mQualityUser != null && mSafetyUser != null) {
        		group.setQuality_user(mQualityUser.getUser_id());
        		group.setSafety_user(mSafetyUser.getUser_id());
        	}
        } else {
            group.setNode_module(getNodeModules());      	
        }
        mGroupList.add(group);
    }
    
    private void saveCurrentPage(int index) {
        if (index > mGroupList.size()) {
            addNodeToList();
        } else {
        	//保存第一页
            ZH_group group = mGroupList.get(index - 1);
            group.setNode_name(mNodeNameEdit.getText().toString());
            if (mNodeType == 2) {
            	group.setQuality_user(mQualityUser.getUser_id());
            	group.setSafety_user(mSafetyUser.getUser_id());
            } else {
                group.setNode_module(getNodeModules());            	
            }
            mGroupList.set(index - 1, group);
        }
    }

    private void setupModifyButtons() {
        mPreviousButton.setVisibility(View.GONE);
        mNextButton.setText(getString(R.string.save));
        mNextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            	if (!validationNodeName() || !validationNodeModule() || !validationNodeUser()) {
                    return;
                }
                mGroup.setNode_name(mNodeNameEdit.getText().toString());
                if (mNodeType == 2) {
                	if (mQualityUser != null && mSafetyUser != null) {
                    	Log.v("cccc","mQualityUser:"+mQualityUser+", mSafetyUser="+mSafetyUser);
                		mGroup.setQuality_user(mQualityUser.getUser_id());
                		mGroup.setSafety_user(mSafetyUser.getUser_id());
                	}                	
                } else {
                    mGroup.setNode_module(getNodeModules());
                }
                setResult();
            }
        });

        mFinishButton.setText(getString(R.string.cancel));
        mFinishButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private String getNodeModules() {
        StringBuilder nodeModules = new StringBuilder();
        
        if (mCheckBox1.isChecked()) {
            nodeModules.append(GLOBAL.COMBINATION_NODE_TYPE[0][0] + ",");
        }
        if (mCheckBox2.isChecked()) {
            nodeModules.append(GLOBAL.COMBINATION_NODE_TYPE[1][0] + ",");
        }
        if (mCheckBox3.isChecked()) {
            nodeModules.append(GLOBAL.COMBINATION_NODE_TYPE[2][0] + ",");
        }
        if (mCheckBox4.isChecked()) {
            nodeModules.append(GLOBAL.COMBINATION_NODE_TYPE[3][0] + ",");
        }
        if (mCheckBox5.isChecked()) {
            nodeModules.append(GLOBAL.COMBINATION_NODE_TYPE[4][0] + ",");
        }
        if (mCheckBox6.isChecked()) {
            nodeModules.append(GLOBAL.COMBINATION_NODE_TYPE[5][0] + ",");
        }        
        String modules = nodeModules.substring(0, nodeModules.length() - 1);
        return modules;
    }

    private void updateCurrentTitle(int current, int total) {        
		mTitle.setText(getString(mNodeType == 2 ? R.string.combination_new_node_plan : R.string.combination_new_node) + " ("
            + current + "/" + total + ")");    	
    }

    private boolean validationNodeName() {
        String res = mNodeNameEdit.getText().toString();
        if (res == null || res.equals("")) {       	
            Toast.makeText(NodeCreatorActivity.this,
                    getString(mNodeType == 2 ? R.string.combination_empty_node_name_plan : R.string.combination_empty_node_name) ,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    
    private boolean validationNodeModule() {
    	if (mNodeType == 2) {
    		return true;
    	} else {
	    	if (mCheckBox1.isChecked() || mCheckBox2.isChecked()
	    			|| mCheckBox3.isChecked() || mCheckBox4.isChecked()
	    			|| mCheckBox5.isChecked() || mCheckBox6.isChecked()) {
	    		return true;
	    	} else {
	    		Toast.makeText(NodeCreatorActivity.this,
	                    getString(R.string.combination_empty_node_module),
	                    Toast.LENGTH_SHORT).show();
	    		return false;
	    	}
    	}
    }
    
    private boolean validationNodeUser() {
    	if (mNodeType == 2) {
	    	if (mSafetyEdit.getText() != null && !mSafetyEdit.getText().equals("") 
	    			&& mQualityEdit.getText() != null && !mQualityEdit.getText().equals("")) {
	    		return true;
	    	} else {
	    		Toast.makeText(NodeCreatorActivity.this,
	                    getString(R.string.combination_empty_node_user),
	                    Toast.LENGTH_SHORT).show();
	    		return false;
	    	}
    	} else {
    		//组合状态直接返回true
    		return true;
    	}
    }    

    private void setResult() {
        Intent intent = new Intent();
        if (mOperation == NODE_ADD) {
            intent.putExtra("group_list", mGroupList);
        } else if (mOperation == NODE_MODIFY) {
            intent.putExtra("group", mGroup);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
    
    @Override
    public void finish() {
    	View editView = findViewById(R.id.node_name_edit);
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(editView.getWindowToken(), 0);
    	super.finish();
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 0) {
            User user = (User) data.getSerializableExtra("user");
            if (user != null) {
            	mSafetyUser = user;
            	mSafetyEdit.setText(mSafetyUser.getName());
            }
        } else if (requestCode == 1) {
            User user = (User) data.getSerializableExtra("user");
            if (user != null) {
            	mQualityUser = user;
            	mQualityEdit.setText(mQualityUser.getName());
            }    	
        }
        
    }    

}
