package service.department;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Fetch;

import play.mvc.Controller;
import play.mvc.Http.Request;

import models.AndroidDepartmentInfo;
import models.AndroidTaskInfo;
import models.AndroidUserInfo;
import models.Project;
import models.ResultInfo;
import service.task.TaskInfoService;
import util.UtilValidate;

public class DepartmentManageService{


	public static ResultInfo getDepartmentList(Integer user_id, Integer p , Integer ps, Request request) {
		ResultInfo info = new ResultInfo();
		Integer userDepartment = 0;
		
		long count = AndroidDepartmentInfo.count();
		List<AndroidDepartmentInfo> androidDepartmentInfoList = new ArrayList<AndroidDepartmentInfo>();

		if (user_id == 0) {
			androidDepartmentInfoList = AndroidDepartmentInfo.find("order by id asc").fetch(p,ps);
			
			info.setCodeAndMsg(200);
			info.setCount(count);
			info.setInfo(androidDepartmentInfoList);
			info.setRequest(request.path);
			return info;

		}else {
			AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ?", user_id).first();
			if (androidUserInfo == null ) {
			    info.setCodeAndMsg(400);
			    info.setRequest(request.path);
			    return info;
			}
			userDepartment = androidUserInfo.department_id;
			List<AndroidDepartmentInfo> tmpAndroidDepartmentInfoList = null;
			//首先取登录用户所在部门
			AndroidDepartmentInfo tmpAndroidDepartmentInfo = AndroidDepartmentInfo.find("id = ?", userDepartment).first();
			if (tmpAndroidDepartmentInfo != null) {
				androidDepartmentInfoList.add(tmpAndroidDepartmentInfo);
			}
			//如果登录用户不是局长，则再列出局长所在部门
			AndroidUserInfo tmpAndroidUserInfo =  null;
			if (androidUserInfo.user_type > 2) {
				tmpAndroidUserInfo = AndroidUserInfo.find("user_type = 1").first();
				if (tmpAndroidUserInfo != null && tmpAndroidUserInfo.department_id != null && tmpAndroidUserInfo.department_id > 0) {
					tmpAndroidDepartmentInfo = AndroidDepartmentInfo.find("id = ?", tmpAndroidUserInfo.department_id).first();
					if (tmpAndroidDepartmentInfo != null) {
						androidDepartmentInfoList.add(tmpAndroidDepartmentInfo);
					}
				}
			}
			//取其他部门列表
			if (tmpAndroidUserInfo != null && tmpAndroidUserInfo.department_id != null && tmpAndroidUserInfo.department_id > 0) {
				tmpAndroidDepartmentInfoList = AndroidDepartmentInfo.find("id != ? AND id != ? order by id asc",
						tmpAndroidUserInfo.department_id, userDepartment).fetch(p,ps);
			}else {
				tmpAndroidDepartmentInfoList = AndroidDepartmentInfo.find("id != ? order by id asc",userDepartment).fetch(p,ps);
			}
			if (tmpAndroidDepartmentInfoList != null && tmpAndroidDepartmentInfoList.size() > 0) {
				androidDepartmentInfoList.addAll(tmpAndroidDepartmentInfoList);
			}	
			if (androidDepartmentInfoList != null && androidDepartmentInfoList.size() > 0) {
				for (AndroidDepartmentInfo androidDepartmentInfo : androidDepartmentInfoList) {
					if (androidDepartmentInfo != null) {
						androidDepartmentInfo.user_num =   AndroidUserInfo.count("department_id = ?", androidDepartmentInfo.id);
						if (userDepartment > 0 && userDepartment == androidDepartmentInfo.id && androidDepartmentInfo.user_num > 1) {//当前登录用户对应部门下的用户数减1
							androidDepartmentInfo.user_num = androidDepartmentInfo.user_num - 1;
						}
					}
				}
			}
			info.setCodeAndMsg(200);
			info.setCount(count);
			info.setInfo(androidDepartmentInfoList);
			info.setRequest(request.path);
			return info;
		}
	}


}
