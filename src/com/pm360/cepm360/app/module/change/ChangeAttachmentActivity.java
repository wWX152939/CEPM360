package com.pm360.cepm360.app.module.change;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.common.adpater.SimpleGridViewAdapter;
import com.pm360.cepm360.app.common.view.ZoomImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 附件查看
 * 
 * 这里是主要是维护附件的路径
 * 根据传入的路径列表，显示，查看，可以添加，删除操作，然后返回最后的路径列表
 * 返回之后，ChangeDocumentFragment根据路径列表的变化，来完成添加，删除，更新的操作
 *
 */
public class ChangeAttachmentActivity extends Activity {

    private GridView mGridView;
    private SimpleGridViewAdapter mGridViewAdapter;
    private ZoomImageView mContentImageView;
    private ImageView mForwardAction, mNextAction;
    private Button mSaveButton, mCancelButton;
    private String mContentImagePath;
    private int mCurrentPosition;
    private Dialog mPickDialog;
    private boolean mViewOnly;
    
    private ArrayList<String> mThumbs = new ArrayList<String>();

    private static final int IMAGE_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final String CHANGE_DIR = Environment.getExternalStorageDirectory()
            + "/CEPM360/ChangeDocument/";
    private static final String CAMERA_UPLOAD_DIR = CHANGE_DIR + "upload/";
    private static final String CAMERA_UPLOAD_PIC = CAMERA_UPLOAD_DIR + "workupload.jpg";
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.change_attachment_activity);
        
        Intent intent = getIntent();
        @SuppressWarnings("unchecked")
        ArrayList<String> thumbs = (ArrayList<String>) intent.getSerializableExtra("thumbs");
        mViewOnly = intent.getBooleanExtra("viewonly", false);
        
        if (thumbs.size() > 0) {
            mThumbs.addAll(thumbs);
        }
        if (thumbs.size() < 3 && !mViewOnly) {
            mThumbs.add(mThumbs.size(),"DEFAULT_ICON");
        }
              
        mContentImageView = (ZoomImageView) findViewById(R.id.content_imageview);
        mForwardAction = (ImageView) findViewById(R.id.forward);
        mNextAction = (ImageView) findViewById(R.id.next);
        mForwardAction.setOnClickListener(mClicklistener);
        mNextAction.setOnClickListener(mClicklistener);
        
        mSaveButton = (Button) findViewById(R.id.btn_left);
        mCancelButton = (Button) findViewById(R.id.btn_right);
        mSaveButton.setText(getString(R.string.save));
        mCancelButton.setText(getString(R.string.cancel));
        mSaveButton.setOnClickListener(mClicklistener);
        mCancelButton.setOnClickListener(mClicklistener);
        
        initGridView();
        initPickDialog();
    }
    
    private void initGridView() {
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridViewAdapter = new SimpleGridViewAdapter(this, mThumbs);
        mGridViewAdapter.setMaxItemCount(3);
        mGridViewAdapter.setViewOnly(mViewOnly);
        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                String path = mGridViewAdapter.getItem(position);
                if (path.equals("DEFAULT_ICON") && mThumbs.size() < 4) {
                    mPickDialog.show();
                } else {
                    mCurrentPosition = position;
                    mContentImagePath = mGridViewAdapter.getItem(position);
                    setContentImageView(mContentImagePath);
                    updateActionIcons(mThumbs, position);
                }
            }
        });

        if (mThumbs.size() > 1) {
            mCurrentPosition = 0;
            mContentImagePath = mGridViewAdapter.getItem(0);
            setContentImageView(mContentImagePath);
        }
    }
    
    private void initPickDialog() {
        mPickDialog = new Dialog(this, R.style.MyDialogStyle);
        mPickDialog.setContentView(R.layout.change_attachment_pic_pick_dialog);

        ((Button) mPickDialog.findViewById(R.id.local_picture)).setOnClickListener(mClicklistener);
        ((Button) mPickDialog.findViewById(R.id.take_picture)).setOnClickListener(mClicklistener);
        mPickDialog.setCanceledOnTouchOutside(true);
    }
    
    View.OnClickListener mClicklistener = new View.OnClickListener() {           
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
            case R.id.forward: // 上一张
                mCurrentPosition--;
                setContentImageView(mThumbs.get(mCurrentPosition));
                break;
            case R.id.next: // 下一张
                mCurrentPosition++;
                setContentImageView(mThumbs.get(mCurrentPosition));
                break;
            case R.id.btn_left: // 保存
                setResult();
                break;
            case R.id.btn_right: // 关闭
                finish();
                break;
            case R.id.local_picture: // 图片
                pickLocalPicture();
                mPickDialog.dismiss();
                break;
            case R.id.take_picture:  // 拍照
                pickByCapture();
                mPickDialog.dismiss();
                break;
            }            
        }
    };
    
    private void updateActionIcons(List<String> thumbs, int position) {
        if (thumbs.size() > 1) {
            mForwardAction.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
            if (position >= 0 && position < thumbs.size() - 1) {
                mNextAction.setVisibility(View.VISIBLE);
            } else {
                mNextAction.setVisibility(View.GONE);
            }
            if (position == thumbs.size() - 2) {
                if (mThumbs.get(mThumbs.size() - 1).equals("DEFAULT_ICON")) {
                    mNextAction.setVisibility(View.GONE);
                }
            }
        } else {
            mForwardAction.setVisibility(View.GONE);
            mNextAction.setVisibility(View.GONE);    
        }
    }
    
    private void setContentImageView(String path) {
        if ((new File(path)).exists()) {
            try {
                Bitmap cambitmap = BitmapFactory.decodeFile(path); 
                if(null != cambitmap ) {
                    //int scale = ImageUtils.reckonThumbnail(cambitmap.getWidth(), cambitmap.getHeight(), 480, 360);
                    //Bitmap bitMap = ImageUtils.PicZoom(cambitmap, cambitmap.getWidth() / scale, cambitmap.getHeight() / scale);
                    //cambitmap.recycle();
                    mContentImageView.setScaleType(ScaleType.FIT_CENTER);
                    mContentImageView.setImageBitmap(cambitmap); 
                    mContentImageView.setBackgroundColor(Color.BLACK);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mContentImageView.setImageDrawable(getResources().getDrawable(R.drawable.img_download_failed_big)); 
        }
        updateActionIcons(mThumbs, mCurrentPosition);
    }
    
    private void pickLocalPicture() {
        Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_PICK);
        startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
    }
    
    private void pickByCapture() {
        if (mThumbs.size() > 4) return;
        File file = new File(CAMERA_UPLOAD_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = Uri.fromFile(new File(CAMERA_UPLOAD_PIC));  
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }
    
    private void setResult() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        if (mThumbs.get(mThumbs.size() - 1).equals("DEFAULT_ICON")) {
            mThumbs.remove(mThumbs.size() - 1);
        }
        bundle.putSerializable("thumbs", mThumbs);
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        String path = "";
        switch (requestCode) {
        case CAMERA_REQUEST_CODE:
            path = CAMERA_UPLOAD_PIC;
            break;
        case IMAGE_REQUEST_CODE:
            path = ImageUtils.UriToPath(this, data.getData());
            break;
        }
        try {
            Bitmap cambitmap = BitmapFactory.decodeFile(path); 
            if(null != cambitmap ) {
                int scale = ImageUtils.reckonThumbnail(cambitmap.getWidth(),
                        cambitmap.getHeight(), 480, 360);
                Bitmap bitMap = ImageUtils.PicZoom(cambitmap,
                        cambitmap.getWidth() / scale, cambitmap.getHeight() / scale);
                //cambitmap.recycle();
        
                String localPath = path;
                if (requestCode == CAMERA_REQUEST_CODE) {
                    localPath = CAMERA_UPLOAD_DIR + System.currentTimeMillis() + ".jpg";
                    ImageUtils.savaPhotoToLocal(bitMap, localPath);
                }
                mThumbs.add(mThumbs.size() - 1, localPath);
                if (mThumbs.size() == 4) {
                    mCurrentPosition = mThumbs.size() - 1;
                } else {
                    mCurrentPosition = mThumbs.size() - 2;
                }
                mGridViewAdapter.notityRefresh();
                mContentImageView.setImageBitmap(bitMap);
                mContentImageView.setBackgroundColor(Color.BLACK);
                updateActionIcons(mThumbs, mCurrentPosition);
            }
        } catch (Exception e) {           
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}