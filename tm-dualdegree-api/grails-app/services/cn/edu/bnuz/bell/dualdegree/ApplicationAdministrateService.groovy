package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.http.ForbiddenException
import grails.gorm.transactions.Transactional

@Transactional
class ApplicationAdministrateService {
    ApplicationFormService applicationFormService
    PaperFormService paperFormService

    def list(String departmentId, Long awardId) {
        DegreeApplication.executeQuery '''
select new map(
  form.id as id,
  student.id as studentId,
  student.name as studentName,
  student.sex as sex,
  adminClass.name as className,
  form.dateSubmitted as date,
  paperApprover.name as paperApprover,
  form.status as status
)
from DegreeApplication form
join form.student student
join student.adminClass adminClass
join form.award award
join award.department department
left join form.paperApprover paperApprover
where department.id = :departmentId and award.id = :awardId
order by form.dateSubmitted
''',[departmentId: departmentId, awardId: awardId]
    }

    def getFormForReview(String departmentId, Long awardId, Long id) {
        def form = applicationFormService.getFormInfo(id)
        if (!form || form.awardId !=awardId) {
            throw new ForbiddenException()
        }

        return [
                form     : form,
                fileNames: applicationFormService.findFiles(form.studentId, form.awardId),
                paperForm: paperFormService.getPaperForm(form.studentId, id)
        ]
    }
}
