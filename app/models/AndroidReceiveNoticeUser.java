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
 *通知消息推送用户表
 * @author 
 * @date 2014-11-18
 */
@Entity
@Table(name="android_receive_notice_user")
public class AndroidReceiveNoticeUser extends GenericModel{
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", unique = true, nullable = false) 
	public Integer id;//doc的索引
	public Integer notice_id = 0;//doc的索引
	public String title = "";// 标题 VARCHAR(256)*
	public Integer notice_type = 0;//0：公告；1：消息
	public Integer push_user_id = 0;// 发送人
	public String push_user_name = "";//
	public Integer receive_user_id = 0;// 接收人
	public String receive_user_name = "";//
	public Integer receive_user_type = 0;//接收人类型：1：全部成员；2：本科室成员；3：指定人员
	public Date create_time = new Date();
	public Integer department_id = 0 ;//所属部门，0：局长级别，不属于任何部门；1：质量；2：安全；3：执法
	public String department_name ="";//	所属部门名称
	public Integer if_read = 0;//是否已读；0：未读；1：已读
	public Date read_time = new Date(); 
	@Transient
	public String create_time_string;// 下派任务接收人编号，以",分割"

	public static AndroidReceiveNoticeUser findByNoticeAndUser(Integer notice_id, Integer receive_user_id) {
	    return AndroidReceiveNoticeUser.find("notice_id = ? and receive_user_id = ?", notice_id,receive_user_id).first();
	}

	public static List<AndroidReceiveNoticeUser> findByNoticeTypeAndUser(Integer notice_type, 
			Integer receive_user_id/*,Integer p, Integer ps,String keywords*/) {
//		if (keywords != null && keywords.length() > 0) {
//			return AndroidReceiveNoticeUser.find(" title LIKE \'%" + keywords + "%\' AND notice_type = ? and receive_user_id = ? group by notice_id " +
//		    		"order by notice_id desc", notice_type,receive_user_id).fetch(p,ps);
//		}else {
			return AndroidReceiveNoticeUser.find("notice_type = ? and receive_user_id = ? group by notice_id " +
		    		"order by notice_id desc", notice_type,receive_user_id).fetch();
//		}
	    
	}
	
	public static List<AndroidReceiveNoticeUser> findByNoticeTypeAndUserAndIfRead(Integer notice_type, 
			Integer receive_user_id,Integer if_read/*,Integer p, Integer ps,String keywords*/) {
//		if (keywords != null && keywords.length() > 0) {
//			return AndroidReceiveNoticeUser.find(" title LIKE \'%" + keywords + "%\' AND notice_type = ? and receive_user_id = ? and if_read = ? group by notice_id " +
//		    		"order by notice_id desc", notice_type,receive_user_id,if_read).fetch(p,ps);
//		}else {
			return AndroidReceiveNoticeUser.find("notice_type = ? and receive_user_id = ? and if_read = ? group by notice_id " +
		    		"order by notice_id desc", notice_type,receive_user_id,if_read).fetch();
//		}
	    
	}

	public static AndroidReceiveNoticeUser findByNoticeAndDepartment(Integer notice_id, Integer department_id) {
	    return AndroidReceiveNoticeUser.find("notice_id = ? and department_id = ?", notice_id,department_id).first();
	}

}
