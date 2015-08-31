package controllers.company;

import java.util.ResourceBundle;

import play.mvc.Controller;
import models.AndroidCompanyInfo;
import models.AndroidTaskInfo;
import models.ResultInfo;
import service.company.CompanyInfoService;
import service.task.TaskInfoService;

public class CompanyInfo extends Controller{

    /**
     * 添加一个企业
     * 
     * @param app
     *            文章信息 <必填>
     */
    public static void addCompanyInfo(AndroidCompanyInfo companyInfo) {
 //   	AndroidUserInfo androidUserInfo = UserInfoService.getCurrentUser();
	ResultInfo info = CompanyInfoService.addCompanyInfo(companyInfo, request);

	renderJSON(info);

    }
    
    /**
     * 查询企业列表接口
     * TaskInfo.java
     * @param p
     * @param ps
     * 2015年8月16日
     */
    public static void getCompanyList(Integer p, Integer ps) {
	if (p == null) {
	    p = Integer.valueOf(ResourceBundle.getBundle("config").getString("page"));
	}
	if (ps == null) {
	    ps = Integer.valueOf(ResourceBundle.getBundle("config").getString("pagecount"));
	}
	
	ResultInfo info = CompanyInfoService.getCompanyList(p, ps, request);
	renderJSON(info);

    }

    /**
     * 查询企业详情接口
     * TaskInfo.java
     * @param id 编号
     * 2015年8月16日
     */
    public static void getCompanyInfo(Integer id) {
	ResultInfo info = CompanyInfoService.getCompanyInfo(id, request);
	renderJSON(info);

    }
    
    /**
     * 编辑企业详情接口
     * TaskInfo.java
     * @param id 编号
     * 2015年8月16日
     */
    public static void editCompanyInfo(AndroidCompanyInfo companyInfo) {
	ResultInfo info = CompanyInfoService.editCompanyInfo(companyInfo, request);
	renderJSON(info);

    }
    
    
    /**
     * 删除企业详情接口
     * TaskInfo.java
     * @param id 编号
     * 2015年8月16日
     */
    public static void deleteCompanyInfo(Integer id) {
	ResultInfo info = CompanyInfoService.deleteCompanyInfo(id, request);
	renderJSON(info);

    }
}
