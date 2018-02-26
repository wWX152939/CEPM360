package com.pm360.cepm360.app.module.announcement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.PermissionCache;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.custinterface.BaseWidgetInterface;
import com.pm360.cepm360.app.common.custinterface.CommonListInterface;
import com.pm360.cepm360.app.common.custinterface.FloatingMenuInterface;
import com.pm360.cepm360.app.common.custinterface.OptionMenuInterface;
import com.pm360.cepm360.app.common.custinterface.ServiceInterface;
import com.pm360.cepm360.app.common.custinterface.SimpleDialogInterface;
import com.pm360.cepm360.app.common.view.OptionsMenuView;
import com.pm360.cepm360.app.common.view.OptionsMenuView.SubMenuListener;
import com.pm360.cepm360.app.common.view.parent.BaseListFragment;
import com.pm360.cepm360.app.common.view.richeditor.RichEditor;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.common.util.TwoNumber;
import com.pm360.cepm360.entity.Announcement;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.services.document.RemoteDocumentsService;
import com.pm360.cepm360.services.system.RemoteAnnouncementService;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnouncementFragment extends BaseListFragment<Announcement> {
	private LinearLayout mRoot;
	private PopupWindow mPopupWindow;
	private ViewGroup mPopupViewGroup;
	private WebView mWebView;
	private RichEditor mRichEditor;
	protected OptionsMenuView mPubOptionsMenu;
	protected OptionsMenuView mNoPubOptionsMenu;
	
	private RemoteAnnouncementService mService;
	
	private Dialog mPickDialog;
	private Uri mImageFileUri;
	
	private LayoutInflater mInflater;
	
	private static final int IMAGE_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    
    private static final int INSERT_IMAGE = 0;
    
    private boolean mIsTextColor;
	
    @Override
    protected void initFloatingMenu() {
    	super.initFloatingMenu();
    	LayoutParams params =  (LayoutParams) mFloatingMenu.getLayoutParams();
		params.rightMargin = getActivity().getResources().getDimensionPixelOffset(R.dimen.dp16_w);
		mFloatingMenu.setLayoutParams(params);
    }
    
	/**
	 * 创建视图
	 */
	@Override
	public View onCreateView( final LayoutInflater inflater,
							  final ViewGroup container,
							  Bundle savedInstanceState) {
		mInflater = inflater;
		mRoot = (LinearLayout) inflater.inflate(R.layout.webview_layout, container, false);
		
		// 初始化接口
		mBaseWidgetInterface = new BaseWidgetInterface() {
			
			@Override
			public TwoNumber<View, android.widget.LinearLayout.LayoutParams> createExtraLayout() {
				android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(UtilTools.dp2pxW(getActivity(), 488),
									UtilTools.dp2pxH(getActivity(), 280));
				params.gravity = Gravity.CENTER_HORIZONTAL;
				params.leftMargin = UtilTools.dp2pxW(getActivity(), 4);
				params.topMargin = UtilTools.dp2pxH(getActivity(), 12);
				
				TwoNumber<View, android.widget.LinearLayout.LayoutParams> viewParams 
						= new TwoNumber<View, android.widget.LinearLayout.LayoutParams>(mRoot, params);
				return viewParams;
			}

			@Override
			public Integer[] getImportantColumns() {
				// TODO Auto-generated method stub
				return new Integer[] {0};
			}
		};

		initWebView();
		initRichEditor();
		
		forceEnableOption(true);
		// 初始化类型和接口
		init( Announcement.class, 
			  mListInterface, 
			  mRequestInterface,
			  mFloatingMenuInterface,
			  mOptionMenuInterface,
			  mDialogInterface);
		
		setPermissionIdentity(GLOBAL.SYS_ACTION[57][0], GLOBAL.SYS_ACTION[56][0]);
		mService = RemoteAnnouncementService.getInstance();
		
		enableNormalMultSelect();
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		mNoPubOptionsMenu = mOptionsMenu;
		mOptionMenuImplement = mPubOptionMenuInterface;
		initOptionsMenu();
		mPubOptionsMenu = mOptionsMenu;
		
		if (mDialog != null) {
			mDialog.setFirstButton(getResources().getString(R.string.publish), new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Announcement a = mCurrentItem;
					mSaveData = mDialog.SaveData();
					if (mIsAddOperation == true) {
						a = new Announcement();
						a.setStatus(1);
						a.setTitle(mSaveData.get(getString(R.string.title)));
						a.setContent(mRichEditor.getHtml());
						a.setCreater(UserCache.getCurrentUser().getUser_id());
						a.setTenant_id(UserCache.getCurrentUser().getTenant_id());
						mService.addAnnouncement(getServiceManager(), a, UserCache.getUserLists());
					} else {
						a.setStatus(1);
						a.setTitle(mSaveData.get(getString(R.string.title)));
						a.setContent(mRichEditor.getHtml());
						mService.updateAnnouncement(getServiceManager(), a, UserCache.getUserLists());
					}
					mDialog.dismiss();
				}
			});
		}
		
		return view;
	}
	
	/**
	 * 列表接口，该接口实现的内容比较杂，主要用来提供资源
	 */
	CommonListInterface<Announcement> mListInterface 
						= new CommonListInterface<Announcement>() {
		@Override
		public int getListItemId(Announcement t) {
			return t.getAnnouncement_id();
		}

		@Override
		public String[] getDisplayFeilds() {
			return new String[] {
					SERIAL_NUMBER,
					"title",
					"creater",
					"publish_time",
					"status"
			};
		}

		@Override
		public int getListHeaderLayoutId() {
			return R.layout.announcement_listitem_layout;
		}
		
		@Override
		public int getListLayoutId() {
			return getListHeaderLayoutId();
		}

		@Override
		public int getListHeaderNames() {
			return R.array.announcement_list_header_names;
		}

		@Override
		public int getListHeaderIds() {
			return R.array.announcement_list_header_ids;
		}
		
		@Override
		public Map<String, Map<String, String>> getDisplayFieldsSwitchMap() {
			Map<String, Map<String, String>> fieldSwitchMap
							= new HashMap<String, Map<String, String>>();
			
			fieldSwitchMap.put(mDisplayFields[2], UserCache
					.getUserMaps());
			fieldSwitchMap.put(mDisplayFields[4], globalIdNameMap(GLOBAL.PUBLISH_STATUS));

			return fieldSwitchMap;
		}

		@Override
		public int getListItemIds() {
			return getListHeaderIds();
		}
	};
	
	FloatingMenuInterface mFloatingMenuInterface = new FloatingMenuInterface() {

            @Override
            public int[] getFloatingMenuImages() {
                    return new int[] { R.drawable.icn_add };
            }

            @Override
            public String[] getFloatingMenuTips() {
                    return new String[] { getActivity().getResources().getString(
                                    R.string.add) };
            }

            @Override
            public OnItemClickListener getFloatingMenuListener() {
                    OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                                    switch (arg2) {
                                    case 0:
                                    	mDialog.getFirstButton().setVisibility(View.VISIBLE);
                						mIsAddOperation = true;
                						switchToRichEditor();
                						mDialog.SetDefaultValue(null);
                						mDialog.show();
                						mFloatingMenu.dismiss();
                                    default:
                                        break;
                                    }
                            }

                    };
                    return mOnItemClickListener;
            }

    };
	
	OptionMenuInterface mOptionMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.announcement_option_menu_names;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
						case 0:		// 详情
							switchToWebView();
							showUpdateDialog(false);
							mDialog.getFirstButton().setVisibility(View.GONE);
							break;
						case 1:		// 修改
							mIsAddOperation = false;
							switchToRichEditor();
							showUpdateDialog(true);
							break;
						case 2:		// 删除
							commonConfirmDelete();
							break;
						case 3:		// 发布
							mCurrentItem.setStatus(1);
							mRequestInterface.updateItem(mCurrentItem);
							break;
					}
				}
			};
		}
	};
	
	OptionMenuInterface mPubOptionMenuInterface = new OptionMenuInterface() {

		@Override
		public int getOptionMenuNames() {
			return R.array.announcement_pub_option_menu_names;
		}

		@Override
		public SubMenuListener getOptionMenuClickListener() {
			return new SubMenuListener() {
				
				@Override
				public void onSubMenuClick(View view) {
					mOptionsMenu.dismiss();
					switch ((Integer) view.getTag()) {
						case 0:		// 详情
							switchToWebView();
							showUpdateDialog(false);
							mDialog.getFirstButton().setVisibility(View.GONE);
							break;
						case 1:		// 删除
							commonConfirmDelete();
							break;
						default:
							break;
					}
				}
			};
		}
	};
	
	@SuppressLint("SetJavaScriptEnabled") 
	private void initWebView() {
		mWebView = (WebView) mRoot.findViewById(R.id.announcement_webview);
		
		WebSettings webSettings = mWebView.getSettings();       
		webSettings.setJavaScriptEnabled(true);
		
		// 设置webview编码方式
		webSettings.setDefaultTextEncodingName("UTF-8");
		
		mWebView.setWebViewClient(new WebViewClient() {
		      @Override
		      public boolean shouldOverrideUrlLoading(WebView view, String url) {
		          view.loadUrl(url);
		          return true;
		      }
		  });
	}
	
	private void initRichEditor() {
		mRichEditor = (RichEditor) mRoot
				.findViewById(R.id.announcement_richeditor);
		mRichEditor.setEditorHeight(160);
		mRichEditor.setEditorFontSize(16);
		mRichEditor.setEditorFontColor(Color.BLACK);
		mRichEditor.setPadding(4, 10, 4, 10);
		mRichEditor.setPlaceholder("Insert text here...");
		
		OnClickListener listener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.action_undo:
					mRichEditor.undo();
					break;
				case R.id.action_redo:
					mRichEditor.redo();
					break;
				case R.id.action_bold:
					mRichEditor.setBold();
					break;
				case R.id.action_italic:
					mRichEditor.setItalic();
					break;
				case R.id.action_subscript:
					mRichEditor.setSubscript();
					break;
				case R.id.action_superscript:
					mRichEditor.setSuperscript();
					break;
				case R.id.action_strikethrough:
					mRichEditor.setStrikeThrough();
					break;
				case R.id.action_underline:
					mRichEditor.setUnderline();
					break;
				case R.id.action_heading1:
					mRichEditor.setHeading(1);
					break;
				case R.id.action_heading2:
					mRichEditor.setHeading(2);
					break;
				case R.id.action_heading3:
					mRichEditor.setHeading(3);
					break;
				case R.id.action_heading4:
					mRichEditor.setHeading(4);
					break;
				case R.id.action_heading5:
					mRichEditor.setHeading(5);
					break;
				case R.id.action_heading6:
					mRichEditor.setHeading(6);
					break;
				case R.id.action_txt_color:
					mIsTextColor = true;
					initPopupWindow();
					mPopupWindow.showAsDropDown(v, 
							-((mPopupWindow.getWidth() - v.getWidth()) / 2) , 2);
					break;
				case R.id.action_bg_color:
					mIsTextColor = false;
					initPopupWindow();
					mPopupWindow.showAsDropDown(v, 
							-((mPopupWindow.getWidth() - v.getWidth()) / 2) , 2);
					break;
				case R.id.action_indent:
					mRichEditor.setIndent();
					break;
				case R.id.action_outdent:
					mRichEditor.setOutdent();
					break;
				case R.id.action_align_left:
					mRichEditor.setAlignLeft();
					break;
				case R.id.action_align_center:
					mRichEditor.setAlignCenter();
					break;
				case R.id.action_align_right:
					mRichEditor.setAlignRight();
					break;
				case R.id.action_blockquote:
					mRichEditor.setBlockquote();
					break;
				case R.id.action_insert_image:
					initPickDialog();
					mPickDialog.show();
					break;
				case R.id.action_insert_link:
					showLinkDialog();
					break;
				case R.id.action_insert_checkbox:
					mRichEditor.insertTodo();
					break;
				default:
					break;
				}
			}
		};

		mRoot.findViewById(R.id.action_undo).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_redo).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_bold).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_italic).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_subscript).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_superscript).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_strikethrough).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_underline).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_heading1).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_heading2).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_heading3).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_heading4).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_heading5).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_heading6).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_txt_color).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_bg_color).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_indent).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_outdent).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_align_left).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_align_center).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_align_right).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_blockquote).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_insert_image).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_insert_link).setOnClickListener(listener);
		mRoot.findViewById(R.id.action_insert_checkbox).setOnClickListener(listener);
	}
	
	private void showLinkDialog() {
		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.setContentView(R.layout.autolink_layout);
		
		final EditText autoUrlText = (EditText) dialog.findViewById(R.id.autolink_url);
		Editable urlEditable = autoUrlText.getText();
		Spannable spannable = urlEditable;
		Selection.setSelection(spannable, urlEditable.length());
		
		Button saveButton = (Button) dialog.findViewById(R.id.save);
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String autoUrl = autoUrlText.getText().toString();
				LogUtil.e("autoText = " + autoUrl);
				mRichEditor.insertLink(autoUrl, "Please select the link text");
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	 /**
     * 初始化选择附件对话框
     */
    private void initPickDialog() {
    	if (mPickDialog == null) {
	        mPickDialog = new Dialog(getActivity(), R.style.MyDialogStyle);
	        mPickDialog.setContentView(R.layout.pictrue_pick_dialog);
	        
	        Button takePicture = (Button) mPickDialog.findViewById(R.id.take_picture);
	        Button localFile = (Button) mPickDialog.findViewById(R.id.local_picture);
	        
	        takePicture.setOnClickListener(mButtonBarClickListener);
	        localFile.setOnClickListener(mButtonBarClickListener);
	
	        mPickDialog.setCanceledOnTouchOutside(true);
    	}
    }
    
    @SuppressWarnings("deprecation")
	private void initPopupWindow() {
    	if (mPopupWindow == null) {
	    	mPopupViewGroup = (ViewGroup) mInflater.inflate(R.layout.color_panel, null);
	    	
	    	OnClickListener listener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String color = ((Button) v).getText().toString();
					if (mIsTextColor) {
						mRichEditor.setTextColor(color);
					} else {
						mRichEditor.setTextBackgroundColor(color);
					}
					
					mPopupWindow.dismiss();
				}
			};
			
	    	mPopupViewGroup.findViewById(R.id.color_white).setOnClickListener(listener);
	    	mPopupViewGroup.findViewById(R.id.color_black).setOnClickListener(listener);
	    	mPopupViewGroup.findViewById(R.id.color_red).setOnClickListener(listener);
	    	mPopupViewGroup.findViewById(R.id.color_green).setOnClickListener(listener);
	    	mPopupViewGroup.findViewById(R.id.color_fuchsia).setOnClickListener(listener);
	    	mPopupViewGroup.findViewById(R.id.color_yellow).setOnClickListener(listener);
	    	mPopupViewGroup.findViewById(R.id.color_aqua).setOnClickListener(listener);
	    	mPopupViewGroup.findViewById(R.id.color_darkgray).setOnClickListener(listener);
	    	mPopupViewGroup.findViewById(R.id.color_blue).setOnClickListener(listener);
	    	
	    	mPopupWindow = new PopupWindow(mPopupViewGroup, 
	    				UtilTools.dp2pxW(getActivity(), 126), 
	    				UtilTools.dp2pxW(getActivity(), 124), true);
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    	}
    }
    
    /* 保存、发送、取消   响应处理   */
    View.OnClickListener mButtonBarClickListener = new View.OnClickListener() {
        
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
	            case R.id.take_picture:  // 拍照
	                pickByCapture();
	                mPickDialog.dismiss();
	                break;
	            case R.id.local_picture: // 本地图片
	            	pickLocalPicture();
	                mPickDialog.dismiss();
	                break;
	            default:
	                break;
            }
            
        }
    };
    
    /**
     *  拍照
     */
    private void pickByCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "testing");
        values.put(MediaStore.Images.Media.DESCRIPTION, "this is description");

        mImageFileUri = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageFileUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }
    
    /**
     * 从gallery选择本地图片
     */
    private void pickLocalPicture() {
        Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_PICK);
        startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
    }
	
	@SuppressLint("SetJavaScriptEnabled")
	private void switchToWebView() {
		mDialog.SetDefaultValue(null);
		
		// switch
		mWebView.setVisibility(View.VISIBLE);
		mRoot.findViewById(R.id.announcement_scrollview).setVisibility(View.GONE);
		mRichEditor.setVisibility(View.GONE);
		
		mWebView.loadData(mCurrentItem.getContent(), "text/html; charset=UTF-8", null);
	}
	
	private void switchToRichEditor() {
		mDialog.switchModifyDialog(true);
		
		if (mIsAddOperation) {
			mRichEditor.setHtml(null);
		} else {
			mRichEditor.setHtml(mCurrentItem.getContent());
		}
		
		// switch
		mWebView.setVisibility(View.GONE);
		mRoot.findViewById(R.id.announcement_scrollview).setVisibility(View.VISIBLE);
		mRichEditor.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 服务请求接口实现
	 */
	ServiceInterface<Announcement> mRequestInterface 
								= new ServiceInterface<Announcement>() {

		@Override
		public void getListData() {
			mService.getAnnouncementList(getServiceManager(), 
			        UserCache.getCurrentUser().getTenant_id());
		}

		@Override
		public void addItem(Announcement t) {
				t.setStatus(5);
				t.setContent(mRichEditor.getHtml());
				t.setCreater(UserCache.getCurrentUser().getUser_id());
				t.setTenant_id(UserCache.getCurrentUser().getTenant_id());
				mService.addAnnouncement(getServiceManager(), t, UserCache.getUserLists());
		}

		@Override
		public void deleteItem(Announcement t) {
			mService.deleteAnnouncement(getServiceManager(), t.getAnnouncement_id());
		}

		@Override
		public void updateItem(Announcement t) {
			t.setContent(mRichEditor.getHtml());
			mService.updateAnnouncement(getServiceManager(), t, UserCache.getUserLists());
		}
	};	
	
	/**
	 * 对话框风格，数据，映射及额外初始化接口实现
	 */
	SimpleDialogInterface mDialogInterface = new SimpleDialogInterface() {
		@Override
		public int getDialogTitleId() {
			return R.string.announcement_dialog_title;
		}

		@Override
		public int getDialogLableNames() {
			return R.array.announcement_dialog_lable_names;
		}
		
		@Override
		public String[] getUpdateFeilds() {
			return new String[] {
					"title"
			};
		}

		@Override
		public Integer[] getImportantColumns(Map<String, String> saveDataMap) {
			return mBaseWidgetInterface.getImportantColumns();
		}
	};
	
	@Override
	protected boolean doExtraAddFloatingMenuEvent() {
		switchToRichEditor();
		return false;
	}
	
	@SuppressWarnings("deprecation")
    private String URIToURL(Uri uri) {
        String[] imgs = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(uri, imgs, null, null, null);
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String url = cursor.getString(index);
        if (Integer.parseInt(Build.VERSION.SDK) < 14) {
            cursor.close();
        }
        return url;
    }
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        
        switch (requestCode) {
	        case CAMERA_REQUEST_CODE:
	        	String path = URIToURL(mImageFileUri);
	        	RemoteDocumentsService.getInstance().uploadFile(new DataManagerInterface() {
					
					@Override
					public void getDataOnResult(ResultStatus status, List<?> list) {
						if (status.getCode() == AnalysisManager.SUCCESS_UPLOAD) {
							Files files = (Files) list.get(0);
							sendMsg(INSERT_IMAGE, files.getFile_path());
						}
					}
				}, null, UserCache
					.getCurrentUser().getTenant_id(), new File(path));
	            break;
	        case IMAGE_REQUEST_CODE:
            	RemoteDocumentsService.getInstance().uploadFile(new DataManagerInterface() {
					
					@Override
					public void getDataOnResult(ResultStatus status, List<?> list) {
						if (status.getCode() == AnalysisManager.SUCCESS_UPLOAD) {
							Files files = (Files) list.get(0);
							sendMsg(INSERT_IMAGE, files.getFile_path());
						} else {
							sendMessage(SHOW_TOAST, getString(R.string.fail_insert_picture));
						}
					}
				}, null, UserCache
					.getCurrentUser().getTenant_id(), new File(URIToURL(data.getData())));
	            break;
            default:
            	break;
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
	
	/**
	 * 消息处理
	 */
	@SuppressLint("HandlerLeak") 
	private Handler mMyHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case INSERT_IMAGE:
					mRichEditor.insertImage((String) msg.obj, "");
					break;
				default:
					break;
			}
		}
	};
	
	/**
	 * 发送消息，本地处理
	 * @param what
	 * @param object
	 */
	protected void sendMsg(int what, Object object) {
		if (object != null) {
			// 创建初始化Message
			Message msg = Message.obtain();
			msg.what = what;
			msg.obj = object;
			
			// 发送消息到mHandler
			mMyHandler.sendMessage(msg);
		} else {
			mMyHandler.sendEmptyMessage(what);
		}
	}
	
	/**
	 * 发送消息，本地处理
	 * @param what
	 */
	protected void sendMsg(int what) {
		sendMessage(what, null);
	}
	
	protected void switchOptionMenu() {
		if (mCurrentItem.getStatus() 
				== Integer.parseInt(GLOBAL.PUBLISH_STATUS[0][0])) {
			mOptionsMenu = mPubOptionsMenu;
			if (!PermissionCache.hasSysPermission(GLOBAL.SYS_ACTION[56][0])) {
				int arr[] = {1};
				mOptionsMenu.setVisibileMenu(arr, false);
			}			
		} else {
			mOptionsMenu = mNoPubOptionsMenu;
			if (!PermissionCache.hasSysPermission(GLOBAL.SYS_ACTION[56][0])) {
				int arr[] = {1,2,3};
				mOptionsMenu.setVisibileMenu(arr, false);
			}
		}
	}
}
