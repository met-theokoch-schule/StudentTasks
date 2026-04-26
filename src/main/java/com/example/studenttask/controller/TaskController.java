package com.example.studenttask.controller;

import com.example.studenttask.dto.TaskIframeViewDataDto;
import com.example.studenttask.service.TaskIframeQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/student")
public class TaskController {

    @Autowired
    private TaskIframeQueryService taskIframeQueryService;

    @GetMapping("/tasks/{taskId}/iframe")
    public String viewTaskIframe(@PathVariable Long taskId, 
                                @RequestParam(required = false) Long userId,
                                @RequestParam(required = false) Integer version,
                                Authentication authentication, Model model) {
        TaskIframeViewDataDto viewData = taskIframeQueryService.getTaskIframeViewData(
            taskId,
            authentication.getName(),
            userId != null,
            version
        );
        model.addAttribute("task", viewData.getTask());
        model.addAttribute("taskView", viewData.getTaskView());
        model.addAttribute("userTask", viewData.getUserTask());
        model.addAttribute("userTaskId", viewData.getUserTask().getId());
        model.addAttribute("currentContent", viewData.getCurrentContent());
        model.addAttribute("renderedDescription", viewData.getRenderedDescription());
        model.addAttribute("isIframe", true);
        model.addAttribute("isTeacherView", viewData.isTeacherView());

        return viewData.getTaskView().getTemplatePath();
    }
}
