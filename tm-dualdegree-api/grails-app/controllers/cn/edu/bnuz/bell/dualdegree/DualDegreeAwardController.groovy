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

    /**
     * 编辑数据
     */
    def edit(Long id) {
        renderJson([
                form: dualDegreeAwardService.getFormForShow(id),
                departments: dualDegreeAwardService.myDepartments
        ])
    }

    def show(Long id) {
        renderJson(dualDegreeAwardService.getFormForShow(id))
    }

    /**
     * 创建
     */
    def create() {
        renderJson([
                form: [ ],
                departments: dualDegreeAwardService.myDepartments
        ])
    }

    /**
     * 更新数据
     */
    def update(Long id) {
        def cmd = new AwardCommand()
        bindData(cmd, request.JSON)
        cmd.id = id
        dualDegreeAwardService.update(cmd)
        renderOk()
    }
}
