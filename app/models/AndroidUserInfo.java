package models;

import static javax.persistence.GenerationType.AUTO;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import play.db.jpa.GenericModel;
/**
 * 媒体热点推送文章信息表
 * @author 
 * @date 2014-11-18
 */
@Entity
@Table(name="android_user_info")
public class AndroidUserInfo extends GenericModel{
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", unique = true, nullable = false) 
	public Integer id;//
	public String name = "";// 标题 VARCHAR(256)*
	public String phone = "";// 手机号
	public String imei = "";//
	public Integer status = 1;//状态：1：正常；2：不可用
	public String jpush_registration_id = "";
	public String password = "";
	public String user_name = "";//	用户名
	public Integer user_type = 4;//用户类型：0:管理员；1：局长；2：副局长；3：科室l领导；4：科室成员
	public Integer department_id = 0 ;//所属部门，0：局长级别，不属于任何部门；1：质量；2：安全；3：执法
	public String department_name ="";//	所属部门名称
	public Integer user_group = 0;// 用户分组，暂无用处
	public Date create_time	= new Date();//	创建时间
	public Integer version_code = 0;
	public String pwd = "";
	
	@Transient
	public Integer if_task_power = 0;//是否有发布和查看任务列表的权限，只有质检站、安监站、监察大队、局领导的成员，
					 //才能点击任务发布和任务列表两个按钮，其他人员不能操作工程任务和工程列表。0:无权限；1有权限

	/**
	 * 获取本部门员工信息
	 * AndroidUserInfo.java
	 * @param department_id
	 * @return
	 * 2015年8月16日
	 */
	public static List<AndroidUserInfo> findUserInfoByDepartmentId( Integer department_id) {
	    return AndroidUserInfo.find("department_id = ?", department_id).fetch();
	}
	/**
	 * 根据department_id获取本部门成员信息和局长级领导信息
	 * AndroidUserInfo.java
	 * @param department_id
	 * @return
	 * 2015年8月16日
	 */
	public static List<AndroidUserInfo> findUserInfoByDepartment(Integer department_id) {
	    String departmentIds = "0,";
	    if (department_id != 0) {
		departmentIds += department_id;
	    }else {
		departmentIds = "0";
	    }
	    return AndroidUserInfo.find("department_id IN (" +departmentIds+ ")").fetch();
	}
	/**
	 * 根据department_id获取本部门成员信息和局长级领导信息
	 * AndroidUserInfo.java
	 * @param department_id
	 * @return
	 * 2015年8月16日
	 */
	public static List<AndroidUserInfo> findUserInfoNotDepartmentAndUserTypes(Integer department_id,String user_types) {
	    return AndroidUserInfo.find("department_id != ? AND user_type NOT IN (" +user_types+ ") order by department_id asc", department_id).fetch();
	}

	/**
	 * 根据user_type获取用户信息
	 * AndroidUserInfo.java
	 * @param user_types
	 * @return
	 * 2015年8月16日
	 */
	public static List<AndroidUserInfo> findUserInfoByUserTypes(String user_types) {
	    return AndroidUserInfo.find("user_type IN (" +user_types+ ")").fetch();
	}
	
	/**
	 * 根据department_id获取本部门成员信息和局长级领导信息
	 * AndroidUserInfo.java
	 * @param department_id
	 * @return
	 * 2015年8月16日
	 */
	public static List<AndroidUserInfo> findUserInfoByDepartmentAndUser(Integer department_id, Integer user_id) {
		return AndroidUserInfo.find("department_id = ? and id != ?", department_id,user_id).fetch();
	}
}
