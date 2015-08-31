package models;

import static javax.persistence.GenerationType.AUTO;

import java.util.Date;

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
@Table(name="android_notice_info")
public class AndroidNoticeInfo extends GenericModel{
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", unique = true, nullable = false) 
	public Integer id;//消息标题
	public String title;// 标题 VARCHAR(256)*
	public Date create_time;// 入库时间 DATETIME
	public String content;// 消息内容
	public Integer push_user_id;//发送人编号
	public String push_user_name;//发送人
	public Integer push_flag;// 是否已推送
	public String push_range;// 推送范围
	public Integer notice_type = 1;//0：公告；1：消息
	public Long push_msg_id = 0L;
	public Integer department_id = 0 ;//所属部门，0：局长级别，不属于任何部门；1：质量；2：安全；3：执法
	public String department_name ="";//	所属部门名称
//	public Integer task_type;//任务类别，1：上报任务；2：下派任务；
	public Integer reply_notice_id =0;//回复的消息编号，只对群发消息有效，比如A发送给B、C、D三个人，B、C、D回复A的时候，此处记录A发送消息的记录，如果不是回复消息，此处为0
	public Integer receive_user_type = 1;//接收人类型：1：全部成员；2：本科室成员；3：指定人员
	@Transient
	public String create_time_string;// 
	@Transient
	public String receive_user_ids;//接收人编号列表



}
