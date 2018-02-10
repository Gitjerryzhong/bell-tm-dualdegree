package cn.edu.bnuz.bell.dualdegree

import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasRole("ROLE_DUALDEGREE_STUDENT")')
class PaperFormController {
    PaperFormService paperFormService

    def index(String studentId, Long applicationFormId) {
        renderJson paperFormService.getPaperForm(studentId, applicationFormId)
    }

    def save(String studentId, Long applicationFormId) {
        def cmd = new PaperFormCommand()
        bindData(cmd, request.JSON)
        def form = paperFormService.create(studentId, applicationFormId, cmd)
        renderJson([id: form.id])
    }
}
