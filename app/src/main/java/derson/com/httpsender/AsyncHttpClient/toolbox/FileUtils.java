package derson.com.httpsender.AsyncHttpClient.toolbox;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

/**
 * 用于文件操作的工具类
 */
public class FileUtils {
    private final static String TAG = FileUtils.class.getSimpleName();
    private final static int BUFFER = 8192;
    private final static long ONE_DAY_MILLIS = 24*60*60*1000;//一天毫秒数


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getAvailableStorageSize(File dir) {
        long size = -1;
        if(dir != null && dir.exists() && dir.isDirectory()) {
        	try{
                StatFs statFs = new StatFs(dir.getPath());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    size = statFs.getAvailableBytes();
                } else {
                    size = (long) statFs.getAvailableBlocks() * (long) statFs.getBlockSize();
                }
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
        return size;
    }

    public static long getDirSize(File file) {
        long size = 0;
        if(null != file && file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (null != files && files.length > 0) {
                    for (File f : files) {
                        size += getDirSize(f);
                    }
                }
            } else {
                size += file.length();
            }
        }
        return size;
    }

    //复制文件或目录
    public static void copy(File sourceFile, File targetFile) throws IOException {
        if(null!=sourceFile && !sourceFile.exists()){
        }else{
            if(sourceFile.isFile()){
                copyFile(sourceFile, targetFile);
            } else {
                copyDirectory(sourceFile, targetFile);
            }
        }
    }

    //复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        if(null!=sourceFile && null!=targetFile){
        	try {
        		inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
        		outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
        		byte[] buffer = new byte[BUFFER];
        		int length;
        		while ((length = inBuff.read(buffer)) != -1) {
        			outBuff.write(buffer, 0, length);
        		}
        		outBuff.flush();
        	} finally {
                IOUtils.closeQuietly(inBuff);
                IOUtils.closeQuietly(outBuff);
        	}
        }
    }

    //复制文件夹
    public static void copyDirectory(File sourceDir, File targetDir)
            throws IOException {
        //新建目标目录
        targetDir.mkdirs();
        if(null!=sourceDir){
        	//遍历源目录下所有文件或目录
        	File[] file = sourceDir.listFiles();
        	for (int i = 0; i < file.length; i++) {
        		if (file[i].isFile()) {
        			File sourceFile = file[i];
        			File targetFile = new
        					File(targetDir.getAbsolutePath()
        							+ File.separator + file[i].getName());
        			copyFile(sourceFile, targetFile);
        		} else if (file[i].isDirectory()) {
        			File dir1 = new File(sourceDir, file[i].getName());
        			File dir2 = new File(targetDir, file[i].getName());
        			copyDirectory(dir1, dir2);
        		}
        	}
        }
    }

    //删除文件或目录
    public static boolean delete(File file) {
        if(null!=file && !file.exists()){
            Log.i(TAG, "the file is not exists: " + file.getAbsolutePath());
            return false;
        }else{
            if(null!=file && file.isFile()){
                return deleteFile(file);
            }else{
                return deleteDirectory(file, true);
            }
        }
    }

    //删除文件
    public static boolean deleteFile(File file) {
        if(null!=file && file.isFile() && file.exists()){
            file.delete();
            return true;
        }else{
            Log.i(TAG, "the file is not exists: " +
                    file.getAbsolutePath());
            return false;
        }
    }

    //删除目录
    public static boolean deleteDirectory(File dirFile, boolean includeSelf) {
        return deleteDirectory(dirFile, null, includeSelf, false);
    }

    //删除目录
    public static boolean deleteDirectory (
            File dirFile, String extension, boolean includeSelf, boolean onlyFile) {
        if(!dirFile.exists() || !dirFile.isDirectory()){
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for(int i=0;i<files.length;i++) {
            if(files[i].isFile()){
                if(extension == null ||
                        files[i].getName().toLowerCase().endsWith("." + extension.toLowerCase())) {
//                    System.out.println("DELETE FILE: " + files[i].getName());
                    flag = deleteFile(files[i]);
                    if(!flag){
                        break;
                    }
                }
            } else {
                if(!onlyFile) {
                    flag = deleteDirectory(files[i], true);
                    if(!flag){
                        break;
                    }
                }
            }
        }

        if(!flag){
            return false;
        }

        if(includeSelf) {
            if(dirFile.delete()){
                return true;
            }else{
                return false;
            }
        } else {
            return true;
        }
    }

    public static void move(File src, File dest) throws IOException {
        copy(src, dest);
        delete(src);
    }

    //从输入流读取文本内容
	public static String readTextInputStream(InputStream is) throws IOException {
		if(null==is) return null;
        StringBuffer strbuffer = new StringBuffer();
        String line;
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new InputStreamReader(is));
            while((line = reader.readLine()) != null) {
                strbuffer.append(line).append("\r\n");
            }
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return strbuffer.toString();
    }

    //从文件读取文本内容
	public static String readTextFile(File file) throws IOException {
        String text = null;
        InputStream is = null;
        if(null!=file){
        	try {
        		is = new FileInputStream(file);
        		text = readTextInputStream(is);;
        	} finally {
                IOUtils.closeQuietly(is);
        	}
        }
        return text;
    }

    //将文本内容写入文件
    public static void writeTextFile(File file, String str) throws IOException {
        DataOutputStream out = null;
        if(null!=file){
        	try {
        		out = new DataOutputStream(new FileOutputStream(file));
        		out.write(str.getBytes());
        	} finally {
                IOUtils.closeQuietly(out);
        	}
        }
    }

    //将一系列字符串写入文件
    public static void writeTextFile(File file, String[] strArray) throws IOException {
    	String str = "";
    	if(null!=file && null!=strArray){
    		for(int i=0; i<strArray.length; i++) {
    			str += strArray[i];
    			if(i!=strArray.length-1)
    				str += "\r\n";
    		}

    		DataOutputStream out = null;
    		try {
    			out = new DataOutputStream(new FileOutputStream(file));
    			out.write(str.getBytes());
    		} finally {
    			IOUtils.closeQuietly(out);
    		}
    	}
    }

    /***
     * 按照最后修改时间删除文件
     * @param dirFile
     * @param day  最后修改时间大于day
     * @return
     */
     public static boolean deleteDirectoryByTime(File dirFile,int day){
         if(null!=dirFile && !dirFile.exists() || !dirFile.isDirectory()){
             return false;
         }
         boolean flag = true;
         if(null!=dirFile){
        	 File[] files = dirFile.listFiles();
        	 if(null!=files && files.length>0){
        		 for(int i=0;i<files.length;i++) {
        			 File file = files[i];
        			 long  time = System.currentTimeMillis() - file.lastModified()-day*ONE_DAY_MILLIS;
        			 if(time>0){
        				 if(file.isDirectory()){
        					 flag = deleteDirectory(file,true);
        				 }else{
        					 flag = delete(file);
        				 }
        			 }
        		 }
        	 }
         }
         return flag;
     }

    //合并多个文本文件的内容到一个文件
    public static void combineTextFile(File[] sFiles, File dFile) throws IOException {
        BufferedReader in = null;
        BufferedWriter out = null;
        if(null!=dFile && null!=sFiles){
        	try {
        		out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dFile)));

        		for(int i=0; i<sFiles.length; i++) {
        			in = new BufferedReader(new InputStreamReader(new FileInputStream(sFiles[i])));
        			String oldLine = in.readLine();
        			String newLine = null;
        			while((newLine = in.readLine()) != null) {
        				out.write(oldLine);
        				out.newLine();
        				oldLine = newLine;
        			}
        			out.write(oldLine);

        			if(i != sFiles.length - 1)
        				out.newLine();

        			out.flush();
        		}
        	} finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
        	}
        }
    }

    //写入数据到文件
    public static void writeFile(File file, byte[] data) throws Exception {
    	DataOutputStream out = null;
    	if(null!=file && null!=data){
    		try {
    			out = new DataOutputStream(new FileOutputStream(file));
    			out.write(data);
    		} finally {
                IOUtils.closeQuietly(out);
    		}
    	}
    }

    //将输入流中的数据写入文件
    public static int writeFile(File file, InputStream inStream) throws IOException {
        long dataSize = 0;
    	DataInputStream in = null;
    	DataOutputStream out = null;
    	if(null!=inStream && null!=file){
    		try {
    			byte buffer[] = new byte[BUFFER];
    			out = new DataOutputStream(new FileOutputStream(file));
    			in = new DataInputStream(inStream);

    			int nbyteread;
    			while((nbyteread = in.read(buffer)) != -1) {
    				out.write(buffer, 0, nbyteread);
    				dataSize += nbyteread;
    			}
    		} finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
    		}
    	}

        return (int)(dataSize/1024);
    }

    /***
     * 读取文件到byte数组
     * @param file
     * @return
     * @throws java.io.IOException
     */
    public static byte[] readFileToByte(File file) throws IOException{  
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int)file.length());  
        BufferedInputStream in = null;  
        try{  
            in = new BufferedInputStream(new FileInputStream(file));  
            int buf_size = 1024;  
            byte[] buffer = new byte[buf_size];  
            int len = 0;  
            while(-1 != (len = in.read(buffer,0,buf_size))){  
                bos.write(buffer,0,len);  
            }  
            return bos.toByteArray();  
        }finally{
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(bos);
        }  
    }  
    
    public static String getFileName(String path){
		return path.substring(path.lastIndexOf("/")+1);
	}
    
    /**
     * 字节数组转成流
     * @param data
     * @return
     */
    public static InputStream byteToInputSteram(byte[] data){
        InputStream is=null;
        if(null!=data&& data.length>0){
            is = new ByteArrayInputStream(data);  
        }
        return is;
    }


    public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            fileSizeString=null;
            df=null;
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        df=null;
        wrongSize=null;
        return fileSizeString;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String formatFileSizeOnlyKBMB(long fileS) {
//		DecimalFormat df = new DecimalFormat("#");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            fileSizeString=null;
//			df=null;
            return wrongSize;
        }
        if (fileS < 1048576) {
            fileSizeString =Math.round((double) fileS / 1024) + "KBbyte";
        } else  {
            fileSizeString = Math.round((double) fileS / 1048576) + "MBbyte";
        }
//		df=null;
        wrongSize=null;
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double formatFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df
                        .format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }



    /**
     * 获得缓存目录地址
     *
     * @param context
     * @param cacheDirName
     *            SDK卡缓存目录名 例如"hupu/games/cache"
     * @return
     */
    public static final String getCachePath(Context context, String cacheDirName) {
        String cache_path = DeviceInfo.isSdcardExist() ? getExternalCacheDir(context, cacheDirName).getPath() : context.getCacheDir().getPath();
        return cache_path;
    }

    public static final String getCachePath(Context context) {
        String cache_path = DeviceInfo.isSdcardExist() ? getExternalCacheDir(context, getCacheBaseDir(context)).getPath() : context.getCacheDir().getPath();
        return cache_path;
    }

    private static String getCacheBaseDir(Context context) {
        return "Android/data/" + context.getPackageName() + "/cache";
    }

    /**
     * 获得SD卡缓存目录
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context, String cacheDirName) {
        File file = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            file = context.getExternalCacheDir();
        }
        // Before Froyo we need to construct the external cache dir ourselves
        if (file == null) {
            file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + cacheDirName);
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

}
