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
import models.AndroidDepartmentInfo;
import models.AndroidFileInfo;
import models.AndroidReceiveNoticeUser;
import models.AndroidReceiveTaskUser;
import models.AndroidTaskInfo;
import models.AndroidUserGroup;
import models.AndroidUserInfo;
import models.Project;
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

	/**
	 * 添加一个任务
	 * 
	 * @param app
	 *            文章信息 <必填>
	 */
	public static ResultInfo addTaskInfo(
			AndroidTaskInfo androidTaskInfo, String receive_user_ids, Integer receive_type, Request request) {
		ResultInfo info = new ResultInfo();

		if (androidTaskInfo == null ) {//参数为空
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
		if (androidTaskInfo.task_type == 1 && androidTaskInfo.project_id > 0) {//工程任务
		    Project project = Project.find("id = ?", androidTaskInfo.project_id).first();
		    if (project != null) {
			androidTaskInfo.project_name = project.name;
		    }
		}
		//不再进行审核，全部状态改为1
		androidTaskInfo.status = 1;
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

		//插入接收人信息
		List<AndroidUserInfo> androidUserInfoList = null;
		if (receive_type == 0) {//本科室
		//取本部门除了自己以外的所有员工
		    androidUserInfoList = AndroidUserInfo.findUserInfoByDepartmentAndUser(androidUserInfo.department_id,androidUserInfo.id);
			String base_tag_name = "tag_department_";
			JSONObject jsonObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			String tag_name = base_tag_name + androidTaskInfo.department_id;
			jsonArray.add(tag_name);
			jsonObject.put("tag", jsonArray);
			androidTaskInfo.push_range = jsonObject.toString();

		}else if(receive_type == 1){//全部
		//取除本人以外的所有人
		    androidUserInfoList = AndroidUserInfo.find("id != ?", androidUserInfo.id).fetch();
		    androidTaskInfo.push_range = "all";
		}else {//2：指定人（具体接收人在receive_user_ids中指明）
			String base_tag_name = "tag_user_";
		    if (receive_user_ids !=null && receive_user_ids.length() > 0) {
				androidUserInfoList = new ArrayList<AndroidUserInfo>();
				String[] receive_user_id = receive_user_ids.split(",");
				JSONObject jsonObject = new JSONObject();
				JSONArray jsonArray = new JSONArray();
				for (int i = 0; i < receive_user_id.length; i++) {
				    AndroidUserInfo receiveAndroidUserInfo = AndroidUserInfo.find("id = ?", Integer.valueOf(receive_user_id[i])).first();
				    if (receiveAndroidUserInfo == null) {
					continue;
				    }
				    androidUserInfoList.add(receiveAndroidUserInfo);
				    String tag_name = base_tag_name + receive_user_id[i];
					jsonArray.add(tag_name);
				}
				jsonObject.put("tag", jsonArray);
				androidTaskInfo.push_range = jsonObject.toString();
		    }else {
		    	androidTaskInfo.push_range = "";
			}
		}
		androidTaskInfo.push_flag = 0;
		androidTaskInfo.create_time = new Date();
		androidTaskInfo.save();
		//添加任务接收人信息
		if (androidUserInfoList != null && androidUserInfoList.size() > 0) {
		    for (AndroidUserInfo receiveAndroidUserInfo : androidUserInfoList) {
			if (receiveAndroidUserInfo != null) {//为每个接收用户增加一条接收记录
			    addReceiveTaskUser(androidTaskInfo.id, androidTaskInfo.name, androidTaskInfo.task_type,
				    receiveAndroidUserInfo.id, receiveAndroidUserInfo.name,
				    androidTaskInfo.department_id, androidTaskInfo.department_name,
				    androidTaskInfo.create_user_id,androidTaskInfo.create_user_name,receive_type);
			}
		    }
		}
	    	
		//推送数据
		if (androidTaskInfo != null && androidTaskInfo.id != null) {
		   PushDoc pushDoc= new PushDoc();
		   String task_type_string = "工程任务";
		   if (androidTaskInfo.task_type == 2) {
			   task_type_string = "自定义任务";
		   }
		   // String pushDocContent = "由" + androidTaskInfo.create_user_id + "创建的" + task_type_string + ",任务名称为：" + androidTaskInfo.name + "!";
		   String pushDocContent = "由" + androidTaskInfo.create_user_name + "创建的" + task_type_string + ",任务名称为：" + androidTaskInfo.name + "!";
		   androidTaskInfo.push_msg_id = pushDoc.pushDoc(androidTaskInfo.name, 5, pushDocContent, androidTaskInfo.id, androidTaskInfo.push_range);
		   if (androidTaskInfo.push_msg_id != null && androidTaskInfo.push_msg_id > 0) {
			   androidTaskInfo.push_flag = 1;
		   }else {
			   androidTaskInfo.push_flag = 0;
		   }
		   androidTaskInfo.save();
		}
		
		info.setCodeAndMsg(200);
		info.setInfo(androidTaskInfo);
		info.setRequest(request.path);
		return info;

	}
    
	    /**
	     * 添加一则通知或消息的接收人信息
	     * 
	     * @param app
	     *            文章信息 <必填>
	     */
	    public static boolean addReceiveTaskUser(Integer task_id,String name,Integer task_type,
		    Integer receive_user_id,String receive_user_name, Integer department_id,String department_name,
		    Integer push_user_id,String push_user_name ,Integer receive_user_type) {
		  //接收人信息
		    AndroidReceiveTaskUser androidReceiveTaskUser = null;
		    //首先判断该消息对应的user是否已插入
		    if (receive_user_id == 0) {//全部人员或本部门人员
			androidReceiveTaskUser = AndroidReceiveTaskUser.findByTaskAndDepartment(task_id, department_id);
		    }else {
			androidReceiveTaskUser = AndroidReceiveTaskUser.findByTaskAndUser(task_id, receive_user_id);
		    }
		    
		    if (androidReceiveTaskUser == null) {//如果没有，则插入
			androidReceiveTaskUser = new AndroidReceiveTaskUser();
			androidReceiveTaskUser.task_id = task_id;
			androidReceiveTaskUser.name = name;
			androidReceiveTaskUser.task_type = task_type;
			androidReceiveTaskUser.receive_user_id = receive_user_id;
			androidReceiveTaskUser.receive_user_name = receive_user_name;
			androidReceiveTaskUser.department_id = department_id;
			androidReceiveTaskUser.department_name = department_name;
			androidReceiveTaskUser.push_user_id = push_user_id;
			androidReceiveTaskUser.push_user_name = push_user_name;
			androidReceiveTaskUser.receive_user_type = receive_user_type;
			androidReceiveTaskUser.if_read = 0;
			androidReceiveTaskUser.save();
		    }
		    return true;
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
	public static ResultInfo getUserTaskList(Integer user_id, /*Integer department_id,*/
		    Integer task_type,Integer status,String keywords,Integer p, Integer ps, Request request) {
		ResultInfo info = new ResultInfo();
		if (keywords != null && keywords.length() > 0) {//首先将keyword去空格
		    keywords = keywords.replaceAll(" ", "");//半角空格
		    if (keywords != null && keywords.length() > 0) {
			keywords = keywords.replaceAll("　", "");//全角空格
		    }
		}
		AndroidUserInfo androidUserInfo = null;
		String department_ids = "";
		if (user_id > 0) {
			androidUserInfo = AndroidUserInfo.findById(user_id);
			if (androidUserInfo == null) {
				info.setCodeAndMsg(400);
				 info.setRequest(request.path);
				 return info;
			}
			
			if (androidUserInfo.user_type >= 3) {//科主任、科室成员只能查看本部门数据
//				if (department_id > 0 && androidUserInfo.department_id != department_id) {
//					info.setCodeAndMsg(1015);
//					 info.setRequest(request.path);
//					 return info;
//				}
				//取同组部门列表
				department_ids = getGroupDepartmentIds(androidUserInfo.department_id);
				if (department_ids.equals("")) {//如果没有分组
				    department_ids += androidUserInfo.department_id;
				}
//				department_id = androidUserInfo.department_id;
//				user_id = 0;//任务列表不区分用户，只根据部门区分
			}else {//局长、副局可以查看全部任务
				user_id = 0;
				department_ids = "";
			}
		}else {
			info.setCodeAndMsg(400);
		    info.setRequest(request.path);
		    return info;
		}
		
//		List<AndroidTaskInfo> androidTaskInfoList = null;
//		if (keywords != null && keywords.length() > 0) {
//			if (user_id == 0) {//查询全部用户数据,主要针对局长、副局用户,不需要分部门
////			    if (department_ids.equals("")) {//部门为空,查询全部部门
//					if (task_type == 0) {//任务类型为空
//					    androidTaskInfoList = AndroidTaskInfo.find("name LIKE \'%" + keywords + "%\'  order by create_time desc").fetch(p,ps);
//					}else {//任务类别不为空
//					    androidTaskInfoList = AndroidTaskInfo.find("name LIKE \'%" + keywords + "%\' and task_type = ? order by create_time desc", task_type).fetch(p,ps);
//					}
////			    }else {//部门不为空,查询指定部门
////					if (task_type == 0) {//任务类型为空
////					    androidTaskInfoList = AndroidTaskInfo.find("name LIKE \'%" + keywords + "%\' and department_id IN (" + department_ids + ") and if_open = 1 order by create_time desc").fetch(p,ps);
////					}else {//任务类别不为空
////					    androidTaskInfoList = AndroidTaskInfo.find("name LIKE \'%" + keywords + "%\' and task_type = ? and department_id IN (" + department_ids + ") and if_open = 1  order by create_time desc", task_type).fetch(p,ps);
////					}
////			    }
//			}else {//查询指定用户的数据,主要针对科室领导和科员，内容主要分为：1、自己发布；2、自己接收；3、自己部门公开；4、自己相关部门公开
////			    if (department_ids.equals("")) {//部门为空,查询全部部门
////					if (task_type == 0) {//任务类型为空,查询全部
////					    androidTaskInfoList = AndroidTaskInfo.find("name LIKE \'%" + keywords + "%\' and create_user_id = ? order by create_time desc",user_id).fetch(p,ps);
////					}else {//任务类别不为空
////					    androidTaskInfoList = AndroidTaskInfo.find("name LIKE \'%" + keywords + "%\' and create_user_id = ? and task_type = ? order by create_time desc",user_id, task_type).fetch(p,ps);
////	//				    if (status == 0) {//全部
////	//					androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and task_type = ? order by create_time desc",user_id,user_id, task_type).fetch(p,ps);
////	//				    }else if(status == 2){//未完成
////	//						androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and task_type = ? and status IN (0,2) order by create_time desc",user_id,user_id,task_type).fetch(p,ps);
////	//				    }else {
////	//					androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and task_type = ? and status = ? order by create_time desc",user_id,user_id,task_type, status).fetch(p,ps);
////	//				    }
////					}
////			    }else {//部门不为空,查询指定部门
//					if (task_type == 0) {//任务类型为空
//					    androidTaskInfoList = AndroidTaskInfo.find("(name LIKE \'%" + keywords + "%\' and department_id IN (" + department_ids + ") and if_open = 1 ) or " +
//					    		"(name LIKE \'%" + keywords + "%\' and create_user_id = ?) order by create_time desc", user_id).fetch(p,ps);
//					}else {//任务类别不为空
//					    androidTaskInfoList = AndroidTaskInfo.find("(name LIKE \'%" + keywords + "%\' and task_type = ? and department_id IN (" + department_ids + ") and if_open = 1) or" +
//					    		"(name LIKE \'%" + keywords + "%\' and create_user_id = ? and task_type = ?) order by create_time desc", task_type,user_id,task_type).fetch(p,ps);
//					}
//			    }
////			}
//		}else {
//			if (user_id == 0) {//查询全部用户数据,主要针对局长、副局用户,不需要分部门
//				if (task_type == 0) {//任务类型为空
//				    androidTaskInfoList = AndroidTaskInfo.find(" order by create_time desc").fetch(p,ps);
//				}else {//任务类别不为空
//				    androidTaskInfoList = AndroidTaskInfo.find("task_type = ? order by create_time desc", task_type).fetch(p,ps);
//				}
//			}else {//查询指定用户的数据,主要针对科室领导和科员，内容主要分为：1、自己发布；2、自己接收；3、自己部门公开；4、自己相关部门公开
//					if (task_type == 0) {//任务类型为空
//					    androidTaskInfoList = AndroidTaskInfo.find("(department_id IN (" + department_ids + ") and if_open = 1 ) or " +
//					    		"(create_user_id = ?) order by create_time desc", user_id).fetch(p,ps);
//					}else {//任务类别不为空
//					    androidTaskInfoList = AndroidTaskInfo.find("(task_type = ? and department_id IN (" + department_ids + ") and if_open = 1) or" +
//					    		"(create_user_id = ? and task_type = ?) order by create_time desc", task_type,user_id,task_type).fetch(p,ps);
//					}
//			    }
//
////			if (user_id == 0) {//查询全部用户数据
////			    if (department_ids.equals("")) {//部门为空,查询全部部门
////					if (task_type == 0) {//任务类型为空
////					    androidTaskInfoList = AndroidTaskInfo.find("order by create_time desc").fetch(p,ps);
////					}else {//任务类别不为空
////					    androidTaskInfoList = AndroidTaskInfo.find("task_type = ? order by create_time desc", task_type).fetch(p,ps);
////					}
////			    }else {//部门不为空,查询指定部门
////					if (task_type == 0) {//任务类型为空
////					    androidTaskInfoList = AndroidTaskInfo.find("(department_id IN (" + department_ids + ") and if_open = 1)  order by create_time desc").fetch(p,ps);
////					}else {//任务类别不为空
////					    androidTaskInfoList = AndroidTaskInfo.find("task_type = ? and department_id IN (" + department_ids + ") and if_open = 1 order by create_time desc", task_type).fetch(p,ps);
////					}
////			    }
////			}else {//查询指定用户的数据
////			    if (department_ids.equals("")) {//部门为空,查询全部部门
////					if (task_type == 0) {//任务类型为空,查询全部
////					    androidTaskInfoList = AndroidTaskInfo.find("create_user_id = ? order by create_time desc",user_id).fetch(p,ps);
////					}else {//任务类别不为空
////					    androidTaskInfoList = AndroidTaskInfo.find("create_user_id = ? and task_type = ? order by create_time desc",user_id, task_type).fetch(p,ps);
////	//				    if (status == 0) {//全部
////	//					androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and task_type = ? order by create_time desc",user_id,user_id, task_type).fetch(p,ps);
////	//				    }else if(status == 2){//未完成
////	//						androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and task_type = ? and status IN (0,2) order by create_time desc",user_id,user_id,task_type).fetch(p,ps);
////	//				    }else {
////	//					androidTaskInfoList = AndroidTaskInfo.find("(receive_user_id = ? or create_user_id = ?) and task_type = ? and status = ? order by create_time desc",user_id,user_id,task_type, status).fetch(p,ps);
////	//				    }
////					}
////			    }else {//部门不为空,查询指定部门
////					if (task_type == 0) {//任务类型为空
////					    androidTaskInfoList = AndroidTaskInfo.find("(create_user_id = ? and department_id IN (" + department_ids + ") and if_open = 1) " +
////					    		"or (create_user_id = ?)  order by create_time desc", user_id,user_id).fetch(p,ps);
////					}else {//任务类别不为空
////					    androidTaskInfoList = AndroidTaskInfo.find("(create_user_id = ? and task_type = ? and department_id IN (" + department_ids + ") " +
////					    		"and if_open = 1) or (create_user_id = ? and task_type = ?) order by create_time desc", user_id,task_type,user_id,task_type).fetch(p,ps);
////					}
////			    }
////			}
//		}

		
		List<AndroidReceiveTaskUser> androidReceiveTaskUserList = null;
		if (keywords != null && keywords.length() > 0) {
		    if (user_id == 0) {//查询全部用户数据,主要针对局长、副局用户,不需要分部门
			if (task_type == 0) {//任务类型为空
			    androidReceiveTaskUserList = AndroidReceiveTaskUser.find("name LIKE \'%" + keywords + "%\' group by task_id order by create_time desc").fetch(p,ps);
			}else {//任务类别不为空
			    androidReceiveTaskUserList = AndroidReceiveTaskUser.find("name LIKE \'%" + keywords + "%\' and task_type = ?  group by task_id  order by create_time desc", task_type).fetch(p,ps);
			}
		    }else {//查询指定用户的数据,主要针对科室领导和科员，内容主要分为：1、自己发布；2、自己接收；3、自己部门公开；4、自己相关部门公开
			if (task_type == 0) {//任务类型为空
			    androidReceiveTaskUserList = AndroidReceiveTaskUser.find("(name LIKE \'%" + keywords + "%\' and department_id IN (" + department_ids + ") and if_open = 1 ) or " +
			    		"(name LIKE \'%" + keywords + "%\' and (push_user_id = ? or receive_user_id = ?))  group by task_id  order by create_time desc", user_id,user_id).fetch(p,ps);
			}else {//任务类别不为空
			    androidReceiveTaskUserList = AndroidReceiveTaskUser.find("(name LIKE \'%" + keywords + "%\' and task_type = ? and department_id IN (" + department_ids + ") and if_open = 1) or" +
			    		"(name LIKE \'%" + keywords + "%\' and (push_user_id = ?  or receive_user_id = ?) and task_type = ?)  group by task_id  order by create_time desc", task_type,user_id,user_id,task_type).fetch(p,ps);
			}
		    }
		}else {
		    if (user_id == 0) {//查询全部用户数据,主要针对局长、副局用户,不需要分部门
			if (task_type == 0) {//任务类型为空
			    androidReceiveTaskUserList = AndroidReceiveTaskUser.find(" id > 0 group by task_id  order by create_time desc").fetch(p,ps);
			}else {//任务类别不为空
			    androidReceiveTaskUserList = AndroidReceiveTaskUser.find("task_type = ?  group by task_id  order by create_time desc", task_type).fetch(p,ps);
			}
		    }else {//查询指定用户的数据,主要针对科室领导和科员，内容主要分为：1、自己发布；2、自己接收；3、自己部门公开；4、自己相关部门公开
			if (task_type == 0) {//任务类型为空
			    androidReceiveTaskUserList = AndroidReceiveTaskUser.find("(department_id IN (" + department_ids + ") and if_open = 1 ) or " +
			    		"(push_user_id = ?  or receive_user_id = ?) group by task_id  order by create_time desc", user_id,user_id).fetch(p,ps);
			}else {//任务类别不为空
			    androidReceiveTaskUserList = AndroidReceiveTaskUser.find("(task_type = ? and department_id IN (" + department_ids + ") and if_open = 1) or" +
			    		"((push_user_id = ?  or receive_user_id = ?) and task_type = ?)  group by task_id order by create_time desc", task_type,user_id,user_id,task_type).fetch(p,ps);
			}
		    }
		}
		
		String task_ids = "";
		if (androidReceiveTaskUserList != null && androidReceiveTaskUserList.size() > 0) {
		    for (AndroidReceiveTaskUser androidReceiveTaskUser : androidReceiveTaskUserList) {
			if (androidReceiveTaskUser != null) {
			    task_ids += androidReceiveTaskUser.task_id;
			    task_ids += ",";
			}
		    }
		}
		if (task_ids.endsWith(",")) {
		    task_ids = task_ids.substring(0, task_ids.length() - 1);
		}
		
		List<AndroidTaskInfo> androidTaskInfoList = AndroidTaskInfo.findByIds(task_ids);

		if (androidTaskInfoList != null && androidTaskInfoList.size() > 0) {//将附件的编号转换成url
		    for (AndroidTaskInfo androidTaskInfo : androidTaskInfoList ) {
			if (androidTaskInfo != null) {
				//取消息已读未读状态
				if (androidTaskInfo.create_user_id != user_id) {//如果本人是接收人，取已读未读状态
					AndroidReceiveTaskUser androidReceiveTaskUser = AndroidReceiveTaskUser.findByTaskAndUser(androidTaskInfo.id, user_id);
					if (androidReceiveTaskUser != null) {
						androidTaskInfo.if_read = androidReceiveTaskUser.if_read;
					}else {
						androidTaskInfo.if_read = 1;
					}
				}else {//本人发送的公告消息默认为已读
					androidTaskInfo.if_read =1 ;
				}

				
			    if (androidTaskInfo.attachment != null && androidTaskInfo.attachment.length() > 0) {
					if (androidTaskInfo.attachment.endsWith(",")) {
							androidTaskInfo.attachment = androidTaskInfo.attachment.substring(0,androidTaskInfo.attachment.length() - 1);
					}
					Map<String, String> attachmentNameMap = AndroidFileInfo.findByIds(androidTaskInfo.attachment);
					if (attachmentNameMap != null&& attachmentNameMap.size() > 0) {
						androidTaskInfo.attachment = attachmentNameMap.get("orginFileName");
						androidTaskInfo.attachment_simp = attachmentNameMap.get("thumbFileName");// 图片缩略图
					}else {
						androidTaskInfo.attachment = "";
						androidTaskInfo.attachment_simp = "";// 图片缩略图
					}
			    }
			    if (androidTaskInfo.media != null && androidTaskInfo.media.length() > 0) {
					if (androidTaskInfo.media.endsWith(",")) {
					    androidTaskInfo.media = androidTaskInfo.media.substring(0, androidTaskInfo.media.length() - 1);
					}
					Map<String, String> mediaNameMap = AndroidFileInfo.findByIds(androidTaskInfo.media);
					if (mediaNameMap != null && mediaNameMap.size() > 0) {
						androidTaskInfo.media = mediaNameMap.get("orginFileName");
					}else {
						androidTaskInfo.media = "";
					}
			    }
			    androidTaskInfo.create_time_string = DateUtil.date2String(androidTaskInfo.create_time, "yyyy-MM-dd HH:mm:ss");
			    String create_time_string = DateUtil.date2String(androidTaskInfo.create_time, "yyyy-MM-dd HH:mm:ss");
			    try {
				androidTaskInfo.create_time = DateUtil.string2UtilDate(create_time_string, "yyyy-MM-dd HH:mm:ss");
			    } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
			}
		    }
		}else {
		    info.setCodeAndMsg(200);
		    info.setInfo(androidTaskInfoList);
		    info.setCount(0);
		    info.setRequest(request.path);
		    return info;
		}

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
	public static ResultInfo getUserTaskInfo(Integer id,Integer user_id, Request request) {
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
		    Map<String, String> attachmentNameMap = AndroidFileInfo.findByIds(androidTaskInfo.attachment);
			if (attachmentNameMap != null&& attachmentNameMap.size() > 0) {
				androidTaskInfo.attachment = attachmentNameMap.get("orginFileName");
				androidTaskInfo.attachment_simp = attachmentNameMap.get("thumbFileName");// 图片缩略图
			}else {
				androidTaskInfo.attachment = "";
				androidTaskInfo.attachment_simp = "";// 图片缩略图
			}
		}
		if (androidTaskInfo.media != null && androidTaskInfo.media.length() > 0) {
		    if (androidTaskInfo.media.endsWith(",")) {
			androidTaskInfo.media = androidTaskInfo.media.substring(0, androidTaskInfo.media.length() - 1);
		    }
		    Map<String, String> mediaNameMap = AndroidFileInfo.findByIds(androidTaskInfo.media);
			if (mediaNameMap != null && mediaNameMap.size() > 0) {
				androidTaskInfo.media = mediaNameMap.get("orginFileName");
			}else {
				androidTaskInfo.media = "";
			}
		}
		String create_time_string =  DateUtil.date2String(androidTaskInfo.create_time, "yyyy-MM-dd HH:mm:ss");
		  //获取任务转发信息
		androidTaskInfo.taskForwardInfo = "该任务于" + create_time_string + "由" + androidTaskInfo.create_user_name + "创建！";

		androidTaskInfo.create_time_string = DateUtil.date2String(androidTaskInfo.create_time, "yyyy-MM-dd HH:mm:ss");
		 String create_time_string1 = DateUtil.date2String(androidTaskInfo.create_time, "yyyy-MM-dd HH:mm:ss");
			
		try {
		    androidTaskInfo.create_time = DateUtil.string2UtilDate(create_time_string1,	 "yyyy-MM-dd HH:mm:ss");
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		androidTaskInfo.if_open_power = 0;
		androidTaskInfo.if_receive_user = 0;
		//如果user_id不是发送人，同时是接收人，则user_id可以回复该任务
		if(!androidTaskInfo.create_user_id.equals(user_id)){
		    AndroidReceiveTaskUser androidReceiveTaskUser = AndroidReceiveTaskUser.findByTaskAndUser(androidTaskInfo.id, user_id);
		    if (androidReceiveTaskUser != null) {//是接收人，可以回复
			androidTaskInfo.if_receive_user = 1;
		    }
		}
		//如果user_id是部门领导，则可以公开该任务
		AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ?", user_id).first();
		if (androidUserInfo != null && androidUserInfo.user_type == 3 && androidUserInfo.department_id == androidTaskInfo.department_id) {//科室领导可以公开任务
		    androidTaskInfo.if_open_power = 1;		    
		}
		
		//修改已读标志
	    AndroidReceiveTaskUser receiveTaskUser = AndroidReceiveTaskUser.findByTaskAndUser(id, user_id);
	    if (receiveTaskUser != null) {
	    	receiveTaskUser.if_read = 1;
	    	receiveTaskUser.read_time = new Date();
	    	receiveTaskUser.save();
		}
	    
	    if (androidTaskInfo.create_user_id.equals(user_id)) {//对于发布人查看公告消息，同步返回已读和未读用户列表；
			  //取公告已读接收人记录
			List<AndroidReceiveTaskUser> androidReceiveTaskUserList = 
				AndroidReceiveTaskUser.find("task_id = ? and receive_user_id != ? and if_read = 1", id,androidTaskInfo.create_user_id).fetch();
			    String receive_user_ids = "";
			    String receive_user_names = "";
			if (androidReceiveTaskUserList != null && androidReceiveTaskUserList.size() > 0) {
				for (AndroidReceiveTaskUser androidReceiveTaskUser : androidReceiveTaskUserList) {
				    if (androidReceiveTaskUser != null) {
					receive_user_ids += androidReceiveTaskUser.receive_user_id;
					receive_user_ids += ",";
					receive_user_names += androidReceiveTaskUser.receive_user_name;
					receive_user_names += ",";
				    }
				}
			}
			if (receive_user_ids.endsWith(",")) {
			    receive_user_ids = receive_user_ids.substring(0, receive_user_ids.length() - 1);
			}
			if (receive_user_names.endsWith(",")) {
				receive_user_names = receive_user_names.substring(0, receive_user_names.length() - 1);
			}
			androidTaskInfo.receive_user_ids = receive_user_ids;
			androidTaskInfo.receive_user_names = receive_user_names;
			
			//取公告未读接收人记录
			List<AndroidReceiveTaskUser> unReadAndroidReceiveTaskUserList = 
				AndroidReceiveTaskUser.find("task_id = ? and receive_user_id != ? and if_read = 0", id,androidTaskInfo.create_user_id).fetch();
		    String unreceive_user_ids = "";
		    String unreceive_user_names = "";
			if (unReadAndroidReceiveTaskUserList != null && unReadAndroidReceiveTaskUserList.size() > 0) {
				for (AndroidReceiveTaskUser androidReceiveTaskUser : unReadAndroidReceiveTaskUserList) {
				    if (androidReceiveTaskUser != null) {
					unreceive_user_ids += androidReceiveTaskUser.receive_user_id;
					unreceive_user_ids += ",";
					unreceive_user_names += androidReceiveTaskUser.receive_user_name;
					unreceive_user_names += ",";
				    }
				}
			}
			if (unreceive_user_ids.endsWith(",")) {
				unreceive_user_ids = unreceive_user_ids.substring(0, unreceive_user_ids.length() - 1);
			}
			if (unreceive_user_names.endsWith(",")) {
				unreceive_user_names = unreceive_user_names.substring(0, unreceive_user_names.length() - 1);
			}
	//		if(receive_user_ids.equals("null")){
	//			receive_user_ids = "";
	//		}
			androidTaskInfo.unreceive_user_ids = unreceive_user_ids;
			androidTaskInfo.unreceive_user_names = unreceive_user_names;
		}

	    
		info.setCodeAndMsg(200);
		info.setInfo(androidTaskInfo);
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
	public static String getGroupDepartmentIds(Integer department_id) {
		AndroidDepartmentInfo  androidDepartmentInfo = AndroidDepartmentInfo.findById(department_id);
		if (androidDepartmentInfo == null) {
		    return "";
		}
		String group_ids = "";
		List<AndroidUserGroup> androidUserGroupList = AndroidUserGroup.findGroupInfoByUserAndGroupType(department_id, 2);
		if (androidUserGroupList == null || androidUserGroupList.size() <= 0) {//如果该部门没有分组
		   return "";
		}else {
		    for (AndroidUserGroup androidUserGroup : androidUserGroupList) {
			if (androidUserGroup != null && androidUserGroup.user_id > 0) {
			    group_ids += androidUserGroup.group_id;
			    group_ids += ",";
			}
		    }
		}
		if (group_ids.endsWith(",")) {
		    group_ids = group_ids.substring(0, group_ids.length() - 1);
		}
		String department_ids = "";

		if (group_ids.length() > 0) {
		    androidUserGroupList = AndroidUserGroup.findGroupInfoByGroupAndGroupType(group_ids,2);
		    if (androidUserGroupList != null && androidUserGroupList.size() > 0) {
			for (AndroidUserGroup androidUserGroup : androidUserGroupList) {
			    if (androidUserGroup != null && androidUserGroup.user_id > 0) {
				department_ids += androidUserGroup.user_id;
				department_ids += ",";
			    }
			}
		    }
		}
		if (department_ids.endsWith(",")) {
		    department_ids = department_ids.substring(0, department_ids.length() - 1);
		}
		return department_ids;

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

		info.setCodeAndMsg(200);
		info.setInfo(androidTaskInfo);
		info.setRequest(request.path);
		return info;
	}

	/**
	 * 将一个任务置为公开,如果当前任务是回复任务，则同时将其回复的任务也公开
	 * TaskInfoService.java
	 * @param user_id
	 * @param task_id
	 * @param request
	 * @return
	 * 2016年1月31日
	 */
	public static ResultInfo setTaskOpen(Integer user_id, Integer task_id, Integer if_open,
			Request request) {
		ResultInfo info = new ResultInfo();
		AndroidTaskInfo androidTaskInfo = AndroidTaskInfo.findById(task_id);
		if (androidTaskInfo == null) {
		    info.setCodeAndMsg(1010);
		    info.setRequest(request.path);
		    return info;
		}
		
		AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ?", user_id).first();
		if (androidUserInfo == null) {
		    info.setCodeAndMsg(400);
		    info.setRequest(request.path);
		    return info;
		}
		if (androidUserInfo.user_type != 3) {//只有科室领导可以公开任务
		    info.setCodeAndMsg(1022);
		    info.setRequest(request.path);
		    return info;
		}
		androidTaskInfo.if_open = if_open;//公开该任务
		androidTaskInfo.save();
		//更新接收表数据
		setReceiveTaskOpen(androidTaskInfo.id, if_open);
		
		//如果当前任务是回复任务，则同时将其回复的任务也公开
		while(androidTaskInfo.reply_task_id > 0){
		    androidTaskInfo = AndroidTaskInfo.findById(androidTaskInfo.reply_task_id);
		    if (androidTaskInfo == null) {
			break;
		    }else {
			setReceiveTaskOpen(androidTaskInfo.id, if_open);
			androidTaskInfo.if_open = if_open;
			androidTaskInfo.save();
		    }
		}
		
		info.setCodeAndMsg(200);
		info.setInfo(androidTaskInfo);
		info.setRequest(request.path);
		return info;

	}
	
	
	/**
	 * 将一个任务置为公开,如果当前任务是回复任务，则同时将其回复的任务也公开
	 * TaskInfoService.java
	 * @param user_id
	 * @param task_id
	 * @param request
	 * @return
	 * 2016年1月31日
	 */
	public static void setReceiveTaskOpen(Integer task_id, Integer if_open) {
		List<AndroidReceiveTaskUser> androidReceiveTaskUserList = AndroidReceiveTaskUser.findByTaskIds(task_id);
		if (androidReceiveTaskUserList != null && androidReceiveTaskUserList.size() > 0) {
		    for (AndroidReceiveTaskUser androidReceiveTaskUser : androidReceiveTaskUserList) {
			if (androidReceiveTaskUser != null) {
			    androidReceiveTaskUser.if_open = if_open;
			    androidReceiveTaskUser.save();
			}
		    }
		}
	}

	
	
//	/**
//         * 任务审核/完成记录，同步更新AndroidTaskInfo表的status字段
//         * TaskInfoService.java
//         * @param task_id 任务编号
//         * @param verify_status 审批状态：0:待审核；1:已完成；2：未完成(即审核未通过);
//         * @param verify_user_id 审核/下派人
//         * @param receive_user_id 转发任务接收人
//         * @param verify_comment 说明
//         * @param verify_type 处理类型：1：审核；2：转发
//         * 2015年8月16日
//	 */
//	public static ResultInfo verifyTaskInfo( Integer task_id, Integer verify_status, Integer verify_user_id,
//		Integer receive_user_id ,String verify_comment, Integer verify_type,
//			Request request) {
//		ResultInfo info = new ResultInfo();
//		AndroidTaskInfo androidTaskInfo = AndroidTaskInfo.findById(task_id);
//		if (androidTaskInfo == null) {
//			info.setCodeAndMsg(1010);
//			info.setRequest(request.path);
//			return info;
//		}
//		AndroidUserInfo receiveAndroidUserInfo = AndroidUserInfo.findById(receive_user_id);
//		if (receiveAndroidUserInfo == null) {//必须指定任务接收人
//			info.setCodeAndMsg(1012);
//			info.setRequest(request.path);
//			return info;
//		}
//		
//		Integer maxVerifyOrder = 0;
//		List<AndroidTaskVerifyInfo> androidTaskVerifyInfoList = AndroidTaskVerifyInfo.findByTaskIdDesc(task_id);
//		if (androidTaskVerifyInfoList != null && androidTaskVerifyInfoList.size() > 0) {//如果该任务已经转发过
//		    AndroidTaskVerifyInfo androidTaskVerifyInfo = androidTaskVerifyInfoList.get(0);//返回值按照verify_order降序排列，因此第一条数据是最后一次转发数据
//		    maxVerifyOrder = androidTaskVerifyInfo.verify_order;
//		    if (androidTaskVerifyInfo.receive_user_id != verify_user_id) {
//			    info.setCodeAndMsg(1011);
//			    info.setRequest(request.path);
//			    return info;
//		    }
//		}else {
//			if (androidTaskInfo.receive_user_id != verify_user_id) {//判断只能由任务接收人进行审核
//			    info.setCodeAndMsg(1011);
//			    info.setRequest(request.path);
//			    return info;
//			}
//		}
//		//记录审核数据
//		AndroidTaskVerifyInfo androidTaskVerifyInfo = new AndroidTaskVerifyInfo();
//		androidTaskVerifyInfo.task_id = task_id;
//		androidTaskVerifyInfo.task_name = androidTaskInfo.name;
//		androidTaskVerifyInfo.task_type = androidTaskInfo.task_type;
//		androidTaskVerifyInfo.receive_user_id = receive_user_id;
//		androidTaskVerifyInfo.receive_user_name = receiveAndroidUserInfo.user_name;
//		androidTaskVerifyInfo.verify_user_id = androidTaskInfo.receive_user_id;
//		androidTaskVerifyInfo.verify_user_name = androidTaskInfo.receive_user_name;
//		androidTaskVerifyInfo.verify_comment = verify_comment;
//		androidTaskVerifyInfo.verify_order = maxVerifyOrder + 1;
//		androidTaskVerifyInfo.verify_type = verify_type;
//		androidTaskVerifyInfo.verify_status = verify_status;
//		if (verify_type == 1) {//审核，同步更新任务表的status
//		    androidTaskInfo.status = verify_status;
//		    androidTaskInfo.save();
//		}else {//转发任务，将上一条记录置为已完成,本条记录审核状态置为0：待审核
//		    AndroidTaskVerifyInfo lastAndroidTaskVerifyInfo = androidTaskVerifyInfoList.get(0);
//		    lastAndroidTaskVerifyInfo.verify_status = 1;
//		    lastAndroidTaskVerifyInfo.verify_time = new Date();
//		    lastAndroidTaskVerifyInfo.save();
//		    androidTaskVerifyInfo.verify_status = 0;
//		}
//		androidTaskVerifyInfo.save();
//
//		info.setCodeAndMsg(200);
//		info.setInfo(androidTaskVerifyInfo);
//		info.setRequest(request.path);
//		return info;
//
//	}

}
