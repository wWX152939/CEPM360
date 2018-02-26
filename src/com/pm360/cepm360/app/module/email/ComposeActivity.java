package com.pm360.cepm360.app.module.email;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.ContactCache;
import com.pm360.cepm360.app.cache.TenantCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.activity.ListSelectActivity;
import com.pm360.cepm360.app.common.adpater.DataListAdapter;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ListAdapterInterface;
import com.pm360.cepm360.app.common.adpater.DataListAdapter.ViewHolder;
import com.pm360.cepm360.app.common.custinterface.CallBack;
import com.pm360.cepm360.app.common.custinterface.SimplePageChangeListenerInterface;
import com.pm360.cepm360.app.common.fileexplorer.FileExplorerActivity;
import com.pm360.cepm360.app.common.fileexplorer.Util;
import com.pm360.cepm360.app.common.view.CustomTranslateAnimation;
import com.pm360.cepm360.app.common.view.ViewContainer;
import com.pm360.cepm360.app.common.view.parent.BaseViewPager;
import com.pm360.cepm360.app.module.common.attachment.UploadAttachManager;
import com.pm360.cepm360.app.module.contract.ContractChangeAttributeActivity;
import com.pm360.cepm360.app.module.settings.UserCooperationFragment;
import com.pm360.cepm360.app.module.settings.UserSelectFragment;
import com.pm360.cepm360.app.module.template.DocTemplateFragment;
import com.pm360.cepm360.app.utils.FileIconHelper;
import com.pm360.cepm360.app.utils.IntentBuilder;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.DateUtils;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.entity.Contract_change;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.MailBox;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.mail.RemoteMailService;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComposeActivity extends FragmentActivity {
	
	// Intent数据存取key定义
	public static final String MAILBOX_KEY = "mailboxKey";
    
	public static final int BASE_POSITION = 0;
	
	public static final int INBOX = BASE_POSITION;
	public static final int OUTBOX = BASE_POSITION + 1;
	public static final int DRAFTBOX = BASE_POSITION + 2;
	
	public static final int OPERATION_NEW = BASE_POSITION ;
	public static final int OPERATION_EDIT = BASE_POSITION + 1;
    public static final int OPERATION_REPLY = BASE_POSITION + 2;
    public static final int OPERATION_REPLY_ALL = BASE_POSITION + 3;
    public static final int OPERATION_FORWORD = BASE_POSITION + 4;
    
    public static final String EMAIL_MAILBOX_KEY = "sender_type";
    public static final String EMAIL_OPERATION_KEY = "operation";
    public static final String EMAIL_OBJECT_BEAN_KEY = "objectbean";
    public static final String EMAIL_PROJECT_KEY = "project";
    public static final String EMAIL_WRITE_TO_KEY = "write_to_key";
    public static final String EMAIL_ATTACHMENT_KEY = "attachment";
    public static final String EMAIL_CONTRACT_CHANGE_KEY = "contractChange";
    
    // 更新邮箱ACTION
    public static final String REFRESH_INBOX 
    			= "com.pm360.cepm360.app.module.email.ComposeActivity.refreshInbox";
    public static final String REFRESH_OUTBOX 
				= "com.pm360.cepm360.app.module.email.ComposeActivity.refreshOutbox";
    public static final String REFRESH_DRAFTBOX 
				= "com.pm360.cepm360.app.module.email.ComposeActivity.refreshDraftbox";
    
    // 模板目录
    public static String TEMPLATE_DIR = FileUtils.SDPATH + "CEPM360/temp/";    
    // 模板缓存目录
    public static String TEMPLATE_CACHE_DIR = FileUtils.SDPATH + "CEPM360/temp/cache/";
    
	protected static final int SHOW_TOAST = BASE_POSITION;
	protected static final int SHOW_PROGRESS_DIALOG = BASE_POSITION + 1;
	protected static final int DISMISS_PROGRESS_DIALOG = BASE_POSITION + 2;
    
    private static final String EMAIL_INTERVAL_STRING = "-------------------"
    		+ "--------------------------------------------------------------"
    		+ "--------------------------------\n"; 
    private static final String NEW_LINE = "\n"; 
    
    private static final int SELECT_TEMPLATE_CODE = 1;
    private static final int FILE_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
        
    private ArrayList<User> mRecipientLists = new ArrayList<User>();
    private ArrayList<User> mCcLists = new ArrayList<User>();
    private ArrayList<String> mAttachmentLists = new ArrayList<String>();
    private ArrayList<Files> mAttachments = new ArrayList<Files>();
    private Contract_change mContractChange;
    
    private BaseViewPager mViewPager;
    
    private int mMailBoxType;
    private int mOperationType;
    private MailBox mCurrentBean; // 邮件对象
    private List<User> mWriteToUsers;
    
    private TextView mTitle; // 界面标题
    private LinearLayout mTitlelLayout; // 界面标题
    private EditText mSubjectEdit; //主题编辑控件
    private Button mSaveButton;
    private Button mSendButton;
    private ImageView mCloseButton;
    private Dialog mPickDialog;
    private Dialog mRenameDialog;
    private String mCurrentOldName;
    private TextView mCurrentAttamentView;
    private Uri mImageFileUri;
    
    private File mCurrentFile;
    
    private ImageView mAddRecipient;
    private ImageView mAddCc;
    private ImageView mAddAttachment;
    
    private ViewGroup mEmailWriteContent;
    
    private LinearLayout mHeaderLayout;  
    private ViewContainer mRecipientLayout;
    private ViewContainer mCcLayout;
    private ScrollView mEmailMainView;
    private ViewGroup mContactLayout;
    private LinearLayout mEmailButtonBarLayout;
    private LinearLayout mContactButtonBarLayout;
    
    private EditText mEmailBodyContent;
    
    private ViewGroup mRecipientInput;
    private ViewGroup mCcInput;
    
    private EditText mRecipientEditText;
    private EditText mCcEditText;
    
    private ListView mContactSelectListView;
    private ViewGroup mContactSelectGroup;
    private DataListAdapter<User> mUserAdapter;
    private PopupWindow mPopupWindow;
    
    // 添加接收人还是抄送人
    private boolean mIsAddCc;
    private boolean mIsSave;
    private boolean mIsSendCmd;
    private boolean mInputAddContact;
    private boolean mIsTemplateAttachmentAdd;
    private boolean mIsAddFirst = true;
    
    // 数据是否加载成功
 	protected boolean mDataLoaded;
    private Map<TextView, User> mViewUserMap = new HashMap<TextView, User>();
    private TextView mCurrentContacTextView;
    
    // 进度对话框
 	private ProgressDialog mProgressDialog;
    
    private int mUploadRemain;
    private int mUploadFailedCount;
    
    private RemoteMailService mMailService = RemoteMailService.getInstance();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.email_compose_activity);
        
        // 初始化基本布局
        initBasicLayout();
        
        // 获取Intent参数
        getArgument();
        
        initContactSelectLayout();
        initPopWindow();
        
        initRecipientLayout();
        initCcLayout();
        
        initSubjectLayout();
        initPickDialog();
        initRenameDialog();
        initContactLayout();
        
        // 初始化进度对话框
        createProgressDialog();
        
        // 初始化控件内容
        initData();
    }
    
    /**
     * 初始化基本布局
     */
	private void initBasicLayout() {
    	mTitle = (TextView) findViewById(R.id.title);
    	mTitlelLayout = (LinearLayout) findViewById(R.id.title_layout);
        mSaveButton = (Button) findViewById(R.id.save);
        mSendButton = (Button) findViewById(R.id.send);
        mCloseButton = (ImageView) findViewById(R.id.btn_close);
        mSaveButton.setOnClickListener(mButtonBarClickListener);
        mSendButton.setOnClickListener(mButtonBarClickListener);
        mCloseButton.setOnClickListener(mButtonBarClickListener);
        
        mEmailWriteContent = (ViewGroup) findViewById(R.id.email_write_content);
        mEmailWriteContent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {  
		    	    case R.id.email_write_content:  
		    	    	InputMethodManager imm = (InputMethodManager)  
		    	         				getSystemService(Context.INPUT_METHOD_SERVICE);  
		    	    	imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
		    	        break;  
				}
			}
		});
        mHeaderLayout = (LinearLayout) findViewById(R.id.header_layout);
    }
	
    /**
     * 获取Intent参数
     */
    @SuppressWarnings("unchecked")
	private void getArgument() {
    	Intent intent = getIntent();
    	Bundle bundle = intent.getExtras();
    	
    	// 获取邮箱类型
    	if (bundle.containsKey(EMAIL_MAILBOX_KEY)) {
    		mMailBoxType = bundle.getInt(EMAIL_MAILBOX_KEY, INBOX);
    	}
    	
    	// 获取操作类型
    	if (bundle.containsKey(EMAIL_OPERATION_KEY)) {
    		mOperationType = bundle.getInt(EMAIL_OPERATION_KEY, OPERATION_NEW);
    	}
    	
    	// 获取操作数据
    	if (bundle.containsKey(EMAIL_OBJECT_BEAN_KEY)) {
    		mCurrentBean = (MailBox) bundle.getSerializable(EMAIL_OBJECT_BEAN_KEY);
    	}
    	
    	// 获取直接接收人
    	if (bundle.containsKey(EMAIL_WRITE_TO_KEY)) {
    		mWriteToUsers = (List<User>) bundle.getSerializable(EMAIL_WRITE_TO_KEY);
    	}
    	
    	// 获取附件列表数据
    	if (bundle.containsKey(EMAIL_ATTACHMENT_KEY)) {
    		mAttachments = (ArrayList<Files>) bundle.getSerializable(EMAIL_ATTACHMENT_KEY);
    	}
    	
    	// 获取合同变更对象
    	if (bundle.containsKey(EMAIL_CONTRACT_CHANGE_KEY)) {
    		mContractChange = (Contract_change) bundle
    						.getSerializable(EMAIL_CONTRACT_CHANGE_KEY);
    		
    		// 从变更启动，不能保存为草稿箱
    		mSaveButton.setVisibility(View.GONE);
    	}
    }
    
    /**
     * 初始化联系人选择布局
     */
    private void initContactSelectLayout() {
    	mContactSelectGroup = (ViewGroup) getLayoutInflater()
						.inflate(R.layout.listview_layout, null, false);
    	
    	mContactSelectListView = (ListView) 
    			mContactSelectGroup.findViewById(R.id.list_view);
    	mUserAdapter = new DataListAdapter<User>(this, false, mUserInterface);
    	mContactSelectListView.setAdapter(mUserAdapter);
    }
    
    /**
     * 用户列表接口实现
     */
    ListAdapterInterface<User> mUserInterface = new ListAdapterInterface<User>() {

		@Override
		public int getLayoutId() {
			return R.layout.contact_list_item;
		}

		@Override
		public View getHeaderView() {
			return null;
		}

		@Override
		public void regesterListeners(ViewHolder viewHolder, final int position) {
			for (int i = 0; i < 3; i++) {
				viewHolder.tvs[i].setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mInputAddContact = true;
						addContacts(mUserAdapter.getItem(position), mIsAddCc);
						
						if (mIsAddCc) {
							mCcEditText.setText("");
						} else {
							mRecipientEditText.setText("");
						}
						
						mPopupWindow.dismiss();
					}
				});
			}
		}

		@Override
		public void initListViewItem(View convertView, ViewHolder holder,
									DataListAdapter<User> adapter, int position) {
			User user = adapter.getItem(position);
			
			holder.tvs[0].setText(user.getName());
			holder.tvs[1].setText(user.getMobile());
			holder.tvs[2].setText(TenantCache
					.getTenantName(String.valueOf(user.getTenant_id())));
		}

		@Override
		public void initLayout(View convertView, ViewHolder holder) {
			holder.tvs = new TextView[3];
			
			holder.tvs[0] = (TextView) convertView.findViewById(R.id.contact_name);
			holder.tvs[1] = (TextView) convertView.findViewById(R.id.contact_phone);
			holder.tvs[2] = (TextView) convertView.findViewById(R.id.contact_company_department);
		}

		@Override
		public List<User> findByCondition(Object... condition) {
			String name = (String) condition[BASE_POSITION];
			List<User> filterUsers = new ArrayList<User>();
			
			for (User user : UserCache.getUserLists()) {
				if (user.getName().contains(name)) {
					filterUsers.add(user);
				}
			}
			
			return filterUsers;
		}

		@Override
		public boolean isSameObject(User t1, User t2) {
			return t1.getUser_id() == t2.getUser_id();
		}
	};
    
    /**
     * 初始化弹出窗口
     */
    @SuppressWarnings("deprecation")
	private void initPopWindow() {
    	mPopupWindow = new PopupWindow(mContactSelectGroup, 300, 240, true);
    	mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    /**
     * 初始化接收人布局
     */
    private void initRecipientLayout() {
        RelativeLayout parent = (RelativeLayout) findViewById(R.id.recipients_layout);
        ((TextView) parent.findViewById(R.id.label)).setText(getString(R.string.email_send_to));
        mRecipientLayout = (ViewContainer) parent.findViewById(R.id.contacts_layout);
        mRecipientLayout.setChildSize(80, 28);
        
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
        				LayoutParams.MATCH_PARENT);
        mRecipientInput = buildContactInputView(false);
        mRecipientLayout.addView(mRecipientInput, params);
        
        mAddRecipient = (ImageView) parent.findViewById(R.id.add_contacts);
        mAddRecipient.setTag(0);
        mAddRecipient.setOnClickListener(mAddContactListener);
    }
    
    /**
     * 初始化抄送人布局
     */
    private void initCcLayout() {
        RelativeLayout parent = (RelativeLayout) findViewById(R.id.cc_layout);
        ((TextView) parent.findViewById(R.id.label)).setText(getString(R.string.email_send_cc));
        mCcLayout = (ViewContainer) parent.findViewById(R.id.contacts_layout);
        mCcLayout.setChildSize(80, 28);
        
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
        mCcInput = buildContactInputView(true);
        mCcLayout.addView(mCcInput, params);
        
        mAddCc = (ImageView) parent.findViewById(R.id.add_contacts);
        mAddCc.setTag(1);
        mAddCc.setOnClickListener(mAddContactListener);
    }
    
    /**
     * 添加自动补全
     */
	private ViewGroup buildContactInputView(final boolean isAddCc) {
    	ViewGroup parent = (ViewGroup) getLayoutInflater()
    					.inflate(R.layout.contact_input_layout, null, false);
    	
    	final EditText editText = (EditText) parent.findViewById(R.id.contact_input);
        
        // 设置文本内容变化监听器
    	editText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				mIsAddCc = isAddCc;
				
				if (s.toString().equals("")) {
					if (mPopupWindow.isShowing()) {
						mPopupWindow.dismiss();
					}
					
					editText.setTextColor(getResources().getColor(R.color.black));
				} else {
					List<User> users = mUserInterface.findByCondition(s.toString());
					if (users.size() > 0) {
						if (!mPopupWindow.isShowing()) {
							mPopupWindow.showAsDropDown(editText, 0, 10);
						}
						
						editText.setTextColor(getResources().getColor(R.color.black));
					} else {
						if (mPopupWindow.isShowing()) {
							mPopupWindow.dismiss();
						}
						editText.setTextColor(getResources().getColor(R.color.red));
					}
					
					mUserAdapter.setShowDataList(users);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				mPopupWindow.setFocusable(false);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				mPopupWindow.setFocusable(true);
			}
		});
    	
    	editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				mIsAddCc = isAddCc;
				
				String name = ((TextView) v).getText().toString();
				if (hasFocus && !name.equals("")) {
					List<User> users = mUserInterface.findByCondition(name);
					if (users.size() > 0) {
						if (!mPopupWindow.isShowing()) {
							mPopupWindow.showAsDropDown(editText, 0, 10);
						}
						
						editText.setTextColor(getResources().getColor(R.color.black));
					} else {
						if (mPopupWindow.isShowing()) {
							mPopupWindow.dismiss();
						}
						editText.setTextColor(getResources().getColor(R.color.red));
					}
					
					mUserAdapter.setShowDataList(users);
				} else {
					if (mPopupWindow.isShowing()) {
						mPopupWindow.dismiss();
					}
				}
			}
		});
    	
    	editText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mIsAddCc = isAddCc;
				
				String name = ((TextView) v).getText().toString();
				if (name.equals("")) {
					if (mPopupWindow.isShowing()) {
						mPopupWindow.dismiss();
					}
				} else {
					List<User> users = mUserInterface.findByCondition(name);
					if (users.size() > 0) {
						if (!mPopupWindow.isShowing()) {
							mPopupWindow.showAsDropDown(editText, 0, 10);
						}
						
						editText.setTextColor(getResources().getColor(R.color.black));
					} else {
						if (mPopupWindow.isShowing()) {
							mPopupWindow.dismiss();
						}
						editText.setTextColor(getResources().getColor(R.color.red));
					}
					
					mUserAdapter.setShowDataList(users);
				}
			}
		});
    	
    	if (isAddCc) {
    		mCcEditText = editText;
    	} else {
    		mRecipientEditText = editText;
    	}
        
        return parent;
    }
    
	/**
	 * 重写返回键
	 */
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if (keyCode == KeyEvent.KEYCODE_DEL
        				&& event.getRepeatCount() == 0) {
        	if (mCcEditText.isFocused()
        				&& mCcEditText.getText().toString().equals("")) {
        		if (mCcLayout.getChildCount() > 1) {
	        		TextView child = (TextView)
	        				mCcLayout.getChildAt(mCcLayout.getChildCount() - 2);
	        		child.performClick();
        		}
        		
        		return true;
        	} else if (mRecipientEditText.isFocused() 
        				&& mRecipientEditText.getText().toString().equals("")) {
        		if (mRecipientLayout.getChildCount() > 1) {
	        		TextView child = (TextView)
	        				mRecipientLayout.getChildAt(mRecipientLayout.getChildCount() - 2);
	        		child.performClick();
        		}
        		
        		return true;
        	}
        }
        
        return super.onKeyDown(keyCode, event);
    }
	
    /**
     * 初始化主题
     */
    private void initSubjectLayout() {
        RelativeLayout parent = (RelativeLayout) findViewById(R.id.subject_layout);
        mSubjectEdit = (EditText) parent.findViewById(R.id.subject);
        
        mAddAttachment = (ImageView) parent.findViewById(R.id.add_attachments);
        mAddAttachment.setTag(2);
        mAddAttachment.setOnClickListener(mAddContactListener);
    }

    /**
     * 初始化数据
     */
    private void initData() {
    	switch (mOperationType) {
	    	case OPERATION_EDIT:
				editEmail();
				break;
			case OPERATION_NEW:
				writeEmail();
				break;
			case OPERATION_REPLY:
	            replyEmail();
				break;
			case OPERATION_REPLY_ALL:
	            replyAllEmail();
				break;
			case OPERATION_FORWORD:
	            forwordEmail();
				break;
			default:
				break;
    	}
    }
    
    
    /**
     * 编辑邮件
     */
    private void editEmail() {
    	
    	// 设置标题
    	mTitle.setText(getString(R.string.email_operation_edit));
    	
    	List<MailBox> mailBoxs = new ArrayList<MailBox>();
    	mailBoxs.add(mCurrentBean);
    	ContactCache.load(MailBoxUtils.getAllContacts(mailBoxs), new CallBack<Void, Integer>() {
			
			@Override
			public Void callBack(Integer a) {
				
				// 添加接收人
		    	List<String> recievers = MailBoxUtils.getRecieverIds(mCurrentBean, false);
				mInputAddContact = false;
		    	for (String reciever : recievers) {
		    		User user = ContactCache.getContact(Integer.valueOf(reciever));
		    		addContacts(user, false);
		    	}
		    	
		    	// 添加抄送人
		    	recievers = MailBoxUtils.getRecieverIds(mCurrentBean, true);

		    	for (String reciever : recievers) {
		    		User user = ContactCache.getContact(Integer.valueOf(reciever));
		    		addContacts(user, true);
		    	}
				return null;
			}
		});
    	
    	// 设置主题
    	mSubjectEdit.setText(mCurrentBean.getTitle());
    	
    	// 添加附件
    	if (mAttachments != null) {
	    	for (Files files : mAttachments) {
	    		insertAttachment(files);
	    	}
    	}
    	
    	// 设置邮件内容
    	mEmailBodyContent.setText(mCurrentBean.getContent());
    }
    
    /**
     * 写邮件
     */
    private void writeEmail() {
    	
    	// 设置邮件标题
    	mTitle.setText(getString(R.string.email_operation_write));
    	
    	if (mWriteToUsers != null && !mWriteToUsers.isEmpty()) {
    		for (int i = 0; i < mWriteToUsers.size(); i++) {
    			addContacts(mWriteToUsers.get(i), false);
    		}
    	}
    	
    	// 插入合同变更
    	if (mContractChange != null) {
    		insertContractChange();
    	}
    }
    
    /**
     * 回复邮件
     */
    private void replyEmail() {
    	
    	// 邮件正文获取焦点
    	mEmailBodyContent.requestFocus();
    	
    	// 设置标题
    	String title = getString(R.string.email_reply) + mCurrentBean.getTitle();
    	// 设置标题
    	mTitle.setText(getString(R.string.email_operation_reply));
    	
    	// 添加发件人
    	User recieverUser = ContactCache.getContact(mCurrentBean.getSender());
    	mInputAddContact = false;
    	addContacts(recieverUser, false);
    	
    	mSubjectEdit.setText(title);
    	
    	mEmailBodyContent.setText(NEW_LINE + buildEmailHeader(mCurrentBean)
    			+ mCurrentBean.getContent());
    	mEmailBodyContent.setSelection(0);
    }
    
    /**
     * 回复所有
     */
    private void replyAllEmail() {
    	
    	// 邮件正文获取焦点
    	mEmailBodyContent.requestFocus();
    	
    	// 设置标题
    	mTitle.setText(getString(R.string.email_operation_reply_all));
    	
    	// 非手动输入添加联系人
    	mInputAddContact = false;
    	
    	// 添加发件人
    	User recieverUser = ContactCache.getContact(mCurrentBean.getSender());
    	addContacts(recieverUser, false);
    	
    	// 添加接收人
    	List<String> recievers = MailBoxUtils.getRecieverIds(mCurrentBean, false);
    	for (String reciever : recievers) {
    		User user = ContactCache.getContact(Integer.valueOf(reciever));
    		addContacts(user, false);
    	}
    	
    	// 添加抄送人
    	recievers = MailBoxUtils.getRecieverIds(mCurrentBean, true);
    	for (String reciever : recievers) {
    		User user = ContactCache.getContact(Integer.valueOf(reciever));
    		addContacts(user, true);
    	}
    	
    	// 设置主题
    	mSubjectEdit.setText(getString(R.string.email_reply)
				+ mCurrentBean.getTitle());
    	
    	// 设置邮件内容
    	mEmailBodyContent.setText(NEW_LINE + buildEmailHeader(mCurrentBean)
    			+ mCurrentBean.getContent());
    	mEmailBodyContent.setSelection(0);
    }
    
    /**
     * 转发邮件
     */
    private void forwordEmail() {
    	
    	// 邮件正文获取焦点
    	mEmailBodyContent.requestFocus();
    	
    	// 设置标题
    	String title = getString(R.string.email_forword) + mCurrentBean.getTitle();
    	mTitle.setText(getString(R.string.email_operation_forward));
    	
    	// 设置标题
    	mSubjectEdit.setText(title);
    	
    	// 添加附件
    	if (mAttachments != null) {
	    	for (Files files : mAttachments) {
	    		insertAttachment(files);
	    	}
    	}
    	
    	// 设置邮件内容
    	mEmailBodyContent.setText(NEW_LINE + buildEmailHeader(mCurrentBean)
    			+ mCurrentBean.getContent());
    	mEmailBodyContent.setSelection(0);
    }
    
    /**
     * 构造邮件头字符串
     * @param mailBox
     * @return
     */
    private String buildEmailHeader(MailBox mailBox) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(EMAIL_INTERVAL_STRING);
    	
    	// 发件人
    	sb.append("| " + getString(R.string.email_sender) + ": ");
    	User sender = ContactCache.getContact(mailBox.getSender());
    	if (sender != null) {
    		sb.append(sender.getName() + NEW_LINE);
    	}
    	
    	// 发送时间
    	sb.append("| " + getString(R.string.email_send_time) + " ");
    	sb.append(DateUtils.dateToString(DateUtils.FORMAT_LONG,
    						mailBox.getMail_time()) + NEW_LINE);
    	
    	// 收件人
    	sb.append("| " + getString(R.string.email_reciever) + ": ");
    	List<String> recievers = MailBoxUtils.getRecieverIds(mailBox, false);
    	for (String reciever : recievers) {
    		User user = ContactCache.getContact(Integer.valueOf(reciever));
    		if (user != null) {
    			sb.append(user.getName() + "; ");
    		}
    	}
    	sb.setCharAt(sb.length()-1, '\n');
    	
    	// 抄送人
    	sb.append("| " + getString(R.string.email_cc) + " ");
    	recievers = MailBoxUtils.getRecieverIds(mailBox, true);
    	for (String reciever : recievers) {
    		User user = ContactCache.getContact(Integer.valueOf(reciever));
    		if (user != null) {
    			sb.append(user.getName() + "; ");
    		}
    	}
    	sb.setCharAt(sb.length() - 1, '\n');
    	
    	// 主题
    	sb.append("| " + getString(R.string.email_title) + ": ");
    	sb.append(mailBox.getTitle() + NEW_LINE);
    	sb.append(EMAIL_INTERVAL_STRING);
    	
    	return sb.toString();
    }
    
    /**
     * 插入合同变更
     */
    private void insertContractChange() {
		final View contractChangeView = getLayoutInflater()
						.inflate(R.layout.email_attachment_layout, null);
		TextView fileName = (TextView) contractChangeView.findViewById(R.id.file_name);
		TextView fileSize = (TextView) contractChangeView.findViewById(R.id.file_size);
		ImageView fileType = (ImageView) contractChangeView.findViewById(R.id.thumbnail);
		ImageView romoveIcon = (ImageView) contractChangeView
						.findViewById(R.id.remove_attachment);

		/**
		 * 打开附件监听
		 */
		OnClickListener openContractChangeListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ComposeActivity.this, 
							ContractChangeAttributeActivity.class);
				intent.putExtra(ContractChangeAttributeActivity.CONTRACT_CHANGE_KEY, 
							mContractChange);
				intent.putExtra(ContractChangeAttributeActivity.IS_MODIFY_KEY, false);
				intent.putExtra(ContractChangeAttributeActivity.IS_CHANGE_APPLY_KEY, true);
				intent.putExtra(ContractChangeAttributeActivity.EMAIL_INTER_KEY, true);
				startActivity(intent);
			}
		};

		fileType.setOnClickListener(openContractChangeListener);
		fileName.setOnClickListener(openContractChangeListener);
		fileSize.setOnClickListener(openContractChangeListener);

		fileType.setImageDrawable(getFileType(mContractChange.getName()));
		fileName.setText(getString(R.string.contract_change_table) + "-" + mContractChange.getName());
		fileSize.setText("");

		romoveIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				mHeaderLayout.removeView(contractChangeView);
				mContractChange = null;
			}
		});

		mHeaderLayout.addView(contractChangeView);
    }
    
    /**
     * 插入附件
     * @param path
     */
    private void insertAttachment(final String path) {
    	// 检查是否已经添加过该附件
    	for (String filePath : mAttachmentLists) {
    		if (filePath.equals(path)) {
    			Log.v("cccc","already Add ?? filePath:"+filePath+", path:"+path);
    			sendMessage(SHOW_TOAST, 
    					getString(R.string.email_attachment_add_repeat));
    			return ;
    		}
    	}
    	
        final View attachmentView = getLayoutInflater().inflate(
                				R.layout.email_attachment_layout, null);
        TextView fileName= (TextView) attachmentView.findViewById(R.id.file_name);
        TextView fileSize= (TextView) attachmentView.findViewById(R.id.file_size);
        ImageView fileType = (ImageView) attachmentView.findViewById(R.id.thumbnail);
        ImageView romoveIcon = (ImageView) attachmentView.findViewById(R.id.remove_attachment);
        
        /**
         * 打开附件监听
         */
        OnClickListener openFileListener = new OnClickListener() {
    		
    		@Override
    		public void onClick(View v) {
    			String name = ((TextView)v).getText().toString();
    			for (int i = 0; i < mAttachmentLists.size(); i++) {
    				if (name.equals(FileUtils.getFileFullName(mAttachmentLists.get(i)))) {
    	    			Log.v("cccc","i:"+i+", mAttachments:"+mAttachments+", mAttachmentLists:"+mAttachmentLists+", index0 getFileFullName="+FileUtils.getFileFullName(mAttachmentLists.get(0)));    					
    					IntentBuilder.viewFile(ComposeActivity.this, mAttachmentLists.get(i));
    				}
    			}		
    		}
    	};
    	
    	OnLongClickListener renameFileListener = new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				mRenameDialog.show();
				String nameStr = ((TextView)arg0).getText().toString();
				EditText nameEt = (EditText)mRenameDialog.findViewById(R.id.new_name);
				mCurrentOldName = nameStr;
				nameEt.setText(nameStr);
				nameEt.setSelection(nameStr.length());
				nameEt.setSelectAllOnFocus(true);
				mCurrentAttamentView = (TextView)arg0;
				return true;
			}
		};
		fileName.setOnLongClickListener(renameFileListener);
        fileType.setOnClickListener(openFileListener);
        fileName.setOnClickListener(openFileListener);
        fileSize.setOnClickListener(openFileListener);
        
        String name = FileUtils.getFileFullName(path);
        fileName.setText(name);
        fileSize.setText("(" + FileUtils.getFileSize(path) + ")");
        fileType.setImageDrawable(getFileType(path));
        romoveIcon.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                mHeaderLayout.removeView(attachmentView);
                mAttachmentLists.remove(path);
            }
        });
        
        mHeaderLayout.addView(attachmentView);
        mAttachmentLists.add(path);

    }
    
    /**
     * 插入附件
     * @param files
     */
    private void insertAttachment(final Files files) {
    	
        final View attachmentView = getLayoutInflater().inflate(
                				R.layout.email_attachment_layout, null);
        TextView fileName= (TextView) attachmentView.findViewById(R.id.file_name);
        TextView fileSize= (TextView) attachmentView.findViewById(R.id.file_size);
        ImageView fileType = (ImageView) attachmentView.findViewById(R.id.thumbnail);
        ImageView romoveIcon = (ImageView) attachmentView.findViewById(R.id.remove_attachment);
        
        fileName.setText(files.getFile_name());
        fileSize.setText("(" + files.getFile_size() + ")");
        fileType.setImageDrawable(getFileType(files.getFile_name()));
        
        romoveIcon.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                mHeaderLayout.removeView(attachmentView);
                mAttachments.remove(files);
            }
        });
        
        mHeaderLayout.addView(attachmentView);
    }
    
    /**
     * 添加收件人监听
     */
    View.OnClickListener mAddContactListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
        	
        	// 先隐藏键盘
        	InputMethodManager imm = (InputMethodManager)  
     				getSystemService(Context.INPUT_METHOD_SERVICE);  
        	imm.hideSoftInputFromWindow(view.getWindowToken(), 0);  
        	
            switch ((Integer) view.getTag()) {
	            case 0:
	            	mIsAddCc = false;
	                showContactView(mRecipientLists);
	                break;
	            case 1:
	            	mIsAddCc = true;
	            	showContactView(mCcLists);
	                break;
	            case 2:
	                // 添加附件
	                mPickDialog.show();
	            	mIsAddFirst = true;
	                break;
            }
        }
    };
    
    /* 保存、发送、取消   响应处理   */
    View.OnClickListener mButtonBarClickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
	            case R.id.save:
	            	if (!mIsSendCmd) {
	            		mIsSave = true;
	            		save();
	            	} else {
	            		sendMessage(SHOW_TOAST, 
	            				getResources().getString(R.string.cannot_repeat_commit));
	            	}
	                break;
	            case R.id.send:
	            	if (!mIsSendCmd) {
		            	mIsSave = false;
		            	send(false);
		            } else {
		        		sendMessage(SHOW_TOAST, 
		        				getResources().getString(R.string.cannot_repeat_commit));
		        	}
	                break;
	            case R.id.cancel:
	                setResultForEmail(true);
	                break;
	            case R.id.btn_close:
	                setResultForEmail(true);
	                break;
	            case R.id.local_picture: // 本地文件
	                pickLocalFile();
	                mPickDialog.dismiss();
	                break;
	            case R.id.take_picture:  // 拍照
	                pickByCapture();
	                mPickDialog.dismiss();
	                break;
	            case R.id.edit_template:  // 编辑模板
	                editTemplate();
	                mPickDialog.dismiss();
	                break;
	            default:
	                break;
            }
            
        }
    };
    
    /**
     * 禁用功能按键
     */
    private void enableFunctionButton(boolean enable) {
    	if (enable) {
    		mIsSendCmd = false;
    		
    		mSaveButton.setTextColor(getResources().getColor(R.color.black));
        	mSendButton.setTextColor(getResources().getColor(R.color.black));
    	} else {
    		mIsSendCmd = true;
    		
    		mSaveButton.setTextColor(getResources().getColor(R.color.gray));
    		mSendButton.setTextColor(getResources().getColor(R.color.gray));
    	}
    }
    
    /**
     * 保存邮件
     */
    private void save() {
    	if (mMailBoxType == DRAFTBOX
    			&& mOperationType == OPERATION_EDIT) {
    		enableFunctionButton(false);
    		
    		draftBoxEditSave();
    	} else {
    		send(true);
    	}
    }
    
    /**
     * 检查邮件有效性
     */
    private boolean checkEmailValid() {
    	if (mRecipientLists.isEmpty() && mCcLists.isEmpty()) {
    		sendMessage(SHOW_TOAST, getString(R.string.email_no_receiver));
    		return false;
    	}
    	
    	if (mSubjectEdit.getText().toString().equals("")) {
    		sendMessage(SHOW_TOAST, getString(R.string.email_no_subject));
    		return false;
    	}
    	
    	return true;
    }
    
    /**
     * 草稿箱编辑保存，再次编辑可能会添加附件
     */
    private void draftBoxEditSave() {
    	sendMessage(SHOW_PROGRESS_DIALOG);
    	
    	if (mAttachmentLists.isEmpty()) {
    		update(true);
    	} else {
    		uploadAttachment(mAttachmentLists, true);
    	}
    }
    
    /**
     * 上传邮件附件
     */
    private void send(boolean isSaveStatus) {
    	if (checkEmailValid()) {
    		enableFunctionButton(false);
    		
	    	sendMessage(SHOW_PROGRESS_DIALOG);
	    	
	    	if (mAttachmentLists.isEmpty()) {
	    		if (mMailBoxType == DRAFTBOX 
	    				&& mOperationType == OPERATION_EDIT) {
	    			update(isSaveStatus);
	    		} else {
	    			add(isSaveStatus);
	    		}
	    	} else {
		    	uploadAttachment(mAttachmentLists, isSaveStatus);
	    	}
    	}
    }
    
    /**
     * 发送邮件
     */
    private void add(boolean isSaveStatus) {
    	
    	// 构建邮件对象
    	buildMailBox();
    	
    	// 请求服务
    	if (isSaveStatus) {
    		mMailService.saveSendMail(mMailManager, mCurrentBean);
    	} else {
    		mMailService.sendMail(mMailManager, mCurrentBean);
    	}
    }
    
    /**
	 * 上传附件
	 * @param attachments
	 */
	private void uploadAttachment(List<String> attachments, boolean isSaveStatus) {
		mUploadRemain = mAttachmentLists.size();
		
		UploadAttachManager manager;
		if (isSaveStatus) {
			manager = new UploadAttachManager(this, mSaveDocumentManager, null);
		} else {
			manager = new UploadAttachManager(this, mDocumentManager, null);
		}
					
		for (String attachmentPath : mAttachmentLists) {
			manager.uploadDocument(attachmentPath,
						Integer.parseInt(GLOBAL.FILE_TYPE[17][0]), 0, 0);
		}
	}

	/**
     * 更新
     */
    private void update(boolean isSaveStatus) {
    	// 构建邮件对象
    	buildMailBox();
    	
    	// 请求服务
    	if (isSaveStatus) {
    		mMailService.updateSendMailByDraft(mMailManager, mCurrentBean);
    	} else {
    		mMailService.sendMailByDraft(mMailManager, mCurrentBean);
    	}
    }
    
    /**
     * 构建邮件对象
     */
    private void buildMailBox() {
    	if (mCurrentBean == null) {
    		mCurrentBean = new MailBox();
    	}
    	
    	mCurrentBean.setAttachment(MailBoxUtils.buildAttachmentIds(mAttachments));
    	mCurrentBean.setContent(mEmailBodyContent.getText().toString());
    	mCurrentBean.setDel_in(MailBoxUtils.buildDelIn(mRecipientLists, mCcLists));
    	mCurrentBean.setDel_out(GLOBAL.MSG_DEL[0][0]);
    	mCurrentBean.setIs_read(MailBoxUtils.buildRead(mRecipientLists, mCcLists));
    	mCurrentBean.setReceive_type(MailBoxUtils.buildReceiverType(mRecipientLists, mCcLists));
    	mCurrentBean.setReceiver(MailBoxUtils.buildReceiver(mRecipientLists, mCcLists));
    	mCurrentBean.setSender(UserCache.getCurrentUser().getUser_id());
    	mCurrentBean.setIs_send(mIsSave ? GLOBAL.MSG_SENT[0][0] : GLOBAL.MSG_SENT[1][0]);
    	mCurrentBean.setTitle(mSubjectEdit.getText().toString());
    	mCurrentBean.setMail_time(new Date());
    	
    	// 添加合同变更
    	if (mContractChange != null) {
    		mCurrentBean.setMail_table_type(Integer
    					.parseInt(GLOBAL.MAIL_TABLE_TYPE[0][0]));
    		mCurrentBean.setMail_table_id(mContractChange.getId());
    	}
    }
    
    /**
	 * 消息处理
	 */
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SHOW_TOAST:
	                Toast toast = Toast.makeText( getApplication(),
			                		(CharSequence) msg.obj, Toast.LENGTH_SHORT);
	                toast.setGravity(Gravity.CENTER, 0, 0);
	                toast.show();
					break;
				case SHOW_PROGRESS_DIALOG:
					if (!mDataLoaded && !mProgressDialog.isShowing()) {
						mProgressDialog.show();
						mProgressDialog.setContentView(R.layout.layout_progress);
					}
					break;
				case DISMISS_PROGRESS_DIALOG:
					if (mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
						mDataLoaded = false;
					}
					break;
			}
		}
	};
	
	/**
	 * 发送消息，本地处理
	 * @param what
	 * @param object
	 */
	protected void sendMessage(int what, Object object) {
		if (object != null) {
			// 创建初始化Message
			Message msg = Message.obtain();
			msg.what = what;
			msg.obj = object;
			
			// 发送消息到mHandler
			mHandler.sendMessage(msg);
		} else {
			mHandler.sendEmptyMessage(what);
		}
	}
	
	/**
	 * 发送消息，本地处理
	 * @param what
	 */
	protected void sendMessage(int what) {
		sendMessage(what, null);
	}
	
	/**
	 * 初始化进度对话框
	 */
	private void createProgressDialog() {
		mProgressDialog = new ProgressDialog(this);
		
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(true);
	}
	
	/**
     * 附件服务
     */
    DataManagerInterface mSaveDocumentManager = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			switch (status.getCode()) {
				case AnalysisManager.SUCCESS_DB_ADD:
					mAttachments.add((Files) list.get(0));
					
					if (--mUploadRemain == 0) {
						mDataLoaded = true;
						if (mUploadFailedCount == 0) {
							if (mMailBoxType == DRAFTBOX
				        			&& mOperationType == OPERATION_EDIT) {
				    			update(true);
				    		} else {
				    			add(true);
				    		}
						} else {
							sendMessage(DISMISS_PROGRESS_DIALOG);
							sendMessage(SHOW_TOAST, getString(R.string.upload_fail_retry));
							enableFunctionButton(true);
						}
						mUploadFailedCount = 0;
					}
					break;
				case AnalysisManager.EXCEPTION_DB_ADD:
					mUploadFailedCount++;
					
					if (--mUploadRemain == 0) {
						mUploadFailedCount = 0;
						sendMessage(DISMISS_PROGRESS_DIALOG);
						sendMessage(SHOW_TOAST, getString(R.string.upload_fail_retry));
						enableFunctionButton(true);
					}
					break;
				default:
					break;
			}
		}
	};
    
    /**
     * 附件服务
     */
    DataManagerInterface mDocumentManager = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			switch (status.getCode()) {
				case AnalysisManager.SUCCESS_DB_ADD:
					mAttachments.add((Files) list.get(0));
					
					if (--mUploadRemain == 0) {
						mDataLoaded = true;
						if (mUploadFailedCount == 0) {
							if (mMailBoxType == DRAFTBOX
				        			&& mOperationType == OPERATION_EDIT) {
				    			update(false);
				    		} else {
				    			add(false);
				    		}
						} else {
							sendMessage(DISMISS_PROGRESS_DIALOG);
							sendMessage(SHOW_TOAST, getString(R.string.upload_fail_retry));
							enableFunctionButton(true);
						}
						mUploadFailedCount = 0;
					}
					break;
				case AnalysisManager.EXCEPTION_DB_ADD:
					mUploadFailedCount++;
					
					if (--mUploadRemain == 0) {
						mUploadFailedCount = 0;
						sendMessage(DISMISS_PROGRESS_DIALOG);
						sendMessage(SHOW_TOAST, getString(R.string.upload_fail_retry));
						enableFunctionButton(true);
					}
					break;
				default:
					break;
			}
		}
	};
    
	/**
	 * 邮件服务
	 */
    DataManagerInterface mMailManager = new DataManagerInterface() {
		
		@Override
		public void getDataOnResult(ResultStatus status, List<?> list) {
			
			// 成功失败都要先关闭进度对话框
			sendMessage(DISMISS_PROGRESS_DIALOG);
			
			enableFunctionButton(true);
			sendMessage(SHOW_TOAST, status.getMessage());
			
			switch (status.getCode()) {
				case AnalysisManager.SUCCESS_DB_ADD:
					addInteration();
					break;
				case AnalysisManager.SUCCESS_DB_UPDATE:
					updateInteration();
					break;
				default:
					break;
			}
		}
	};
	
	/**
	 * 增加成功Activity交互
	 */
	private void addInteration() {
		switch (mMailBoxType) {
			case INBOX:
				if (mIsSave) {
					notifyRefresh(REFRESH_DRAFTBOX);
				} else {
					notifyRefresh(REFRESH_OUTBOX);
				}
				break;
			case OUTBOX:
				if (mIsSave) {
					notifyRefresh(REFRESH_DRAFTBOX);
				} else {
					if (MailBoxUtils.isReceiver(mCurrentBean,
								UserCache.getCurrentUser())) {
						notifyRefresh(REFRESH_INBOX);
					}
				}
				break;
			case DRAFTBOX:
				if (mOperationType == OPERATION_NEW) {
					if (!mIsSave) {
						notifyRefresh(REFRESH_OUTBOX);
						if (MailBoxUtils.isReceiver(mCurrentBean,
								UserCache.getCurrentUser())) {
							notifyRefresh(REFRESH_INBOX);
						}
					}
				}
				break;
			default:
				break;
		}
		setResultForEmail(false);
	}
	
	/**
	 * 更新成功Activity交互
	 */
	private void updateInteration() {
		switch (mMailBoxType) {
			case INBOX:
				break;
			case OUTBOX:
				break;
			case DRAFTBOX:
				if (!mIsSave) {
					notifyRefresh(REFRESH_OUTBOX);
					if (MailBoxUtils.isReceiver(mCurrentBean,
							UserCache.getCurrentUser())) {
						notifyRefresh(REFRESH_INBOX);
					}
				}
				break;
			default:
				break;
		}
		setResultForEmail(false);
	}
	
	/**
	 * 通知刷新
	 * @param clazz
	 */
	private void notifyRefresh(String action) {
		sendBroadcast(new Intent(action));
	}
    
    /**
     * 返回结果
     */
    private void setResultForEmail(boolean isCancel) {
		if (!isCancel) {
			Intent intent = new Intent();
			intent.putExtra(MAILBOX_KEY, mCurrentBean);
			setResult(Activity.RESULT_OK, intent);
		} else {
			setResult(Activity.RESULT_CANCELED, null);
		}
		
		// 结束当前Activity
		finish();
    }
    
    /**
     * 初始化选择附件对话框
     */
    private void initPickDialog() {
        mPickDialog = new Dialog(this, R.style.MyDialogStyle);
        mPickDialog.setContentView(R.layout.change_attachment_pic_pick_dialog);
        
        Button takePicture = (Button) mPickDialog.findViewById(R.id.take_picture);
        Button localFile = (Button) mPickDialog.findViewById(R.id.local_picture);
        localFile.setText(getString(R.string.local_file));
        
        View beforeEditTemplate = mPickDialog.findViewById(R.id.before_edit_template);
        Button editTemplate = (Button) mPickDialog.findViewById(R.id.edit_template);
        beforeEditTemplate.setVisibility(View.VISIBLE);
        editTemplate.setVisibility(View.VISIBLE);
        
        takePicture.setOnClickListener(mButtonBarClickListener);
        localFile.setOnClickListener(mButtonBarClickListener);
        editTemplate.setOnClickListener(mButtonBarClickListener);

        mPickDialog.setCanceledOnTouchOutside(true);
    }
    

    /**
     * 从文件管理器选择本地文件
     */
    private void pickLocalFile() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClass(ComposeActivity.this, FileExplorerActivity.class);
        intent.putExtra(FileExplorerActivity.IS_MUTIL_SELECT_KEY, true);
        intent.putExtra(FileExplorerActivity.MAX_MUTIL_SELECT_KEY, 10); // 最多只能10个附件
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }
    
    /**
     * 初始化重命名对话框
     */
    private void initRenameDialog() {
        mRenameDialog = new Dialog(this, R.style.MyDialogStyle);
        mRenameDialog.setContentView(R.layout.attachment_rename_dialog);
        final EditText nameEt = (EditText)mRenameDialog.findViewById(R.id.new_name);
        TextView titleTv = (TextView)mRenameDialog.findViewById(R.id.edit_title);
        titleTv.setText(R.string.attachment_rename);        
        ImageView closeIv = (ImageView)mRenameDialog.findViewById(R.id.btn_close);
        closeIv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mRenameDialog.dismiss();
			}
		});
        Button confirmBt = (Button)mRenameDialog.findViewById(R.id.btn_left);
        confirmBt.setText(R.string.confirm);
        confirmBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String newFileName = nameEt.getText().toString();
				Log.v("cccc","newFileName:"+newFileName+", mAttachments:"+mAttachments+", mAttachmentLists:"+mAttachmentLists);
				String path = null;
				for (int i = 0; i < mAttachmentLists.size(); i++) {
					if (mCurrentOldName.equals(FileUtils.getFileFullName(mAttachmentLists.get(i)))) {
						path = FileUtils.getDirctoryPath(mAttachmentLists.get(i));
						Log.v("cccc","getDirctoryPath:"+FileUtils.getDirctoryPath(mAttachmentLists.get(i)));
					}
				}
				
				if (newFileName != null && newFileName.length() != 0 && path != null) {
					File oldFile = new File(path+mCurrentOldName);
					Log.v("cccc","oldFile:"+oldFile.getAbsolutePath());
					if (oldFile.renameTo(new File(path + newFileName))){
						mCurrentAttamentView.setText(newFileName);
						for (int i = 0; i < mAttachmentLists.size(); i++) {
							if (mAttachmentLists.get(i).equals(path + mCurrentOldName)) {
								mAttachmentLists.set(i, path + newFileName);
							}
						}
					}					
				} else {
					Toast.makeText(getBaseContext(), R.string.attachment_rename_null, Toast.LENGTH_SHORT).show();
				}
				mRenameDialog.dismiss();
			}
		});
        mRenameDialog.setCanceledOnTouchOutside(true);
    }    
    
    /**
     *  拍照
     */
    private void pickByCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "testing");
        values.put(MediaStore.Images.Media.DESCRIPTION, "this is description");

        mImageFileUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageFileUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }
    
    /**
     * 编辑模板
     */
    private void editTemplate() {
    	Intent intent = new Intent(this, ListSelectActivity.class);
    	Bundle bundle = new Bundle();
    	bundle.putSerializable(ListSelectActivity.FRAGMENT_KEY,
    			DocTemplateFragment.class);
    	bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY, 
    			ListSelectActivity.SINGLE_SELECT);
    	intent.putExtras(bundle);
    	startActivityForResult(intent, SELECT_TEMPLATE_CODE);
    }
    
    /**
     * 附件选择结果处理
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        
        switch (requestCode) {
        	case SELECT_TEMPLATE_CODE:
        		//从服务器选择模板，先判断本地是否有备份，若没有，需要先下载
				final Files files = (Files) data
						.getSerializableExtra(ListSelectActivity.RESULT_KEY);
				mIsTemplateAttachmentAdd = true;
				Log.v("cccc","file_name:"+files.getFile_name()+", file_path:"+files.getFile_path()+", path:"+files.getPath()+", file==="+files);
		        File file = new File(TEMPLATE_DIR + files.getFile_name());
		        
		        if (!file.exists()) {
		        	mIsTemplateAttachmentAdd = false;
		            IntentBuilder.download(ComposeActivity.this, new DataManagerInterface() {
		                @Override
		                public void getDataOnResult(ResultStatus status,
		                        List<?> list) {
		                    if (status.getCode() != AnalysisManager.SUCCESS_DOWNLOAD) {
		                        Toast.makeText(getBaseContext(), getResources()
		                                        .getString(R.string.document_download_fail),
		                                Toast.LENGTH_SHORT).show();
		                        Log.v("cccc","SUCCESS_fail");
		                    } else {
		                    	
		                    	String filePath = Util.copyFile(TEMPLATE_DIR + files.getFile_name(), TEMPLATE_CACHE_DIR);
		                    	Log.v("cccc","SUCCESS_DOWNLOAD filePath:"+filePath);
		        				if (filePath != null) {
		        					mCurrentFile = new File(filePath);	
		        					mIsTemplateAttachmentAdd = true;
		        					onResume();
		        				} else {
		        					mCurrentFile = null;
		        					Toast.makeText(getBaseContext(), R.string.file_null_or_path_error, Toast.LENGTH_SHORT).show();
		        				}		  
		        				
		                    }
		                }
		            }, files, TEMPLATE_DIR + files.getFile_name(), true);
		        } else {
		        	Log.v("cccc","file already exists!");
                	String filePath = Util.copyFile(TEMPLATE_DIR + files.getFile_name(), TEMPLATE_CACHE_DIR);
    				if (filePath != null) {
    					mCurrentFile = new File(filePath);					
    				} else {
    					mCurrentFile = null;
    					Toast.makeText(getBaseContext(), R.string.file_null_or_path_error, Toast.LENGTH_SHORT).show();
    				}		        	
		        }		        				
				
				Log.v("cccc","TDF:"+TEMPLATE_DIR + files.getFile_name());
				
        		break;
	        case FILE_REQUEST_CODE:
	            List<String> pathList = data.getExtras()
	            			.getStringArrayList(FileExplorerActivity.MUTIL_SELECT_LIST_KEY);
	        	Log.v("cccc","FILE_REQUEST_CODE pathList:"+pathList);
	            for (String path : pathList) {
	            	insertAttachment(path);
	            }
	            break;
	        case CAMERA_REQUEST_CODE:
	        	String path = URIToURL(mImageFileUri);
	            insertAttachment(path);
	            break;
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	protected void onResume() {
		super.onResume();
		Log.v("cccc","onResume");
		if (mIsTemplateAttachmentAdd) {
			
			if (mCurrentFile != null && mCurrentFile.exists()) {
				Log.v("cccc","onResume mCurrentFile:"+mCurrentFile.getAbsolutePath());
				insertAttachment(mCurrentFile.getAbsolutePath());
				
				mIsAddFirst = true;
				mIsTemplateAttachmentAdd = false;
			} else if (!mIsAddFirst) {
				mIsAddFirst = true;
				mIsTemplateAttachmentAdd = false;
				sendMessage(SHOW_TOAST, getResources()
						.getString(R.string.email_attachment_add_failed));
			}
			
			if (mIsTemplateAttachmentAdd) {
				mIsAddFirst = false;
			}
		}
		
		switch (mOperationType) {
			case OPERATION_NEW:
				break;
			case OPERATION_REPLY:
				// fall through
			case OPERATION_REPLY_ALL:
				// fall through
			case OPERATION_FORWORD:
				mEmailBodyContent.requestFocus();
				break;
			default:
				break;
		}
	}

	/**
     * 创建联系人控件，加入指定父控件
     * @param user
     * @return
     */
    private void addContacts(User user, final boolean isCc) {
    	if (user != null) {
    		final List<User> users = isCc ? mCcLists : mRecipientLists;
			if (checkRepeatAddContact(users, user)) {
				if (mInputAddContact) {
					sendMessage(SHOW_TOAST, getString(R.string.email_contact_exist));
				} else {
					//sendMessage(SHOW_TOAST, getString(R.string.email_contact_repeat));
				}
				return;
			}
			
			final ViewContainer container = isCc ? mCcLayout : mRecipientLayout;
    		
	        LayoutParams params = new ViewGroup
	        		.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0);
	        TextView textView = new TextView(this);
	        textView.setLayoutParams(params);
	        textView.setText(user.getName());
	        textView.setTextColor(Color.BLACK);
	        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
	                .getDimensionPixelSize(R.dimen.sp16_s));
	        textView.setBackgroundResource(R.drawable.corners_gray_text_bg);
	        textView.setPadding(4, 4, 4, 0);
	        textView.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	
	            	if (mCurrentContacTextView != null 
	            			&& mCurrentContacTextView.getTag() != null) {
	            		mCurrentContacTextView.setBackgroundResource(R.drawable.corners_gray_text_bg);
	            		mCurrentContacTextView.setCompoundDrawables(null, null, null, null);
	            		mCurrentContacTextView.setTag(null);
	            	}
	                
	                if (v == mCurrentContacTextView) {
                		users.remove(mViewUserMap.get(v));
                		container.removeView(v);
	                } else {
	                    v.setTag(1);
	                    Drawable drawable = getResources().getDrawable(R.drawable.icn_remove);
	                    drawable.setBounds(0, 0, 20, 20);//必须设置图片大小，否则不显示
	                    ((TextView) v).setCompoundDrawables(null, null, drawable, null);
	                    ((TextView) v).setBackgroundResource(R.drawable.email_contact_yellow);
	                }
	                
	                mCurrentContacTextView = (TextView) v;
	            }
	        });
	        
        	users.add(user);
        	container.addView(textView, container.getChildCount() - 1);
	        mViewUserMap.put(textView, user);
    	}
    }
    
    /**
     * 检查是否重复添加
     * @param users
     * @param user
     * @return
     */
    public boolean checkRepeatAddContact(List<User> users, User user) {
    	for (User existUser : users) {
    		if (existUser.getUser_id() == user.getUser_id()) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    @SuppressWarnings("deprecation")
    private String URIToURL(Uri uri) {
        String[] imgs = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.managedQuery(uri, imgs, null, null, null);
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String url = cursor.getString(index);
        if (Integer.parseInt(Build.VERSION.SDK) < 14) {
            cursor.close();
        }
        return url;
    }
    
    /**
     * 获取文件对应的类型图标
     * @param path
     * @return
     */
    private Drawable getFileType(String path) {
        int res = FileIconHelper.getIconByFilename(path);
        Drawable drawable = getResources().getDrawable(res);
        return drawable;
    }
    
    /**
     * 切换到选择用户界面
     * @param selectedList 要过滤的用户
     */
	private void showContactView(List<User> selectedList) {
	    CustomTranslateAnimation.fadeLeft(mTitlelLayout, 100);
    	CustomTranslateAnimation.fadeLeft(mEmailButtonBarLayout, 100);
        CustomTranslateAnimation.showRight(mContactLayout, 100);
        CustomTranslateAnimation.showRight(mContactButtonBarLayout, 100);
        
        initViewPager(selectedList);
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
	private void initViewPager(List<User> selectedList) {
    	if (mViewPager == null) {
	    	mViewPager = new BaseViewPager(this, mContactLayout);
	        
	        String[] titles = new String[] {
	        		getString(R.string.the_company),
	        		getString(R.string.cooperation_company)
	        };
	        
	        Class<? extends Fragment>[] fragmentClasses = new Class[] {
	        	UserSelectFragment.class,
	        	UserCooperationFragment.class
	        };
	        
	        Bundle bundle = new Bundle();
	        bundle.putBoolean(ListSelectActivity.SELECT_MODE_KEY, ListSelectActivity.MULTI_SELECT);
	        bundle.putSerializable(ListSelectActivity.FILTER_DATA_KEY, (ArrayList<User>) selectedList);
	        Project project = null;
	        if (mContractChange != null) {
	        	project = new Project();
	        	project.setProject_id(getIntent().getIntExtra(EMAIL_PROJECT_KEY, 0));
	        	project.setTenant_id(UserCache.getTenantId());
	        } else {
	        	project = (Project) getIntent().getSerializableExtra(EMAIL_PROJECT_KEY);
	        }
	        bundle.putSerializable(EMAIL_PROJECT_KEY, project);
	        
	        final Bundle[] bundles = new Bundle[2];
	        bundles[0] = bundle;
	        bundles[1] = bundle;
	        
	        mViewPager.initFragments(titles, fragmentClasses, 
	        		new SimplePageChangeListenerInterface() {
	
				@Override
				public Bundle[] getBundleData() {
					return bundles;
				}
	        });
	        mViewPager.enableInnerButton(false);
    	} else {
    		mViewPager.setFilterData(selectedList);
    	}
    }
    
    /**
     * 初始化邮件主体布局
     */
    private void initContactLayout() {
        mEmailMainView = (ScrollView) findViewById(R.id.email_main_layout);
        mEmailButtonBarLayout = (LinearLayout) findViewById(R.id.email_button_bar);
        mContactLayout = (ViewGroup) findViewById(R.id.contact_select_layout);
        //mContactLayout.setBackgroundResource(R.drawable.corners_top_light_gray);
        mContactButtonBarLayout = (LinearLayout) findViewById(R.id.contact_button_bar);
        
        mEmailBodyContent = (EditText) mEmailMainView.findViewById(R.id.body_content);
        
		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (view.getId() == R.id.add) {
					@SuppressWarnings("unchecked")
					List<User> userlist = (List<User>) mViewPager.getSelectedDataList();
					mInputAddContact = false;
					for (User user : userlist) {
						addContacts(user, mIsAddCc);
					}
				}

				// 动画切换
				CustomTranslateAnimation.showLeft(mTitlelLayout);
				CustomTranslateAnimation.showLeft(mEmailButtonBarLayout);
				CustomTranslateAnimation.fadeRight(mContactLayout);
				CustomTranslateAnimation.fadeRight(mContactButtonBarLayout);
				mEmailMainView.setBackgroundResource(R.color.white);
			}
		};

		Button addContact = (Button) findViewById(R.id.add);
		Button closeContact = (Button) findViewById(R.id.close);
		addContact.setOnClickListener(clickListener);
		closeContact.setOnClickListener(clickListener);
    }
}
