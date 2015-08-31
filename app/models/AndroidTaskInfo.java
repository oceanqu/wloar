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
 * @author 
 * @date 2014-11-18
 */
@Entity
@Table(name="android_task_info")
public class AndroidTaskInfo extends GenericModel{
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", unique = true, nullable = false) 
	public Integer id;//消息标题
	public String name = "";// 标题 
	public Integer receive_user_id=0;//指派人员
	public String receive_user_name = "";// 指派人员名称
	public Integer company_id=0;//公司编号
	public String company_name = "";//公司名称
	public String contact = "";//联系方式
	public String handle_mode = "";//整改方式
	public String remark = "";//备注
	public Integer is_reply=1;//是否书面回复：1：回复；2：不回复
	public Integer is_review=1;//是否复查，1：复查；2：不复查
	public String attachment = "";//附件，图片视频上传列表，此处存储附件编号列表
	public String media = "";//音频消息，为附件编号列表
	public Integer department_id=1;//任务编号，1：质量；2：安全；3：执法
	public String department_name = "";// 部门名称
	public Integer task_type=1;//任务类别，1：上报任务；2：下派任务；
	public Date create_time= new Date();//
	public Integer create_user_id=0;//发送人
	public String create_user_name = "";// 
	public Integer status = 0;//是否处理/审批完成，0:待处理；1：已完成；2：未完成。所有新建审批/下派任务该状态为2.
	
	@Transient
	public String taskForwardInfo = "";//任务转发信息，即任务处理流程中转发信息
	@Transient
	public String create_time_string;

	public static AndroidTaskInfo findById(Integer task_id) {
	    return AndroidTaskInfo.find("id = ?", task_id).first();
	}
}
