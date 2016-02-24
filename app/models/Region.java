package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.db.jpa.Model;
import util.UtilValidate;

@Entity(name = "region")
public class Region extends Model {
	public String regionname;// varchar(30)
	public Long parentid;// smallint(3)
	public String parentstr;// varchar(50)
	public Long orderid;// smallint(5) unsigned
	public Integer delete_flag = 0;

	@Transient
	public boolean hasChildren = false;

	public static String getRegionName(Long id) {
		String regionname = "";
		Region region = Region.findById(id);
		if (UtilValidate.isNotEmpty(region)) {
			regionname = region.regionname;
		}

		return regionname;
	}

	public static List<Region> allInfos() {
		return Region.find("delete_flag=0 order by id asc").fetch();
	}

	public static List<Region> regionList(Long parentId) {
		return Region.find("delete_flag=0 and parentid=? order by id asc", parentId).fetch();
	}

	public static boolean hasChildren(Long id) {
		long count = Region.count("parentid=?", id);
		boolean has = false;
		if (count > 0) {
			has = true;
		}
		return has;
	}

	public static Long getMaxOrderId() {
		Region region = Region.find("order by id desc").first();
		Long orderId = 1L;
		if (UtilValidate.isNotEmpty(region)) {
			orderId = region.orderid + 1;
		}

		return orderId;
	}

	public static String getParentStr(Long parentId) {
		String parentStr = "";
		if (parentId == 0 || parentId == null) {
			parentStr = "0,";
		} else {
			Region region = Region.findById(parentId);
			parentStr = region.parentstr + parentId + ",";
		}

		return parentStr;
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	public static boolean deleteInfo(Long id) {
		Region info = Region.findById(id);
		boolean success = false;
		if (UtilValidate.isNotEmpty(info)) {
			// info.delete();
			info.delete_flag = 1;
			success = info.save().isPersistent();
		}
		return success;
	}
}
