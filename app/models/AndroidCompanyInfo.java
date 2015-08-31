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
 * 企业信息表
 * @author 
 * @date 2014-11-18
 */
@Entity
@Table(name="android_company_info")
public class AndroidCompanyInfo extends GenericModel{
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", unique = true, nullable = false) 
	public Integer id;//消息标题
	public String name = "";// 标题 VARCHAR(256)*
	public String contact = "";// 联系方式
	public String address = "";// 地址
	public String longitude = "";//经度
	public String latitude = "";//纬度
	public String locate = "";// 地图位置表示
	public String remark = "";//备注
	public Date create_time = new Date();// 创建时间
	public Integer create_user_id = 0;//发送人编号
	public String create_user_name = "";//发送人

	@Transient
	public String create_time_string;
}
