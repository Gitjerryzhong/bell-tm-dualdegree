package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.commands.SubmitCommand
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasRole("ROLE_DUALDEGREE_STUDENT")')
class ApplicationFormController {
    DegreeApplicationFormService degreeApplicationFormService

    def show(Long awardId, String userId) {
        renderJson degreeApplicationFormService.getFormForShow(awardId, userId)
    }

    def create(Long awardId, String userId) {
        renderJson degreeApplicationFormService.getFormForCreate(awardId, userId)
    }

    def save(String userId) {
//        def cmd = new BookingFormCommand()
//        bindData(cmd, request.JSON)
//        def form = bookingFormService.create(userId, cmd)
        renderJson([id: form.id])
    }

    def edit(String userId, Long id) {
//        renderJson bookingFormService.getFormForEdit(userId, id)
    }

    def update(String userId, Long id) {
//        def cmd = new BookingFormCommand()
//        bindData(cmd, request.JSON)
//        cmd.id = id
//        bookingFormService.update(userId, cmd)
        renderOk()
    }

    def delete(String userId, Long id) {
//        bookingFormService.delete(userId, id)
        renderOk()
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
