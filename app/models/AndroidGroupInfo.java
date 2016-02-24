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
 * 分组信息
 * @author 
 * @date 2014-11-18
 */
@Entity
@Table(name="android_group_info")
public class AndroidGroupInfo extends GenericModel{
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", unique = true, nullable = false) 
	public Integer id;//
	public String name = "";// 分组名称
	public Integer group_type = 2;//分组类型：1：用户分组；2：部门分组；
	public Date create_time= new Date();//
	public Integer create_user_id=0;//创建人
	public String create_user_name = "";// 
	public Integer if_delete = 1;//是否有效，0：删除；1：有效

	/**
	 * 获取分组信息
	 * AndroidGroupInfo.java
	 * @param id
	 * @return
	 * 2015年8月16日
	 */
	public static AndroidGroupInfo findGroupInfoById( Integer id) {
	    return AndroidUserGroup.find("id = ?", id).first();
	}

}
