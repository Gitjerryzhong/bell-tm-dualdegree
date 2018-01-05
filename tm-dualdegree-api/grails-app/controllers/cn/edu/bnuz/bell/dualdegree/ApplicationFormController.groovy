package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.commands.SubmitCommand
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasRole("ROLE_DUALDEGREE_STUDENT")')
class ApplicationFormController {
    DegreeApplicationFormService degreeApplicationFormService
    AwardService awardService

    def show(Long awardId, String userId) {
        renderJson degreeApplicationFormService.getFormForShow(awardId, userId)
    }

    def create(Long awardId, String userId) {
        renderJson degreeApplicationFormService.getFormForCreate(awardId, userId)
    }

    def patch(String userId, Long id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.SUBMIT:
                def cmd = new SubmitCommand()
                bindData(cmd, request.JSON)
                cmd.id = id
                degreeApplicationFormService.submit(userId, cmd)
                break
        }
        renderOk()
    }
}
