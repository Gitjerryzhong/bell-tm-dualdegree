package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.commands.SubmitCommand
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasRole("ROLE_DUALDEGREE_STUDENT")')
class ApplicationFormController {
    DegreeApplicationFormService degreeApplicationFormService

    /**
     * @param studentId 学号
     * @return 可申请授予和已申请单
     */
    def index(String studentId) {
        renderJson degreeApplicationFormService.list(studentId)
    }
    /**
     * 保存数据
     * @param studentId 学号
     * @return id
     */
    def save(String studentId) {
        def cmd = new ApplicationFormCommand()
        bindData(cmd, request.JSON)
        println cmd.value
        def form = degreeApplicationFormService.create(studentId, cmd)
        renderJson([id: form.id])
    }

    /**
     * 编辑数据
     * @param studentId 学号
     * @param id 申请单id
     */
    def edit(String studentId, Long id) {
        renderJson degreeApplicationFormService.getFormForEdit(studentId, id)
    }

    /**
     * 显示数据
     * @param studentId 学号
     * @param awardPublicId 学位授予批次id
     * @param id 申请单id
     * @return
     */
    def show(String studentId, Long id) {
        def form = degreeApplicationFormService.getFormForShow(studentId, id)
        renderJson ([
                        form: form,
                        fileNames: degreeApplicationFormService.findFiles(studentId, form.awardId)
        ])
    }

    /**
     * 更新数据
     */
    def update(String studentId, Long awardPublicId, Long id) {
        def cmd = new ApplicationFormCommand()
        bindData(cmd, request.JSON)
        cmd.id = id
        degreeApplicationFormService.update(studentId, cmd)
        renderOk()
    }

    /**
     * 创建
     */
    def create(String studentId, Long awardId) {
        renderJson degreeApplicationFormService.getFormForCreate(studentId, awardId)
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
