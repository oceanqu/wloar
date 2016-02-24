package models;

import static javax.persistence.GenerationType.AUTO;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import play.db.jpa.GenericModel;
import util.DateUtil;
import util.UtilValidate;

@Entity(name = "android_department_info")
public class AndroidDepartmentInfo extends GenericModel {
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", unique = true, nullable = false) 
	public Integer id;//
	public String name;// varchar(50)
	public String introduction;// varchar(100)

	@Temporal(TemporalType.DATE)
	public Date create_time;// int(10) unsigned
	public String create_user_name;// varchar(100)
	public Integer create_user_id = 0;// enum('0','1')
	public Integer delete_flag = 0;
	
	@Transient
	public long user_num;//该部门下用户数量

	public static List<AndroidDepartmentInfo> getAllDepartment() {
		return AndroidDepartmentInfo.findAll();
	}

//	public static List<Project> filterInfo(int p, int ps, String sSearch, Long regionId, Long cid, Long bid, Long sid, String goals,
//			String schedule, String starttime, String endtime) {
//		StringBuffer sb = new StringBuffer();
//		sb.append(" 1=1");
//		if (UtilValidate.isNotEmpty(regionId) && regionId > 0) {
//			// sb.append(" and (regionid=" + regionId + " or parentstr like '%" + regionId + ",%')");
//			sb.append(" and regionid=" + regionId);
//		}
//		if (UtilValidate.isNotEmpty(goals)) {
//			sb.append(" and goals = '" + goals + "'");
//		}
//
//		// 进度. 1 :未启动; 2: 进行中; 3: 已完成
//		if (UtilValidate.isNotEmpty(schedule)) {
//			if (UtilValidate.areEqual("1", schedule)) {
//				sb.append(" and schedule in ('0', '0%')");
//			} else if (UtilValidate.areEqual("2", schedule)) {
//				sb.append(" and (schedule > '0' or schedule > '0%')");
//				sb.append(" and (schedule < '100' or schedule < '100%')");
//			} else if (UtilValidate.areEqual("3", schedule)) {
//				sb.append(" and (schedule >= '100' or schedule >= '100%')");
//			}
//		}
//
//		if (UtilValidate.isNotEmpty(starttime)) {
//			sb.append(" and checktime >= '" + starttime + "'");
//		}
//		if (UtilValidate.isNotEmpty(endtime)) {
//			sb.append(" and checktime <= '" + endtime + " 23:59:59.999'");
//		}
//
//		if (UtilValidate.isNotEmpty(cid)) {
//			sb.append(" and construction_id=" + cid);
//		}
//		if (UtilValidate.isNotEmpty(bid)) {
//			sb.append(" and build_id=" + bid);
//		}
//		if (UtilValidate.isNotEmpty(sid)) {
//			sb.append(" and supervise_id=" + sid);
//		}
//		if (UtilValidate.isNotEmpty(sSearch)) {
//
//			sb.append(" and ( name like '%" + sSearch + "%'");
//			sb.append("    or address like '%" + sSearch + "%'");
//			sb.append("    or area like '%" + sSearch + "%'");
//			sb.append("    or cost like '%" + sSearch + "%'");
//			sb.append("    or structure like '%" + sSearch + "%'");
//			sb.append("    or layers like '%" + sSearch + "%'");
//			sb.append("    or height like '%" + sSearch + "%'");
//			// sb.append("    or goals like '%" + sSearch + "%'");
//			if (DateUtil.isValidDate(sSearch)) {
//				sb.append("    or starttime like '%" + sSearch + "%'");
//				sb.append("    or endtime like '%" + sSearch + "%'");
//			}
//			sb.append("  )");
//		}
//
//		sb.append(" order by id desc");
//
//		List<Project> taskList = Project.find(sb.toString()).fetch(p, ps);
//		// for (AndroidTaskInfo task : taskList) {
//		//
//		// }
//
//		return taskList;
//	}

}
