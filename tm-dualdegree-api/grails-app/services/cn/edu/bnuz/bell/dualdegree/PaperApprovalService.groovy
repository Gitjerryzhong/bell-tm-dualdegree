package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.service.DataAccessService
import cn.edu.bnuz.bell.workflow.State
import grails.gorm.transactions.Transactional

@Transactional
class PaperApprovalService {
    DataAccessService dataAccessService

    def findTobeList(String teacherId, Map args) {
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
left join form.paperApprover paperApprover
where form.approver.id = :teacherId
and current_date between award.requestBegin and award.approvalEnd
and form.status = :status
order by form.dateSubmitted
''',[teacherId: teacherId, status: State.PROGRESS], args
    }

    def findNextList(String teacherId, Map args) {
        DegreeApplication.executeQuery '''
select new map(
  form.id as id,
  student.id as studentId,
  student.name as studentName,
  student.sex as sex,
  adminClass.name as className,
  paperApprover.name as paperApprover,
  form.dateSubmitted as date,
  form.status as status
)
from DegreeApplication form
join form.student student
join student.adminClass adminClass
left join form.paperApprover paperApprover
where form.approver.id = :teacherId
and form.datePaperApproved is not null
and form.status = :status
order by form.datePaperApproved desc
''',[teacherId: teacherId, status: State.FINISHED], args
    }

    def countTobeList(String teacherId) {
        dataAccessService.getLong '''
select count(*)
from DegreeApplication form join form.award award
where current_date between award.requestBegin and award.approvalEnd
and form.status = :status
and (form.approver.id = :teacherId or form.paperApprover.id = :teacherId)
''', [teacherId: teacherId, status: State.PROGRESS]
    }

    def countNextList(String teacherId) {
        dataAccessService.getLong '''
select count(*)
from DegreeApplication form join form.award award
where current_date between award.requestBegin and award.approvalEnd
and form.status = :status
and (form.approver.id = :teacherId or form.paperApprover.id = :teacherId)
''', [teacherId: teacherId, status: State.FINISHED]
    }
}
