package controllers.verify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import models.AndroidTaskVerifyInfo;
import models.AndroidNoticeInfo;
import models.AndroidTaskVerifyInfo;
import models.ResultInfo;
import play.Logger;
import play.mvc.Controller;
import service.verify.ApplyVerifyInfoService;



/**
 * 后台自定义资讯添加接口
 * @author 
 *
 */
public class ApplyVerifyInfo extends Controller{
    
    /**
     * 根据user_id和审批状态，查询申请审批列表
     * CustomDocinfo.java
     * @param id
     * @param user_id 0：查询全部用户；其他：根据user_id查询
     * @param verify_step 0：未审核；1：一级审核完成；2：二级审核完成;3:全部
     * @param task_id 任务编号 1:质量检查;2:安全检查；3：执法管理
    * 2015年6月15日
     */
    public static void getApplyList(Integer user_id, Integer verify_step,Integer task_id,Integer p, Integer ps) {
	if (user_id == null) {
	    user_id = 0;
	}
	if (task_id == null) {
	    task_id = 0;
	}
	if (verify_step == null) {
	    verify_step = 3;
	}
	if (p == null) {
	    p = 1;
	}
	if (ps == null) {
	    ps = 10;
	}
	ResultInfo info = ApplyVerifyInfoService.getApplyList(user_id, verify_step,task_id, p, ps, request);
	renderJSON(info);

    }

    /**
     * 添加一个审批申请
     * 
     *           
     */
    public static void addApplyInfo(AndroidTaskVerifyInfo androidTaskVerifyInfo) {
	ResultInfo info = ApplyVerifyInfoService.addApplyInfo(androidTaskVerifyInfo, request);

	renderJSON(info);

    }

    /**
     * 批准一个审批申请
     * 
     * @param App
     *            文章信息 <必填>
     */
    public static void verifyApplyInfo(Integer id,Integer verify_step, Integer verify_status,
	    Integer user_id, String user_name,String comment) {
	ResultInfo info = ApplyVerifyInfoService.verifyApplyInfo(id, verify_step, verify_status,
		user_id, user_name, comment, request);

	renderJSON(info);

    }

    /**
     * 根据id,获取一个审批的状态
     * 
     */
    public static void getApplyInfo(Integer id) {
	ResultInfo info = ApplyVerifyInfoService.getApplyInfo(id, request);
	
	renderJSON(info);

    }

 }
