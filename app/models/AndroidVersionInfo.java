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

import play.db.jpa.GenericModel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * app版本信息表
 * @author 
 * @date 2014-11-18
 */
@Entity
@Table(name="android_version_info")
public class AndroidVersionInfo extends GenericModel{
    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", unique = true, nullable = false) 
    public int id;				//版本编号
    public String version_name; 		//版本号
    public String download_address ; 	//下载地址	
    public Date create_time; 			//创建时间
    public String version_introduce;			//版本介绍信息
    public String version_qrcode;	    //二维码下载地址
    public int flag;				//标志位，0：正常；1：不可用
    @Transient
    public String[] introduce;			//版本介绍信息


	public String getVersion_name() {
		return version_name;
	}

	public void setVersion_name(String version_name) {
		this.version_name = version_name;
	}

	public String getVersion_qrcode() {
		return version_qrcode;
	}

	public void setVersion_qrcode(String version_qrcode) {
		this.version_qrcode = version_qrcode;
	}

	public String[] getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		
		JsonParser parser = new JsonParser();
		JsonArray jsonArray = parser.parse(introduce).getAsJsonArray();
		
		List<String> introduceList = new ArrayList<String>();
		
		for (int i = 0; i < jsonArray.size(); i++) {
			
			JsonElement jsonItemIntroduce = (JsonElement) jsonArray.get(i);
			String itemIntroduce = jsonItemIntroduce.getAsString();
			introduceList.add(itemIntroduce);			
			
		}
		
		this.introduce = introduceList.toArray(new String[0]);
		
	}

	public String getDownload_address() {
		return download_address;
	}

	public void setDownload_address(String download_address) {
		this.download_address = download_address;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	
}

