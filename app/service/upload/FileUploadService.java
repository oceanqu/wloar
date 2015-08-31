package service.upload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.AndroidFileInfo;
import models.AndroidTaskInfo;
import models.AndroidUserInfo;
import models.ResultInfo;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.util.Streams;
import org.hibernate.mapping.Array;

import play.mvc.Controller;
import play.mvc.Http.Request;
import service.task.TaskInfoService;

public class FileUploadService {

    /**
     * 根据用户或文件编号获取文件相关信息列表，如果user_id不为空，优先根据user_id获取，user_id和ids两个参数至少要包含一个
     * 
     * @param user_id
     *            根据用户获取文件
     * @param ids
     *            根据文件编号获取文件，用于获取任务附件
     */
    public static ResultInfo getFileList(Integer user_id, String ids,
	    Request request) {
	ResultInfo info = new ResultInfo();
	List<AndroidFileInfo> androidFileInfoList = null;
	if (user_id != null && user_id > 0) {
	    androidFileInfoList = AndroidFileInfo.find(
		    "user_id = ? order by create_time desc", user_id).fetch();
	} else if (ids != null && ids.length() > 0) {
	    androidFileInfoList = AndroidFileInfo.find(" id IN (" + ids + ")")
		    .fetch();
	} else {
	    info.setRequest(request.path);
	    info.setCodeAndMsg(1006);
	    return info;
	}
	if (androidFileInfoList != null && androidFileInfoList.size() > 0) {
	    info.setRequest(request.path);
	    info.setCodeAndMsg(200);
	    info.setCount(androidFileInfoList.size());
	    info.setInfo(androidFileInfoList);
	    return info;
	} else {
	    info.setRequest(request.path);
	    info.setCodeAndMsg(200);
	    return info;
	}
    }

    public static ResultInfo uploadImg(List<File> upfiles, String savePath,
	    Integer user_id, String user_name, Integer file_type,
	    String content,Integer task_id, Integer task_file_type, Request request) {
	// {"originalName":"demo.jpg","name":"demo.jpg","url":"upload\/demo.jpg","size":"99697","type":".jpg","state":"SUCCESS"}
	ResultInfo info = new ResultInfo();
	if (user_name == null || user_name.length() <= 0) {
	    AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ?",
		    user_id).first();
	    if (androidUserInfo != null) {
		user_name = androidUserInfo.name;
	    } else {
		user_name = "";
	    }
	}
	String fileIds = "";
	List<AndroidFileInfo> androidFileInfoList = new ArrayList<AndroidFileInfo>();
	String contentType = request.contentType;
	if (contentType == null
		|| !contentType.toLowerCase().startsWith("multipart/")) {
	    info.setCodeAndMsg(500);
	    info.setRequest(request.path);
	    return info;
	    // return resultMap;
	} else {
	    if (upfiles != null && upfiles.size() > 0) {
		int i = 0;// 自增编号，用于生成文件名，防止批量提交时文件名冲突
		for (File upfile : upfiles) {
		    String fileName = upfile.getName();// 上传文件名

		    String type = fileName.substring(fileName.lastIndexOf('.')); // 文件类型
		    String originalName = fileName;// 原始文件名

		    long size = 0;// 文件大小

		    if (savePath.equals("") || savePath == null) {
			savePath = "public/upload/";// 保存路径
		    }

		    String newFileName = String.valueOf(System
			    .currentTimeMillis()) + i + type;
		    i++;// i自增
		    String url = savePath + newFileName; // 输出文件地址

		    DiskFileItemFactory dff = new DiskFileItemFactory();
		    dff.setRepository(new File(savePath));
		    try {
			InputStream inStream = new FileInputStream(
				upfile.getAbsolutePath());
			BufferedInputStream in = new BufferedInputStream(
				inStream);
			File file = new File(url);
			FileOutputStream out = new FileOutputStream(file);
			BufferedOutputStream output = new BufferedOutputStream(
				out);
			Streams.copy(in, output, true);
			size = file.length();
			// UE中只会处理单张上传，完成后即退出
		    } catch (Exception e) {
			// e.printStackTrace();
			// state = errorInfo.get("UNKNOWN");
		    }
		    AndroidFileInfo androidFileInfo = new AndroidFileInfo();
		    androidFileInfo.original_file_name = originalName;
		    androidFileInfo.file_name = newFileName;
		    androidFileInfo.url = url;
		    androidFileInfo.user_id = user_id;
		    androidFileInfo.user_name = user_name;
		    androidFileInfo.file_type = file_type;
		    androidFileInfo.content = content;
		    androidFileInfo.save();
		    fileIds += androidFileInfo.id;
		    fileIds += ",";
		    androidFileInfoList.add(androidFileInfo);
		}
	    } else {
		info.setCodeAndMsg(1005);
		info.setRequest(request.path);
		return info;
	    }
	    if (fileIds.endsWith(",")) {
		fileIds = fileIds.substring(0, fileIds.length() - 1);
	    }
	    if (task_id > 0) {//如果任务号不为空，则同步更新任务附件信息
		AndroidTaskInfo androidTaskInfo = AndroidTaskInfo.findById(task_id);
		if (androidTaskInfo == null) {
		    info.setCodeAndMsg(1010);
		    info.setRequest(request.path);
		    return info;
		}
		if (task_file_type == 1) {//attachment
			androidTaskInfo.attachment = TaskInfoService.removeChar(fileIds);//去掉字符串中不是数字和","的字符
			if (androidTaskInfo.attachment.startsWith(",")) {
				androidTaskInfo.attachment = androidTaskInfo.attachment.substring(1);
			}
		    if (androidTaskInfo.attachment.endsWith(",")) {
			androidTaskInfo.attachment = androidTaskInfo.attachment.substring(0, androidTaskInfo.attachment.length() - 1);
		    }
		}else {//media
			androidTaskInfo.media = TaskInfoService.removeChar(fileIds);//去掉字符串中不是数字和","的字符
			if (androidTaskInfo.media.startsWith(",")) {
				androidTaskInfo.media = androidTaskInfo.media.substring(1);
			}
		    if (androidTaskInfo.media.endsWith(",")) {
			androidTaskInfo.media = androidTaskInfo.media.substring(0, androidTaskInfo.media.length() - 1);
		    }
		}
		androidTaskInfo.save();
	    }
	    
	    info.setCodeAndMsg(200);
	    info.setRequest(request.path);
	    info.setCount(androidFileInfoList.size());
	    info.setInfo(fileIds);
	    return info;
	}

    }
	/**
	 * 根据原始文件名，删除图片、视频、音频
	 * @param file_name 原始文件名
	 * @param file_type 文件类型：1：图片；2：视频；3：音频
	 */
    public static ResultInfo deleteFileInfo(String file_name,
	    Integer file_type, Request request) {
	ResultInfo info = new ResultInfo();
	AndroidFileInfo androidFileInfo = AndroidFileInfo
		.findByOriginalFileName(file_name, file_type);
	if (androidFileInfo != null) {
	    androidFileInfo.delete();
	    deleteFile(androidFileInfo.url);
	} else {
	    info.setCodeAndMsg(1009);
	    info.setRequest(request.path);
	    return info;
	}
	info.setCodeAndMsg(200);
	info.setRequest(request.path);
	info.setInfo(androidFileInfo);
	return info;

    }
    
	/**
	 * 删除单个文件
	 * 
	 * @param sPath
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 */
	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}
}
