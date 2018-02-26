
package com.pm360.cepm360.app.common.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ObsCache;
import com.pm360.cepm360.app.cache.TenantCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.view.DatePickText;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.system.RemoteUserService;

import java.util.List;

public class PersonalSettings extends ActionBarActivity {

    private User mUser;

    private EditText mLoginName;
    private EditText mName;
    private String mPwdText;
    private Button mPwdUpdate;
    private RadioGroup mSex;
    private DatePickText mBirth;
    private EditText mObsName;
    private EditText mCompanyName;

    private EditText mOfficePhone;
    private EditText mTel;
    private EditText mAddress;
    private EditText mMobile;
    private EditText mEmail;

    private Button mSave;
    private Dialog mPwdDialog;

    private EditText mPwd1, mPwd2;
    private Button mPwdOK;
    private TextView mPwdTitle;
    private ImageView mPwdCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMenuView().setVisibility(View.GONE);
        setContentView(R.layout.personal_settings_activity);
        mUser = UserCache.getCurrentUser();
        mLoginName = (EditText) findViewById(R.id.login_name);
        mName = (EditText) findViewById(R.id.name);
        mPwdUpdate = (Button) findViewById(R.id.pwd);
        mSex = (RadioGroup) findViewById(R.id.sex);
        mBirth = (DatePickText) findViewById(R.id.birth);
        mObsName = (EditText) findViewById(R.id.obs_name);
        mCompanyName = (EditText) findViewById(R.id.company_name);

        mOfficePhone = (EditText) findViewById(R.id.office_phone);
        mTel = (EditText) findViewById(R.id.tel);
        mAddress = (EditText) findViewById(R.id.address);
        mMobile = (EditText) findViewById(R.id.mobile);
        mEmail = (EditText) findViewById(R.id.email);
        mSave = (Button) findViewById(R.id.personal_settings_save);

        mLoginName.setText(mUser.getLogin_name());
        mLoginName.setEnabled(false);
        mName.setText(mUser.getName());
        mPwdText = mUser.getPwd();
		if (mUser.getSex() != null) {
			mSex.check(mUser.getSex().equals(getString(R.string.male)) ? R.id.male
					: R.id.female);
		}
        mBirth.setDate(mUser.getBirth());
        if (mUser.getObs_id() > 0) {
            mObsName.setText(ObsCache.getObsIdMaps()
                    .get(String.valueOf(mUser.getObs_id()))
                    + "");
            mCompanyName.setText(TenantCache.getTenantName(mUser.getTenant_id()+""));
        }

        mOfficePhone.setText(mUser.getOffice_phone());
        mTel.setText(mUser.getTel());
        mAddress.setText(mUser.getAddress());
        mMobile.setText(mUser.getMobile());
        mEmail.setText(mUser.getEmail());

        mPwdDialog = new Dialog(this, R.style.MyDialogStyle);
        mPwdDialog.setContentView(R.layout.password_update_dialog);
        mPwdDialog.setCanceledOnTouchOutside(false);

        mPwd1 = (EditText) mPwdDialog.findViewById(R.id.pwd1);
        mPwd2 = (EditText) mPwdDialog.findViewById(R.id.pwd2);

        mPwdOK = (Button) mPwdDialog.findViewById(R.id.ok);
        mPwdTitle = (TextView) mPwdDialog.findViewById(R.id.edit_title);
        mPwdTitle.setText(getString(R.string.pwd_update_title));
        mPwdCancel = (ImageView) mPwdDialog.findViewById(R.id.btn_close);

        View.OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ok:
                        if (validateNewPwd()) {
                            mPwdText = mPwd1.getText().toString();
                            mPwdDialog.dismiss();
                        }
                        break;
                    case R.id.btn_close:
                        mPwdDialog.dismiss();
                        break;
                }

            }
        };
        mPwdOK.setOnClickListener(listener);
        mPwdCancel.setOnClickListener(listener);

        mPwdUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mPwdDialog.show();
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final User user = (User) mUser.clone();
                user.setPwd(mPwdText);
                user.setSex(mSex.getCheckedRadioButtonId() == R.id.male ? "男" : "女");
                user.setBirth(mBirth.getDate());
                user.setOffice_phone(mOfficePhone.getText().toString());
                user.setTel(mTel.getText().toString());
                user.setAddress(mAddress.getText().toString());
                user.setMobile(mMobile.getText().toString());
                user.setEmail(mEmail.getText().toString());
                showProgressDialog("update User");
                RemoteUserService.getInstance().updateUser(new DataManagerInterface() {

                    @Override
                    public void getDataOnResult(ResultStatus status, List<?> list) {
                        dismissProgressDialog();
                        if (status.getCode() != AnalysisManager.SUCCESS_DB_QUERY
                                && status.getMessage() != null
                                && !status.getMessage().equals("")) {
                            UtilTools.showToast(PersonalSettings.this, status.getMessage());
                        }

                        if (status.getCode() == AnalysisManager.SUCCESS_DB_UPDATE) {
                            mUser = user;
                            UserCache.setCurrentUser(mUser);
                        }
                    }
                }, user);
            }
        });
        createProgressDialog(true, true);
    }

    private boolean validateNewPwd() {
        String pwd1 = mPwd1.getText().toString();
        String pwd2 = mPwd2.getText().toString();
        if (pwd1.equals("") || pwd2.equals("")) {
            UtilTools.showToast(this, R.string.update_password_err1);
            return false;
        }
        if (!pwd1.equals(pwd2)) {
            UtilTools.showToast(this, R.string.update_password_err2);
            return false;
        }
        return true;
    }

}
