package controllers.upload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.ResultInfo;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.util.Streams;

import play.Logger;
import play.mvc.Controller;
import service.upload.FileUploadService;

public class FileUpload  extends Controller{
	public static void index() {
		render();
	}

	/**
	 * 图片、视频、音频批量上传
	 * @param upfile 文件
	 * @param user_id 用户
	 * @param user_name
	 * @param file_type 文件类型：1：图片；2：视频；3：音频
	 * @param content 文件说明
	 * @param task_id 任务号
	 * @param task_file_type 文件位置：1：attachment；2：media
	 */
	public static void imageThumbnail(List<File> upfile, Integer user_id, 
		String user_name, Integer file_type, String content,Integer task_id, 
		Integer task_file_type) {
	    Logger.info("FileUpload.imageThumbnail user_id = " + user_id);
		String savePath = "public/upload/";// 保存路径
		if (file_type == null) {
		    file_type = 1;
		}
		if (file_type == 1) {
			savePath += "image/";
		}else if (file_type == 2) {
			savePath += "media/";
		}else {
			savePath += "audio/";
		}
		if (user_id == null) {
		    user_id = 0;
		}
		if (content == null) {
		    content = "";
		}
		if (task_id == null) {
		    task_id = 0;
		}
		if (task_file_type == null) {
		    task_file_type = 1;
		}
//		Map<String, Object> resultMap = uploadImg(upfile, savePath);
		ResultInfo info = FileUploadService.uploadImg(upfile, savePath, user_id,
			user_name, file_type, content,task_id, task_file_type, request);
		renderJSON(info);
	}
	
	/**
	 * 根据原始文件名，删除图片、视频、音频
	 * @param file_name 原始文件名
	 * @param file_type 文件类型：1：图片；2：视频；3：音频
	 */
	public static void deleteFileInfo(String file_name, Integer file_type) {
	    Logger.info("FileUpload.deleteFileInfo file_name = " + file_name);
//		Map<String, Object> resultMap = uploadImg(upfile, savePath);
		ResultInfo info = FileUploadService.deleteFileInfo(file_name,file_type,  request);
		renderJSON(info);
	}

	/**
	 * 根据用户或文件编号获取文件相关信息列表，如果user_id不为空，优先根据user_id获取，user_id和ids两个参数至少要包含一个
	 * @param user_id  根据用户获取文件
	 * @param ids 根据文件编号获取文件，用于获取任务附件
	 */
	public static void getFileList(Integer user_id, String ids) {
	    Logger.info("FileUpload.getFileList user_id = " + user_id);

		ResultInfo info = FileUploadService.getFileList( user_id,ids, request);
		renderJSON(info);
	}
}
