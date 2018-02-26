
package com.pm360.cepm360.app.common.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.CepmApplication;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.lockpattern.LockPatternActivity;
import com.pm360.cepm360.app.common.view.imagecoverflow.CoverFlowAdapter;
import com.pm360.cepm360.app.common.view.imagecoverflow.CoverFlowView;
import com.pm360.cepm360.app.common.view.imagecoverflow.CoverFlowView.CoverFlowListener;
import com.pm360.cepm360.app.module.home.HomeActivity;
import com.pm360.cepm360.app.module.message.MessageService;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.config.EnvironmentConfig;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.net.HttpClientTransmit;
import com.pm360.cepm360.entity.APPProperties;
import com.pm360.cepm360.entity.Role;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.RemoteLoginService;
import com.pm360.cepm360.services.common.RemoteCommonService;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录界面
 * 
 * 	登录流程：
 * 	1. 检查更新checkApplicationUpdate()
 * 	2. 登录请求 requestLogin
 * 	3. 检查是否为新用户登录，是否勾选记住用户名密码，保存当前用户到Cache中	
 * 	4. 启动消息服务MessageService
 * 	5. 获取当前用户的getSYSPermission
 * 	6. 如果为新用户登录，需要重新设置解锁图案
 * 	7. 进入Home主页
 * 
 */
public class LoginActivity extends Activity implements DataManagerInterface {

    private static final int REQ_CREATE_PATTERN = 1;

    private SharedPreferences mSharedPreferences;
    private ImageView mEnterpriseImage;
    private TextView mEnterpriseText;
    private EditText mLoginNameEdit;
    private EditText mPasswordEdit;
    private ImageView mClearLoginName;
    private ImageView mClearPassword;
    private ImageView mLoginButton;
    private CheckBox mRemember;
    private String mLoginName;
    private String mPassword;
    private ProgressDialog mProgressDialog;
    private String mOldLoginName;
    private int mEnterpriseType;
    
    private ArrayList<Bitmap> mEnterpriseTypeList;
    private int[] mEnterpriseTypeIcons;
    

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ((CepmApplication) getApplicationContext()).setDeviceInfo();
        setContentView(R.layout.login_activity_v2);
        initSwitchIP();
        EnvironmentConfig.parseFiles();
        mEnterpriseTypeIcons = new int[] {
                R.drawable.login_enterprise_type_icon_1,
                R.drawable.login_enterprise_type_icon_2,
                R.drawable.login_enterprise_type_icon_3,
                R.drawable.login_enterprise_type_icon_4,
                R.drawable.login_enterprise_type_icon_5,
                R.drawable.login_enterprise_type_icon_6,
                R.drawable.login_enterprise_type_icon_7,
        };
        
        mEnterpriseImage = (ImageView) findViewById(R.id.enterprise_image);
        mEnterpriseText = (TextView) findViewById(R.id.enterprise_text);
        mEnterpriseImage.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                showEnterpriseDialog();                
            }
        });
        
        mLoginButton = (ImageView) findViewById(R.id.login);
        mLoginNameEdit = (EditText) findViewById(R.id.login_name);        
        mClearLoginName = (ImageView) findViewById(R.id.clear_username);
        mClearLoginName.setOnClickListener(new View.OnClickListener() {            
            @Override
            public void onClick(View view) {
                mLoginNameEdit.setText("");
                mClearLoginName.setVisibility(View.INVISIBLE);
            }
        });
        
        mPasswordEdit = (EditText) findViewById(R.id.password);
        mClearPassword = (ImageView) findViewById(R.id.clear_pwd);
        mClearPassword.setOnClickListener(new View.OnClickListener() {            
            @Override
            public void onClick(View view) {
                mPasswordEdit.setText("");
                mClearPassword.setVisibility(View.INVISIBLE);
            }
        });
                
        mRemember = (CheckBox) findViewById(R.id.remember_pwd);
        mSharedPreferences = getSharedPreferences(
                GLOBAL.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        mEnterpriseType = mSharedPreferences.getInt("enterprise_type", -1);
        if (mEnterpriseType == -1) {
            mEnterpriseType = 0;
        }
//        mEnterpriseImage.setImageResource(mEnterpriseTypeIcons[mEnterpriseType]);
//        mEnterpriseText.setText(GLOBAL.ENTERPRISE_TYPE[mEnterpriseType][1]);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEnterpriseType == -1) {
                    showToast(getString(R.string.select_enterprise_type_first));
                    return;
                }
                if (mLoginNameEdit.getText().toString().equals("")
                        || mPasswordEdit.getText().toString().equals("")) {
                    showToast(getString(R.string.input_account_password_first));
                    return;
                }
                    
                mLoginName = mLoginNameEdit.getText().toString();
                mPassword = mPasswordEdit.getText().toString();
                if (mLoginName.equals("") || mPassword.equals("")) {
                    showToast(getString(R.string.input_account_password));
                    return;
                }
                
                // 开始登陆
                showProgressDialog("Loginning...");
                RemoteLoginService.getInstance().requestLogin(LoginActivity.this, mLoginName,
                        mPassword);
            }
        });
        mOldLoginName = mSharedPreferences.getString("login_name", "");
        if (!mOldLoginName.equals("")
                && !getIntent().getBooleanExtra("clear_username", false)) {
            mLoginNameEdit.setText(mOldLoginName);
        } else {
            // 获取本机手机号码
            String phoneNumber = getPhoneNumber();
            if (phoneNumber != null && !phoneNumber.equals("")) {
                mLoginNameEdit.setText(phoneNumber.substring(3, phoneNumber.length()));
            }
        }
        String oldLoginPwd = mSharedPreferences.getString("login_pwd", "");
        if (!oldLoginPwd.equals("")
                && !getIntent().getBooleanExtra("clear_username", false)
                && !getIntent().getBooleanExtra("clear_password", false)
                && !getIntent().getBooleanExtra("forget_pattern", false)) {
            mPasswordEdit.setText(oldLoginPwd);
        }
        boolean isRemember = mSharedPreferences.getBoolean("login_remember", false);
        mRemember.setChecked(isRemember);
        if (UserCache.getCurrentUser() != null) {
            toHome(false);
        } else {
            checkApplicationUpdate();
        }
        
        if (mEnterpriseType == -1) {
            showEnterpriseDialog();
        }

        mLoginNameEdit.addTextChangedListener(mTextWatcher);
        mLoginNameEdit.setOnFocusChangeListener(mFocusChangeListener);  
        mLoginNameEdit.setSelection(mLoginNameEdit.getText().length());
        mPasswordEdit.addTextChangedListener(mTextWatcher);
        mPasswordEdit.setOnFocusChangeListener(mFocusChangeListener);
    }
	
	private void initSwitchIP() {
		Switch IpSwitch = (Switch) findViewById(R.id.switch_ip);
		final EditText ipEt = (EditText) findViewById(R.id.et_ip);
		final Button btn = (Button) findViewById(R.id.save);
		IpSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
			  
            @Override  
            public void onCheckedChanged(CompoundButton buttonView,  
                    boolean isChecked) {  
                // TODO Auto-generated method stub  
                if (isChecked) {
                	ipEt.setVisibility(View.VISIBLE);
                	btn.setVisibility(View.VISIBLE);
                } else {
                	ipEt.setVisibility(View.GONE);
                	btn.setVisibility(View.GONE);
                }  
            }  
        }); 
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HttpClientTransmit.setServerIp(ipEt.getText().toString());
				Toast.makeText(LoginActivity.this, R.string.save_succ, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean focused) {
            if (view.getId() == R.id.login_name) {
                if (focused && mLoginNameEdit.getText().length() > 0) {
                    mClearLoginName.setVisibility(View.VISIBLE);
                } else {
                    mClearLoginName.setVisibility(View.INVISIBLE);
                }                
            } else if (view.getId() == R.id.password) {
                if (focused && mPasswordEdit.getText().length() > 0) {
                    mClearPassword.setVisibility(View.VISIBLE);
                } else {
                    mClearPassword.setVisibility(View.INVISIBLE);
                }
            }            
        }	    
	};

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            if (mLoginNameEdit.hasFocus()) {
                if (s.length() > 0) {
                    mClearLoginName.setVisibility(View.VISIBLE);
                } else {
                    mClearLoginName.setVisibility(View.INVISIBLE);
                }
            } else {
                if (s.length() > 0) {
                    mClearPassword.setVisibility(View.VISIBLE);
                } else {
                    mClearPassword.setVisibility(View.INVISIBLE);
                }
            }
        }
    };
	
    /**
     * 获取本机的电话号码，联通的可以获得，其他的不一定能够获得
     * 
     * @return
     */
    private String getPhoneNumber() {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
    }

    /**
     * 登陆请求的返回
     */
    @Override
    public void getDataOnResult(ResultStatus status, List<?> list) {
        //dismissProgressDialog();
        if (status.getCode() != AnalysisManager.SUCCESS_LOGIN && status.getMessage() != null
                && !status.getMessage().equals("")) {
            dismissProgressDialog();
            showToast(status.getMessage());
        }
        if (status.getCode() == AnalysisManager.SUCCESS_LOGIN && list.size() > 0
                && list.get(0) instanceof User) {
            User user = (User) list.get(0);
            if (user.getUser_id() > 0) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                if (!mOldLoginName.equals(mLoginName)) {
                	// 清除锁屏图案和当前项目
                    editor.putString("lock_pattern", "");
                    editor.putString("recent", "");
                }
                // 保存用户名，密码，是否记住密码
                editor.putString("login_name", mLoginName);
                if (mRemember.isChecked()) {
                    editor.putString("login_pwd", mPassword);
                } else {
                    editor.remove("login_pwd");
                }
                editor.putBoolean("login_remember", mRemember.isChecked());
                editor.commit();
                
                // 登陆成功，设置当前用户到cache
                UserCache.setCurrentUser(user);
                
                // 启动消息服务
                Intent msg_intent = new Intent(this, MessageService.class);
                startService(msg_intent);
                
                // 获取当前用户的系统权限
                getSYSPermission();
            }
        }
    }
    
    /**
     * 获取当前用户的系统权限
     */
    private void getSYSPermission() {
        //showProgressDialog("Get sytem permission...");
        RemoteCommonService.getInstance().getSYSPermission(new DataManagerInterface() {

            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                // dismissProgressDialog();
                if (status.getCode() !=
                        AnalysisManager.SUCCESS_DB_QUERY && status.getMessage() != null &&
                        !status.getMessage().equals("")) {
                    showToast(status.getMessage());
                }
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
                        && list != null
                        && list.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (Object object : list) {
                        if (object instanceof Role) {
                            Role role = (Role) object;
                            String action = role.getAction();
                            if (action != null) {
                                builder.append(action + ",");
                            }
                        }
                    }
                    String role_text = builder.toString();
                    if (role_text.endsWith(",")) {
                        role_text = role_text.substring(0, role_text.length() - 1);
                    }
                    String[] permissions = role_text.split(",");
                    if (permissions != null) {
                        PermissionCache.setSysPermissions(permissions);
                    }
                }
                // 进入主页面
                toHome(true);
            }
        }, UserCache.getCurrentUser());
    }
    
    /**
     * 获取完整的项目对象
     * 
     * @param project 只包括project id的对象
     */
//    private void getRecentProject(Project project) {
//        RemoteProjectService.getInstance().getProject(new DataManagerInterface() {
//
//            @Override
//            public void getDataOnResult(ResultStatus status, List<?> list) {
//                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
//                        && list != null && list.size() > 0) {
//                    for (Object object : list) {
//                        if (object instanceof Project) {
//                            ProjectCache.setCurrentProject((Project) object);
//                        }
//                    }
//                } else {
//                    toHome(true);
//                }
//            }
//        }, project);
//    }
    
    /**
     * 获取项目权限
     * 
     * @param projectId
     */
//    private void getPermissionByProject(int projectId) {
//        RemoteCommonService.getInstance().getPermissionByProject(
//                new DataManagerInterface() {
//
//                    @Override
//                    public void getDataOnResult(ResultStatus status,
//                            List<?> list) {
//                        // dismissProgressDialog();
//                        if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
//                                && status.getMessage() != null
//                                && !status.getMessage().equals("")) {
//                            showToast(status.getMessage());
//                        }
//                        if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
//                                && list != null && list.size() > 0) {
//                            StringBuilder builder = new StringBuilder();
//                            for (Object object : list) {
//                                if (object instanceof Role) {
//                                    Role role = (Role) object;
//                                    String action = role.getAction();
//                                    if (action != null) {
//                                        builder.append(action + ",");
//                                    }
//                                }
//                            }
//                            String role_text = builder.toString();
//                            if (role_text.endsWith(",")) {
//                                role_text = role_text.substring(0,
//                                        role_text.length() - 1);
//                            }
//                            String[] permissions = role_text.split(",");
//                            PermissionCache
//                                    .setProjectPermissions(permissions);
//                        }
//                        toHome(true);
//                    }
//                }, projectId, UserCache.getCurrentUser());
//    }

    public void toHome(boolean isFirst) {
        dismissProgressDialog();
        mEnterpriseType = UserCache.getCurrentUser().getTenant_type();
        ((CepmApplication) getApplicationContext()).setEnterpriseType(mEnterpriseType);                
        String testChars = mSharedPreferences.getString("lock_pattern", "");
        if (!testChars.equals("")) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.putExtra("type", mEnterpriseType);
            if (isFirst) intent.putExtra("login", true);
            intent.setClass(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else { // 设置解锁图案
            Intent intent = new Intent(
                    LockPatternActivity.ACTION_CREATE_PATTERN, null, this,
                    LockPatternActivity.class);
            intent.putExtra("current_user", UserCache.getCurrentUser().getName());
            startActivityForResult(intent, REQ_CREATE_PATTERN);
        }         
    }
    
    /**
     * 设置锁屏图案后，返回结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CREATE_PATTERN:
                if (resultCode == RESULT_OK) {
                    char[] pattern = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
                    StringBuffer buffer = new StringBuffer();
                    for (char c : pattern) {
                        buffer.append(c);
                    }

                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("lock_pattern", buffer.toString());
                    editor.commit();
                    
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClass(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("type", mEnterpriseType);
                    intent.putExtra("login", true); // 进入HomeActivity 后不需要解锁了。
                    startActivity(intent);
                    finish();
                }
            break;
        }
    }
    
    /**
     * 弹出企业类型选择界面
     */
    private void showEnterpriseDialog() {
        int[] imageIds = new int[] {
                R.drawable.login_enterprise_type_image_1,
                R.drawable.login_enterprise_type_image_2,
                R.drawable.login_enterprise_type_image_3,
                R.drawable.login_enterprise_type_image_4,
                R.drawable.login_enterprise_type_image_5,
                R.drawable.login_enterprise_type_image_6,
                R.drawable.login_enterprise_type_image_7,
        };
        final Dialog dialog = new Dialog(this, R.style.MyDialogStyle);
        dialog.setContentView(R.layout.login_enterprise_dialog_layout);
        dialog.setCanceledOnTouchOutside(true);
        @SuppressWarnings("unchecked")
        CoverFlowView<MyCoverFlowAdapter> mCoverFlowView = ((CoverFlowView<MyCoverFlowAdapter>) dialog
                .findViewById(R.id.coverflow));
        mEnterpriseTypeList = new ArrayList<Bitmap>();
        for (int id : imageIds) {
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), id);
            mEnterpriseTypeList.add(bitmap);
        }
        MyCoverFlowAdapter adapter = new MyCoverFlowAdapter();
        mCoverFlowView.setAdapter(adapter);
        mCoverFlowView.setSelection(mSharedPreferences.getInt("enterprise_type", 0), false);
        mCoverFlowView.setCoverFlowListener(new CoverFlowListener<MyCoverFlowAdapter>() {

            @Override
            public void imageOnTop(
                    CoverFlowView<MyCoverFlowAdapter> coverFlowView,
                    int position, float left, float top, float right,
                    float bottom) {
            }

            @Override
            public void topImageClicked(
                    CoverFlowView<MyCoverFlowAdapter> coverFlowView,
                    int position) {
                updateEnterpriseType(position);
                dialog.dismiss();                
            }

            @Override
            public void invalidationCompleted() {
            }
            
        });
        dialog.show();
    }
    
    /**
     * 更新企业类型到本地数据库
     * 
     * @param position
     */
    private void updateEnterpriseType(int position) {
        mEnterpriseType = position;
        mEnterpriseImage.setImageResource(mEnterpriseTypeIcons[position]);
        mEnterpriseText.setText(GLOBAL.ENTERPRISE_TYPE[position][1]);

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("enterprise_type", position);
        editor.putString("recent", "");
        editor.commit();
    }
    
    private class MyCoverFlowAdapter extends CoverFlowAdapter {
        public int getCount() {
            // 当前总共有7个企业类型
            return 7;
        }

        public Bitmap getImage(int position) {
            return mEnterpriseTypeList.get(position);
        }
    }

    private void showProgressDialog(String text) {
        dismissProgressDialog();
        mProgressDialog = UtilTools.showProgressDialog(LoginActivity.this, true, false);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    private void showToast(String text) {
        Toast.makeText(LoginActivity.this, text, Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * 检查App升级
     */
    private void checkApplicationUpdate() {
        PackageManager manager;
        PackageInfo info = null;
        manager = this.getPackageManager();
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        final int versionCode = info.versionCode;
        final String versionName = info.versionName;
        APPProperties bean = new APPProperties();
        bean.setAppVersionCode(versionCode);
        bean.setAppVersionName(versionName);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("current_app_version", versionCode);
        editor.putString("current_app_name", versionName);
        editor.commit();
        showProgressDialog("Checking Version...");
        RemoteCommonService.getInstance().checkVersion(
                new DataManagerInterface() {
                    @Override
                    public void getDataOnResult(ResultStatus status, List<?> list) {
                        dismissProgressDialog();
                        if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY
                                && list != null && list.size() > 0) {
                            APPProperties ap = (APPProperties) list.get(0);
                            int newCode = ap.getAppVersionCode();
                            String newName = ap.getAppVersionName();
                            String description = ap.getAppDescript();
                            String remotePath = ap.getDownloadaddress();
                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            //保存新版APP的信息到SharedPreferences中，
                            //目的是为了在【系统设置】-【更多设置】-【检查更新】时使用
                            if (newCode > versionCode) {
                                editor.putInt("new_app_version", newCode);
                                editor.putString("new_app_name", newName);
                                editor.putString("new_app_description",
                                        description);
                                editor.commit();

                                Intent intent = new Intent();
                                intent.putExtra("name", newName);
                                intent.putExtra("description", description);
                                intent.putExtra("remote_path", remotePath);
                                intent.setClass(LoginActivity.this,
                                        ApplicationUpdateActivity.class);
                                startActivity(intent);
                            } else {
                                editor.remove("new_app_version");
                                editor.remove("new_app_name");
                                editor.remove("new_app_description");
                                editor.commit();
                            }
                        }
                        
                        mLoginButton.setEnabled(true);
                    }
                }, bean);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {
                    0, 0
            };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
