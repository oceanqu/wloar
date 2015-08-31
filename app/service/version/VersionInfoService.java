/*
 * @(#)AppVersionService.Version.service
 * 
 * Version information:0.1
 * 
 * Date:14/09/02
 * 
 * Copyright notice:Copyright (c) 2014 INT-YT. All rights reserved.
 */
package service.version;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.persistence.Query;

import play.Logger;
import play.mvc.Http.Request;
import models.AndroidVersionInfo;
import models.ResultInfo;

public class VersionInfoService {

	/**
	 * 获取最新版本信息
	 * 
	 */

	public static ResultInfo getVersionInfo(Integer id,Request request){
		ResultInfo info = new ResultInfo();
//	    String sqlString = "SELECT MAX(id) FROM android_version_info where flag = 0";
		 List<Integer> versionIdList = null;
	    Query query = AndroidVersionInfo.em().createQuery(
	    		"SELECT MAX(id) FROM AndroidVersionInfo where flag = 0");
	    	try {
	    		versionIdList =  query.getResultList(); // 成功返回3
	    	} catch (Exception e) {
	    	    // TODO: handle exception
	    		info.setCode(1019);
	    		info.setMsg("删除对应社会化标签历史数据错误！");
	    		info.setHasNextPage(false);
	    		return info;
	    	}
	    	
	    
	    if (versionIdList != null && versionIdList.size() > 0) {
	    	Integer maxId = versionIdList.get(0);
	    	if (maxId > id) {//有新版本
	    		AndroidVersionInfo androidVersionInfo = AndroidVersionInfo.find("id = ?", maxId).first();
	    		if (androidVersionInfo != null) {
	    			androidVersionInfo.setIntroduce(androidVersionInfo.version_introduce);
	    			info.setCodeAndMsg(200);
					info.setRequest(request.path);
					info.setInfo(androidVersionInfo);
					return info;
				}else {
					info.setCodeAndMsg(1019);
					info.setRequest(request.path);
					return info;
				}			
	    	}else {
				info.setCodeAndMsg(1019);
				info.setRequest(request.path);
				return info;
			}
		}else {
			info.setCodeAndMsg(1019);
			info.setRequest(request.path);
			return info;
		}

	}	

	
	/**
	 * 根据输入版本号获取版本信息
	 * 
	 */

//	public static VersionInfo getVersionInfoByVersionCode(Integer version_code,Integer device_platform){
//		VersionInfo versionInfo = new VersionInfo();
//		Connection conn = null;
//        PreparedStatement stmt = null;
//        try {
//            conn = DBUtil.db.getConnection();
//            String sql = "SELECT * FROM cdf_app_version" +
//            		" WHERE version_code = ? and device_platform = ?";
//            stmt = conn.prepareStatement(sql);
//            stmt.setInt(1, version_code);
//            stmt.setInt(2, device_platform);
//            ResultSet resultSet = stmt.executeQuery(); 
//            while (resultSet.next()) {	
//            	versionInfo.setVersion_code(resultSet.getInt("version_code"));
//            	versionInfo.setCreate_time(resultSet.getTimestamp("create_time"));
//            	versionInfo.setVersion_name(resultSet.getString("version_name"));            	
//            	versionInfo.setDownload_address(resultSet.getString("download_address"));
//            	versionInfo.setIntroduce(resultSet.getString("version_introduce"));
//            	versionInfo.setVersion_qrcode(resultSet.getString("version_qrcode"));
//            }  
//            resultSet.close();
//            stmt.close();
//        } catch (Exception e) {
//            Logger.error("Result CheckUserInfoODBC:"+e.getMessage());
//        }
//        return versionInfo;
//	}
}
