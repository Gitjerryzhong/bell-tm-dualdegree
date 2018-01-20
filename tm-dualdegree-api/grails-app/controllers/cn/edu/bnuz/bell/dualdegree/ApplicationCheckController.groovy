package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.ListCommand
import cn.edu.bnuz.bell.workflow.ListType
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_DUALDEGREE_DEPT_ADMIN")')
class ApplicationCheckController {
    ApplicationCheckService applicationCheckService
    def index(String teacherId, ListCommand cmd) {
        renderJson(applicationCheckService.list(teacherId, cmd))
    }

    def show(String teacherId, Long applicationCheckId, String id, String type) {
        ListType listType = ListType.valueOf(type)
        if (id == 'undefined') {
            renderJson applicationCheckService.getFormForReview(teacherId, applicationCheckId, listType, Activities.CHECK)
        } else {
            renderJson applicationCheckService.getFormForReview(teacherId, applicationCheckId, listType, UUID.fromString(id))
        }
    }
}
