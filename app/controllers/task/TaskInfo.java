package controllers.task;

import java.util.ResourceBundle;

import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;

import play.Logger;
import play.data.validation.Required;
import play.mvc.Controller;
import models.AndroidNoticeInfo;
import models.AndroidTaskInfo;
import models.AndroidUserInfo;
import models.ResultInfo;
import service.notice.CustomDocinfoService;
import service.task.TaskInfoService;
import service.user.UserInfoService;

public class TaskInfo extends Controller{

    /**
     * 添加一个任务
     * TaskInfo.java
     * @param taskInfo
     * @param receive_user_ids 接收用户列表,主要针对接收用户指定人时
     * @param receive_type 接受用户类型，主要针对科长或科员发布任务，0：本科室；1：全部；2：指定人（具体接收人在receive_user_ids中指明）
     * 2016年1月30日
     */
    public static void addTaskInfo(AndroidTaskInfo taskInfo, String receive_user_ids, Integer receive_type) {
 //   	AndroidUserInfo androidUserInfo = UserInfoService.getCurrentUser();
	ResultInfo info = TaskInfoService.addTaskInfo(taskInfo, receive_user_ids,receive_type, request);

	renderJSON(info);

    }
    
    /**
     * 查询用户任务列表接口
     * TaskInfo.java
     * @param user_id 用户编号,如果user_id=0，则默认为局长级，可以查看所有数据
     * @param department_id 所属部门
     * @param task_type //任务类别，1：工程任务；2：自定义任务
     * @param status //是否处理/审批完成，0:全部；1：已完成；2：未完成。所有新建审批/下派任务该状态为2.
     * @param p
     * @param ps
     * 2015年8月16日
     */
    public static void getUserTaskList(Integer user_id,
	    Integer task_type,Integer status,String keywords, Integer p, Integer ps) {
	if (user_id == null) {
	    user_id = 0;
	}
//	if (department_id == null) {
//	    department_id = 0;
//	}
	if (task_type == null) {
	    task_type = 0;
	}
	if (status == null) {
	    status = 0;
	}
	if (p == null) {
	    p = Integer.valueOf(ResourceBundle.getBundle("config").getString("page"));
	}
	if (ps == null) {
	    ps = Integer.valueOf(ResourceBundle.getBundle("config").getString("pagecount"));
	}
	
	ResultInfo info = TaskInfoService.getUserTaskList(user_id, task_type,status,keywords, p, ps, request);
	renderJSON(info);

    }

    /**
     * 查询用户任务详情接口
     * TaskInfo.java
     * @param id 编号
     * @param user_id
     * 2015年8月16日
     */
    public static void getUserTaskInfo(@Required Integer id, Integer user_id) {
    	Logger.info("TaskInfo.getUserTaskInfo : user_id = " + user_id + ", "
    			+ "id = " + id );
	if (user_id == null) {
	    user_id = 0;
	}
	ResultInfo info = TaskInfoService.getUserTaskInfo(id,user_id, request);
	renderJSON(info);

    }
    
    /**
     *编辑用户任务详情接口
     * TaskInfo.java
     * @param id 编号
     * 2015年8月16日
     */
    public static void editUserTaskInfo(AndroidTaskInfo taskInfo) {
	ResultInfo info = TaskInfoService.editUserTaskInfo(taskInfo, request);
	renderJSON(info);

    }
    
    
    /**
     * 删除用户任务详情接口
     * TaskInfo.java
     * @param id 编号
     * 2015年8月16日
     */
    public static void deleteUserTaskInfo(Integer id) {
	ResultInfo info = TaskInfoService.deleteUserTaskInfo(id, request);
	renderJSON(info);

    }
    
    
    /**
     * 将一个任务置为公开,如果当前任务是回复任务，则同时将其回复的任务也公开
     * TaskInfo.java
     * @param user_id
     * @param task_id
     * 2016年1月31日
     */
    public static void setTaskOpen(Integer user_id, Integer task_id, Integer if_open) {
	if (if_open == null || if_open < 0) {
	    if_open = 1;
	}
 //   	AndroidUserInfo androidUserInfo = UserInfoService.getCurrentUser();
	ResultInfo info = TaskInfoService.setTaskOpen(user_id,task_id, if_open, request);

	renderJSON(info);

    }
    
    
//    /**
//     * 任务审核/完成记录，同步更新AndroidTaskInfo表的status字段
//     * TaskInfo.java
//     * @param task_id 任务编号
//     * @param verify_status 审批状态：0:待审核；1:已完成；2：未完成(即审核未通过);
//     * @param verify_user_id 审核/下派人
//     * @param receive_user_id 转发任务接收人
//     * @param verify_comment 说明
//     * @param verify_type 处理类型：1：审核；2：转发
//     * 2015年8月16日
//     */
//    public static void verifyUserTaskInfo(Integer task_id, Integer verify_status,
//	    Integer verify_user_id,Integer receive_user_id ,String verify_comment, Integer verify_type) {
//	if (task_id == null) {
//	    task_id = 0;
//	}
//	if (verify_status == null) {
//	    verify_status = 0;
//	}
//	if (verify_user_id == null) {
//	    verify_user_id = 0;
//	}
//	if (verify_comment == null) {
//	    verify_comment = "";
//	}
//	if (receive_user_id == null) {
//	    receive_user_id = 0;
//	}
//	ResultInfo info = TaskInfoService.verifyTaskInfo(task_id, verify_status, verify_user_id,receive_user_id, verify_comment,verify_type, request);
//	renderJSON(info);
//    }
//    /**
//     * 任务审核/完成记录，同步更新AndroidTaskInfo表的status字段
//     * TaskInfo.java
//     * @param id 编号
//     * 2015年8月16日
//     */
//    public static void verifyUserTaskInfo(Integer task_id, Integer verify_status,String verify_comment) {
//	ResultInfo info = TaskInfoService.verifyUserTaskInfo(taskInfo, request);
//	renderJSON(info);
//    } 
    
}
