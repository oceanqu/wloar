package controllers;

import play.*;
import play.mvc.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.util.Streams;

import models.*;

public class Application extends Controller {

	public static void index() {
		render();
	}

//	/*
//	 * 图片上传
//	 */
//	public static void imageThumbnail(File upfile) {
//		String savePath = "public/upload/";// 保存路径
//		Map<String, Object> resultMap = uploadImg(upfile, savePath);
//		renderJSON(resultMap);
//	}
//
//	private static Map<String, Object> uploadImg(File upfile, String savePath) {
//		// {"originalName":"demo.jpg","name":"demo.jpg","url":"upload\/demo.jpg","size":"99697","type":".jpg","state":"SUCCESS"}
//		String state = "SUCCESS";
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		resultMap.put("originalName", "demo.jpg");
//		resultMap.put("name", "demo.jpg");
//		resultMap.put("url", "upload/demo.jpg");
//		resultMap.put("size", "99697");
//		resultMap.put("type", ".jpg");
//		resultMap.put("state", state);
//
//		String contentType = request.contentType;
//		if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
//			resultMap.put("state", "读取错误");
//			return resultMap;
//		} else {
//			String fileName = upfile.getName();// 上传文件名
//
//			String type = fileName.substring(fileName.lastIndexOf('.')); // 文件类型
//			String originalName = fileName;// 原始文件名
//
//			long size = 0;// 文件大小
//
//			if (savePath.equals("") || savePath == null) {
//				savePath = "public/upload/";// 保存路径
//			}
//
//			String newFileName = String.valueOf(System.currentTimeMillis()) + type;
//			String url = savePath + newFileName; // 输出文件地址
//
//			DiskFileItemFactory dff = new DiskFileItemFactory();
//			dff.setRepository(new File(savePath));
//			try {
//				// ServletFileUpload sfu = new ServletFileUpload(dff);
//				// sfu.setSizeMax(maxSize * 1024);
//				// sfu.setHeaderEncoding("utf-8");
////				if (!checkFileType(originalName)) {
////					state = errorInfo.get("TYPE");
////					renderText(state);
////				}
//
//				InputStream inStream = new FileInputStream(upfile.getAbsolutePath());
//				BufferedInputStream in = new BufferedInputStream(inStream);
//				File file = new File(url);
//				FileOutputStream out = new FileOutputStream(file);
//				BufferedOutputStream output = new BufferedOutputStream(out);
//				Streams.copy(in, output, true);
//				state = "SUCCESS";
//				size = file.length();
//				// UE中只会处理单张上传，完成后即退出
//			} catch (Exception e) {
//				 e.printStackTrace();
//				state = "UNKNOWN";
////				state = errorInfo.get("UNKNOWN");
//			}
//			resultMap.put("originalName", originalName);
//			resultMap.put("name", newFileName);
//			resultMap.put("url", url);
//			resultMap.put("size", size);
//			resultMap.put("type", type);
//			resultMap.put("state", state);
//			return resultMap;
//		}
//	}
}