package service.project;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.mvc.Controller;
import play.mvc.Http.Request;
import models.AndroidTaskInfo;
import models.BuildUnit;
import models.ConstructionUnit;
import models.Project;
import models.Region;
import models.ResultInfo;
import models.SuperviseUnit;
import service.task.TaskInfoService;
import util.DateUtil;
import util.UtilValidate;

public class ProjectManageService{


	public static ResultInfo getProjectList(Integer p , Integer ps, String keywords, Request request) {
		ResultInfo info = new ResultInfo();
		if (keywords != null && keywords.length() > 0) {//首先将keyword去空格
		    keywords = keywords.replaceAll(" ", "");//半角空格
		    if (keywords != null && keywords.length() > 0) {
			keywords = keywords.replaceAll("　", "");//全角空格
		    }
		}
		long count = 0l;
		List<Project> projectList = null;
		if (keywords != null && keywords.length() > 0) {
			count = Project.count("name LIKE \'%" + keywords + "%\'");
			projectList = Project.find("name LIKE \'%" + keywords + "%\' order by id desc").fetch(p,ps);

		}else {
			count = Project.count();
			projectList = Project.find("order by id desc").fetch(p,ps);

		}
		info.setCodeAndMsg(200);
		info.setCount(count);
		info.setInfo(projectList);
		info.setRequest(request.path);
		return info;
	}

	

	public static ResultInfo getProjectInfo(Long id, Request request) {
		ResultInfo info = new ResultInfo();
		Project project = Project.find("id = ?", id).first();
		if (project == null) {
			info.setCodeAndMsg(1021);
			info.setCount(0);
			info.setInfo(null);
			info.setRequest(request.path);
			return info;

		}else {
			project.starttimeString = DateUtil.date2String(project.starttime, "yyyy-MM-dd");//"yyyy-MM-dd HH:mm:ss"
			String create_time_string =
				 DateUtil.date2String(project.starttime,
				 "yyyy-MM-dd");
				
				 try {
					 project.starttime =
				 DateUtil.string2UtilDate(create_time_string,
				 "yyyy-MM-dd");
				 } catch (Exception e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
				 }		

					project.endtimeString = DateUtil.date2String(project.endtime, "yyyy-MM-dd");//"yyyy-MM-dd HH:mm:ss"
					String end_time_string =
						 DateUtil.date2String(project.endtime,
						 "yyyy-MM-dd");
						
						 try {
							 project.endtime =
						 DateUtil.string2UtilDate(end_time_string,
						 "yyyy-MM-dd");
						 } catch (Exception e) {
						 // TODO Auto-generated catch block
					e.printStackTrace();
				 }		

		    project.regionname = Region.getRegionName(project.regionid);

                    // { "mDataProp": "cname" }, //施工单位
                    // { "mDataProp": "manager" }, //项目经理
                    // { "mDataProp": "telephone" }, //联系电话
                    if (UtilValidate.isNotEmpty(project.construction_id)) {
                            ConstructionUnit constructionUnit = ConstructionUnit.findById(project.construction_id);
                            project.cname = constructionUnit.name;
                            project.manager = constructionUnit.manager;
                            project.telephone = constructionUnit.telephone;
                    }

                    // { "mDataProp": "bname" }, //建设单位
                    if (UtilValidate.isNotEmpty(project.build_id)) {
                            BuildUnit buildUnit = BuildUnit.findById(project.build_id);
                            project.bname = buildUnit.name;
                    }

                    // { "mDataProp": "sname" }, //监理单位
                    if (UtilValidate.isNotEmpty(project.supervise_id)) {
                            SuperviseUnit superviseUnit = SuperviseUnit.findById(project.supervise_id);
                            project.sname = superviseUnit.name;
                    }

                    if (UtilValidate.isEmpty(project.schedule)) {
                            project.schedule = 0;
                    }
                    if (UtilValidate.areEqual(project.goals, "goals_none")) {
						project.goals = "无";
					}
			info.setCodeAndMsg(200);
			info.setCount(1);
			info.setInfo(project);
			info.setRequest(request.path);
			return info;

		}
	}
}
