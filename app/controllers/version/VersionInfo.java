package controllers.version;


import models.AndroidVersionInfo;
import models.ResultInfo;
import play.Logger;
import play.data.validation.Required;
import play.mvc.Controller;
import service.version.VersionInfoService;

public class VersionInfo extends Controller {

    /**
     * 获取最新版本信息 
     * AppVersion.java
     * 
     * @param 
     * 2015年2月11日
     */
    public static void getVersion(Integer id) {

	Logger.info("VersionInfo.getVersion id = "+ id);
	if (id == null) {
	    id = 0;
	}
	ResultInfo info =  VersionInfoService
		.getVersionInfo(id,request);
	renderJSON(info);
	
    }

}
