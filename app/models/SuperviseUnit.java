package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import play.db.jpa.Model;
import util.UtilValidate;

@Entity
public class SuperviseUnit extends Model {
	// 监理单位
	// id int(7) unsigned
	// projectid mediumint(8) unsigned
	public String name; // name varchar(20)
	public String director; // director varchar(20) 主管
	public String qualification; // qualification varchar(20)
	public String telephone; // telephone varchar(15) 联系电话
	public Date create_time = new Date();
	public Integer delete_flag = 0;
	
	public static List<SuperviseUnit> allInfos() {
		return SuperviseUnit.find("delete_flag=0 order by id desc").fetch();
	}
	
	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	public static boolean deleteInfo(Long id) {
		SuperviseUnit info = SuperviseUnit.findById(id);
		boolean success = false;
		if (UtilValidate.isNotEmpty(info)) {
			// info.delete();
			info.delete_flag = 1;
			success = info.save().isPersistent();
		}
		return success;
	}
}
