package models;

import static javax.persistence.GenerationType.AUTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.mapping.Array;

import play.db.jpa.GenericModel;
/**
 * 媒体热点推送文章信息表
 * @author 
 * @date 2014-11-18
 */
@Entity
@Table(name="android_file_info")
public class AndroidFileInfo extends GenericModel{
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", unique = true, nullable = false) 
	public Integer id;//
	public String url;// 
	public Date create_time = new Date();// 入库时间 DATETIME
	public String original_file_name;// 原始文件名
	public Integer file_type = 1;//文件类型，1：图片；2：视频；3：音频
	public String file_name;// 
	public Integer user_id = 0;//
	public String user_name;// 
	public String content;//图片说明

	public static AndroidFileInfo findByOriginalFileName(String original_file_name, Integer file_type) {
	    List<AndroidFileInfo> androidFileInfoList = AndroidFileInfo.find("original_file_name = ? and file_type = ?", original_file_name, file_type).fetch();
	    Integer maxId = 0;
	    AndroidFileInfo androidFileInfo = null;
	    if (androidFileInfoList != null && androidFileInfoList.size() > 0) {
		for (AndroidFileInfo androidFileInfo2 : androidFileInfoList) {
		    if (androidFileInfo2 != null) {
			if (androidFileInfo2.id > maxId) {
			    maxId = androidFileInfo2.id;
			    androidFileInfo = androidFileInfo2;
			}
		    }		    
		}
	    }
	    return androidFileInfo;
	}
	
	public static String findByIds(String ids) {
	    List<AndroidFileInfo> androidFileInfoList = new ArrayList<AndroidFileInfo>();
	    	if (ids != null && ids.length() > 0 ) {
				if (ids.endsWith(",")) {
					ids = ids.substring(0, ids.length() - 1);
				}
			}
	    	if (ids.length() > 0) {
				androidFileInfoList = AndroidFileInfo.find("id IN (" + ids + ")").fetch();
			}
	    	
	    String fileNames = "";
	    if (androidFileInfoList != null && androidFileInfoList.size() > 0) {
		for (AndroidFileInfo androidFileInfo : androidFileInfoList) {
		    if (androidFileInfo != null) {
			    fileNames += androidFileInfo.url;
			    fileNames += ",";
		    }
		}
	    }
	    if (fileNames.endsWith(",")) {
		fileNames = fileNames.substring(0, fileNames.length() - 1);
	    }
	    return fileNames;
	}
}
