package cn.edu.bnuz.bell.dualdegree

import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasRole("ROLE_DUALDEGREE_ADMIN_DEPT")')
class DualDegreeAwardController {
    DualDegreeAwardService dualDegreeAwardService

    def index() {
        renderJson(dualDegreeAwardService.list())
    }

    /**
     * 保存数据
     */
    def save() {
        def cmd = new AwardCommand()
        bindData(cmd, request.JSON)
        def form = dualDegreeAwardService.create(cmd)
        renderJson([id: form.id])
    }
}
