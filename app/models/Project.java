package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import play.db.jpa.Model;
import util.DateUtil;
import util.UtilValidate;

@Entity(name = "project")
public class Project extends Model {
	public String name;// varchar(50)
	public Long regionid;// smallint(3) unsigned
	public String address;// varchar(100)
	public String area;// varchar(10)
	public String cost;// varchar(10)

	@Temporal(TemporalType.DATE)
	public Date starttime;// int(10) unsigned
	@Transient
	public String starttimeString;// int(10) unsigned

	@Temporal(TemporalType.DATE)
	public Date endtime;// int(10) unsigned
	@Transient
	public String endtimeString;// int(10) unsigned
	
	public String structure;// varchar(50)
	public String layers;// varchar(20)
	public String height;// varchar(20)
	public String goals;// varchar(100)
	public Integer completed = 0;// enum('0','1')
	public Integer delete_flag = 0;

	public Integer schedule = 0; // 工程进度

	@Transient
	public String manager = "";

	@Transient
	public String telephone = "";

	public Long construction_id;
	@Transient
	public String cname = ""; // 施工单位

	public Long build_id;
	@Transient
	public String bname = ""; // 建设单位

	public Long supervise_id;
	@Transient
	public String sname = ""; // 监理单位

	public Date checktime = new Date();

	@Transient
	public String regionname;//所属区域

	public String result;

	/**
	 * 所有未结束的工程
	 * 
	 * @return
	 */
	public static List<Project> getEffectiveProjects() {
		return Project.find("endtime >= CURDATE()").fetch();
	}

	public static List<Project> getAllProjects() {
		return Project.findAll();
	}

	public static List<Project> filterInfo(int p, int ps, String sSearch, Long regionId, Long cid, Long bid, Long sid, String goals,
			String schedule, String starttime, String endtime) {
		StringBuffer sb = new StringBuffer();
		sb.append(" 1=1");
		if (UtilValidate.isNotEmpty(regionId) && regionId > 0) {
			// sb.append(" and (regionid=" + regionId + " or parentstr like '%" + regionId + ",%')");
			sb.append(" and regionid=" + regionId);
		}
		if (UtilValidate.isNotEmpty(goals)) {
			sb.append(" and goals = '" + goals + "'");
		}

		// 进度. 1 :未启动; 2: 进行中; 3: 已完成
		if (UtilValidate.isNotEmpty(schedule)) {
			if (UtilValidate.areEqual("1", schedule)) {
				sb.append(" and schedule in ('0', '0%')");
			} else if (UtilValidate.areEqual("2", schedule)) {
				sb.append(" and (schedule > '0' or schedule > '0%')");
				sb.append(" and (schedule < '100' or schedule < '100%')");
			} else if (UtilValidate.areEqual("3", schedule)) {
				sb.append(" and (schedule >= '100' or schedule >= '100%')");
			}
		}

		if (UtilValidate.isNotEmpty(starttime)) {
			sb.append(" and checktime >= '" + starttime + "'");
		}
		if (UtilValidate.isNotEmpty(endtime)) {
			sb.append(" and checktime <= '" + endtime + " 23:59:59.999'");
		}

		if (UtilValidate.isNotEmpty(cid)) {
			sb.append(" and construction_id=" + cid);
		}
		if (UtilValidate.isNotEmpty(bid)) {
			sb.append(" and build_id=" + bid);
		}
		if (UtilValidate.isNotEmpty(sid)) {
			sb.append(" and supervise_id=" + sid);
		}
		if (UtilValidate.isNotEmpty(sSearch)) {

			sb.append(" and ( name like '%" + sSearch + "%'");
			sb.append("    or address like '%" + sSearch + "%'");
			sb.append("    or area like '%" + sSearch + "%'");
			sb.append("    or cost like '%" + sSearch + "%'");
			sb.append("    or structure like '%" + sSearch + "%'");
			sb.append("    or layers like '%" + sSearch + "%'");
			sb.append("    or height like '%" + sSearch + "%'");
			// sb.append("    or goals like '%" + sSearch + "%'");
			if (DateUtil.isValidDate(sSearch)) {
				sb.append("    or starttime like '%" + sSearch + "%'");
				sb.append("    or endtime like '%" + sSearch + "%'");
			}
			sb.append("  )");
		}

		sb.append(" order by id desc");

		List<Project> taskList = Project.find(sb.toString()).fetch(p, ps);
		// for (AndroidTaskInfo task : taskList) {
		//
		// }

		return taskList;
	}

	public static Long filterInfoCount(String sSearch, Long regionId, Long cid, Long bid, Long sid, String goals, String schedule,
			String starttime, String endtime) {
		StringBuffer sb = new StringBuffer();
		sb.append(" 1=1");
		if (UtilValidate.isNotEmpty(regionId) && regionId > 0) {
			// sb.append(" and (regionid=" + regionId + " or parentstr like '%" + regionId + ",%')");
			sb.append(" and regionid=" + regionId);
		}
		if (UtilValidate.isNotEmpty(goals)) {
			sb.append(" and goals = '" + goals + "'");
		}

		// 进度. 1 :未启动; 2: 进行中; 3: 已完成
		if (UtilValidate.isNotEmpty(schedule)) {
			if (UtilValidate.areEqual("1", schedule)) {
				sb.append(" and schedule = 0");
			} else if (UtilValidate.areEqual("2", schedule)) {
				sb.append(" and schedule > 0");
				sb.append(" and schedule < 100");
			} else if (UtilValidate.areEqual("3", schedule)) {
				sb.append(" and schedule >= 100");
			}
		}

		if (UtilValidate.isNotEmpty(starttime)) {
			sb.append(" and checktime >= '" + starttime + "'");
		}
		if (UtilValidate.isNotEmpty(endtime)) {
			sb.append(" and checktime <= '" + endtime + " 23:59:59.999'");
		}

		if (UtilValidate.isNotEmpty(cid)) {
			sb.append(" and construction_id=" + cid);
		}
		if (UtilValidate.isNotEmpty(bid)) {
			sb.append(" and build_id=" + bid);
		}
		if (UtilValidate.isNotEmpty(sid)) {
			sb.append(" and supervise_id=" + sid);
		}
		if (UtilValidate.isNotEmpty(sSearch)) {

			sb.append(" and ( name like '%" + sSearch + "%'");
			sb.append("    or address like '%" + sSearch + "%'");
			sb.append("    or area like '%" + sSearch + "%'");
			sb.append("    or cost like '%" + sSearch + "%'");
			sb.append("    or structure like '%" + sSearch + "%'");
			sb.append("    or layers like '%" + sSearch + "%'");
			sb.append("    or height like '%" + sSearch + "%'");
			// sb.append("    or goals like '%" + sSearch + "%'");
			if (DateUtil.isValidDate(sSearch)) {
				sb.append("    or starttime like '%" + sSearch + "%'");
				sb.append("    or endtime like '%" + sSearch + "%'");
			}
			sb.append("  )");
		}

		Long count = Project.count(sb.toString());

		return count;
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	public static boolean deleteInfo(Long id) {
		Project info = Project.findById(id);
		boolean success = false;
		if (UtilValidate.isNotEmpty(info)) {
			// info.delete();
			info.delete_flag = 1;
			success = info.save().isPersistent();
		}
		return success;
	}
}
