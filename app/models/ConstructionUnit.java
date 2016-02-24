package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import play.db.jpa.Model;
import util.UtilValidate;

@Entity
public class ConstructionUnit extends Model {
	// 施工单位
	// id int(7) unsigned
	// projectid mediumint(8) unsigned
	public String name; // name varchar(20)
	public String legal; // legal varchar(20) 法人
	public String qualification;// qualification varchar(20)
	public String manager;// manager varchar(10) 项目经理
	public String license;// license varchar(20)
	public String telephone;// telephone varchar(15)
	public String level;// level tinyint(2) unsigned
	public Date create_time = new Date();
	public Integer delete_flag = 0;
	
	public static List<ConstructionUnit> allInfos() {
		return ConstructionUnit.find("delete_flag=0 order by id desc").fetch();
	}
	
	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	public static boolean deleteInfo(Long id) {
		ConstructionUnit info = ConstructionUnit.findById(id);
		boolean success = false;
		if (UtilValidate.isNotEmpty(info)) {
			// info.delete();
			info.delete_flag = 1;
			success = info.save().isPersistent();
		}
		return success;
	}
}
