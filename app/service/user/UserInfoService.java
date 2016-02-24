package service.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javax.sound.midi.MidiDevice.Info;

import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.device.DeviceClient;
import cn.jpush.api.device.TagAliasResult;
import models.AndroidNoticeInfo;
import models.AndroidUserGroup;
import models.AndroidUserInfo;
import models.ResultInfo;
import models.ResultObject;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Scope.Session;
import service.notice.CustomDocinfoService;
import util.PushDoc;
import util.UtilValidate;



/**
 * 用户管理接口
 * @author 
 *
 */
public class UserInfoService{
    
    /**
	 * 获取用户列表
	 * @param user_id //用户编号，user_id=0取全部数据
	 * @param department_id //所属部门，0：局长级别，不属于任何部门；1：质量；2：安全；3：执法
 	 * @param type 接口类型，与user_type配合使用，0：取user_type同级用户；1：取user_type上级用户；2：取user_type下一级用户
    * 2015年6月15日
     */
    public static ResultInfo getUserList(Integer user_id, Integer department_id,Integer type,Request request) {
	ResultInfo info = new ResultInfo();
	
	List<AndroidUserInfo> androidUserInfoList = new ArrayList<AndroidUserInfo>();
	
	if (user_id == 0) {
	    if (department_id <= 0) {
		    androidUserInfoList = AndroidUserInfo.findAll();
		}else {
			androidUserInfoList = AndroidUserInfo.findUserInfoByDepartment(department_id);
		}
	}else {
		AndroidUserInfo androidUserInfo = AndroidUserInfo.findById(user_id);
		if (androidUserInfo == null ) {
		    info.setCodeAndMsg(400);
		    info.setRequest(request.path);
		    return info;

		}
		
		if (type == null) {//如果未传入type，则返回全部，但是将本人所在部门联系人优先显示
		    department_id = androidUserInfo.department_id;
		    List<AndroidUserInfo> tmpAndroidUserInfoList = null; 
		    //首先取出本部门的所有user_id
		    tmpAndroidUserInfoList = AndroidUserInfo.findUserInfoByDepartmentId(department_id);
		    if (tmpAndroidUserInfoList != null && tmpAndroidUserInfoList.size() > 0) {
			androidUserInfoList.addAll(tmpAndroidUserInfoList);
		    }
		    
		    if (androidUserInfo.user_type > 2) {//如果不是局领导
			    //如果department_id不是局长，则再优先取出局长
			    tmpAndroidUserInfoList = AndroidUserInfo.findUserInfoByUserTypes("1,2");
			    if (tmpAndroidUserInfoList != null && tmpAndroidUserInfoList.size() > 0) {
				androidUserInfoList.addAll(tmpAndroidUserInfoList);
			    }
			    
		    }
		    //取出其他部门人员
		    tmpAndroidUserInfoList = AndroidUserInfo.findUserInfoNotDepartmentAndUserTypes(department_id,"1,2");
		    if (tmpAndroidUserInfoList != null && tmpAndroidUserInfoList.size() > 0) {
			androidUserInfoList.addAll(tmpAndroidUserInfoList);
		    }

		}else {
			Integer user_type = androidUserInfo.user_type;
			if (type == 0) {//取同级数据
				if (user_type == 0) {//取全部数据
					androidUserInfoList = AndroidUserInfo.findAll();
				}else if (user_type == 1 || user_type == 2) {//局长或副局
					androidUserInfoList = AndroidUserInfo.find("user_type = ? ", user_type).fetch();
				}else {//科主任或科室成员，只能看到本科室的人
					androidUserInfoList = AndroidUserInfo.find("user_type = ? and department_id = ? ", user_type,department_id).fetch();
				}
			}else if(type == 1){//取user_type上级用户
				if (user_type == 1 || user_type == 0) {//局长级没有上级用户
					info.setCodeAndMsg(1003);
					info.setRequest(request.path);
					return info;
				}else if (user_type == 2 || user_type == 3) {//副局/科主任，返回局级/副局级数据
					androidUserInfoList = AndroidUserInfo.find("user_type = ? ", user_type - 1).fetch();
				}else {//科室成员，只能看到本科室的科主任
					androidUserInfoList = AndroidUserInfo.find("user_type = ? and department_id = ? ", user_type - 1,department_id).fetch();
				}
			}else if (type == 2) {//取user_type下一级用户
				if (user_type == 1 || user_type == 0 || user_type == 2) {//局长/副局的下一级为副局/科主任
					androidUserInfoList = AndroidUserInfo.find("user_type = ? ", user_type + 1).fetch();
				}else if (user_type == 3) {//科主任下一级为本部门科室成员
					androidUserInfoList = AndroidUserInfo.find("user_type = ? and department_id = ? ", user_type + 1,department_id).fetch();
				}else {//科室成员没有下一级用户
					info.setCodeAndMsg(1004);
					info.setRequest(request.path);
					return info;
				}

			}
		}

	}
	

	//组合返回结果
	List<ResultObject> resultObjectList = new ArrayList<ResultObject>();
	if (androidUserInfoList != null && androidUserInfoList.size() > 0) {
		for (AndroidUserInfo androidUserInfo1 : androidUserInfoList) {
			if (androidUserInfo1 != null) {
			    if (androidUserInfo1.id == user_id) {//接收人列表中去掉本人
				continue;
			    }
				ResultObject resultObject = new ResultObject();
				resultObject.id = androidUserInfo1.id;
				resultObject.name = androidUserInfo1.name;
				resultObject.department_id = androidUserInfo1.department_id;
				resultObject.department_name = androidUserInfo1.department_name;
				resultObjectList.add(resultObject);
			}
		}
	}
	info.setCodeAndMsg(200);
	info.setInfo(resultObjectList);
	info.setCount(resultObjectList.size());
	info.setRequest(request.path);
	return info;

    }
    
    /**
     * 在极光服务器为该用户打标签，用于接收下派任务
     * @param user_id
     * @param jpush_registration_id
     * @param request
     * @return
     */
    public static ResultInfo updateUserRegistrationId(Integer user_id, 
	    String jpush_registration_id,Request request) {
	ResultInfo info = new ResultInfo();
	AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ?", user_id).first();
	if (androidUserInfo == null) {
		info.setCodeAndMsg(1010);
		info.setRequest(request.path);
		return info;

	}else {
	    androidUserInfo.jpush_registration_id = jpush_registration_id;
	}
	androidUserInfo.save();
	info = PushDoc.updateJPushGroupTagOrAlias(jpush_registration_id, androidUserInfo, request);
	if (info.getCode() == 200) {
		info.setCodeAndMsg(200);
		info.setInfo(androidUserInfo);
		info.setRequest(request.path);
		return info;

	}else {
	    return info;
	}

    }
    
    /**
     * 在极光服务器为该用户打标签，用于接收下派任务
     * @param user_id
     * @param jpush_registration_id
     * @param request
     * @return
     */
    public static ResultInfo updateUserPassword(Integer user_id, 
	    String password,String new_password, Request request) {
	ResultInfo info = new ResultInfo();
	AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ? and password = ?", user_id,password).first();
	if (androidUserInfo == null) {
		info.setCodeAndMsg(1002);
		info.setRequest(request.path);
		return info;

	}else {
	    androidUserInfo.password = new_password;
	}
	androidUserInfo.save();
	info.setCodeAndMsg(200);
	info.setInfo(androidUserInfo);
	info.setRequest(request.path);
	return info;

    }
    
    /**
     * 登陆接口，根据用户名密码登录，同时传入jpush_registration_id，更新极光服务器上该设备的推送标签
     * @param user_name
     * @param password
     * @param jpush_registration_id
     * @param request
     * @return
     */
    public static ResultInfo login(String user_name, String password,String jpush_registration_id,
    		Integer mVersionCode, Request request) {
	ResultInfo info = new ResultInfo();
	List<AndroidUserInfo> androidUserInfoList = null;
	if (UtilValidate.isMobileNO(user_name)) {// 手机号登录，直接登录
		if (mVersionCode > 3) {//升级加密以后的版本
			androidUserInfoList = AndroidUserInfo.find("phone = ? and password = ?", user_name,password).fetch();
			if (androidUserInfoList != null && androidUserInfoList.size() == 1) {//如果只有一个
				if (!androidUserInfoList.get(0).version_code.equals(mVersionCode)) {
					updateUserVersionCode(androidUserInfoList.get(0).id, mVersionCode);
				}
			}
		}else {//升级加密之前的版本，密码使用明文
			androidUserInfoList = AndroidUserInfo.find("phone = ? and pwd = ?", user_name,password).fetch();
		}
	    
	}else {
		if (mVersionCode > 3) {//升级加密以后的版本
			androidUserInfoList = AndroidUserInfo.find("name = ? and password = ?", user_name,password).fetch();
			if (androidUserInfoList != null && androidUserInfoList.size() == 1) {//如果只有一个
				if (!androidUserInfoList.get(0).version_code.equals(mVersionCode)) {
					updateUserVersionCode(androidUserInfoList.get(0).id, mVersionCode);
				}
			}
		}else {//升级加密之前的版本，密码使用明文
			androidUserInfoList = AndroidUserInfo.find("name = ? and pwd = ?", user_name,password).fetch();
		}
	}
	 
	if (androidUserInfoList == null || androidUserInfoList.size() <= 0) {
		info.setCodeAndMsg(1002);
		info.setRequest(request.path);
		return info;
	}else if (androidUserInfoList.size() >= 2) {//如果有重名的用户，提示手机号登录
		info.setCodeAndMsg(1020);
		info.setRequest(request.path);
		return info;
	}else {
	    if (jpush_registration_id != null && jpush_registration_id.length() > 0) {
		for (AndroidUserInfo androidUserInfo : androidUserInfoList) {
		    if (androidUserInfo != null) {
			//判断是否有查看和发布任务的权限
			androidUserInfo.if_task_power = getTaskPower(androidUserInfo);
			info = PushDoc.updateJPushGroupTagOrAlias(jpush_registration_id, androidUserInfo, request);
			break;
		    }
		}
		
	    }
	}
//	ResultObject resultObject = new ResultObject();
//	resultObject.id = androidUserInfo.id;
//	resultObject.name = androidUserInfo.user_name;
	
	info.setCodeAndMsg(200);
	info.setInfo(androidUserInfoList.get(0));
	info.setRequest(request.path);
	return info;

    }
   
    //查看用户是否有发布和查看任务的权限
    private static Integer getTaskPower(AndroidUserInfo androidUserInfo) {
  	//首先判断是不是局领导，如果是，可以发布和查看
	if (androidUserInfo.user_type <= 2) {//局长或副局
	    return 1;
	}
	//从配置文件取有任务权限分组id
	String group_ids = ResourceBundle.getBundle("config").getString("task_power_group_id");
	List<AndroidUserGroup> androidUserGroupList = AndroidUserGroup.findGroupInfoByGroupUserAndGroupType(group_ids, 2,androidUserInfo.department_id);
	if (androidUserGroupList != null && androidUserGroupList.size() > 0) {
	    return 1;
	}
	
      return 0;
    }
    
  //将用户登录信息写入到session
	private static void resetAndGetSessionUser(AndroidUserInfo androidUserInfo) {
		Session s = Session.current();
		s.put("user_id", androidUserInfo.id);
//		s.put("user_name", cdptUserInfo.user_name);
		s.put("user_name", androidUserInfo.phone);//20150717 zx
		s.put("type", androidUserInfo.user_type);
		s.put("department_id", androidUserInfo.department_id);
	}
//获取当前登录用户信息
	public static AndroidUserInfo getCurrentUser() {
		Session s = Session.current();
		AndroidUserInfo androidUserInfo = null;
		if (s.get("user_id") != null && s.get("user_id").length() > 0) {
			androidUserInfo = new AndroidUserInfo();
			androidUserInfo = AndroidUserInfo.find("id = ? ", Integer.parseInt(s.get("user_id")))
					.first();
			if (null != androidUserInfo) {
				resetAndGetSessionUser(androidUserInfo);
			}
		}
		return androidUserInfo;
	}
	
	//获取当前登录用户信息
	public static void updateUserVersionCode(Integer user_id, Integer version_code) {
		AndroidUserInfo androidUserInfo = AndroidUserInfo.find("id = ?", user_id).first();
		if (androidUserInfo != null) {
			androidUserInfo.version_code = version_code;
			androidUserInfo.save();
		}
	}
}
