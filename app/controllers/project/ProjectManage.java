package controllers.project;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.mvc.Controller;

import models.AndroidTaskInfo;
import models.Project;
import models.ResultInfo;
import service.project.ProjectManageService;
import service.task.TaskInfoService;
import util.UtilValidate;

public class ProjectManage extends Controller {

	public static void index() {
		render();
	}
	public static void getProjectList(Integer p , Integer ps, String keywords) {
		if (p == null) {
			p = 1;
		}
		if (ps == null) {
			ps = 1000;
		}
		ResultInfo info = ProjectManageService.getProjectList(p,ps,keywords, request);
		renderJSON(info);
	}

	public static void getProjectInfo(Long id) {
		ResultInfo info = ProjectManageService.getProjectInfo(id, request);
		renderJSON(info);
	}
}
