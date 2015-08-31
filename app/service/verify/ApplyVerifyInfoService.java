package service.verify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import models.AndroidTaskVerifyInfo;
import models.AndroidNoticeInfo;
import models.AndroidUserInfo;
import models.ResultInfo;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.Request;
import service.notice.CustomDocinfoService;



/**
 * 后台自定义资讯添加接口
 * @author 
 *
 */
public class ApplyVerifyInfoService  {
    
    /**
     * 根据审批状态，查询申请审批列表
     * CustomDocinfo.java
     * @param id
     * @param user_id 0：查询全部用户；其他：根据user_id查询
     * @param verify_step 0：未审核；1：一级审核完成；2：二级审核完成
     * 2015年6月15日
     */
    public static ResultInfo getApplyList(Integer user_id, Integer verify_step,Integer task_id,Integer p, Integer ps, Request request) {
	ResultInfo info = new ResultInfo();
	List<AndroidTaskVerifyInfo> androidApplyVerifyInfoList = null;
	if (user_id == 0) {//全部用户
	    if (verify_step == 0) {//未审核
		androidApplyVerifyInfoList = AndroidTaskVerifyInfo.find("task_id = ? and first_verify_status = ?  order by create_time desc", task_id,0).fetch(p,ps);
	    }else if (verify_step == 1) {//一级审核完成
		androidApplyVerifyInfoList = AndroidTaskVerifyInfo.find("task_id = ? and first_verify_status > 0 and second_verify_status = 0  order by create_time desc",task_id).fetch(p,ps);
	    }else if (verify_step == 2)  {//二级审核完成
		androidApplyVerifyInfoList = AndroidTaskVerifyInfo.find("task_id = ? and second_verify_status > 0  order by create_time desc", task_id).fetch(p,ps);
	    }else {
		androidApplyVerifyInfoList = AndroidTaskVerifyInfo.findAll();
	    }
	}else {
	    if (verify_step == 0) {//未审核
		androidApplyVerifyInfoList = AndroidTaskVerifyInfo.find("task_id = ? and apply_user_id = ? and first_verify_status = ?  order by create_time desc",task_id,user_id, 0).fetch(p,ps);
	    }else if (verify_step == 1) {//一级审核完成
		androidApplyVerifyInfoList = AndroidTaskVerifyInfo.find("task_id = ? and apply_user_id = ? and first_verify_status > 0 and second_verify_status = 0  order by create_time desc",task_id,user_id).fetch(p,ps);
	    }else  if (verify_step == 2) {//二级审核完成
		androidApplyVerifyInfoList = AndroidTaskVerifyInfo.find("task_id = ? and apply_user_id = ? and second_verify_status > 0  order by create_time desc",task_id,user_id).fetch(p,ps);
	    }else {
		androidApplyVerifyInfoList = AndroidTaskVerifyInfo.find("task_id = ? and apply_user_id = ?",task_id,user_id).fetch(p,ps);
	    }
	}
	info.setCodeAndMsg(200);
	info.setCount(androidApplyVerifyInfoList.size());
	info.setRequest(request.path);
	info.setInfo(androidApplyVerifyInfoList);
	return info;

    }



    /**
     * 添加一个审批申请
     * 
     * @param app
     *            文章信息 <必填>
     */
    public static ResultInfo addApplyInfo(AndroidTaskVerifyInfo androidApplyVerifyInfo,Request request) {
	ResultInfo info =new ResultInfo();
	if (androidApplyVerifyInfo.receive_user_name == null || androidApplyVerifyInfo.receive_user_name.length() <= 0) {
	    AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ?", androidApplyVerifyInfo.receive_user_id).first();
		if (androidUserInfo != null) {
		    androidApplyVerifyInfo.receive_user_name = androidUserInfo.name;
		}else {
		    androidApplyVerifyInfo.receive_user_name = "";
		}
	}
//	androidApplyVerifyInfo.first_verify_comment = "";
//	if (androidApplyVerifyInfo.task_type == 1) {//上报任务不需要审核，默认为1级审核通过
//		androidApplyVerifyInfo.first_verify_status = 1;
//		androidApplyVerifyInfo.first_verify_time = new Date();
//		androidApplyVerifyInfo.first_verify_user_id = androidApplyVerifyInfo.apply_user_id;
//		androidApplyVerifyInfo.first_verify_user_name = androidApplyVerifyInfo.apply_user_name;
//	}else {
//		androidApplyVerifyInfo.first_verify_status = 0;
//		androidApplyVerifyInfo.first_verify_time = new Date();
//		androidApplyVerifyInfo.first_verify_user_id = 0;
//		androidApplyVerifyInfo.first_verify_user_name = "";
//	}
//
//	androidApplyVerifyInfo.second_verify_comment = "";
//	androidApplyVerifyInfo.second_verify_status = 0;
//	androidApplyVerifyInfo.second_verify_time = new Date();
//	androidApplyVerifyInfo.second_verify_user_id = 0;
//	androidApplyVerifyInfo.second_verify_user_name = "";
	androidApplyVerifyInfo.save();
	info.setCodeAndMsg(200);
	info.setRequest(request.path);
	info.setInfo(androidApplyVerifyInfo);
	return info;

    }

    /**
     * 修改文章信息 修改方式为新建一条记录，将修改的数据存储到新纪录中，然后删除历史记录,
     * 同时删除push表的记录,使得修改记录在推荐列表中可以显示在最新位置
     * 
     * @param App
     *            文章信息 <必填>
     */
    public static ResultInfo verifyApplyInfo(Integer id,Integer verify_step, Integer verify_status,
	    Integer user_id, String user_name,String comment, Request request) {
	ResultInfo info = new ResultInfo();
	AndroidTaskVerifyInfo androidApplyVerifyInfo = AndroidTaskVerifyInfo.find("id = ?", id).first();
	if (androidApplyVerifyInfo == null) {
		info.setCodeAndMsg(1005);
		info.setRequest(request.path);
		return info;
	}
	    if (user_name == null || user_name.length() <= 0) {
		AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ?", user_id).first();
		if (androidUserInfo != null) {
		    user_name = androidUserInfo.name;
		}else {
		    user_name = "";
		}
	    }

//	if (verify_step == 1) {
//	    androidApplyVerifyInfo.first_verify_user_id = user_id;
//	    androidApplyVerifyInfo.first_verify_user_name = user_name;
//	    androidApplyVerifyInfo.first_verify_time = new Date();
//	    androidApplyVerifyInfo.first_verify_comment = comment;
//	    androidApplyVerifyInfo.first_verify_status = verify_status;
//	}else {
//	    androidApplyVerifyInfo.second_verify_user_id = user_id;
//	    androidApplyVerifyInfo.second_verify_user_name = user_name;
//	    androidApplyVerifyInfo.second_verify_time = new Date();
//	    androidApplyVerifyInfo.second_verify_comment = comment;
//	    androidApplyVerifyInfo.second_verify_status = verify_status;
//
//	}
	androidApplyVerifyInfo.save();
	info.setCodeAndMsg(200);
	info.setRequest(request.path);
	info.setInfo(androidApplyVerifyInfo);
	return info;
    }

    /**
     * 根据id,获取一个审批的状态
     * 
     * @param id
     *            文章id <必填>
     */
    public static ResultInfo getApplyInfo(Integer id, Request request) {
	ResultInfo info = new ResultInfo();
	AndroidTaskVerifyInfo androidApplyVerifyInfo = AndroidTaskVerifyInfo.find("id = ?", id).first();
	if (androidApplyVerifyInfo == null) {
		info.setCodeAndMsg(1005);
		info.setRequest(request.path);
		return info;
	}
	info.setCodeAndMsg(200);
	info.setRequest(request.path);
	info.setInfo(androidApplyVerifyInfo);
	return info;

    }
    
}
