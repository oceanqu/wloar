package service.notice;

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
import models.AndroidNoticeInfo;
import models.AndroidReceiveNoticeUser;
import models.AndroidReceiveTaskUser;
import models.AndroidUserInfo;
import models.ResultInfo;
import play.Logger;
import play.mvc.Http.Request;
import util.DateUtil;
import util.PushDoc;



/**
 * 后台自定义资讯添加接口
 * @author 
 *
 */
public class CustomDocinfoService {
    
//    public static ResourceBundle rb = ResourceBundle.getBundle("config");
//    static String keywordsCnkeetServer = rb.getString("keywords.cnkeet.server");


    /**
     * 添加一个文章
     * CustomDocinfo.java
     * @param androidNoticeInfo
     * @param receive_user_ids 接收用户列表，主要针对消息发送
     * @param receive_type 接受用户类型，主要针对科长发布公告，0：本科室；1：全部；2：指定人（具体接收人在receive_user_ids中指明）
     */
    public static ResultInfo addNoticeInfo(AndroidNoticeInfo androidNoticeInfo, String receive_user_ids, Integer receive_type, Request request) {
	ResultInfo info = new ResultInfo();
		AndroidUserInfo pushAndroidUserInfo = AndroidUserInfo.find("id = ?", androidNoticeInfo.push_user_id).first();
		if (pushAndroidUserInfo == null) {
//		    androidNoticeInfo.push_user_name = "";
			info.setCodeAndMsg(400);
			info.setRequest(request.path);
			return info;
		}

	    androidNoticeInfo.push_user_name = pushAndroidUserInfo.name;
		if (androidNoticeInfo.department_id == null) {
		    androidNoticeInfo.department_id = pushAndroidUserInfo.department_id;
		    androidNoticeInfo.department_name = pushAndroidUserInfo.department_name;
		}

	try {
	    //根据消息类型，确定消息push_range
	    if(androidNoticeInfo.notice_type == 0){//公告，管理员、局长、副局全局发送；科长、副科本科室发送，科员不能发送公告
	    	if (pushAndroidUserInfo.user_type == 4) {//科员不能发送公告
	    		info.setCodeAndMsg(1016);
				 info.setRequest(request.path);
				 return info;
			}else if (pushAndroidUserInfo.user_type == 3) {//科长、副科发送,有两种选择：本科室所有人员和全员。
//			    if (pushAndroidUserInfo.department_id > 0 && pushAndroidUserInfo.department_id != androidNoticeInfo.department_id) {//只能发布本部门的任务
//				 info.setCodeAndMsg(1018);
//				 info.setRequest(request.path);
//				 return info;
//			    }
			    if (receive_type == 0) {//本科室
					String base_tag_name = "tag_department_";
					JSONObject jsonObject = new JSONObject();
					JSONArray jsonArray = new JSONArray();
					String tag_name = base_tag_name + androidNoticeInfo.department_id;
					jsonArray.add(tag_name);
					jsonObject.put("tag", jsonArray);
					androidNoticeInfo.push_range = jsonObject.toString();
			    }else if (receive_type == 1) {//1：全部
			    	androidNoticeInfo.push_range = "all";
			    }else {//2：指定人（具体接收人在receive_user_ids中指明）
					String base_tag_name = "tag_user_";
					if (receive_user_ids !=null && receive_user_ids.length() > 0) {
					    String[] receive_user_id = receive_user_ids.split(",");
						JSONObject jsonObject = new JSONObject();
						JSONArray jsonArray = new JSONArray();
					    for (int i = 0; i < receive_user_id.length; i++) {
						String tag_name = base_tag_name + receive_user_id[i];
						jsonArray.add(tag_name);
					    }
						jsonObject.put("tag", jsonArray);
						androidNoticeInfo.push_range = jsonObject.toString();
					}else {
						androidNoticeInfo.push_range = "";
					}
			    }

			}else {//管理员、局长、副局全局发送；
		    		androidNoticeInfo.push_range = "all";
			}
	    	
	    }else {//消息，可群发消息，app端传所有接收用户列表
//		if (androidNoticeInfo.receive_user_type == 1) {//全部成员
//		    androidNoticeInfo.push_range = "all";
//		}else if (androidNoticeInfo.receive_user_type == 2) {//本科室成员
//		    List<AndroidUserInfo> androidUserInfoList = AndroidUserInfo.findUserInfoByDepartmentId(androidNoticeInfo.department_id);
//		    if (androidUserInfoList != null && androidUserInfoList.size() > 0) {
//			String base_tag_name = "tag_user_";
//			JSONObject jsonObject = new JSONObject();
//			JSONArray jsonArray = new JSONArray();
//			for (AndroidUserInfo androidUserInfo : androidUserInfoList) {
//			    String tag_name = base_tag_name + androidUserInfo.id;
//			    jsonArray.add(tag_name);
//			    jsonObject.put("tag", jsonArray);
//			    androidNoticeInfo.push_range = jsonObject.toString();
//			}
//		    }
//		}else {//指定个人
			String base_tag_name = "tag_user_";
			if (receive_user_ids !=null && receive_user_ids.length() > 0) {
			    String[] receive_user_id = receive_user_ids.split(",");
				JSONObject jsonObject = new JSONObject();
				JSONArray jsonArray = new JSONArray();
			    for (int i = 0; i < receive_user_id.length; i++) {
				String tag_name = base_tag_name + receive_user_id[i];
				jsonArray.add(tag_name);
			    }
				jsonObject.put("tag", jsonArray);
				androidNoticeInfo.push_range = jsonObject.toString();
			}else {
				androidNoticeInfo.push_range = "";
			}
//		}
	    }
	    	androidNoticeInfo.create_time = new Date();
	    	androidNoticeInfo.push_flag = 0;

		androidNoticeInfo.save();
	} catch (Exception e) {
	    Logger.error(e, " Error add App App.addApp ");
		info.setCodeAndMsg(500);
		info.setRequest(request.path);
		e.printStackTrace();
		return info;
	}
	//插入接收人信息
	List<AndroidUserInfo> androidUserInfoList = null;
	if(androidNoticeInfo.notice_type == 0){//公告，不指定接收人，有系统判断接收人
    	if (pushAndroidUserInfo.user_type == 4) {//科员不能发送公告
    		info.setCodeAndMsg(1016);
			 info.setRequest(request.path);
			 return info;
		}else if (pushAndroidUserInfo.user_type == 3) {//科长、副科本科室发送
//		    if (pushAndroidUserInfo.department_id > 0 && pushAndroidUserInfo.department_id != androidNoticeInfo.department_id) {//只能发布本部门的任务
//			 info.setCodeAndMsg(1018);
//			 info.setRequest(request.path);
//			 return info;
//		    }
		    if (receive_type == 0) {//本科室
			//取本部门除了自己以外的所有员工
			    androidUserInfoList = AndroidUserInfo.findUserInfoByDepartmentAndUser(pushAndroidUserInfo.department_id,pushAndroidUserInfo.id);
		    }else if(receive_type == 1){//全部
		    	//取除本人以外的所有人
			    androidUserInfoList = AndroidUserInfo.find("id != ?", pushAndroidUserInfo.id).fetch();
		    }else {//2：指定人（具体接收人在receive_user_ids中指明）
			  if (receive_user_ids !=null && receive_user_ids.length() > 0) {
			      androidUserInfoList = new ArrayList<AndroidUserInfo>();
			      String[] receive_user_id = receive_user_ids.split(",");
			      for (int i = 0; i < receive_user_id.length; i++) {
				  AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ?", Integer.valueOf(receive_user_id[i])).first();
				  if (androidUserInfo == null) {
				      continue;
				  }
				  androidUserInfoList.add(androidUserInfo);
			      }
			  }
		    }
		}else {//管理员、局长、副局全局发送；
//	    	androidNoticeInfo.push_range = "all";
	    	//取除本人以外的所有人
		    androidUserInfoList = AndroidUserInfo.find("id != ?", pushAndroidUserInfo.id).fetch();
		}
	    if (androidUserInfoList != null && androidUserInfoList.size() > 0) {
			for (AndroidUserInfo androidUserInfo : androidUserInfoList) {
				if (androidUserInfo != null) {//为每个接收用户增加一条接收记录
					addReceiveNoticeUser(androidNoticeInfo.id, androidNoticeInfo.title, androidNoticeInfo.notice_type,
							androidUserInfo.id, androidUserInfo.name,
							androidNoticeInfo.department_id, androidNoticeInfo.department_name,
							androidNoticeInfo.push_user_id,androidNoticeInfo.push_user_name,androidNoticeInfo.receive_user_type);
				}
			}
		}
    	
//	    addReceiveNoticeUser(androidNoticeInfo.id, androidNoticeInfo.title, androidNoticeInfo.notice_type,
//		    0, "", 0, "",androidNoticeInfo.push_user_id,androidNoticeInfo.push_user_name,androidNoticeInfo.receive_user_type);
	}else {//消息，指定接收人，app端发送所有接收人id列表
//	    if (androidNoticeInfo.receive_user_type == 1) {//全部成员
//		addReceiveNoticeUser(androidNoticeInfo.id, androidNoticeInfo.title, androidNoticeInfo.notice_type,
//			    0, "", 0, "",androidNoticeInfo.push_user_id,androidNoticeInfo.push_user_name,androidNoticeInfo.receive_user_type);
//		}else if (androidNoticeInfo.receive_user_type == 2) {//本科室成员
//		    addReceiveNoticeUser(androidNoticeInfo.id, androidNoticeInfo.title, androidNoticeInfo.notice_type,
//			    0, "", androidNoticeInfo.department_id, androidNoticeInfo.department_name,
//			    androidNoticeInfo.push_user_id,androidNoticeInfo.push_user_name,androidNoticeInfo.receive_user_type);
//		}else {
		    if (receive_user_ids !=null && receive_user_ids.length() > 0) {
			    String[] receive_user_id = receive_user_ids.split(",");
			    for (int i = 0; i < receive_user_id.length; i++) {
				AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ?", Integer.valueOf(receive_user_id[i])).first();
				if (androidUserInfo == null) {
				    continue;
				}
				addReceiveNoticeUser(androidNoticeInfo.id, androidNoticeInfo.title, androidNoticeInfo.notice_type,
					Integer.valueOf(receive_user_id[i]), androidUserInfo.name,
					androidNoticeInfo.department_id, androidNoticeInfo.department_name,
					androidNoticeInfo.push_user_id,androidNoticeInfo.push_user_name,androidNoticeInfo.receive_user_type);
			    }
		    }
//		}
	}
	
	
//	if(androidNoticeInfo.notice_type == 0){//群发消息,所有人都能收到
//	    addReceiveNoticeUser(androidNoticeInfo.id, androidNoticeInfo.title, androidNoticeInfo.notice_type,
//		    0, "", 0, "",androidNoticeInfo.push_user_id,androidNoticeInfo.push_user_name,androidNoticeInfo.receive_user_type);
//	}else {
//	    if (androidNoticeInfo.receive_user_type == 1) {//全部成员
//		addReceiveNoticeUser(androidNoticeInfo.id, androidNoticeInfo.title, androidNoticeInfo.notice_type,
//			    0, "", 0, "",androidNoticeInfo.push_user_id,androidNoticeInfo.push_user_name,androidNoticeInfo.receive_user_type);
//		}else if (androidNoticeInfo.receive_user_type == 2) {//本科室成员
//		    addReceiveNoticeUser(androidNoticeInfo.id, androidNoticeInfo.title, androidNoticeInfo.notice_type,
//			    0, "", androidNoticeInfo.department_id, androidNoticeInfo.department_name,
//			    androidNoticeInfo.push_user_id,androidNoticeInfo.push_user_name,androidNoticeInfo.receive_user_type);
//		}else {
//		    if (receive_user_ids !=null && receive_user_ids.length() > 0) {
//			    String[] receive_user_id = receive_user_ids.split(",");
//			    for (int i = 0; i < receive_user_id.length; i++) {
//				AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ?", Integer.valueOf(receive_user_id[i])).first();
//				if (androidUserInfo == null) {
//				    continue;
//				}
//				addReceiveNoticeUser(androidNoticeInfo.id, androidNoticeInfo.title, androidNoticeInfo.notice_type,
//					Integer.valueOf(receive_user_id[i]), androidUserInfo.name,
//					androidNoticeInfo.department_id, androidNoticeInfo.department_name,
//					androidNoticeInfo.push_user_id,androidNoticeInfo.push_user_name,androidNoticeInfo.receive_user_type);
//			    }
//		    }
//		}
//	}
	//推送数据
	if (androidNoticeInfo != null && androidNoticeInfo.id != null) {
	   PushDoc pushDoc= new PushDoc();
	   androidNoticeInfo.push_msg_id = pushDoc.pushDoc(androidNoticeInfo.title, androidNoticeInfo.notice_type, androidNoticeInfo.content, androidNoticeInfo.id, androidNoticeInfo.push_range);
	   if (androidNoticeInfo.push_msg_id != null && androidNoticeInfo.push_msg_id > 0) {
	    androidNoticeInfo.push_flag = 1;
	   }else {
	    androidNoticeInfo.push_flag = 0;
	   }
	   androidNoticeInfo.save();
	}
	info.setCodeAndMsg(200);
	info.setInfo(androidNoticeInfo);
	info.setRequest(request.path);
	return info;

    }

    /**
     * 添加一则通知或消息的接收人信息
     * 
     * @param app
     *            文章信息 <必填>
     */
    public static boolean addReceiveNoticeUser(Integer notice_id,String title,Integer notice_type,
	    Integer receive_user_id,String receive_user_name, Integer department_id,String department_name,
	    Integer push_user_id,String push_user_name ,Integer receive_user_type) {
	  //接收人信息
	    AndroidReceiveNoticeUser androidReceiveNoticeUser = null;
	    //首先判断该消息对应的user是否已插入
	    if (receive_user_id == 0) {//全部人员或本部门人员
	    	androidReceiveNoticeUser = AndroidReceiveNoticeUser.findByNoticeAndDepartment(notice_id, department_id);
	    }else {
	    	androidReceiveNoticeUser = AndroidReceiveNoticeUser.findByNoticeAndUser(notice_id, receive_user_id);
	    }
	    
	    if (androidReceiveNoticeUser == null) {//如果没有，则插入
			androidReceiveNoticeUser = new AndroidReceiveNoticeUser();
			androidReceiveNoticeUser.notice_id = notice_id;
			androidReceiveNoticeUser.title = title;
			androidReceiveNoticeUser.notice_type = notice_type;
			androidReceiveNoticeUser.receive_user_id = receive_user_id;
			androidReceiveNoticeUser.receive_user_name = receive_user_name;
			androidReceiveNoticeUser.department_id = department_id;
			androidReceiveNoticeUser.department_name = department_name;
			androidReceiveNoticeUser.push_user_id = push_user_id;
			androidReceiveNoticeUser.push_user_name = push_user_name;
			androidReceiveNoticeUser.receive_user_type = receive_user_type;
			androidReceiveNoticeUser.if_read = 0;//未读
			androidReceiveNoticeUser.save();
	    }
	    return true;
    }
	
    
    /**
     * 查询用户消息详情,
     * 公告:对于发布人查看公告消息，同步返回已读和未读用户列表；
     * 		对于接收人查看公告消息，不返回已读和未读用户列表；
     * CustomDocinfoService.java
     * @param id
     * @param user_id 阅读人
     * @param request
     * @return
     * 2015年6月15日
     */
    public static ResultInfo getNoticeInfo(Integer id, Integer user_id, Request request) {
	ResultInfo info = new ResultInfo();
	    AndroidNoticeInfo androidNoticeInfo = AndroidNoticeInfo.findById(id);
	    if (androidNoticeInfo == null) {
	    	info.setCodeAndMsg(12001);
			info.setRequest(request.path);
			return info;
		}
	    //修改已读标志
	    AndroidReceiveNoticeUser receiveNoticeUser = AndroidReceiveNoticeUser.findByNoticeAndUser(id, user_id);
	    if (receiveNoticeUser != null) {
	    	receiveNoticeUser.if_read = 1;
	    	receiveNoticeUser.read_time = new Date();
	    	receiveNoticeUser.save();
		}
	    if (/*androidNoticeInfo.notice_type == 0 && */androidNoticeInfo.push_user_id.equals(user_id)) {//对于发布人查看公告消息，同步返回已读和未读用户列表；
			  //取公告已读接收人记录
			List<AndroidReceiveNoticeUser> androidReceiveNoticeUserList = 
				AndroidReceiveNoticeUser.find("notice_id = ? and receive_user_id != ? and if_read = 1", id,androidNoticeInfo.push_user_id).fetch();
			    String receive_user_ids = "";
			    String receive_user_names = "";
			if (androidReceiveNoticeUserList != null && androidReceiveNoticeUserList.size() > 0) {
				for (AndroidReceiveNoticeUser androidReceiveNoticeUser : androidReceiveNoticeUserList) {
				    if (androidReceiveNoticeUser != null) {
					receive_user_ids += androidReceiveNoticeUser.receive_user_id;
					receive_user_ids += ",";
					receive_user_names += androidReceiveNoticeUser.receive_user_name;
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
			androidNoticeInfo.receive_user_ids = receive_user_ids;
			androidNoticeInfo.receive_user_names = receive_user_names;
			
			//取公告未读接收人记录
			List<AndroidReceiveNoticeUser> unReadAndroidReceiveNoticeUserList = 
				AndroidReceiveNoticeUser.find("notice_id = ? and receive_user_id != ? and if_read = 0", id,androidNoticeInfo.push_user_id).fetch();
		    String unreceive_user_ids = "";
		    String unreceive_user_names = "";
			if (unReadAndroidReceiveNoticeUserList != null && unReadAndroidReceiveNoticeUserList.size() > 0) {
				for (AndroidReceiveNoticeUser androidReceiveNoticeUser : unReadAndroidReceiveNoticeUserList) {
				    if (androidReceiveNoticeUser != null) {
					unreceive_user_ids += androidReceiveNoticeUser.receive_user_id;
					unreceive_user_ids += ",";
					unreceive_user_names += androidReceiveNoticeUser.receive_user_name;
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
			androidNoticeInfo.unreceive_user_ids = unreceive_user_ids;
			androidNoticeInfo.unreceive_user_names = unreceive_user_names;
		}

		androidNoticeInfo.create_time_string = DateUtil.date2String(androidNoticeInfo.create_time, "yyyy-MM-dd HH:mm:ss");
		String create_time_string =
			 DateUtil.date2String(androidNoticeInfo.create_time,
			 "yyyy-MM-dd HH:mm:ss");
			
			 try {
				 androidNoticeInfo.create_time =
			 DateUtil.string2UtilDate(create_time_string,
			 "yyyy-MM-dd HH:mm:ss");
			 } catch (Exception e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
			 }		
	    info.setCodeAndMsg(200);
	    info.setInfo(androidNoticeInfo);
	    info.setRequest(request.path);
	    return info;

    }
    
    /**
     * 根据id删除文章,同时删除接收信息列表
     * 
     * @param id
     *            文章id <必填>
     */
    public static ResultInfo deleteNoticeInfo(Long id, Request request) {
	ResultInfo info = new ResultInfo();
	AndroidNoticeInfo androidNoticeInfo = AndroidNoticeInfo.find("id = ?", id).first();
	if (androidNoticeInfo == null) {
		info.setCodeAndMsg(12001);
		info.setRequest(request.path);
		return info;
	}
	androidNoticeInfo.delete();

	//删除文章接收人记录
	List<AndroidReceiveNoticeUser> androidReceiveNoticeUserList = AndroidReceiveNoticeUser.find("notice_id = ? ", id).fetch();
	if (androidReceiveNoticeUserList != null && androidReceiveNoticeUserList.size() > 0) {
		for (AndroidReceiveNoticeUser androidReceiveNoticeUser : androidReceiveNoticeUserList) {
		    if (androidReceiveNoticeUser != null) {
			androidReceiveNoticeUser.delete();
		    }
		}
	}
	info.setCodeAndMsg(200);
	info.setInfo(androidNoticeInfo);
	info.setRequest(request.path);
	return info;

    }
    
    
    /**
     * 查询用户消息列表接口
     * 消息列表：同步返回每个用户的已读回执
     * 公告列表：只需要返回公告标题和内容，不需要返回已读回执，已读回执在详情页展示
     * CustomDocinfo.java
     * @param id
     * @param user_type 0：发送用户；1：接收用户;其他：全部
     * @param notice_type 0：公告；1：消息；公告获取全部， 消息只能获取自己发送或接受到的消息
     * @param if_read 是否已读；0：未读；1：已读
     * 2015年6月15日
     */
    public static ResultInfo getNoticeList(Integer user_id, Integer notice_type,Integer if_read, String keywords, Integer p, Integer ps,Request request) {
	ResultInfo info = new ResultInfo();
	    AndroidUserInfo androidUserInfo = AndroidUserInfo.findById(user_id);
	    if (androidUserInfo == null) {
		info.setCodeAndMsg(400);
		    info.setRequest(request.path);
		    return info;
	    }
		if (keywords != null && keywords.length() > 0) {//首先将keyword去空格
		    keywords = keywords.replaceAll(" ", "");//半角空格
		    if (keywords != null && keywords.length() > 0) {
			keywords = keywords.replaceAll("　", "");//全角空格
		    }
		}

	    List<AndroidNoticeInfo> androidNoticeInfoList = new ArrayList<AndroidNoticeInfo>();
//	    if (notice_type == 0) {//公告
	    	List<AndroidReceiveNoticeUser> androidReceiveNoticeUserList = null;
			String notice_ids = "";
	    	if (if_read == null || if_read > 1) {//获取全部公告
//	    		androidNoticeInfoList = AndroidNoticeInfo.find(" notice_type = ? order by id desc", notice_type).fetch(p, ps);
				//查询本人接收的所有公告
				androidReceiveNoticeUserList = AndroidReceiveNoticeUser.findByNoticeTypeAndUser
					(notice_type, user_id/*, p, ps,keywords*/);
			}else {
				//查询本人接收的所有公告
				androidReceiveNoticeUserList = AndroidReceiveNoticeUser.findByNoticeTypeAndUserAndIfRead
				(notice_type, user_id, if_read/*, p, ps,keywords*/);
			}
			if (androidReceiveNoticeUserList != null && androidReceiveNoticeUserList.size() > 0) {
				for (AndroidReceiveNoticeUser androidReceiveNoticeUser : androidReceiveNoticeUserList) {
					if (androidReceiveNoticeUser != null) {
						notice_ids += androidReceiveNoticeUser.notice_id;
						notice_ids += ",";
					}
				}
			}
			if (notice_ids.endsWith(",")) {
				notice_ids = notice_ids.substring(0, notice_ids.length() - 1);
			}
			if (keywords != null && keywords.length() > 0) {
				if (notice_ids.length() > 0) {
					androidNoticeInfoList = AndroidNoticeInfo.find(" title LIKE '%" + keywords + "%' AND (id IN (" + notice_ids + ") or (notice_type = ? and push_user_id = ? )) " +
							" group by id order by id desc", notice_type,user_id).fetch(p, ps);
				}else {
				    androidNoticeInfoList = AndroidNoticeInfo.find(" title LIKE '%" + keywords + "%' AND (notice_type = ? and push_user_id = ?) "
				    	+ " group by id order by id desc", notice_type,user_id).fetch(p, ps);
				}
			}else {
				if (notice_ids.length() > 0) {
					androidNoticeInfoList = AndroidNoticeInfo.find(" id IN (" + notice_ids + ") or (notice_type = ? and push_user_id = ? ) " +
							" group by id order by id desc", notice_type,user_id).fetch(p, ps);
				}else {
				    androidNoticeInfoList = AndroidNoticeInfo.find(" notice_type = ? and push_user_id = ? "
				    	+ " group by id order by id desc", notice_type,user_id).fetch(p, ps);
				}
			}
		
//	    }else {// 消息只能获取自己发送或接受到的消息
//		
//	    	if (if_read == null || if_read > 0) {//全部消息
//	    		List<AndroidReceiveNoticeUser> androidReceiveNoticeUserList = AndroidReceiveNoticeUser
//				.find("notice_type = ? AND (receive_user_id = ? OR push_user_id = ? OR "
//					+ "(receive_user_id = 0 AND receive_user_type = 1) OR "
//					+ "(receive_user_id = 0 AND receive_user_type = 2 AND department_id = ?))"
//				+ "group by notice_id  order by id desc",notice_type, user_id,
//				user_id,androidUserInfo.department_id).fetch(p,ps);
//			
//				//获取消息编号列表
//				String noticeIds = "";
//				if (androidReceiveNoticeUserList != null && androidReceiveNoticeUserList.size() > 0) {
//				    for (AndroidReceiveNoticeUser androidReceiveNoticeUser : androidReceiveNoticeUserList) {
//					if (androidReceiveNoticeUser != null) {
//					    noticeIds += androidReceiveNoticeUser.notice_id;
//					    noticeIds += ",";
//					}
//				    }
//				}
//				if (noticeIds.endsWith(",")) {
//				    noticeIds = noticeIds.substring(0, noticeIds.length() - 1);
//				}
//				if (noticeIds.length() > 0) {
//				    androidNoticeInfoList = AndroidNoticeInfo.find("id IN (" + noticeIds + ") order by create_time desc").fetch();
//				}
//			}else {
//				List<AndroidReceiveNoticeUser> androidReceiveNoticeUserList = AndroidReceiveNoticeUser
//				.find("notice_type = ? AND if_read = ? AND (receive_user_id = ? OR push_user_id = ? OR "
//					+ "(receive_user_id = 0 AND receive_user_type = 1) OR "
//					+ "(receive_user_id = 0 AND receive_user_type = 2 AND department_id = ?))"
//				+ "group by notice_id  order by id desc",notice_type,if_read, user_id,
//				user_id,androidUserInfo.department_id).fetch(p,ps);
//			
//				//获取消息编号列表
//				String noticeIds = "";
//				if (androidReceiveNoticeUserList != null && androidReceiveNoticeUserList.size() > 0) {
//				    for (AndroidReceiveNoticeUser androidReceiveNoticeUser : androidReceiveNoticeUserList) {
//					if (androidReceiveNoticeUser != null) {
//					    noticeIds += androidReceiveNoticeUser.notice_id;
//					    noticeIds += ",";
//					}
//				    }
//				}
//				if (noticeIds.endsWith(",")) {
//				    noticeIds = noticeIds.substring(0, noticeIds.length() - 1);
//				}
//				if (noticeIds.length() > 0) {
//				    androidNoticeInfoList = AndroidNoticeInfo.find("id IN (" + noticeIds + ") order by create_time desc").fetch();
//				}
//			}
//
//		
//	    }

		//取消息已读未读状态
	    if (androidNoticeInfoList != null && androidNoticeInfoList.size() > 0) {
//		    String receive_user_names = "";
//		    receive_user_names += androidReceiveNoticeUser.receive_user_name;
//		    receive_user_names += ",";
//	
//		if (receive_user_names.endsWith(",")) {
//		    receive_user_names = receive_user_names.substring(0, news_ids.length() - 1);
//		}
//		for (AndroidNoticeInfo androidNoticeInfo : androidNoticeInfoList) {
//		    String create_time_string = DateUtil.date2String(androidNoticeInfo.create_time, "yyyy-MM-dd HH:mm:ss");
//		    
//		    try {
//			androidNoticeInfo.create_time = DateUtil.string2UtilDate(create_time_string, "yyyy-MM-dd HH:mm:ss");
//		    } catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		    }
//		    
//		}
	    	
			for (AndroidNoticeInfo androidNoticeInfo : androidNoticeInfoList) {
				if (androidNoticeInfo != null) {
					if (androidNoticeInfo.push_user_id != user_id) {//如果本人是接收人，取已读未读状态
						AndroidReceiveNoticeUser androidReceiveNoticeUser = AndroidReceiveNoticeUser.findByNoticeAndUser(androidNoticeInfo.id, user_id);
						if (androidReceiveNoticeUser != null) {
							androidNoticeInfo.if_read = androidReceiveNoticeUser.if_read;
						}else {
							androidNoticeInfo.if_read = 1;
						}
					}else {//本人发送的公告消息默认为已读
						androidNoticeInfo.if_read =1 ;
					}
					androidNoticeInfo.create_time_string = DateUtil.date2String(androidNoticeInfo.create_time, "yyyy-MM-dd HH:mm:ss");
					String create_time_string = DateUtil.date2String(androidNoticeInfo.create_time,"yyyy-MM-dd HH:mm:ss");
					 try {
						androidNoticeInfo.create_time = DateUtil.string2UtilDate(create_time_string, "yyyy-MM-dd HH:mm:ss");
					 } catch (Exception e) {
						 // TODO Auto-generated catch block
						 e.printStackTrace();
					 }	
				}
			}
		    info.setCodeAndMsg(200);
		    info.setInfo(androidNoticeInfoList);
		    info.setCount(androidNoticeInfoList.size());
		    info.setRequest(request.path);

	    }else {
		    info.setCodeAndMsg(200);
		    info.setInfo(androidNoticeInfoList);
		    info.setRequest(request.path);

	    }
	    return info;
    }




	
    /**
     * 修改文章信息 修改方式为新建一条记录，将修改的数据存储到新纪录中，然后删除历史记录,
     * 同时删除push表的记录,使得修改记录在推荐列表中可以显示在最新位置
     * 
     * @param App
     *            文章信息 <必填>
     */
    public static ResultInfo editNoticeInfo(AndroidNoticeInfo androidNoticeInfo, Request request) {
	ResultInfo info = new ResultInfo();
	AndroidNoticeInfo newHotMediaDocinfo = AndroidNoticeInfo.find("id = ?", androidNoticeInfo.id).first();
	if (newHotMediaDocinfo == null) {
	    info.setCodeAndMsg(12001);
		info.setRequest(request.path);
		return info;
	}
	try {
	    
		

	} catch (Exception e) {
	    Logger.error(e, " Error update App App.editApp ");
		info.setCodeAndMsg(500);
		info.setRequest(request.path);
		e.printStackTrace();
		return info;
	}
	
	
	info.setCodeAndMsg(200);
	info.setInfo(newHotMediaDocinfo);
	info.setRequest(request.path);
	return info;

    }



//     /**
//     * 查询文章名称是否已存在
//     * 
//     * @param <必填>
//     */
//    public static ResultInfo isExistDoc(String title, Long id, Long category_id, Request request) {
//	ResultInfo info = new ResultInfo();
//	try {
//	    if (null == id) {
//		id = 0l;
//	    }
//	    if (AndroidNoticeInfo.count(" title=? and id!=? and class_id=?", title,
//		    id, category_id) <= 0) {
//		info.setCodeAndMsg(200);
//		info.setRequest(request.path);
//		return info;
//	    } else {
//		info.setCodeAndMsg(12002);
//		info.setRequest(request.path);
//		return info;
//	    }
//	} catch (Exception e) {
//	    Logger.error(e, " Error add Doc Doc.addDoc ");
//		info.setCodeAndMsg(500);
//		info.setRequest(request.path);
//		e.printStackTrace();
//		return info;
//	}
//    }
    
    /**
     * 查询公告/消息未读数
     * @param user_id
     * @param notice_type 0：公告；1：消息；
     */
    public static ResultInfo getUnReadNoticeCount(Integer user_id, Integer notice_type,Request request) {
    	ResultInfo info = new ResultInfo();
    	    AndroidUserInfo androidUserInfo = AndroidUserInfo.findById(user_id);
    	    if (androidUserInfo == null) {
    	    	info.setCodeAndMsg(400);
    		    info.setRequest(request.path);
    		    return info;
    	    }
    	    Map<String, Integer> noticeCountMap = new HashMap<String, Integer>();
    	    //未读数量
    	    long count = AndroidReceiveNoticeUser.count("notice_type = 0 " +
    	    		"and receive_user_id = ? and if_read = 0",user_id);
    	    noticeCountMap.put("gg", (int) count);
    	    count = AndroidReceiveNoticeUser.count("notice_type = 1 " +
    	    		"and receive_user_id = ? and if_read = 0",user_id);
    	    noticeCountMap.put("xx", (int) count);
    	    
    	    //任务未读数量
    	    count = AndroidReceiveTaskUser.count("receive_user_id = ? and if_read = 0",user_id);
    	    noticeCountMap.put("rw", (int) count);
    	    
    		info.setCodeAndMsg(200);
    		info.setInfo(noticeCountMap);
    		info.setRequest(request.path);
    	    return info;
        }

}
