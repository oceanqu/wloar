package controllers.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import models.AndroidNoticeInfo;
import models.ResultInfo;
import play.Logger;
import play.mvc.Controller;
import service.notice.CustomDocinfoService;
import service.user.UserInfoService;

/**
 * 用户管理接口
 * 
 * @author
 * 
 */
public class UserInfo extends Controller {

	/**
	 * 获取用户列表
	 * @param user_id //用户编号，user_id=0取全部数据
	 * @param department_id //所属部门，0：局长级别，不属于任何部门；1：质量；2：安全；3：执法
	 * @param type 接口类型，与user_type配合使用，0：取user_type同级用户；1：取user_type上级用户；2：取user_type下一级用户
	 */
	public static void getUserList(Integer user_id, Integer department_id,Integer type) {
		Logger.info("UserInfo.getUserList : " );
		if (user_id == null) {
		    user_id = 0;
		}
		if (department_id == null) {//局长级别
			department_id = 0;
		}
		if (type == null) {
			type = 0;
		}
		ResultInfo info = UserInfoService.getUserList(user_id,department_id,type,request);
		renderJSON(info);
	}

	/**
	 * 在极光服务器为该用户打标签，用于接收下派任务
	 * @param user_id
	 * @param jpush_registration_id
	 */
	public static void updateUserRegistrationId(Integer user_id,
			String jpush_registration_id) {
		Logger.info("UserInfo.updateUserRegistrationId : " );
		ResultInfo info = UserInfoService.updateUserRegistrationId(user_id,
				jpush_registration_id, request);
		renderJSON(info);

	}

	/**
	 * 用户登录
	 * @param user_name
	 * @param password
	 * @param jpush_registration_id
	 */
	public static void login(String user_name, String password,
			String jpush_registration_id) {
		Logger.info("UserInfo.login user_name = " + user_name);
		ResultInfo info = UserInfoService.login(user_name, password,
				jpush_registration_id, request);
		renderJSON(info);

	}
	
	/**
	 * 更新密码
	 * @param user_id
	 * @param jpush_registration_id
	 */
	public static void updateUserPassword(Integer user_id,
			String password , String new_password) {
		Logger.info("UserInfo.updateUserPassword user_id = : " + user_id );
		ResultInfo info = UserInfoService.updateUserPassword(user_id,
			password, new_password,request);
		renderJSON(info);

	}
}
