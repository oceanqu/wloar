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
 * 用户分组列表
 * @author 
 * @date 2014-11-18
 */
@Entity
@Table(name="android_user_group")
public class AndroidUserGroup extends GenericModel{
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", unique = true, nullable = false) 
	public Integer id;//
	public String group_id = "";// 分组编号，如果group_type=1，则为用户id；如果group_type=2，则为部门id；
	public String group_name = "";// 分组名称，如果group_type=1，则为用户名；如果group_type=2，则为部门名称；
	public Integer group_type = 2;//分组类型：1：用户分组；2：部门分组；
	public Integer user_id = 0;
	public String user_name = "";
	public Date create_time = new Date();
	public Integer if_delete = 1;//是否有效，0：删除；1：有效
	/**
	 * 获取user_id对应的分组列表
	 * AndroidUserGroup.java
	 * @param user_id
	 * @param group_type
	 * @return
	 * 2016年1月31日
	 */
	public static List<AndroidUserGroup> findGroupInfoByUserAndGroupType( Integer user_id, Integer group_type) {
	    return AndroidUserGroup.find("user_id = ? and group_type = ?", user_id, group_type).fetch();
	}
	
	/**
	 * 获取group_id对应的分组下user_id列表
	 * AndroidUserGroup.java
	 * @param group_id
	 * @param group_type
	 * @return
	 * 2016年1月31日
	 */
	public static List<AndroidUserGroup> findGroupInfoByGroupAndGroupType( String group_ids, Integer group_type) {
	    return AndroidUserGroup.find("group_id IN (" + group_ids + ") and group_type = ? group by user_id",  group_type).fetch();
	}

	/**
	 * 获取group_id对应的分组下user_id列表
	 * AndroidUserGroup.java
	 * @param group_id
	 * @param group_type
	 * @return
	 * 2016年1月31日
	 */
	public static List<AndroidUserGroup> findGroupInfoByGroupUserAndGroupType( String group_ids, Integer group_type, Integer user_id) {
	    return AndroidUserGroup.find("group_id IN (" + group_ids + ") and group_type = ? and user_id = ?",  group_type,user_id).fetch();
	}

}
