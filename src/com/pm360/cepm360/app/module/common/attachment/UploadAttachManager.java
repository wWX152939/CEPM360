package com.pm360.cepm360.app.module.common.attachment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.ProgressBar;

import com.pm360.cepm360.R;
import com.pm360.cepm360.app.cache.UserCache;
import com.pm360.cepm360.app.common.activity.DataManagerInterface;
import com.pm360.cepm360.app.common.adpater.DataTreeListAdapter;
import com.pm360.cepm360.app.utils.UtilTools;
import com.pm360.cepm360.common.GLOBAL;
import com.pm360.cepm360.common.ResultStatus;
import com.pm360.cepm360.common.json.AnalysisManager;
import com.pm360.cepm360.common.util.FileUtils;
import com.pm360.cepm360.common.util.LogUtil;
import com.pm360.cepm360.entity.Document;
import com.pm360.cepm360.entity.Files;
import com.pm360.cepm360.entity.Project;
import com.pm360.cepm360.entity.TaskCell;
import com.pm360.cepm360.entity.User;
import com.pm360.cepm360.services.document.RemoteDocumentsService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadAttachManager {
	
    private DataManagerInterface mDataManagerInterface;
    private String mDocumentName;
    private Project mProject;
    private Activity mActivity;
    private List<User> mUserList;
    private ProgressBar mProgressBar;
    
    public void setProgressBar(ProgressBar progressBar) {
    	mProgressBar = progressBar;
    }
    
    public UploadAttachManager(Activity activity, DataManagerInterface dataManagerInterface, Project project) {
    	mActivity = activity;
    	mDataManagerInterface = dataManagerInterface;
    	mProject = project;
    }
    
    public UploadAttachManager(Activity activity, DataManagerInterface dataManagerInterface, Project project, List<User> userList) {
    	mActivity = activity;
    	mDataManagerInterface = dataManagerInterface;
    	mProject = project;
    	mUserList = userList;
    }
    
    public void setProject(Project project) {
    	mProject = project;
    }
    
    public void setUserList(List<User> userList) {
    	mUserList = userList;
    }
    
    /**
     * 获取用户列表，如安全监督，质量文明，排除相同的user
     * @param t
     * @param listAdapter
     * @return
     */
    public static <T extends TaskCell> List<User> getUserList(T t, DataTreeListAdapter<T> listAdapter) {
    	List<User> userList = new ArrayList<User>();
    	
		String ccString = "";
		if (t.getCc_user() != null) {
			ccString = t.getCc_user().replace("(", "");
			ccString = ccString.replace(")", "");
		}
		String[] users = ccString.split(",");
		for (int i = 0; i < users.length; i++) {
			if (!users[i].equals("")) {
				userList.add(getUser(Integer.parseInt(users[i])));
			}
		}
		
		if (listAdapter == null) {
			// 门户模式 parent_id为父责任人
			if (t.getParents_id() != 0) {
				User user = getUser(t.getParents_id());
				if (!userList.contains(user)) {
					userList.add(user);
				}
			}
			
		} else {
			if (t.getParents_id() != 0) {
				// 正常模式
				for (T b : listAdapter.getShowList()) {
					if (b.getTask_id() == t.getParents_id()) {
						User user = getUser(b.getOwner());
						if (!userList.contains(user)) {
							userList.add(user);
						}
						
						break;
					}
				}
			}	
		}
		
		return userList;
    }
    
    /**
     * 获取用户列表，如安全监督，质量文明，排除相同的user
     * @param t
     * @param listAdapter
     * @return
     */
    public static <T extends TaskCell> String getUserString(T t, DataTreeListAdapter<T> listAdapter) {
    	String retUser = "";
		String ccString = "";
		if (t.getCc_user() != null) {
			ccString = t.getCc_user().replace("(", "");
			ccString = ccString.replace(")", "");
		}
		
		retUser = ccString;
		if (listAdapter == null) {
			// 门户模式 parent_id为父责任人
			if (!retUser.contains(t.getParents_id() + "") && t.getParents_id() != 0) {
				if (retUser.isEmpty()) {
					retUser += t.getParents_id();
				} else {
					retUser += "," + t.getParents_id();
				}
			}
		} else {
			if (t.getParents_id() != 0) {
				for (T b : listAdapter.getShowList()) {
					if (b.getTask_id() == t.getParents_id()) {
						if (!retUser.contains(b.getOwner() + "")) {
							if (retUser.isEmpty()) {
								retUser += b.getOwner();
							} else {
								retUser += "," + b.getOwner();
							}
						}
						
						break;
					}
				}
			}
		}
		
		return retUser;
    }
    
    private static User getUser(int userId) {
		User user = new User();
		user.setTenant_id(UserCache.getCurrentUser().getTenant_id());
		user.setUser_id(userId);
		return user;
	}
    
    private String getFileName(String path) {
        String name = "";
        String[] paths = path.split("/");
        if (paths.length > 1) {
            name = paths[paths.length - 1];
            int pointIndex = name.lastIndexOf('.');
            if (pointIndex == -1) {
                name = name.substring(0, name.length());
            } else {
                name = name.substring(0, pointIndex);
            }
        }
        return name;
    }
    
    public static void saveBitmap(Bitmap bm, File f) {
	  if (f.exists()) {
		  f.delete();
	  }
	  
	  FileUtils.createOrExistsFolder(f.getParentFile());
	  try {
		  FileOutputStream out = new FileOutputStream(f);
		  bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
		  out.flush();
		  out.close();
	  } catch (FileNotFoundException e) {
		  e.printStackTrace();
	  } catch (IOException e) {
		  e.printStackTrace();
	  }

    }
    
    private static File addPictureWaterMarker(Activity activity, String srcPath, String dstPath, List<String> waterMarkerList) {
    	if (waterMarkerList == null || waterMarkerList.isEmpty()) {
    		return null;
    	}
    	
    	Bitmap srcBitmap = BitmapFactory.decodeFile(srcPath);
    	if (srcBitmap == null) {
    		return null;
    	}
    	
    	Bitmap photo = srcBitmap.copy(Bitmap.Config.ARGB_8888, true);
    	int width = photo.getWidth(); 
    	int hight = photo.getHeight();
    	Canvas canvas = new Canvas(photo);//初始化画布绘制的图像到icon上  
    	 
    	Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//设置画笔 
    	float textSize = (float) ((24 * Math.sqrt((width * width) + (hight * hight))) / Math.sqrt(1280 * 1280 + 800 * 800));
    	float offset = (float) (5 * width / 1280);
    	textPaint.setTextSize(textSize);//字体大小
    	textPaint.setTypeface(Typeface.DEFAULT_BOLD);//采用默认的宽度  
    	textPaint.setColor(Color.WHITE);//采用的颜色  
    	
    	int left = width / 128;

    	Paint backgroundPaint = new Paint();
    	backgroundPaint.setARGB(160, 0, 0, 0);
    	float rectWidth = getMaxStringLength(waterMarkerList) * textSize / 2 + offset;
    	if (textSize == 24) {
    		rectWidth += 12;
    	}
    	LogUtil.i("wzw textSize:" + textSize + " width:" + width + " hight:" + hight
    			+ "rectWidth:" + rectWidth + " of:" + offset + "src:" + srcPath + "dst:" + dstPath);
    	float rectHeight = hight - textSize + UtilTools.dp2pxH(activity.getBaseContext(), 5);
    	canvas.drawRect(left, hight - textSize * (waterMarkerList.size() + 1), rectWidth, rectHeight, backgroundPaint);
    	for (int i = 0; i < waterMarkerList.size(); i++) {
    		canvas.drawText(waterMarkerList.get(i), left, hight - textSize * (waterMarkerList.size() - i), textPaint);//绘制上去字，开始未知x,y采用那只笔绘制 
    	}
    	canvas.save(Canvas.ALL_SAVE_FLAG); 
    	canvas.restore();

  	  	File f = new File(dstPath);
    	saveBitmap(photo, f);
    	if (!photo.isRecycled()) {
    		photo.recycle();
    	}
    	return f;
    }
    
    @SuppressLint("ResourceAsColor") 
    private File addPictureWaterMarker(Activity activity, String srcPath, String dstPath, String projectName, String taskName, String typeName, String typeName2) {
    	if (projectName == null || taskName == null) {
    		return null;
    	}

    	projectName = mActivity.getString(R.string.project_name) + ":" + projectName;
    	taskName = mActivity.getString(R.string.work_task) + ":" + taskName;
    	typeName = mActivity.getString(R.string.file_type) + ":" + typeName;
    	if (typeName2 != null) {
    		typeName += "-" + typeName2;
    	}
    	
  	  	String timeName = mActivity.getString(R.string.capture_time) + ":" + UtilTools.getCurrentTime("yyyy-MM-dd HH:mm:ss");
  	  	List<String> showList = new ArrayList<String>();
  	  	showList.add(projectName);
  	  	showList.add(taskName);
  	  	showList.add(typeName);
  	  	showList.add(timeName);
  	  	return addPictureWaterMarker(activity, srcPath, dstPath, showList);
    	
    }
    
    private static int getMaxStringLength(List<String> showList) {
    	int defaultLength = UtilTools.calcStringByte(showList.get(0));
    	for (int i = 1; i < showList.size(); i++) {
    		int iterLength = UtilTools.calcStringByte(showList.get(i));
    		if (defaultLength < iterLength) {
    			defaultLength = iterLength;
    		}
    	}
    	return defaultLength;
    }

    /**
     * 正常上传
     * @param path
     * @param type
     * @param id
     * @param taskId
     */
    public void uploadDocument(String path, int type, int id, int taskId) {
    	uploadDocument(path, type, taskId, taskId, null, null, null);
    }
    
    /**
     * 排除userList中相同的元素
     * @param userList
     * @return
     */
    private static List<User> getUniqueUserList(List<User> userList) {
    	if (userList == null) {
    		return null;
    	}
    	List<User> uniqueUserList = new ArrayList<User>();
    	for (User user : userList) {
    		int i = 0;
    		for (; i < uniqueUserList.size(); i++) {
    			if (uniqueUserList.get(i).getUser_id() == user.getUser_id()) {
    				break;
    			}
    		}
    		if (i == uniqueUserList.size()) {
    			uniqueUserList.add(user);
    		}
    	}
    	
    	return uniqueUserList;
    }
    
    /**
     * 上传文档
     * @param files
     * @param dataManagerInterface
     */
    public static void uploadDocument(Files files, List<User> userList, DataManagerInterface dataManagerInterface) {
    	uploadDocument(null, files, userList, null, dataManagerInterface);
    }
    
    /**
     * 添加水印上传文档
     * @param activity
     * @param files
     * @param userList
     * @param waterMarker 需要显示的水印文字
     * @param dataManagerInterface
     */
    public static void uploadDocument(Activity activity, Files files, List<User> userList, List<String> waterMarker, DataManagerInterface dataManagerInterface) {
    	File file = new File(files.getPath());
    	files.setPath(getFilePath(files.getDir_type(), file.getName()));
    	if (activity != null) {
    		//添加水印，需要在添加水印之前重定向，之后将会将文件拷贝到指定路径中
    		File f = addPictureWaterMarker(activity, file.getPath(), files.getPath(), waterMarker);
    		if ( f != null) {
    			file = f;
    		}
    	}
    	userList = getUniqueUserList(userList);
    	RemoteDocumentsService.getInstance().uploadFile(dataManagerInterface, 
        		null,
                files,
                UserCache.getCurrentUser().getTenant_id(),
                file, (userList == null ? new ArrayList<User>() : userList), 
                UserCache.getCurrentUser().getUser_id());
    }
    
    /**
     * 添加水印上传，提前预置好files对象，需要填写projectName和taskName
     * @param files
     * @param file
     * @param projectName
     * @param taskName
     * @param typeName
     */
    public void uploadDocument(Files files, File file, String projectName, String taskName, String typeName) {
    	int type = files.getDir_type();
    	if (type == 0) type = 1;
    	mDocumentName = GLOBAL.FILE_TYPE[type - 1][1];

    	files.setPath(getFilePath(files.getDir_type(), file.getName()));
    	File f = addPictureWaterMarker(mActivity, file.getPath(), files.getPath(), projectName, taskName, mDocumentName, typeName);
		if ( f != null) {
			file = f;
		}
        uploadFile(files, file);
        
    }
    
	/**
	 * 添加水印上传，需要填写projectName和taskName
	 * @param files
	 * @param file
	 * @param type GLOBAL.FILE_TYPE
	 */
    public void uploadDocument(String path, int type, int id, int taskId, String projectName, String taskName, String typeName) {
    	if (type == 0) type = 1;
    	
        File newFile = new File(path);

        Files files = new Files();
        files.setDir_type(type);
        files.setType_id(id);
        files.setTitle(getFileName(newFile.getPath()));
        files.setAuthor(UserCache.getCurrentUser().getUser_id());
        files.setTenant_id(UserCache.getCurrentUser().getTenant_id());
        files.setFile_size(newFile.length());
        files.setProject_id(mProject == null ? 0 : mProject.getProject_id());
        files.setTask_id(taskId);
        files.setDirectory_id("0");
        
        uploadDocument(files, newFile, projectName, taskName, typeName);
        
//      findRelevancePath(files, newFile, type);
        
    }
    
    public static String getFilePath(int fileType, String name) {
        String fileName = fileType == 0 ? "DEFAULT" : GLOBAL.FILE_TYPE[fileType - 1][1];
    	return GLOBAL.FILE_SAVE_PATH + "/" + fileName + "/" + name;
    }
    
    @SuppressWarnings("unused")
	private void findRelevancePath(final Files files, final File file, final int type) {        
        final Document directory = new Document();
        if (type == AttachmentFragment.ZH_WORKLOG_TYPE) {
        	directory.setDirectory_type(GLOBAL.DIR_TYPE_PERSONAL);
            directory.setUser_id(UserCache.getCurrentUser().getUser_id());
        } else if (type == AttachmentFragment.ZH_RISK_TYPE) {
        	directory.setDirectory_type(GLOBAL.DIR_TYPE_PROJECT);
            directory.setProject_id(mProject.getProject_id());
        } else if (type == AttachmentFragment.JH_WORKLOG_TYPE) {
        	directory.setDirectory_type(GLOBAL.DIR_TYPE_PERSONAL);
            directory.setUser_id(UserCache.getCurrentUser().getUser_id());
        } else if (type == AttachmentFragment.JH_SAFETY_TYPE) {
        	directory.setDirectory_type(GLOBAL.DIR_TYPE_PROJECT);
            directory.setProject_id(mProject.getProject_id());
        } else if (type == AttachmentFragment.JH_QUALITY_TYPE) {
        	directory.setDirectory_type(GLOBAL.DIR_TYPE_PROJECT);
            directory.setProject_id(mProject.getProject_id());
        } else if (type == AttachmentFragment.MAILBOX_ATTACH_TYPE) {
        	directory.setDirectory_type(GLOBAL.DIR_TYPE_PERSONAL);
            directory.setUser_id(UserCache.getCurrentUser().getUser_id());
        } else if (type == AttachmentFragment.TEMPLATE_TYPE) {
        	directory.setDirectory_type(GLOBAL.DIR_TYPE_PERSONAL);
            directory.setUser_id(UserCache.getCurrentUser().getUser_id());
        }
        
        directory.setTenant_id(UserCache.getCurrentUser().getTenant_id());
        
        RemoteDocumentsService.getInstance().getDirectoryList(new DataManagerInterface() {
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                int directoryId = -1;
                if (status.getCode() == AnalysisManager.SUCCESS_DB_QUERY) {
                    if (list != null && list.size() > 0) {
                        @SuppressWarnings("unchecked")
						List<Document> dList = (List<Document>) list;
                        for (Document d : dList) {
                        	if (d.getName().endsWith(mDocumentName)) {
                        		directoryId = d.getDocument_id();
                        		break;
                        	}
 
                        }

                        if (directoryId >= 0) {
                            files.setDirectory_id(directoryId+"");
                            
                            files.setPath("/" + mDocumentName + "/" + getFileName(file.getPath()));
                            uploadFile(files, file);
                        } else {
                        	//没有找到目标目录，创建
                        	addDirectory(directory, files, file);
                        }
                    } else {
                    	//没有目录，创建
                    	addDirectory(directory, files, file);
                    }
                }                
            }            
        }, directory);        
    }
    
    private void addDirectory(final Document directory, final Files files, final File file) {
        directory.setName(mDocumentName);
        directory.setParents_id(0);
        directory.setLevel(0);
        RemoteDocumentsService.getInstance().addDirectory(new DataManagerInterface() {
            @Override
            public void getDataOnResult(ResultStatus status, List<?> list) {
                if (status.getCode() == AnalysisManager.SUCCESS_DB_ADD) {
                    Document d = (Document) list.get(0);
                    directory.setDocument_id(d.getDocument_id());
                    files.setDirectory_id(d.getDocument_id()+"");
                    files.setPath("/" + mDocumentName + "/" + getFileName(file.getPath()));
                    uploadFile(files, file);
                }
            }                            
        }, directory);
    }
    
    // 同时上传对象和文件
    private void uploadFile(Files files, File file) {
    	mUserList = getUniqueUserList(mUserList);
        RemoteDocumentsService.getInstance().uploadFile(mDataManagerInterface, 
        		mProgressBar,
                files,
                UserCache.getCurrentUser().getTenant_id(),
                file, (mUserList == null ? new ArrayList<User>() : mUserList), 
                UserCache.getCurrentUser().getUser_id());
    }
   
}
