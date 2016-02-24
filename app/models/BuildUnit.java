package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import play.db.jpa.Model;
import util.UtilValidate;

@Entity
public class BuildUnit extends Model {
	// 建设单位
	public String name;
	// projectid mediumint(8) unsigned
	public String representing;// varchar(20) 代表人
	public String qualification; // varchar(20)
	public String telephone; // varchar(15)
	public Date create_time = new Date();
	public Integer delete_flag = 0;
	
	public static List<BuildUnit> allInfos() {
		return BuildUnit.find("delete_flag=0 order by id desc").fetch();
	}
	
	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	public static boolean deleteInfo(Long id) {
		BuildUnit info = BuildUnit.findById(id);
		boolean success = false;
		if (UtilValidate.isNotEmpty(info)) {
			// info.delete();
			info.delete_flag = 1;
			success = info.save().isPersistent();
		}
		return success;
	}
}
