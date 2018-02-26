package com.pm360.cepm360.common.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;

public class FileUtils {
    // SD卡的根目录
    public static String SDPATH = Environment.getExternalStorageDirectory() + "/";
    // APP的私有数据文件根目录
    private String FILESPATH;
    // 运行上下文环境
    private Context mContext;

    /**
     * 获取APP的私有文件路径
     * 
     * @return
     */
    public final String getAPPFilePath() {
        return FILESPATH;
    }
    
    public FileUtils() {
    	
    }

    public FileUtils(Context context) {
        mContext = context;
        FILESPATH = mContext.getFilesDir().getPath() + "//";
    }

    /* TODO ======================= */

    /**
     * 测试文件是否存在
     * 
     * @param fileName
     * @return
     */
    public static boolean isFileExists(String fileName) {
        if (fileName == null || (fileName = fileName.trim()).equals("")) {
            return false;
        }

        File file = new File(fileName);
        return isFileExists(file);
    }

    public static boolean isFileExists(File file) {
        if (file == null)
            return false;
        return file.exists();
    }

    /**
     * 检查文件是否是目录
     * 
     * @param file
     * @return
     */
    public static boolean isDirectory(File file) {
        if (file == null)
            return false;

        return file.isDirectory();
    }

    public static boolean isDirectory(String fileName) {
        if (fileName == null || (fileName = fileName.trim()).equals("")) {
            return false;
        }
        File file = new File(fileName);
        return isDirectory(file);
    }

    /**
     * 测试是否是普通文件
     * 
     * @param file
     * @return
     */
    public static boolean isFile(File file) {
        if (file == null)
            return false;
        return file.isFile();
    }

    public static boolean isFile(String fileName) {
        if (fileName == null || (fileName = fileName.trim()).equals("")) {
            return false;
        }
        File file = new File(fileName);
        return isFile(file);
    }

    /**
     * 测试是否存在制定目录，不存在创建之
     * 
     * @param file
     * @return
     */
    public static boolean createOrExistsFolder(File file) {
        if (file == null)
            return false;

        boolean result = false;

        if (isFileExists(file) && isDirectory(file)) {
            return true;
        }

        // 创建目录
        if (file.mkdirs()) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public static boolean createOrExistsFolder(String fileName) {
        if (fileName == null || (fileName = fileName.trim()).equals("")) {
            return false;
        }
        File file = new File(fileName);
        return createOrExistsFolder(file);
    }

    /**
     * 如果不存在制定文件，创建文件
     * 
     * @param file
     * @return
     */
    public static boolean createOrExistsFile(File file) {
        if (file == null)
            return false;
        boolean result = false;

        if (isFileExists(file) && isFile(file)) {
            return true;
        }

        File parentFile = file.getParentFile();
        if (!createOrExistsFolder(parentFile)) {
            return false;
        }
        try {
            if (file.createNewFile()) {
                result = true;
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * 如果不存在制定文件，创建之
     * 
     * @param fileName
     * @return
     */
    public static boolean createOrExistsFile(String fileName) {
        if (fileName == null || (fileName = fileName.trim()).equals("")) {
            return false;
        }
        File file = new File(fileName);
        return createOrExistsFile(file);
    }

    /**
     * 创建一个新文件，如果文件已经存在则删除旧的文件
     * 
     * @param file
     * @return
     */
    public static boolean createFileByDeleteOldFileIfNeeded(File file) {
        delFile(file);
        return createOrExistsFile(file);
    }

    public static boolean createFileByDeleteOldFileIfNeeded(String fileName) {
        if (fileName == null || (fileName = fileName.trim()).equals("")) {
            return false;
        }
        File file = new File(fileName);
        return createFileByDeleteOldFileIfNeeded(file);
    }

    /**
     * 移动文件
     * 
     * @param srcDir
     * @param destDir
     * @return
     */
    public static boolean moveFilesTo(File srcDir, File destDir) {
        if (srcDir == null)
            return false;
        if (destDir == null)
            return false;
        if (!isFileExists(srcDir) || !isDirectory(srcDir)) {
            return false;
        }
        if (!isFileExists(destDir) || !isDirectory(destDir)) {
            if (!createOrExistsFolder(destDir)) {
                return false;
            }
        }

        File[] srcDirFiles = srcDir.listFiles();
        for (int i = 0; i < srcDirFiles.length; i++) {
            if (srcDirFiles[i].isFile()) {
                File oneDestFile = new File(destDir.getPath() + "//"
                        + srcDirFiles[i].getName());
                moveFileTo(srcDirFiles[i], oneDestFile);
                delFile(srcDirFiles[i]);
            } else if (srcDirFiles[i].isDirectory()) {
                File oneDestFile = new File(destDir.getPath() + "//"
                        + srcDirFiles[i].getName());
                moveFilesTo(srcDirFiles[i], oneDestFile);
                delDir(srcDirFiles[i]);
            }
        }
        return true;
    }

    public static boolean moveFileTo(File srcFile, File destFile) {
        if (srcFile == null)
            return false;
        if (destFile == null)
            return false;
        boolean iscopy = copyFileTo(srcFile, destFile);
        if (!iscopy)
            return false;
        if (!delFile(srcFile)) {
            return false;
        }
        return true;
    }

    /**
     * 删除制定文件
     * 
     * @param file
     * @return
     */
    public static boolean delFile(File file) {
        if (file == null)
            return false;
        if (!isFileExists(file)) {
            return true;
        }

        if (file.isDirectory())
            return false;

        return file.delete();
    }

    /**
     * 删除制定目录
     * 
     * @param dir
     * @return
     */
    public static boolean delDir(File dir) {
        if (dir == null)
            return false;
        if (!isFileExists(dir)) {
            return true;
        }

        if (!isDirectory(dir)) {
            return false;
        }

        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                if (!delFile(file)) {
                    return false;
                }
            } else if (file.isDirectory()) {
                if (!delDir(file)) {
                    return false;
                }
            }
        }

        if (!dir.delete()) {
            return false;
        }
        return true;
    }

    /**
     * 拷贝文件到制定目录
     * 
     * @param srcFile
     * @param destFile
     * @return
     */
    public static boolean copyFileTo(File srcFile, File destFile) {
        if (srcFile == null)
            return false;
        if (destFile == null)
            return false;
        if (!isFileExists(srcFile)) {
            return false;
        }

        if (!isFile(srcFile)) {
            return false;
        }
        if (isFileExists(destFile) && !isFile(destFile))
            return false;

        File parentFile = destFile.getParentFile();
        if (!createOrExistsFolder(parentFile)) {
            return false;
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean result = false;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            int readLen = 0;
            byte[] buf = new byte[1024];
            while ((readLen = fis.read(buf)) != -1) {
                fos.write(buf, 0, readLen);
            }
            fos.flush();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            try {
                fos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }

        }

        return result;
    }

    public static boolean copyFilesTo(File srcDir, File destDir) {
        if (srcDir == null)
            return false;
        if (destDir == null)
            return false;

        if (!isFileExists(srcDir) || !isDirectory(srcDir)) {
            return false;
        }

        if (!isFileExists(destDir) || !isDirectory(destDir)) {
            if (!createOrExistsFolder(destDir)) {
                return false;
            }
        }

        File[] srcFiles = srcDir.listFiles();
        for (int i = 0; i < srcFiles.length; i++) {
            if (srcFiles[i].isFile()) {
                File destFile = new File(destDir.getPath() + "//"
                        + srcFiles[i].getName());
                copyFileTo(srcFiles[i], destFile);
            } else if (srcFiles[i].isDirectory()) {
                File theDestDir = new File(destDir.getPath() + "//"
                        + srcFiles[i].getName());
                copyFilesTo(srcFiles[i], theDestDir);
            }
        }
        return true;
    }

    /**
     * 将输入流写入fileName文件中
     * 
     * @param fileName
     * @param is
     * @return
     */
    public static boolean writeToFileFromInputStream(String fileName, InputStream is) {
        if (fileName == null || (fileName = fileName.trim()).equals("")) {
            return false;
        }
        // 获取文件的目录File对象
        File parentFile = new File(fileName).getParentFile();
        if (!createOrExistsFolder(parentFile)) {
            // 如果不存在且创建失败，返回false
            return false;
        }

        boolean result = false;
        File file = null;
        OutputStream os = null;
        try {
            file = new File(fileName);
            os = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            int length = 0;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            os.flush();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            file = null;
            result = false;
        } finally {
            try {
                os.close();

            } catch (Exception e2) {
                e2.printStackTrace();
                file = null;

            }
        }
        return result;
    }

    /**
     * 以截取方式获取文件输出流，用于写文件
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    public static OutputStream writeFile(String fileName) throws IOException {
        File file = new File(fileName);

        // 获取文件目录File对象
        File parentFile = file.getParentFile();
        createOrExistsFolder(parentFile);
        FileOutputStream fos = new FileOutputStream(file);
        return fos;
    }

    /**
     * 以追加方式获取文件输出流，用于写文件
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    public static OutputStream appendFile(String fileName) throws IOException {
        File file = new File(fileName);

        File parentFile = file.getParentFile();
        createOrExistsFolder(parentFile);
        FileOutputStream fos = new FileOutputStream(file, true);
        return fos;
    }

    /**
     * 获取文件输入流，用于读取文件
     * 
     * @param fileName
     * @return
     * @throws IOException
     */
    public static InputStream readFile(String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        return fis;
    }

    /**
     * 判断文件是否存在，如果不存在创建之
     * 
     * @param fileName
     * @return
     */
    public boolean createOrExistsPrivateFile(String fileName) {
        File file = new File(FILESPATH + fileName);
        return createOrExistsFile(file);
    }

    public boolean createOrExistsPrivateFolder(String dirName) {
        File dir = new File(FILESPATH + dirName);
        return createOrExistsFolder(dir);
    }

    public boolean delDataFile(String fileName) {
        File file = new File(FILESPATH + fileName);
        return delFile(file);
    }

    public boolean delDataDir(String dirName) {
        File file = new File(FILESPATH + dirName);
        return delDir(file);
    }

    public boolean copyDataFileTo(String srcFileName, String destFileName) {
        File srcFile = new File(FILESPATH + srcFileName);
        File destFile = new File(FILESPATH + destFileName);
        return copyFileTo(srcFile, destFile);
    }

    public boolean copyDataFilesTo(String srcDirName, String destDirName) {
        File srcDir = new File(FILESPATH + srcDirName);
        File destDir = new File(FILESPATH + destDirName);
        return copyFilesTo(srcDir, destDir);
    }

    public boolean moveDataFileTo(String srcFileName, String destFileName) {
        File srcFile = new File(FILESPATH + srcFileName);
        File destFile = new File(FILESPATH + destFileName);
        return moveFileTo(srcFile, destFile);
    }

    public boolean moveDataFilesTo(String srcDirName, String destDirName) {
        File srcDir = new File(FILESPATH + srcDirName);
        File destDir = new File(FILESPATH + destDirName);
        return moveFilesTo(srcDir, destDir);
    }

    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState()
        				.equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean isSDFileExists(String fileName) {
        File file = new File(SDPATH + fileName);
        return file.exists();
    }

    public static File createOrExistsSDFile(String fileName) {
        File file = new File(SDPATH + fileName);
        if (createOrExistsFile(file))
            return file;
        return null;
    }

    public static boolean createSDDir(String dirName) {
        File file = new File(SDPATH + dirName);
        return createOrExistsFolder(file);
    }

    public static boolean Write2SDFromInputStream(String fileName, InputStream is) {
        String fileName2 = SDPATH + fileName;
        return writeToFileFromInputStream(fileName2, is);
    }

    public static boolean delSDFile(String fileName) {
        File file = new File(SDPATH + fileName);
        return delFile(file);
    }

    public static boolean delSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        return delDir(dir);
    }

    public static boolean renameSDFile(String oldfileName, String newFileName) {
        File oleFile = new File(SDPATH + oldfileName);
        File newFile = new File(SDPATH + newFileName);
        return moveFileTo(oleFile, newFile);
    }

    public static boolean copySDFileTo(String srcFileName, String destFileName) {
        File srcFile = new File(SDPATH + srcFileName);
        File destFile = new File(SDPATH + destFileName);
        return copyFileTo(srcFile, destFile);
    }

    public static boolean copySDFilesTo(String srcDirName, String destDirName) {
        File srcDir = new File(SDPATH + srcDirName);
        File destDir = new File(SDPATH + destDirName);
        return copyFilesTo(srcDir, destDir);
    }

    public static boolean moveSDFileTo(String srcFileName, String destFileName)
            throws IOException {
        File srcFile = new File(SDPATH + srcFileName);
        File destFile = new File(SDPATH + destFileName);
        return moveFileTo(srcFile, destFile);
    }

    public static boolean moveSDFilesTo(String srcDirName, String destDirName) {
        File srcDir = new File(SDPATH + srcDirName);
        File destDir = new File(SDPATH + destDirName);
        return moveFilesTo(srcDir, destDir);
    }

    public static OutputStream writeSDFile(String fileName) throws IOException {
        String filename2 = SDPATH + fileName;
        return writeFile(filename2);
    }

    public static OutputStream appendSDFile(String fileName) throws IOException {
        String filename2 = SDPATH + fileName;
        return appendFile(filename2);
    }

    public static InputStream readSDFile(String fileName) throws IOException {
        String filename2 = SDPATH + fileName;
        return readFile(filename2);
    }

    public OutputStream wirtePrivateFile(String fileName) throws IOException {
        OutputStream os = mContext.openFileOutput(fileName,
                Context.MODE_PRIVATE);
        return os;
    }

    public OutputStream appendPrivateFile(String fileName) throws IOException {
        OutputStream os = mContext
                .openFileOutput(fileName, Context.MODE_APPEND);
        return os;
    }

    /*----------------------------- common --------------------------------*/

    public static String getFileName(String filePath) {
        if (filePath == null) {
            return "";
        }
        String fileName = new File(filePath).getName();
        int pointIndex = fileName.lastIndexOf('.');
        if (pointIndex == -1) {
            return fileName.substring(0, fileName.length());
        }
        return fileName.substring(0, pointIndex);
    }
    
    /**
     * 日期格式2015-12-12-06-12-12转换为2015-12-12 06：12：12
     * @return
     */
    public static String formatFileDate(String inputDate) {
    	if (inputDate == null) {
    		return null;
    	}
    	String[] dateArray = inputDate.split("-");
    	if (dateArray.length != 6) {
    		return null;
    	}

    	String outputDate = "";
    	outputDate += dateArray[0] + "-";
    	outputDate += dateArray[1] + "-";
    	outputDate += dateArray[2] + " ";
    	outputDate += dateArray[3] + ":";
    	outputDate += dateArray[4] + ":";
    	outputDate += dateArray[5];
    	return outputDate;
    }

    public static String getDirctoryPath(String filePath) {
        if (filePath == null) {
            return "";
        }
        int pointIndex = filePath.lastIndexOf('/');
        if (pointIndex == -1) {
            return "";
        }
        return filePath.substring(0, pointIndex + 1);
    }
    
    public static String getFileFullName(String path) {
        if (path == null) {
            return "";
        }
        int pointIndex = path.lastIndexOf('/');
        if (pointIndex == -1) {
            return "";
        }
        return path.substring(pointIndex + 1, path.length());
    }
    
    public static String getFileSize(String path) {
        final File file = new File(path);
        if (!file.exists()) {
            return "0 B";
        }

        long size = file.length();
        
        return getFileSize(size);
    }
    
    public static String getFileSize(long size) {
        String cacheSize = size + " B";
        if (size >= 1024) {
            size /= 1024;
            cacheSize = size + " KB";
        }
        if (size >= 1024) {
            size /= 1024;
            cacheSize = size + " MB";
        }
        if (size >= 1024) {
            size /= 1024;
            cacheSize = size + " GB";
        }
        return cacheSize;
    }
    

    /**
     * 删除单个文件
     * 
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     * 
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        // 如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        // 遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                // 删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } else {
                // 删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        // 删除当前空目录
        return dirFile.delete();
    }

    /**
     * 获取文件夹的使用大小
     * 
     * @param directory 目录的文件
     * @return 占用的大小
     */
    public static long sizeOfDirectory(File directory) {
        if (!directory.exists()) {
            String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        long size = 0;

        File[] files = directory.listFiles();
        if (files == null) {
            return 0L;
        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            if (file.isDirectory()) {
                size += sizeOfDirectory(file);
            } else {
                size += file.length();
            }
        }
        return size;
    }
	
	public static void write(String path, String str) {
		final File file = new File(path);
		if (!file.exists()) {
			createOrExistsFile(file);
		}
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(str);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
