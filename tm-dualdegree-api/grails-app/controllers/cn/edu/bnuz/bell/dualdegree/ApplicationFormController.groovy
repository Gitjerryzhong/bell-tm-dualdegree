package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.commands.SubmitCommand
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasRole("ROLE_DUALDEGREE_STUDENT")')
class ApplicationFormController {
    DegreeApplicationFormService degreeApplicationFormService

    /**
     * 保存数据
     * @param studentId 学号
     * @param awardPublicId 学位授予批次id
     * @return
     */
    def save(String studentId, Long awardPublicId) {
        def cmd = new ApplicationFormCommand()
        bindData(cmd, request.JSON)
        def form = degreeApplicationFormService.create(studentId, awardPublicId, cmd)
        renderJson([id: form.id])
    }

    /**
     * 编辑数据
     * @param studentId 学号
     * @param awardPublicId 学位授予批次id
     * @param id 申请单id
     */
    def edit(String studentId, Long awardPublicId, Long id) {
        renderJson degreeApplicationFormService.getFormForEdit(studentId, awardPublicId)
    }

    /**
     * 显示数据
     * @param studentId 学号
     * @param awardPublicId 学位授予批次id
     * @param id 申请单id
     * @return
     */
    def show(String studentId, Long awardPublicId, Long id) {
        renderJson ([
                        form: degreeApplicationFormService.getFormForShow(studentId, awardPublicId),
                        fileNames: degreeApplicationFormService.findFiles(awardPublicId, studentId)
        ])
    }

    /**
     * 更新数据
     */
    def update(String studentId, Long awardPublicId, Long id) {
        def cmd = new ApplicationFormCommand()
        bindData(cmd, request.JSON)
        cmd.id = id
        degreeApplicationFormService.update(studentId, awardPublicId, cmd)
        renderOk()
    }

    /**
     * 创建
     */
    def create(String studentId, Long awardPublicId) {
        renderJson degreeApplicationFormService.getFormForCreate(studentId, awardPublicId)
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
