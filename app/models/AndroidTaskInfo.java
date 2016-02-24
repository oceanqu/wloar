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
@Table(name="android_task_info")
public class AndroidTaskInfo extends GenericModel{
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", unique = true, nullable = false) 
	public Integer id;//消息标题
	public String name = "";// 标题 
//	public Integer receive_user_id=0;//指派人员
//	public String receive_user_name = "";// 指派人员名称
	public Integer company_id=0;//公司编号
	public String company_name = "";//公司名称
	public String contact = "";//联系方式
	public String handle_mode = "";//整改方式
	public String remark = "";//备注
	public Integer is_reply=1;//是否书面回复：1：回复；2：不回复
	public Integer is_review=1;//是否复查，1：复查；2：不复查
	public String attachment = "";//附件，图片视频上传列表，此处存储附件编号列表
	@Transient
	public String attachment_simp = "";//附件,图片返回缩略图，其他不变

	public String media = "";//音频消息，为附件编号列表
	public Integer department_id=1;//任务编号，1：质量；2：安全；3：执法
	public String department_name = "";// 部门名称
	public Integer task_type=1;//任务类别，1：工程任务；2：自定义任务
	public Date create_time= new Date();//
	public Integer create_user_id=0;//发送人
	public String create_user_name = "";// 
	public Integer status = 1;//是否处理/审批完成，0:待处理；1：已完成；2：未完成。所有新建审批/下派任务该状态为2. 20160116去掉任务审核机制，全部默认审核通过
	public Long project_id = 0l;//项目id.
	public String project_name = "";//项目id.
	public Integer reply_task_id = 0;//回复任务编号，0为首发任务
	public Integer if_open = 0;//是否公开，0：不公开；1：公开；如果当前任务是回复任务，则同时将其回复的任务也公开
	public Integer push_flag;// 是否已推送
	public String push_range;// 推送范围
	public Long push_msg_id = 0L;

	
	@Transient
	public Integer if_receive_user = 0;//是否是接收人，用于判断是否可以回复,0:不是；1：是
	@Transient
	public Integer if_open_power = 0;//是否有公开权限，只有自己发送或接收的科室领导才能公开。
	@Transient
	public String taskForwardInfo = "";//任务转发信息，即任务处理流程中转发信息
	@Transient
	public String create_time_string;
	@Transient
	public String receive_user_ids;//已读人编号列表
	@Transient
	public String receive_user_names;//已读人姓名列表
	@Transient
	public String unreceive_user_names;//未读人姓名列表
	@Transient
	public String unreceive_user_ids;//未读人编号列表
	@Transient 
	public Integer if_read = 1;//是否已读；0：未读；1：已读
	
	
	public static AndroidTaskInfo findById(Integer task_id) {
	    return AndroidTaskInfo.find("id = ?", task_id).first();
	}
	
	public static List<AndroidTaskInfo> findByIds(String task_ids) {
	    if (!task_ids.equals("")) {
		return AndroidTaskInfo.find("id IN (" + task_ids + ")  order by create_time desc").fetch();
	    }else {
		return null;
	    }
	    
	}

}
