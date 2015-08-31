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
 * 任务审批状态表
 * @author 
 * @date 2014-11-18
 */
@Entity
@Table(name="android_task_verify_info")
public class AndroidTaskVerifyInfo extends GenericModel{
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", unique = true, nullable = false) 
	public Integer id;//doc的索引
	public Integer receive_user_id = 0;//转发任务接收人
	public String receive_user_name = "";//申请人名称
	public Integer task_id = 0;// 任务编号
	public String task_name = "";// 
	public Integer task_type = 0;// 任务类别，1：上报任务；2：下派任务；
	public Integer verify_status = 0;// 审批状态：0：待审批；1:已完成；2：未完成(即审核未通过)
	public Integer verify_user_id = 0;// 审批人(指派人员)
	public String verify_user_name = "";//
	public String verify_comment = "";// 批准审核说明
	public Date verify_time = new Date();// 批准审核时间
	public Integer verify_order = 0;// 审批顺序
	public Integer verify_type = 0;// 处理类型：1：审核；2：转发
	
	public static List<AndroidTaskVerifyInfo> findByTaskIdDesc(Integer task_id) {
	    return AndroidTaskVerifyInfo.find("task_id = ? order by verify_order desc", task_id).fetch();
	}
	
	public static List<AndroidTaskVerifyInfo> findByTaskIdAsc(Integer task_id) {
	    return AndroidTaskVerifyInfo.find("task_id = ? order by verify_order asc", task_id).fetch();
	}
}
