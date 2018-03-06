package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.ListCommand
import cn.edu.bnuz.bell.workflow.ListType
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_DUALDEGREE_PAPER_APPROVE")')
class PaperApprovalController {
    PaperApprovalService paperApprovalService

    def index(String approverId, ListCommand cmd) {
        renderJson(paperApprovalService.list(approverId, cmd))
    }

    def show(String approverId, Long paperApprovalId, String id, String type) {
        ListType listType = ListType.valueOf(type)
        if (id == 'undefined') {
            renderJson paperApprovalService.getFormForReview(approverId, paperApprovalId, listType)
        } else {
            renderJson paperApprovalService.getFormForReview(approverId, paperApprovalId, listType, UUID.fromString(id))
        }
    }

    def patch(String approverId, Long paperApprovalId, String id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.ACCEPT:
                def cmd = new AcceptCommand()
                bindData(cmd, request.JSON)
                cmd.id = paperApprovalId
                paperApprovalService.accept(approverId, cmd, UUID.fromString(id))
                break
            case Event.REJECT:
                def cmd = new RejectCommand()
                bindData(cmd, request.JSON)
                cmd.id = paperApprovalId
                paperApprovalService.reject(approverId, cmd, UUID.fromString(id))
                break
            default:
                throw new BadRequestException()
        }

        show(approverId, paperApprovalId, id, 'tobe')
    }
}
