package cn.edu.bnuz.bell.dualdegree

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 协议管理
 */
@PreAuthorize('hasAuthority("PERM_AGREEMENT_WRITE")')
class AgreementController {
	AgreementService agreementService

    def index() {
        renderJson(agreementService.list())
    }

    /**
     * 编辑数据
     */
    def edit(Long id) {
        renderJson([
                form: agreementService.getFormForEdit(id),
                groups: agreementService.groups,
                majors: agreementService.majors])
    }

    def show(Long id) {
        renderJson(agreementService.getFormForShow(id))
    }

    /**
     * 保存数据
     */
    def save() {
        def cmd = new AgreementCommand()
        bindData(cmd, request.JSON)
        def form = agreementService.create(cmd)
        renderJson([id: form.id])
    }

    /**
     * 更新数据
     */
    def update(Long id) {
        def cmd = new AgreementCommand()
        bindData(cmd, request.JSON)
        cmd.id = id
        agreementService.update(cmd)
        renderOk()
    }

    /**
     * 创建
     */
    def create() {
        renderJson([
                form: [
                        items: [],
                ],
                groups: agreementService.groups,
                majors: agreementService.majors
        ])
    }
}
