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
 * @author zx
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
	public Integer user_type = 4;//用户类型：1：局长；2：副局长；3：科室l领导；4：科室成员
	public Integer department_id = 0 ;//所属部门，0：局长级别，不属于任何部门；1：质量；2：安全；3：执法
	public String department_name ="";//	所属部门名称
	public Integer user_group = 0;// 用户分组，暂无用处
	public Date create_time	= new Date();//	创建时间

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
	    departmentIds += departmentIds;
	    return AndroidUserInfo.find("department_id IN (" +departmentIds+ ")").fetch();
	}
}
