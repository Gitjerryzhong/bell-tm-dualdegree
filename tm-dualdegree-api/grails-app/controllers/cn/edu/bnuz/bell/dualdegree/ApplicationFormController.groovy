package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.commands.SubmitCommand
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasRole("ROLE_DUALDEGREE_STUDENT")')
class ApplicationFormController {
    ApplicationFormService applicationFormService
    ApplicationReviewerService applicationReviewerService

    /**
     * @param studentId 学号
     * @return 可申请授予和已申请单
     */
    def index(String studentId) {
        renderJson applicationFormService.list(studentId)
    }
    /**
     * 保存数据
     * @param studentId 学号
     * @return id
     */
    def save(String studentId) {
        def cmd = new ApplicationFormCommand()
        bindData(cmd, request.JSON)
        def form = applicationFormService.create(studentId, cmd)
        renderJson([id: form.id])
    }

    /**
     * 编辑数据
     * @param studentId 学号
     * @param id 申请单id
     */
    def edit(String studentId, Long id) {
        renderJson applicationFormService.getFormForEdit(studentId, id)
    }

    /**
     * 显示数据
     * @param studentId 学号
     * @param awardPublicId 学位授予批次id
     * @param id 申请单id
     * @return
     */
    def show(String studentId, Long id) {
        def form = applicationFormService.getFormForShow(studentId, id)
        renderJson ([
                        form: form,
                        award: applicationFormService.getAward((Long)form.awardId),
                        fileNames: applicationFormService.findFiles(studentId, form.awardId)
        ])
    }

    /**
     * 更新数据
     */
    def update(String studentId, Long id) {
        def cmd = new ApplicationFormCommand()
        bindData(cmd, request.JSON)
        cmd.id = id
        applicationFormService.update(studentId, cmd)
        renderOk()
    }

    /**
     * 创建
     * @param studentId 学号
     * @param awardId 授予Id
     * @return
     */
    def create(String studentId, Long awardId) {
        renderJson applicationFormService.getFormForCreate(studentId, awardId)
    }

    def patch(String studentId, Long id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.SUBMIT:
                def cmd = new SubmitCommand()
                bindData(cmd, request.JSON)
                cmd.id = id
                applicationFormService.submit(studentId, cmd)
                break
        }
        renderOk()
    }

    /**
     * 获取审核人
     * @param applicationFormId 申请Id
     * @return 审核人列表
     */
    def checkers(Long applicationFormId) {
        def form = DegreeApplication.load(applicationFormId)
        if (!form) {
            renderBadRequest()
        } else {
            renderJson applicationReviewerService.getCheckers(form.awardId)
        }
    }
}
