package com.pm360.cepm360.app.module.document;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.view.parent.BaseSlidingPaneActivity;
import com.pm360.cepm360.common.GLOBAL;

import java.util.ArrayList;
import java.util.List;

public class DocumentActivity extends BaseSlidingPaneActivity {

	private SearchConditionBuilder mViewBuilder;

	FragmentManagerInterface mFragmentManagerInterface = new FragmentManagerInterface() {

		@SuppressWarnings("unchecked")
		@Override
		public Class<? extends Fragment>[] getManagerFragments() {
			return new Class[] { DocumentTreeFragment.class,
					DocumentTreeFragment.class, DocumentTreeFragment.class,
					DocumentTreeFragment.class, DocumentSearchFragment.class };
		}

		@Override
		public Class<?>[] getSearchObjectClasses() {
			return null;
		}

		@Override
		public String getHomeTitleName() {
			return "";
		}

		@Override
		public int getNavigationTitleNamesId() {
			return R.array.document_navigation_titiles;

		}

		@Override
		public int getNavigationIconsId() {
			return R.array.document_navigation_icons;
		}

	};

	/**
	 * 设置权限
	 */
	@Override
	protected List<String> getPermission() {
		List<String> list = new ArrayList<String>();
		list.add("3_0"); // 个人文档编辑 默认拥有权限
		list.add("3_0"); // 个人文档查看 默认拥有权限
		list.add(GLOBAL.SYS_ACTION[7][0]);
		list.add(GLOBAL.SYS_ACTION[6][0]);
		list.add(GLOBAL.SYS_ACTION[9][0]);
		list.add(GLOBAL.SYS_ACTION[8][0]);
		list.add(GLOBAL.SYS_ACTION[55][0]);
		list.add(GLOBAL.SYS_ACTION[54][0]);
		return list;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		init(mFragmentManagerInterface, false);
		super.onCreate(savedInstanceState);

		enableMenuView(true);
		enableSearchView(true);
		initSearchConditionView();
	}

	/**
	 * 获取当前左侧导航栏的位置
	 * @return
	 */
	public int getCurrentPosition() {
		return mCurrentPosition;
	}

	/**
	 * 初始化搜索界面
	 */
	private void initSearchConditionView() {
		mViewBuilder = new SearchConditionBuilder(this);
		View view = mViewBuilder.getSearchConditionView();
		getSearchConditionLayout().addView(view);

		setSearchListener(new SearchListener() {
			@Override
			public void doSearch() {
				startSearch();
			}

			@Override
			public void doReset() {
				resetSearchCondition();
			}
		});

		mViewBuilder.getSearchTextInput().setOnEditorActionListener(
				new EditText.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView view, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							hideSoftInput(mViewBuilder.getSearchTextInput());
							getSearchDialog().dismiss();
							startSearch();
							return true;
						}
						return false;
					}
				});
	}

	/**
	 * 返回文档搜索的条件界面
	 * @return
	 */
	public SearchConditionBuilder getBuilder() {
		return mViewBuilder;
	}

	/**
	 * 开始搜索
	 */
	private void startSearch() {
		// 切换到 DocumentSearchFragment.class
		if (mCurrentPosition != -1) {
			switchContent(-1);
		} else {
			((DocumentSearchFragment) mCurrentFragment).searchFile();
		}
	}

	/**
	 * 还原，清除搜索条件
	 */
	private void resetSearchCondition() {
		mViewBuilder.resetSearchCondition();
	}

	/**
	 * 隐藏软键盘
	 * @param v
	 */
	private void hideSoftInput(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}
}
