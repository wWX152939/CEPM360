package com.pm360.cepm360.app.common.adpater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.custinterface.TreeExpandInterface;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.entity.EPS;
import com.pm360.cepm360.entity.Expandable;
import com.pm360.cepm360.entity.Project;

import java.util.ArrayList;
import java.util.List;

public class EpsAdapter extends BaseAdapter implements TreeExpandInterface {

	private Context mContext;
	private ViewHolder holder;
	private int mSelectedItemId = -1;
	private int mSelectedPosition = -1;
	private List<ExpandCell> mShowList = new ArrayList<ExpandCell>();
	private List<ExpandCell> mDataList = new ArrayList<ExpandCell>();
	private ArrayList<EPS> mEPSDataList = new ArrayList<EPS>();
	private ArrayList<Project> mProjectDataList = new ArrayList<Project>();
	private List<Project> mSelectedProjects = new ArrayList<Project>();
	private EpsInterface mEpsInterface;
	
	private boolean isHomeStyle;

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Expandable> List<T> getShowList() {
		return (List<T>) mShowList;
	}
	
	public List<ExpandCell> getDataList() {
		return mDataList;
	}
	
	public int getSelectItemId() {
		return mSelectedItemId;
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			notifyDataSetChanged();
		}
	};
	
    public EpsAdapter(Context context, ArrayList<EPS> EPSList,
            ArrayList<Project> projectList) {
        init(context, EPSList, projectList);
    }

	public EpsAdapter(Context context, ArrayList<EPS> EPSList,
			ArrayList<Project> projectList, EpsInterface epsInterface) {
		mEpsInterface = epsInterface;
		init(context, EPSList, projectList);
	}
	
   public EpsAdapter(Context context, ArrayList<EPS> EPSList,
            ArrayList<Project> projectList, boolean isHome, EpsInterface epsInterface) {
        mEpsInterface = epsInterface;
        isHomeStyle = isHome;
        init(context, EPSList, projectList);
    }
	
	private void init(Context context, ArrayList<EPS> EPSList,
            ArrayList<Project> projectList) {
        mContext = context;
        mEPSDataList = EPSList;
        mProjectDataList = projectList;

        formatExpandCell(mEPSDataList, mProjectDataList);
        filterRootNode(mDataList);
        //mHandler.sendEmptyMessage(0);
	}
	
	public void notifyDataChanged() {
	    formatExpandCell(mEPSDataList, mProjectDataList);
        filterRootNode(mDataList);
        mHandler.sendEmptyMessage(0);
	}

	public void updateTreeList(ArrayList<EPS> list) {
		mEPSDataList = list;
		mShowList.clear();
		formatExpandCell(mEPSDataList, mProjectDataList);
		filterRootNode(mDataList);
		mHandler.sendEmptyMessage(0);
	}

	public List<Project> getSelectedProject() {
		if (mSelectedItemId == -1)
			return mSelectedProjects;
		for (int i = 0; i < mProjectDataList.size(); i++) {
			if (mSelectedItemId == mProjectDataList.get(i).getProject_id()) {
				mSelectedProjects.add(mProjectDataList.get(i));
				break;
			}
		}
		return mSelectedProjects;
	}

	public void clearSelectedProject() {
		mSelectedProjects.clear();
		setSelected(-1, false);
	}

	private void formatExpandCell(ArrayList<EPS> epsList,
			ArrayList<Project> projectlist) {
		for (int i = 0; i < epsList.size(); i++) {
			ExpandCell cell = new ExpandCell();
			cell.setExpanded(false);
			cell.setHasChild(epsList.get(i).isHas_child());
			cell.setLevel(epsList.get(i).getLevel());
			cell.setEPS(true);
			cell.setEPSId(epsList.get(i).getEps_id());
			cell.setProjectId(-1);
			cell.setCellName(epsList.get(i).getName());
			cell.setParentId(epsList.get(i).getParents_id());
			mDataList.add(cell);
		}

		for (int i = 0; i < projectlist.size(); i++) {
			ExpandCell cell = new ExpandCell();
			cell.setExpanded(false);
			cell.setHasChild(false);
			cell.setEPS(false);
			cell.setEPSId(-1);
			cell.setProjectId(projectlist.get(i).getProject_id());
			cell.setCellName(projectlist.get(i).getName());
			cell.setParentId(projectlist.get(i).getEps_id());
			for (EPS eps : epsList) {
				if (eps.getEps_id() == projectlist.get(i).getEps_id()) {
					cell.setLevel(eps.getLevel() + 1);
					break;
				}
			}
			
			cell.setTenantId(projectlist.get(i).getTenant_id());
			mDataList.add(cell);

			resetDateList(projectlist.get(i).getEps_id());
		}
	}

	private void resetDateList(int epsid) {
		for (int i = 0; i < mDataList.size(); i++) {
			if (epsid == mDataList.get(i).getEPSId()) {
				mDataList.get(i).setHasChild(true);
				break;
			}
		}
	}

	private void filterRootNode(List<ExpandCell> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getLevel() == 0 && list.get(i).getParentId() == 0) {
				mShowList.add(list.get(i));
			} else if (list.get(i).getParentId() == 0)
				mShowList.add(list.get(i));
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.project_list_simple_eps_item, parent, false);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.menu_icon = (ImageView) convertView.findViewById(R.id.menu_icon);
			holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.eps_item_linearlayout);
			
			convertView.setTag(holder);
		}

		initListIcon(position);
		initListItem(holder, position);
		holder.text.setText(getItem(position).getCellName());
		TextPaint tp = holder.text.getPaint();
		if (getItem(position).isEPS()) {
			tp.setFakeBoldText(true);
			holder.menu_icon.setVisibility(View.VISIBLE);
		} else {
			tp.setFakeBoldText(false);
			holder.menu_icon.setVisibility(View.GONE);
		}
		
		if (isHomeStyle) {
		    holder.text.setTextColor(Color.WHITE);
		    holder.text.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources()
                    .getDimension(R.dimen.sp14_s));
		} else {
		    holder.text.setTextColor(Color.BLACK);
		}

		if (mSelectedPosition == position) {
            if (isHomeStyle) {
                convertView.setBackgroundColor(Color.parseColor("#565867"));
            } else {
                convertView.setBackgroundResource(R.color.listview_selected_bg);
            }			
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return mShowList.size();
	}

	@Override
	public ExpandCell getItem(int position) {
		return mShowList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private void initListIcon(int position) {
		int level = mShowList.get(position).getLevel();
//		holder.icon.setPadding(UtilTools.dp2pxW(mContext, 11.27f) * (level + 1), holder.icon.getPaddingTop(),
//				0, holder.icon.getPaddingBottom());
		LinearLayout.LayoutParams params = (LayoutParams) holder.icon.getLayoutParams();
        params.setMarginStart(UtilTools.dp2pxW(mContext, 11.27f) * (level + 1));
		holder.icon.setLayoutParams(params);
		if (mShowList.get(position).hasChild()
				&& (mShowList.get(position).isExpanded() == false)) {
			holder.icon.setImageResource(R.drawable.item_collapse);
			holder.icon.setVisibility(View.VISIBLE);
		} else if (mShowList.get(position).hasChild()
				&& (mShowList.get(position).isExpanded() == true)) {
			holder.icon.setImageResource(R.drawable.item_expand);
			holder.icon.setVisibility(View.VISIBLE);
		} else if (!mShowList.get(position).hasChild()) {
			holder.icon.setImageResource(R.drawable.item_collapse);
			holder.icon.setVisibility(View.INVISIBLE);
		}
	}

	private void initListItem(ViewHolder holder, final int position) {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				updateListView(position);
				if (isHomeStyle) {
				    mEpsInterface.initListItem(getItem(position));
				    setSelected(position, false);
				} else {
    				if (!getItem(position).isEPS()) {
    					setSelected(position, true);
    					if (mEpsInterface != null) {
    					    mEpsInterface.initListItem(getItem(position));
    					}
    				} else {
    					if (mEpsInterface != null) {
    					    mEpsInterface.initListItem(getItem(position));
    					}
    					setSelected(-1, false);
    				}
				}
			}
		};
		holder.linearLayout.setOnClickListener(listener);
	}

	public void updateListView(int position) {
		if (!mShowList.get(position).hasChild()) {
			return;
		}

		if (mShowList.get(position).isExpanded()) {
			mShowList.get(position).setExpanded(false);
			ExpandCell cell = mShowList.get(position);
			ArrayList<ExpandCell> temp = new ArrayList<ExpandCell>();

			for (int i = position + 1; i < mShowList.size(); i++) {
				if (cell.getLevel() >= mShowList.get(i).getLevel()) {
					break;
				}
				temp.add(mShowList.get(i));
			}

			mShowList.removeAll(temp);
			mHandler.sendEmptyMessage(0);

		} else {
			mShowList.get(position).setExpanded(true);
			int level = mShowList.get(position).getLevel();
			int nextLevel = level + 1;
			int j = 1;

			for (ExpandCell cell : mDataList) {
				if (cell.getParentId() == mShowList.get(position).getEPSId()) {
					cell.setLevel(nextLevel);
					cell.setExpanded(false);
					mShowList.add(position + j, cell);
					j++;
				}
			}
			mHandler.sendEmptyMessage(0);
		}
	}

	public void setSelected(int position, boolean isProject) {
		mSelectedPosition = position;
		if (isProject) {
			mSelectedItemId = getItem(position).getProjectId();
		} else {
			mSelectedItemId = -1;
		}
		
		mHandler.sendEmptyMessage(0);
	}

	class ViewHolder {
		ImageView icon;
		ImageView menu_icon;
		TextView text;
		LinearLayout linearLayout;
	}

	public static class ExpandCell extends Expandable{
		@Override
		public String toString() {
			return "ExpandCell [mCellName=" + mCellName + ", mExpanded="
					+ mExpanded + ", mEPSId=" + mEPSId + ", mProjectId="
					+ mProjectId + ", mParentId=" + mParentId + ", mIsEPS="
					+ mIsEPS + ", mTenantId=" + mTenantId + ", mLevel="
					+ mLevel + ", mHasChild=" + mHasChild + "]";
		}

		String mCellName;
		boolean mExpanded;
		int mEPSId;
		int mProjectId;
		int mParentId;
		boolean mIsEPS;
		int mTenantId;
		int mLevel;
		boolean mHasChild;

		public void setCellName(String name) {
			mCellName = name;
		}

		public String getCellName() {
			return mCellName;
		}

		public boolean isExpanded() {
			return mExpanded;
		}

		public void setEPSId(int id) {
			mEPSId = id;
		}

		public int getEPSId() {
			return mEPSId;
		}

		public void setProjectId(int id) {
			mProjectId = id;
		}

		public int getProjectId() {
			return mProjectId;
		}

		public void setParentId(int id) {
			mParentId = id;
		}

		public int getParentId() {
			return mParentId;
		}

		public void setEPS(boolean isEPS) {
			mIsEPS = isEPS;
		}

		public boolean isEPS() {
			return mIsEPS;
		}

		public void setExpanded(boolean expanded) {
			mExpanded = expanded;
		}

		public boolean getExpanded() {
			return mExpanded;
		}

		public void setLevel(int level) {
			mLevel = level;
		}

		public int getLevel() {
			return mLevel;
		}

		public void setHasChild(boolean hasChild) {
			mHasChild = hasChild;
		}

		public boolean hasChild() {
			return mHasChild;
		}
		
		public void setTenantId(int tenantId) {
			mTenantId = tenantId;
		}

		public int getTenantId() {
			return mTenantId;
		}

		@Override
		public int getId() {
			if (mIsEPS) {
				return mEPSId;
			} else {
				return -mProjectId;
			}
			
		}

		@Override
		public int getParents_id() {
			return mParentId;
		}

		@Override
		public void setParentsId(int parent_id) {
			mParentId = parent_id;			
		}
	}
	
	public static interface EpsInterface {

		void initListItem(ExpandCell expandCell);
		
	}

}
