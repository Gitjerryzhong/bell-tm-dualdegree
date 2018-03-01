package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import grails.gorm.transactions.Transactional

import javax.swing.text.BadLocationException

@Transactional
class PaperFormService {
    DomainStateMachineHandler domainStateMachineHandler

    def getPaperForm(String studentId, Long applicationFormId) {
        def applicationForm = DegreeApplication.load(applicationFormId)
        if (!applicationForm || applicationForm.student.id != studentId) {
            throw new BadLocationException()
        }

        return applicationForm.paperForm ?: []
    }

    def create(String studentId, Long applicationFormId, PaperFormCommand cmd) {
        def applicationForm = DegreeApplication.load(applicationFormId)
        if (!applicationForm) {
            throw new BadLocationException()
        }
        PaperForm form = new PaperForm(
                name: cmd.name,
                type: cmd.type,
                chineseTitle: cmd.chineseTitle,
                englishTitle: cmd.englishTitle,
                form: applicationForm
        )
        form.save()
        applicationForm.setPaperForm(form)
        applicationForm.setDatePaperSubmitted(new Date())
        applicationForm.save()

        //如果已指定导师则发给导师审核，否则发给管理员
        def toUser = applicationForm.paperApprover ? applicationForm.paperApprover.id : applicationForm.approver.id
        domainStateMachineHandler.accept(applicationForm, studentId, toUser)
        return form
    }
}
