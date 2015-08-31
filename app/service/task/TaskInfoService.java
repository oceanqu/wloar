package service.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import models.AndroidCompanyInfo;
import models.AndroidFileInfo;
import models.AndroidNoticeInfo;
import models.AndroidReceiveNoticeUser;
import models.AndroidTaskContent;
import models.AndroidTaskInfo;
import models.AndroidTaskVerifyInfo;
import models.AndroidUserInfo;
import models.ResultInfo;
import play.Logger;
import play.mvc.Http.Request;
import util.DateUtil;
import util.PushDoc;

/**
 * 任务相关服务接口
 * 
 * @author
 * 
 */
public class TaskInfoService {

	// public static ResourceBundle rb = ResourceBundle.getBundle("config");
	// static String keywordsCnkeetServer =
	// rb.getString("keywords.cnkeet.server");


	/**
	 * 添加一个任务
	 * 
	 * @param app
	 *            文章信息 <必填>
	 */
	public static ResultInfo addTaskInfo(
			AndroidTaskInfo androidTaskInfo, Request request) {
		ResultInfo info = new ResultInfo();

		if (androidTaskInfo == null ) {//未登录
			 info.setCodeAndMsg(1007);
			 info.setRequest(request.path);
			 return info;
		}
		if (androidTaskInfo.create_user_id == null || androidTaskInfo.create_user_id == 0) {//未登录
			 info.setCodeAndMsg(400);
			 info.setRequest(request.path);
			 return info;
		}
		AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ?",
			androidTaskInfo.create_user_id).first();
		if (androidUserInfo.user_type >= 3) {//如果发布人是科主任或科室成员
		    if (androidUserInfo.department_id > 0 && androidUserInfo.department_id != androidTaskInfo.department_id) {//只能发布本部门的任务
			 info.setCodeAndMsg(1008);
			 info.setRequest(request.path);
			 return info;
		    }
		}
		if (androidUserInfo.user_type == 4 && androidTaskInfo.task_type == 2) {//科室成员不能下派任务，只能上报任务
			 info.setCodeAndMsg(1001);
			 info.setRequest(request.path);
			 return info;
		}
		AndroidUserInfo receiveUserInfo = AndroidUserInfo.find("id = ?",
			androidTaskInfo.receive_user_id).first();
		if (receiveUserInfo == null ) {
			info.setCodeAndMsg(1012);
			info.setRequest(request.path);
			return info;
		}
		if (androidTaskInfo.company_id != null && androidTaskInfo.company_id > 0) {
			AndroidCompanyInfo androidCompanyInfo = AndroidCompanyInfo.find("id = ?", androidTaskInfo.company_id).first();
			if (androidCompanyInfo != null) {
				androidTaskInfo.company_name = androidCompanyInfo.name;
			}else {
				info.setCodeAndMsg(1017);
				info.setRequest(request.path);
				return info;
			}
		}
		
		//判断该任务是否存在
		if (androidTaskInfo.status == null || androidTaskInfo.status == 0) {
			androidTaskInfo.status = 2;
		}
		if (androidTaskInfo.attachment != null && androidTaskInfo.attachment.length() > 0) {
			androidTaskInfo.attachment = removeChar(androidTaskInfo.attachment);//去掉字符串中不是数字和","的字符
			if (androidTaskInfo.attachment.startsWith(",")) {
				androidTaskInfo.attachment = androidTaskInfo.attachment.substring(1);
			}
		    if (androidTaskInfo.attachment.endsWith(",")) {
			androidTaskInfo.attachment = androidTaskInfo.attachment.substring(0, androidTaskInfo.attachment.length() - 1);
		    }
		}
		if (androidTaskInfo.media != null && androidTaskInfo.media.length() > 0) {
			androidTaskInfo.media = removeChar(androidTaskInfo.media);//去掉字符串中不是数字和","的字符
			if (androidTaskInfo.media.startsWith(",")) {
				androidTaskInfo.media = androidTaskInfo.media.substring(1);
			}
		    if (androidTaskInfo.media.endsWith(",")) {
			androidTaskInfo.media = androidTaskInfo.media.substring(0, androidTaskInfo.media.length() - 1);
		    }
		}
		androidTaskInfo.create_user_name = androidUserInfo.name;
		androidTaskInfo.receive_user_name = receiveUserInfo.name;
		androidTaskInfo.save();

		// try {
		// //根据消息类型，确定消息push_range
		// if(androidTaskContent.notice_type == 0){//消息
		// androidTaskContent.push_range = "all";
		// }else {//下派任务
		// String base_tag_name = "tag_user_";
		// if (androidNoticeInfo.receive_user_ids !=null &&
		// androidNoticeInfo.receive_user_ids.length() > 0) {
		// String[] receive_user_id =
		// androidNoticeInfo.receive_user_ids.split(",");
		// JSONObject jsonObject = new JSONObject();
		// JSONArray jsonArray = new JSONArray();
		// for (int i = 0; i < receive_user_id.length; i++) {
		// String tag_name = base_tag_name + receive_user_id[i];
		// jsonArray.add(tag_name);
		// }
		// jsonObject.put("tag", jsonArray);
		// androidNoticeInfo.push_range = jsonObject.toString();
		// }
		//
		// }
		// androidNoticeInfo.create_time = new Date();
		// androidNoticeInfo.push_flag = 0;
		//
		// androidNoticeInfo.save();
		// } catch (Exception e) {
		// Logger.error(e, " Error add App App.addApp ");
//		 info.setCodeAndMsg(500);
//		 info.setRequest(request.path);
//		 e.printStackTrace();
//		 return info;
		// }

		// //推送数据
		// if (androidNoticeInfo != null && androidNoticeInfo.id != null) {
		// PushDoc pushDoc= new PushDoc();
		// androidNoticeInfo.push_msg_id = pushDoc.pushDoc(androidNoticeInfo);
		// if (androidNoticeInfo.push_msg_id != null &&
		// androidNoticeInfo.push_msg_id > 0) {
		// androidNoticeInfo.push_flag = 1;
		// }else {
		// androidNoticeInfo.push_flag = 0;
		// }
		// androidNoticeInfo.save();
		// }
		info.setCodeAndMsg(200);
		info.setInfo(androidTaskInfo);
		info.setRequest(request.path);
		return info;

	}
    
	//去掉字符串中不是数字和","的字符
	public static String removeChar(String input) {
		String tmpStr="";
		if(input.length()>0){
			for(int i=0;i<input.length();i++){
				String tmp=""+input.charAt(i);
				if((tmp).matches("[0-9,]")){
					tmpStr+=tmp;
				}
			}
		}
		return tmpStr;
		
	}
	
	/**
	 * 查询用户任务列表接口
	 * TaskInfoService.java
         * @param user_id 用户编号
         * @param department_id 所属部门
         * @param task_type 任务类别，1：上报任务；2：下派任务；
         * @param status //是否处理/审批完成，0:全部；1：已完成；2：未完成。所有新建审批/下派任务该状态为2.
         * @param p
         * @param ps
	 * 2015年8月16日
	 */
	public static ResultInfo getUserTaskList(Integer user_id, Integer department_id,
		    Integer task_type,Integer status,Integer p, Integer ps, Request request) {
		ResultInfo info = new ResultInfo();

		AndroidUserInfo androidUserInfo = null;
		if (user_id > 0) {
			androidUserInfo = AndroidUserInfo.findById(user_id);
			if (androidUserInfo == null) {
				info.setCodeAndMsg(400);
				 info.setRequest(request.path);
				 return info;
			}
			
			if (androidUserInfo.user_type >= 3) {//科主任、科室成员只能查看本部门数据
				if (department_id > 0 && androidUserInfo.department_id != department_id) {
					info.setCodeAndMsg(1015);
					 info.setRequest(request.path);
					 return info;
				}
				department_id = androidUserInfo.department_id;
				user_id = 0;//任务列表不区分用户，只根据部门区分
			}else {
				user_id = 0;
			}
	}
		
		List<AndroidTaskInfo> androidTaskInfoList = null;
		if (user_id == 0) {//查询全部用户数据
		    if (department_id == 0) {//部门为空,查询全部部门
			if (task_type == 0) {//任务类型为空
			    if (status == 0) {//全部
				androidTaskInfoList = AndroidTaskInfo.find("").fetch(p,ps);
			    }else if(status == 2){//未完成
			    	androidTaskInfoList = AndroidTaskInfo.find("status IN (0,2)").fetch(p,ps);
			    }else {
			    	androidTaskInfoList = AndroidTaskInfo.find("status = ?", status).fetch(p,ps);
				}
			}else {//任务类别不为空
			    if (status == 0) {//全部
				androidTaskInfoList = AndroidTaskInfo.find("task_type = ?", task_type).fetch(p,ps);
			    }else if(status == 2){//未完成
			    	androidTaskInfoList = AndroidTaskInfo.find("status IN (0,2) and task_type = ? ", task_type).fetch(p,ps);
			    }else {
				androidTaskInfoList = AndroidTaskInfo.find("task_type = ? and status = ?",task_type, status).fetch(p,ps);
			    }

			}
		    }else {//部门不为空,查询指定部门
			if (task_type == 0) {//任务类型为空
			    if (status == 0) {//全部
				androidTaskInfoList = AndroidTaskInfo.find("department_id = ?", department_id).fetch(p,ps);
			    }else if(status == 2){//未完成
			    	androidTaskInfoList = AndroidTaskInfo.find("status IN (0,2) and department_id = ? ",department_id).fetch(p,ps);
			    }else {
				androidTaskInfoList = AndroidTaskInfo.find("status = ? and department_id = ?", status,department_id).fetch(p,ps);
			    }
			}else {//任务类别不为空
			    if (status == 0) {//全部
				androidTaskInfoList = AndroidTaskInfo.find("task_type = ? and department_id = ?", task_type,department_id).fetch(p,ps);
			    }else if(status == 2){//未完成
					androidTaskInfoList = AndroidTaskInfo.find("task_type = ? and status IN (0,2) and"
							+ " department_id = ? ",task_type,  department_id).fetch(p,ps);
			    }else {
				androidTaskInfoList = AndroidTaskInfo.find("task_type = ? and status = ? and"
					+ " department_id = ? ",task_type, status, department_id).fetch(p,ps);
			    }

			}

		    }
		}else {//查询指定用户的数据
		    if (department_id == 0) {//部门为空,查询全部部门
			if (task_type == 0) {//任务类型为空
			    if (status == 0) {//全部
				androidTaskInfoList = AndroidTaskInfo.find("receive_user_id = ? or create_user_id = ?",user_id,user_id).fetch(p,ps);
			    }else if(status == 2){//未完成
					androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and status IN (0,2) ", user_id,user_id).fetch(p,ps);
			    }else {
				androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and status = ?", user_id,user_id,status).fetch(p,ps);
			    }
			}else {//任务类别不为空
			    if (status == 0) {//全部
				androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and task_type = ?",user_id,user_id, task_type).fetch(p,ps);
			    }else if(status == 2){//未完成
					androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and task_type = ? and status IN (0,2)",user_id,user_id,task_type).fetch(p,ps);
			    }else {
				androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and task_type = ? and status = ?",user_id,user_id,task_type, status).fetch(p,ps);
			    }
			}
		    }else {//部门不为空,查询指定部门
			if (task_type == 0) {//任务类型为空
			    if (status == 0) {//全部
				androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and department_id = ?", user_id,user_id,department_id).fetch(p,ps);
			    }else if(status == 2){//未完成
					androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and status IN (0,2) and department_id = ?", user_id,user_id,department_id).fetch(p,ps);
			    }else {
				androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and status = ? and department_id = ?", user_id,user_id,status,department_id).fetch(p,ps);
			    }
			}else {//任务类别不为空
			    if (status == 0) {//全部
				androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and task_type = ? and department_id = ?", user_id,user_id,task_type,department_id).fetch(p,ps);
			    }else if(status == 2){//未完成
					androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and task_type = ? and status IN (0,2) and"
							+ " department_id = ? ",user_id,user_id,task_type, department_id).fetch(p,ps);
			    }else {
				androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and task_type = ? and status = ? and"
					+ " department_id = ? ",user_id,user_id,task_type, status, department_id).fetch(p,ps);
			    }

			}

		    }

		}

		if (androidTaskInfoList != null && androidTaskInfoList.size() > 0) {//将附件的编号转换成url
		    for (AndroidTaskInfo androidTaskInfo : androidTaskInfoList ) {
			if (androidTaskInfo != null) {
			    if (androidTaskInfo.attachment != null && androidTaskInfo.attachment.length() > 0) {
				if (androidTaskInfo.attachment.endsWith(",")) {
				    androidTaskInfo.attachment = androidTaskInfo.attachment.substring(0, androidTaskInfo.attachment.length() - 1);
				}
				androidTaskInfo.attachment = AndroidFileInfo.findByIds(androidTaskInfo.attachment);
			    }
			    if (androidTaskInfo.media != null && androidTaskInfo.media.length() > 0) {
				if (androidTaskInfo.media.endsWith(",")) {
				    androidTaskInfo.media = androidTaskInfo.media.substring(0, androidTaskInfo.media.length() - 1);
				}
				androidTaskInfo.media = AndroidFileInfo.findByIds(androidTaskInfo.media);
			    }
				androidTaskInfo.create_time_string = DateUtil.date2String(androidTaskInfo.create_time, "yyyy-MM-dd HH:mm:ss");
				 String create_time_string =
					 DateUtil.date2String(androidTaskInfo.create_time,
					 "yyyy-MM-dd HH:mm:ss");
					
					 try {
					androidTaskInfo.create_time =
					 DateUtil.string2UtilDate(create_time_string,
					 "yyyy-MM-dd HH:mm:ss");
					 } catch (Exception e) {
					 // TODO Auto-generated catch block
					 e.printStackTrace();
					 }

			}
		    }
		}
			// String receive_user_names = "";
			// receive_user_names += androidReceiveNoticeUser.receive_user_name;
			// receive_user_names += ",";
			//
			// if (receive_user_names.endsWith(",")) {
			// receive_user_names = receive_user_names.substring(0,
			// news_ids.length() - 1);
			// }
			// for (AndroidNoticeInfo androidNoticeInfo : androidNoticeInfoList)
			// {
//			 String create_time_string =
//			 DateUtil.date2String(androidNoticeInfo.create_time,
//			 "yyyy-MM-dd HH:mm:ss");
//			
//			 try {
//			 androidNoticeInfo.create_time =
//			 DateUtil.string2UtilDate(create_time_string,
//			 "yyyy-MM-dd HH:mm:ss");
//			 } catch (Exception e) {
//			 // TODO Auto-generated catch block
//			 e.printStackTrace();
//			 }
			//
			// }
		info.setCodeAndMsg(200);
		info.setInfo(androidTaskInfoList);
		info.setCount(androidTaskInfoList.size());
		info.setRequest(request.path);
		return info;
	}

	/**
	 * 查询用户任务详情接口
	 * TaskInfoService.java
	 * @param id
	 * @param request
	 * @return
	 * 2015年8月16日
	 */
	public static ResultInfo getUserTaskInfo(Integer id, Request request) {
		ResultInfo info = new ResultInfo();
		AndroidTaskInfo androidTaskInfo = AndroidTaskInfo.findById(id);
		if (androidTaskInfo == null) {
			info.setCodeAndMsg(1010);
			info.setRequest(request.path);
			return info;
		}

		    if (androidTaskInfo.attachment != null && androidTaskInfo.attachment.length() > 0) {
			if (androidTaskInfo.attachment.endsWith(",")) {
			    androidTaskInfo.attachment = androidTaskInfo.attachment.substring(0, androidTaskInfo.attachment.length() - 1);
			}
			androidTaskInfo.attachment = AndroidFileInfo.findByIds(androidTaskInfo.attachment);
		    }
		    if (androidTaskInfo.media != null && androidTaskInfo.media.length() > 0) {
			if (androidTaskInfo.media.endsWith(",")) {
			    androidTaskInfo.media = androidTaskInfo.media.substring(0, androidTaskInfo.media.length() - 1);
			}
			androidTaskInfo.media = AndroidFileInfo.findByIds(androidTaskInfo.media);
		    }
		    String create_time_string =  DateUtil.date2String(androidTaskInfo.create_time, "yyyy-MM-dd HH:mm:ss");
		  //获取任务转发信息
		    String taskForwardInfo = "该任务于" + create_time_string + "由" + androidTaskInfo.create_user_name + "创建！";
		List<AndroidTaskVerifyInfo> androidTaskVerifyInfoList = AndroidTaskVerifyInfo.findByTaskIdAsc(id);
		if (androidTaskVerifyInfoList != null && androidTaskVerifyInfoList.size() > 0) {//有改任务的处理信息
		   for (AndroidTaskVerifyInfo androidTaskVerifyInfo : androidTaskVerifyInfoList) {
			if (androidTaskVerifyInfo != null) {
			    String verify_time_string =  DateUtil.date2String(androidTaskVerifyInfo.verify_time, "yyyy-MM-dd HH:mm:ss");
			    if (androidTaskVerifyInfo.verify_type == 1) {//审核
				taskForwardInfo += "于" + verify_time_string + "由" + androidTaskVerifyInfo.verify_user_name + "处理!";
			    }else {
				taskForwardInfo += "于" + verify_time_string + "由" + androidTaskVerifyInfo.verify_user_name +
					"转发给" + androidTaskVerifyInfo.receive_user_name + "!";
			    }
			}
		    }
		}
		androidTaskInfo.taskForwardInfo = taskForwardInfo;
		androidTaskInfo.create_time_string = DateUtil.date2String(androidTaskInfo.create_time, "yyyy-MM-dd HH:mm:ss");
		 String create_time_string1 = DateUtil.date2String(androidTaskInfo.create_time, "yyyy-MM-dd HH:mm:ss");
			
			 try {
			androidTaskInfo.create_time =
			 DateUtil.string2UtilDate(create_time_string1,
			 "yyyy-MM-dd HH:mm:ss");
			 } catch (Exception e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
			 }	
		info.setCodeAndMsg(200);
		info.setInfo(androidTaskInfo);
		info.setRequest(request.path);
		return info;

	}


	/**
	 * 修改任务信息
	 * 
	 * @param App
	 *            文章信息 <必填>
	 */
	public static ResultInfo editUserTaskInfo(AndroidTaskInfo newAndroidTaskInfo,
			Request request) {
		ResultInfo info = new ResultInfo();
		AndroidTaskInfo androidTaskInfo = AndroidTaskInfo.findById(newAndroidTaskInfo.id);
		if (androidTaskInfo == null) {
			info.setCodeAndMsg(1010);
			info.setRequest(request.path);
			return info;
		}
		androidTaskInfo = newAndroidTaskInfo;
		androidTaskInfo.save();
		
		info.setCodeAndMsg(200);
		info.setInfo(androidTaskInfo);
		info.setRequest(request.path);
		return info;

	}

	/**
	 * 根据id删除任务
	 * 
	 * @param id
	 *            文章id <必填>
	 */
	public static ResultInfo deleteUserTaskInfo(Integer id, Request request) {
		ResultInfo info = new ResultInfo();
		AndroidTaskInfo androidTaskInfo = AndroidTaskInfo.findById(id);
		if (androidTaskInfo == null) {
			info.setCodeAndMsg(1010);
			info.setRequest(request.path);
			return info;
		}
		androidTaskInfo.delete();
		
		//删除任务处理流程信息
		List<AndroidTaskVerifyInfo> androidTaskVerifyInfoList = AndroidTaskVerifyInfo.findByTaskIdAsc(id);
		if (androidTaskVerifyInfoList != null && androidTaskVerifyInfoList.size() > 0) {//有改任务的处理信息
		   for (AndroidTaskVerifyInfo androidTaskVerifyInfo : androidTaskVerifyInfoList) {
			if (androidTaskVerifyInfo != null) {
			    androidTaskVerifyInfo.delete();
			}
		    }
		}
		
		info.setCodeAndMsg(200);
		info.setInfo(androidTaskInfo);
		info.setRequest(request.path);
		return info;
	}

//	/**
//	 * 查询文章名称是否已存在
//	 * 
//	 * @param <必填>
//	 */
//	public static ResultInfo isExistDoc(String title, Long id,
//			Long category_id, Request request) {
//		ResultInfo info = new ResultInfo();
//		try {
//			if (null == id) {
//				id = 0l;
//			}
//			if (AndroidNoticeInfo.count(" title=? and id!=? and class_id=?",
//					title, id, category_id) <= 0) {
//				info.setCodeAndMsg(200);
//				info.setRequest(request.path);
//				return info;
//			} else {
//				info.setCodeAndMsg(12002);
//				info.setRequest(request.path);
//				return info;
//			}
//		} catch (Exception e) {
//			Logger.error(e, " Error add Doc Doc.addDoc ");
//			info.setCodeAndMsg(500);
//			info.setRequest(request.path);
//			e.printStackTrace();
//			return info;
//		}
//	}

	
	/**
         * 任务审核/完成记录，同步更新AndroidTaskInfo表的status字段
         * TaskInfoService.java
         * @param task_id 任务编号
         * @param verify_status 审批状态：0:待审核；1:已完成；2：未完成(即审核未通过);
         * @param verify_user_id 审核/下派人
         * @param receive_user_id 转发任务接收人
         * @param verify_comment 说明
         * @param verify_type 处理类型：1：审核；2：转发
         * 2015年8月16日
	 */
	public static ResultInfo verifyTaskInfo( Integer task_id, Integer verify_status, Integer verify_user_id,
		Integer receive_user_id ,String verify_comment, Integer verify_type,
			Request request) {
		ResultInfo info = new ResultInfo();
		AndroidTaskInfo androidTaskInfo = AndroidTaskInfo.findById(task_id);
		if (androidTaskInfo == null) {
			info.setCodeAndMsg(1010);
			info.setRequest(request.path);
			return info;
		}
		AndroidUserInfo receiveAndroidUserInfo = AndroidUserInfo.findById(receive_user_id);
		if (receiveAndroidUserInfo == null) {//必须指定任务接收人
			info.setCodeAndMsg(1012);
			info.setRequest(request.path);
			return info;
		}
		
		Integer maxVerifyOrder = 0;
		List<AndroidTaskVerifyInfo> androidTaskVerifyInfoList = AndroidTaskVerifyInfo.findByTaskIdDesc(task_id);
		if (androidTaskVerifyInfoList != null && androidTaskVerifyInfoList.size() > 0) {//如果该任务已经转发过
		    AndroidTaskVerifyInfo androidTaskVerifyInfo = androidTaskVerifyInfoList.get(0);//返回值按照verify_order降序排列，因此第一条数据是最后一次转发数据
		    maxVerifyOrder = androidTaskVerifyInfo.verify_order;
		    if (androidTaskVerifyInfo.receive_user_id != verify_user_id) {
			    info.setCodeAndMsg(1011);
			    info.setRequest(request.path);
			    return info;
		    }
		}else {
			if (androidTaskInfo.receive_user_id != verify_user_id) {//判断只能由任务接收人进行审核
			    info.setCodeAndMsg(1011);
			    info.setRequest(request.path);
			    return info;
			}
		}
		//记录审核数据
		AndroidTaskVerifyInfo androidTaskVerifyInfo = new AndroidTaskVerifyInfo();
		androidTaskVerifyInfo.task_id = task_id;
		androidTaskVerifyInfo.task_name = androidTaskInfo.name;
		androidTaskVerifyInfo.task_type = androidTaskInfo.task_type;
		androidTaskVerifyInfo.receive_user_id = receive_user_id;
		androidTaskVerifyInfo.receive_user_name = receiveAndroidUserInfo.user_name;
		androidTaskVerifyInfo.verify_user_id = androidTaskInfo.receive_user_id;
		androidTaskVerifyInfo.verify_user_name = androidTaskInfo.receive_user_name;
		androidTaskVerifyInfo.verify_comment = verify_comment;
		androidTaskVerifyInfo.verify_order = maxVerifyOrder + 1;
		androidTaskVerifyInfo.verify_type = verify_type;
		androidTaskVerifyInfo.verify_status = verify_status;
		if (verify_type == 1) {//审核，同步更新任务表的status
		    androidTaskInfo.status = verify_status;
		    androidTaskInfo.save();
		}else {//转发任务，将上一条记录置为已完成,本条记录审核状态置为0：待审核
		    AndroidTaskVerifyInfo lastAndroidTaskVerifyInfo = androidTaskVerifyInfoList.get(0);
		    lastAndroidTaskVerifyInfo.verify_status = 1;
		    lastAndroidTaskVerifyInfo.verify_time = new Date();
		    lastAndroidTaskVerifyInfo.save();
		    androidTaskVerifyInfo.verify_status = 0;
		}
		androidTaskVerifyInfo.save();

		info.setCodeAndMsg(200);
		info.setInfo(androidTaskVerifyInfo);
		info.setRequest(request.path);
		return info;

	}

}
