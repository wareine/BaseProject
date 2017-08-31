package com.wlazy.baseproject.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.wlazy.baseproject.CommonConfigs;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 路径存储工具类
 * <br/>
 * 2015年12月23日-下午1:50:48
 * @author lifei
 */
public final class StorageUtil {
    private static final String TAG = StorageUtil.class.getSimpleName();


    /**
     * 获取默认的外部存储目录
     * @return eg:/mnt/sdcard1/
     */
    public static String getExternalStorageDirectoryPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取默认的外部存储目录File
     * @return
     */
    public static File getExternalStorageDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory();
        }
        return null;
    }

    /**
     * 获取SD卡根目录或者cache目录 File
     * 获取一个程序的缓存根目录的父文件夹File
     * @param context
     * @return File("/mnt/storage0/") if the phone has SD card,else return
     *         File("data/data/[包名]/cache/")
     */
    public static File getCacheParent(Context context) {
        File externalStorageFile = getExternalStorageDirectory();
        if (null == externalStorageFile) {
            // return context.getDir(Config.APP_BASE_PATH,
            // Context.MODE_WORLD_WRITEABLE);
            return context.getCacheDir();//该目录下的文件在系统内存紧张时，会被清空文件，来腾出空间供系统使用
//            return context.getFilesDir(); //File("data/data/[包名]/files/")//卸载会被删除，其他的状况不会
        } else {
            return externalStorageFile;
        }
    }

    /**
     * 获取一个程序缓存的根目录[该目录为程序全局自定义配置类配置],此处说明指示为approot
     * approot @see {@link CommonConfigs#APP_BASE_PATH}}
     * @param context
     * @return 有SD卡： File("/mnt/storage0/approot_dir/");
     *         无SD卡 ：File("/data/data/[包名]/cache/approot_dir/");
     */
    public static File getAppCacheRoot(Context context) {
        return getDirInCache(context, CommonConfigs.APP_BASE_PATH);
    }

    /**
     * 获取一个程序缓存根目录的路径String,注意该路径已带"/"
     * @param context
     * @return eg. "/mnt/sdcard0/approotdir/"; 
     *              or:"/data/data/[包名]/cache/自定义缓存根目录/";
     */
    public static String getAppCacheRootPath(Context context) {
        String path = getAppCacheRoot(context).getPath();
        if (!TextUtils.isEmpty(path) && !path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        return path;
    }

    /**
     * 1：如果没有SD卡，获取的是data/data/[包名]/cache/文件夹下的各文件夹文件
     * 2：如果有SD卡，则获取的是eg.: /mnt/storage0/ 即SD卡根目录下任何文件夹 
     * 3：注：只能获取文件夹文件File对象，不能传入具体文件名eg.: a.text,传入文件名，仍会被创建成文件夹
     * @param context
     * @param dirName 任何"文件夹"名字
     * @return 无SD卡：eg.: File("/data/data/[包名]/cache/[参数：dirName]/");
     *         有SD卡：eg.: File("/mnt/storage0/[参数：dirName]/");
     */
    public static File getDirInCache(Context context, final String dirName) {
        File dir = new File(getCacheParent(context), dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 1:如果没有外置SD卡，则获取的是各程序缓存根目录下的各文件
     * 此时返回的是：File("/data/data/[包名]/cache/xx.txt")
     * 2:如果有外置SD卡，则获取的是外置SD卡根目录下任何的文件
     * @param context
     * @param fileName
     *            相对于存储根路径的目录下的"文件名"
     * @return 如果没有外置SD卡 eg.: File("/data/data/com.cx/xx/cache/[参数：fileName] ");
     *         如果有SD卡 eg.: File("/mnt/storage0/[参数：fileName]"),but the file
     *         maybe created fail possibly;
     */
    public static File getFileInCache(Context context, final String fileName) {
        File file = new File(getCacheParent(context), fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LogUtils.e(TAG, "", e);
            }
        }
        return file;
    }

    /**
     * 获取本程序自定义储存根目录approotdir 下的各文件File
     *
     * eg.: 如果有SD卡，则为File("/mnt/sdcard/approotdir/fileName");
     *      如果没有SD卡，则为File("/data/data/com.xx.xx/cache/approotdir/fileName");
     * @param mContext
     * @param fileName eg.: /ads/pic.data/; 或者/user.data; 即可多层级
     * @return
     */
    public static File getFileInAppBaseDir(Context mContext,String fileName){
                            // mnt/sdcard/appbasedir/, fileName)
        File file = new File(getAppCacheRoot(mContext),fileName);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if(!file.exists()){
            try {
                file.createNewFile();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return file;
    }
    /**
     * 获取一个程序缓存根目录“/approot/”目录下 能匹配到对应匹配规则的所有文件夹 eg. 找出全是以数字命名的文件夹
     * @param context
     * @param dirNameRegex
     *            匹配正则，eg. 是数字的文件夹
     * @return
     */
    public static File[] getRegexFiles(Context context, final String dirNameRegex) {
        return getAppCacheRoot(context).listFiles(new FileFilter() {
            Pattern mPattern = Pattern.compile(dirNameRegex, Pattern.CASE_INSENSITIVE);

            @Override
            public boolean accept(File pathname) {
                if (!pathname.isDirectory()) {
                    return false;
                }
                String dirName = pathname.getName();
                return mPattern.matcher(dirName).find();
            }
        });
    }

    public static String getThumbnailsDir() {
        return Environment.getExternalStorageDirectory() + "/DCIM/.thumbnails";
    }

    /**
     * 有SD卡情况下：File("/storage/emulated/0/Android/data/应用包名/cache/")
     * 没有SD卡情况下：File("/data/data/应用包名/files")
     * @param context
     * @return
     */
    public static File getAppCacheRootDirWithOutPermission(Context context) {
        if (getExternalStorageDirectory() == null) {
            //没有SD卡的情况下
            return context.getFilesDir();
        }
        return context.getExternalCacheDir();
    }

    public static File getFileInExternalCacheDir(Context context, String fileName) {
        File targetFile = new File(getAppCacheRootDirWithOutPermission(context), fileName);
        File targetFileParentDir = targetFile.getParentFile();
        if (!targetFileParentDir.exists()) {
            targetFileParentDir.mkdirs();
        }
        if (!targetFile.exists()) {
            try {
                targetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return targetFile;
    }

    public static File getDirInExternalCacheDir(Context context, String dirName) {
        File targetDirFile = new File(getAppCacheRootDirWithOutPermission(context), dirName);
        if (!targetDirFile.exists()) {
            targetDirFile.mkdirs();
        }
        return targetDirFile;
    }
}