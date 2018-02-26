package com.pm360.cepm360.app.module.document;

import android.annotation.SuppressLint;

import com.pm360.cepm360.entity.Document;
import com.pm360.cepm360.entity.Files;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {

    public static String getFileFormat(String key) {
        String formats = "";
        if (key.equals("picture")) {
            formats = "'jpg'," + "'jpeg'," + "'gif'," + "'png'," + "'bmp',"
                    + "'wbmp'";
        } else if (key.equals("word")) {
            formats = "'doc'," + "'docx'";
        } else if (key.equals("excel")) {
            formats = "'xls'," + "'xlsx'";
        } else if (key.equals("text")) {
            formats = "'txt'," + "'log'," + "'xml'," + "'ini'," + "'lrc'";
        } else if (key.equals("pdf")) {
            formats = "'pdf'";
        } else if (key.equals("ppt")) {
            formats = "'ppt'," + "'pptx'";
        } else if (key.equals("autocad")) {
            formats = "'dwg'";
        } else if (key.equals("other")) {
            formats = "'zip'," + "'mtz'," + "'rar'";
        } else if (key.equals("video")) {
            formats = "'mp4'," + "'wmv'," + "'mpeg'," + "'m4v'," + "'3gp',"
                    + "'3gpp'," + "'3g2'," + "'3gpp2'," + "'asf'";
        } else {
            formats = "error";
        }

        return formats;
    }

    public static void formatDirectoryList(List<Document> directoryList,
            List<Document> target) {
        for (int i = 0; i < target.size(); i++) {
            directoryList.add(target.get(i));
        }

        for (int i = 0; i < directoryList.size(); i++) {
            int parentId = directoryList.get(i).getParents_id();
            for (int j = 0; j < i; j++) {
                if (parentId == directoryList.get(j).getDocument_id()) {
                    int level = directoryList.get(j).getLevel() + 1;
                    directoryList.get(i).setLevel(level);
                    directoryList.get(j).setHas_child(true);
                }
            }

        }
    }

    public static void calculateFileCount(List<Files> fileList,
            List<Document> mDirectoryList) {
        if (fileList == null || fileList.size() <= 0)
            return;
        for (int i = 0; i < fileList.size(); i++) {
            for (int j = 0; j < mDirectoryList.size(); j++) {
                if (fileList.get(i).getDirectory_id().equals(mDirectoryList.get(j)
                        .getDocument_id()+"")) {
                    int count = mDirectoryList.get(j).getFile_count();
                    mDirectoryList.get(j).setFile_count(count + 1);
                    if (mDirectoryList.get(j).getParents_id() > 0) {
                        setParentDirectoryCount(mDirectoryList,
                                mDirectoryList.get(j), 1);
                    }
                }
            }
        }
    }
    
    /**
     * 计算目录包含的文件个数的递归函数
     * 
     * @param directoryList
     * @param directory
     * @param count
     */
    public static void setParentDirectoryCount(List<Document> directoryList,
            Document directory, int count) {
        for (int i = 0; i < directoryList.size(); i++) {
            if (directory.getParents_id() == directoryList.get(i)
                    .getDocument_id()) {
                int fileCount = directoryList.get(i).getFile_count();
                directoryList.get(i).setFile_count(fileCount + count);
                // 递归设置目录计算
                if (directoryList.get(i).getParents_id() > 0) {
                    setParentDirectoryCount(directoryList,
                            directoryList.get(i), count);
                }
                // 找到就跳出循环
                break;
            }
        }
    }

    public static void removeFromFileList(List<Files> fileList, Files file) {
        for (int i = 0; i < fileList.size(); i++) {
            if (fileList.get(i).getDirectory_id() == file.getDirectory_id()) {
                fileList.remove(fileList.get(i));
                break;
            }
        }
    }
    
    private static ArrayList<String> mServerFilePath = new ArrayList<String>();

    public static String calculateDirPath(List<Document> dirList,
            Document currentDir) {
        String filePath = "/";
        mServerFilePath.clear();
        mServerFilePath.add(currentDir.getName());
        calculateDirPaths(dirList, currentDir);
        for (int i = mServerFilePath.size() - 1; i >= 0; i--) {
            filePath += mServerFilePath.get(i) + "/";
        }
        return filePath;
    }

    private static void calculateDirPaths(List<Document> dirList,
            Document currentDir) {
        for (Document dir : dirList) {
            if (currentDir.getParents_id() != 0
                    && currentDir.getParents_id() == dir.getDocument_id()) {
                mServerFilePath.add(dir.getName());
                calculateDirPaths(dirList, dir);
                break;
            }
        }
    }
	    
    @SuppressLint("SimpleDateFormat")
    public static Boolean compareDate(Date d1,Date d2){  
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        String s1 = sdf.format(d1);  
        String s2 = sdf.format(d2);  
        if(s1.equals(s2))
            return true;  
        else 
            return false;  
    } 
}
