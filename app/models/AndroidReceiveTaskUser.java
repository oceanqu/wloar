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
import javax.persistence.criteria.CriteriaBuilder.In;

import play.db.jpa.GenericModel;
/**
 *任务推送用户表
 * @author 
 * @date 2014-11-18
 */
@Entity
@Table(name="android_receive_task_user")
public class AndroidReceiveTaskUser extends GenericModel{
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", unique = true, nullable = false) 
	public Integer id;//doc的索引
	public Integer task_id = 0;//doc的索引
	public String name = "";// 标题 VARCHAR(256)*
	public Integer task_type = 0;//0：公告；1：消息
	public Integer push_user_id = 0;// 发送人
	public String push_user_name = "";//
	public Integer receive_user_id = 0;// 接收人
	public String receive_user_name = "";//
	public Integer receive_user_type = 0;//接收人类型：1：全部成员；2：本科室成员；3：指定人员
	public Date create_time = new Date();
	public Integer department_id = 0 ;//所属部门，0：局长级别，不属于任何部门；1：质量；2：安全；3：执法
	public String department_name ="";//	所属部门名称
	public Integer if_open = 0;//是否公开，0：不公开；1：公开；如果当前任务是回复任务，则同时将其回复的任务也公开
	public Integer if_read = 0;//是否已读；0：未读；1：已读
	public Date read_time = new Date(); 

	@Transient
	public String create_time_string;// 下派任务接收人编号，以",分割"

	public static AndroidReceiveTaskUser findByTaskAndUser(Integer task_id, Integer receive_user_id) {
	    return AndroidReceiveTaskUser.find("task_id = ? and receive_user_id = ?", task_id,receive_user_id).first();
	}

	public static List<AndroidReceiveTaskUser> findByTaskIds(Integer task_id) {
	    return AndroidReceiveTaskUser.find("task_id = ?", task_id).fetch();
	}

	public static List<AndroidReceiveTaskUser> findByTaskTypeAndUser(Integer task_type, 
			Integer receive_user_id/*,Integer p, Integer ps,String keywords*/) {
		return AndroidReceiveTaskUser.find("task_type = ? and receive_user_id = ? group by task_id " +
		    		"order by task_id desc", task_type,receive_user_id).fetch();
	}
	
	public static List<AndroidReceiveTaskUser> findByTaskTypeAndUserAndIfRead(Integer task_type, 
			Integer receive_user_id,Integer if_read/*,Integer p, Integer ps,String keywords*/) {
		return AndroidReceiveTaskUser.find("task_type = ? and receive_user_id = ? and if_read = ? group by task_id " +
		    		"order by task_id desc", task_type,receive_user_id,if_read).fetch();
	    
	}

	public static AndroidReceiveTaskUser findByTaskAndDepartment(Integer task_id, Integer department_id) {
	    return AndroidReceiveTaskUser.find("task_id = ? and department_id = ?", task_id,department_id).first();
	}

}
