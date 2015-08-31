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
 * 任务内容信息表
 * @author 
 * @date 2014-11-18
 */
@Entity
@Table(name="android_task_content")
public class AndroidTaskContent extends GenericModel{
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", unique = true, nullable = false) 
	public Integer id;//消息标题
	public String name;// 标题 VARCHAR(256)*
	public Integer assign_user_id;//指派人员
	public String assign_user_name;//指派人员
	public String contact;// 联系方式
	public String handle_mode;// 整改方式
	public String remark;//备注
	public Integer is_reply = 0;//是否书面回复：1：回复；2：不回复
	public Integer is_review ;//是否复查，1：复查；2：不复查
	public String attachment ;//附件，图片视频上传列表，此处存储附件编号列表
	public Integer media;//音频消息，为附件编号列表
	public Date create_time;// 创建时间
	public Integer create_user_id;//发送人编号
	public String create_user_name;//发送人
	public Integer task_id = 1;//任务编号 1:质量检查;2:安全检查；3：执法管理
	public String task_name = "质量检查";//任务名称  1:质量检查;2:安全检查；3：执法管理
	public Integer task_type = 1;//任务类别，1：上报任务；2：下派任务；
	
	@Transient
	public String create_time_string;// 

}
