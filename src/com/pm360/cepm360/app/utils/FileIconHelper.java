package com.pm360.cepm360.app.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.pm360.cepm360.R;
import com.pm360.cepm360.entity.Files;

import java.util.HashMap;

public class FileIconHelper {
	
    private static HashMap<String, Integer> fileExtToIcons = new HashMap<String, Integer>();

    static {
        addItem(new String[] {
            "mp3"
        }, R.drawable.file_icon_mp3);
        addItem(new String[] {
            "wma"
        }, R.drawable.file_icon_wma);
        addItem(new String[] {
            "wav"
        }, R.drawable.file_icon_wav);
        addItem(new String[] {
            "mid"
        }, R.drawable.file_icon_mid);
        addItem(new String[] {
                "mp4", "wmv", "mpeg", "m4v", "3gp", "3gpp", "3g2", "3gpp2", "asf"
        }, R.drawable.file_icon_video);
        addItem(new String[] {
                "jpg", "jpeg", "gif", "png", "bmp", "wbmp"
        }, R.drawable.file_documents_picture);
        addItem(new String[] {
                "txt", "log", "xml", "ini", "lrc"
        }, R.drawable.file_documents_txt);
        addItem(new String[] {
                "xls", "xlsx"
        }, R.drawable.file_documents_xls);        
        addItem(new String[] {
        		"doc", "docx"
        }, R.drawable.file_documents_doc);
        
        addItem(new String[] {
        		"ppt", "pptx"
        }, R.drawable.file_documents_ppt);
        
        addItem(new String[] {
            "pdf"
        }, R.drawable.file_documents_pdf);
        addItem(new String[] {
            "zip"
        }, R.drawable.file_icon_zip);
        addItem(new String[] {
            "mtz"
        }, R.drawable.file_icon_theme);
        addItem(new String[] {
            "rar"
        }, R.drawable.file_icon_rar);
    }

    @SuppressLint("DefaultLocale") 
    private static void addItem(String[] exts, int resId) {
        if (exts != null) {
            for (String ext : exts) {
                fileExtToIcons.put(ext.toLowerCase(), resId);
            }
        }
    }

    @SuppressLint("DefaultLocale") 
    public static int getFileIcon(String ext) {
    	if (ext != null) {
    		Integer i = fileExtToIcons.get(ext.toLowerCase());
    		if (i != null) {
    			return i.intValue();
    		}
    	}
    	
    	return R.drawable.file_documents_other;
    }
    
    public static int getIcon(Files fileInfo) {
        String filePath = fileInfo.getPath();
        return getIconByFilename(filePath);
    }
    
    public static int getIconByFilename(String filename) {
        String extFromFilename = null;
        if (!TextUtils.isEmpty(filename)) {
            int dotPosition = filename.lastIndexOf('.');
            if (dotPosition != -1) {
                extFromFilename = filename.substring(dotPosition + 1,
                        filename.length());
            }
        }

        return getFileIcon(extFromFilename);
    }

}
