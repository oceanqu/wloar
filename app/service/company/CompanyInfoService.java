package service.company;

import java.util.Date;
import java.util.List;

import models.AndroidCompanyInfo;
import models.AndroidFileInfo;
import models.AndroidTaskInfo;
import models.AndroidTaskVerifyInfo;
import models.AndroidUserInfo;
import models.ResultInfo;
import play.mvc.Http.Request;
import util.DateUtil;

public class CompanyInfoService {

	/**
	 * 添加一个任务
	 * 
	 * @param app
	 *            文章信息 <必填>
	 */
	public static ResultInfo addCompanyInfo(AndroidCompanyInfo companyInfo, Request request) {
		ResultInfo info = new ResultInfo();

		if (companyInfo == null ) {//
			 info.setCodeAndMsg(1005);
			 info.setRequest(request.path);
			 return info;
		}
		if (companyInfo.create_user_id == null || companyInfo.create_user_id == 0) {//未登录
			 info.setCodeAndMsg(400);
			 info.setRequest(request.path);
			 return info;
		}
		AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ?",
			companyInfo.create_user_id).first();
		
		
		//判断该企业是否存在
		AndroidCompanyInfo androidCompanyInfo = AndroidCompanyInfo.find("name = ?", companyInfo.name).first();
		if (androidCompanyInfo != null) {
			 info.setCodeAndMsg(1014);
			 info.setRequest(request.path);
			 return info;
		}
		companyInfo.create_user_name = androidUserInfo.name ;
		companyInfo.save();

		info.setCodeAndMsg(200);
		info.setInfo(companyInfo);
		info.setRequest(request.path);
		return info;

	}

	/**
	 * 查询企业列表接口
	 * TaskInfoService.java
     * @param user_id 用户编号
     * @param department_id 所属部门
     * @param task_type 任务类别，1：上报任务；2：下派任务；
     * @param status //是否处理/审批完成，0:待处理；1：已完成；2：未完成。所有新建审批/下派任务该状态为2.
     * @param p
     * @param ps
	 * 2015年8月16日
	 */
	public static ResultInfo getCompanyList(Integer p, Integer ps, Request request) {
		ResultInfo info = new ResultInfo();

		List<AndroidCompanyInfo> androidCompanyInfoList = AndroidCompanyInfo.find("order by create_time desc").fetch(p,ps);


		if (androidCompanyInfoList != null && androidCompanyInfoList.size() > 0) {//将附件的编号转换成url
			for (AndroidCompanyInfo androidCompanyInfo : androidCompanyInfoList) {
				if (androidCompanyInfo != null) {
					androidCompanyInfo.create_time_string = DateUtil.date2String(androidCompanyInfo.create_time, "yyyy-MM-dd HH:mm:ss");
				}
			}
			info.setCodeAndMsg(200);
			info.setInfo(androidCompanyInfoList);
			info.setCount(androidCompanyInfoList.size());
			info.setRequest(request.path);
			return info;
		}else {
			info.setCodeAndMsg(200);
			info.setRequest(request.path);
			return info;

		}
	}

	/**
	 * 查询企业详情接口
	 * TaskInfoService.java
	 * @param id
	 * @param request
	 * @return
	 * 2015年8月16日
	 */
	public static ResultInfo getCompanyInfo(Integer id, Request request) {
		ResultInfo info = new ResultInfo();
		AndroidCompanyInfo androidCompanyInfo = AndroidCompanyInfo.findById(id);
		if (androidCompanyInfo == null) {
			info.setCodeAndMsg(1010);
			info.setRequest(request.path);
			return info;
		}
		androidCompanyInfo.create_time_string = DateUtil.date2String(androidCompanyInfo.create_time, "yyyy-MM-dd HH:mm:ss");
		
		info.setCodeAndMsg(200);
		info.setInfo(androidCompanyInfo);
		info.setRequest(request.path);
		return info;

	}


	/**
	 * 修改任务信息
	 * 
	 * @param App
	 *            文章信息 <必填>
	 */
	public static ResultInfo editCompanyInfo(AndroidCompanyInfo companyInfo,
			Request request) {
	    ResultInfo info = new ResultInfo();
		if (companyInfo == null || companyInfo.id == null) {//
			 info.setCodeAndMsg(1007);
			 info.setRequest(request.path);
			 return info;
		}
		AndroidCompanyInfo androidCompanyInfo1 = AndroidCompanyInfo.find("id = ?", companyInfo.id).first();
		if (androidCompanyInfo1 == null) {
		    info.setCodeAndMsg(1010);
		    info.setRequest(request.path);
		    return info;
		}
		
		if (companyInfo.create_user_id == null || companyInfo.create_user_id == 0) {//未登录
			 info.setCodeAndMsg(400);
			 info.setRequest(request.path);
			 return info;
		}
		AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ?",
			companyInfo.create_user_id).first();
		
		
		//判断该企业是否存在
		AndroidCompanyInfo androidCompanyInfo = AndroidCompanyInfo.find("name = ? and id != ?", companyInfo.name,companyInfo.id).first();
		if (androidCompanyInfo != null) {
			 info.setCodeAndMsg(1014);
			 info.setRequest(request.path);
			 return info;
		}
		companyInfo.create_user_name = androidUserInfo.name ;
		companyInfo.save();
		
		info.setCodeAndMsg(200);
		info.setInfo(companyInfo);
		info.setRequest(request.path);
		return info;

	}

	/**
	 * 根据id删除任务
	 * 
	 * @param id
	 *            文章id <必填>
	 */
	public static ResultInfo deleteCompanyInfo(Integer id, Request request) {
		ResultInfo info = new ResultInfo();
		AndroidCompanyInfo androidCompanyInfo = AndroidCompanyInfo.findById(id);
		if (androidCompanyInfo == null) {
			info.setCodeAndMsg(1010);
			info.setRequest(request.path);
			return info;
		}
		androidCompanyInfo.delete();
		info.setCodeAndMsg(200);
		info.setInfo(androidCompanyInfo);
		info.setRequest(request.path);
		return info;
	}

}
