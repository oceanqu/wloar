package controllers.department;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.mvc.Controller;

import models.AndroidTaskInfo;
import models.Project;
import models.ResultInfo;
import service.department.DepartmentManageService;
import service.project.ProjectManageService;
import service.task.TaskInfoService;
import util.UtilValidate;

public class DepartmentManage extends Controller {

	public static void index() {
		render();
	}
	public static void getDepartmentList(Integer user_id ,Integer p , Integer ps) {
		if (p == null) {
			p = 1;
		}
		if (ps == null) {
			ps = 1000;
		}
		if (user_id == null) {
			user_id = 0;
		}
		ResultInfo info = DepartmentManageService.getDepartmentList(user_id,p,ps, request);
		renderJSON(info);
	}

}
