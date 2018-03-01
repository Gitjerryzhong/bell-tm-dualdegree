package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.User
import cn.edu.bnuz.bell.service.DataAccessService
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import cn.edu.bnuz.bell.workflow.ListCommand
import cn.edu.bnuz.bell.workflow.ListType
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.WorkflowActivity
import cn.edu.bnuz.bell.workflow.WorkflowInstance
import cn.edu.bnuz.bell.workflow.Workitem
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import grails.gorm.transactions.Transactional

@Transactional
class PaperApprovalService {
    DataAccessService dataAccessService
    DomainStateMachineHandler domainStateMachineHandler
    ApplicationFormService applicationFormService

    def list(String userId, ListCommand cmd) {
        switch (cmd.type) {
            case ListType.TOBE:
                return [forms: findTobeList(userId, cmd.args), counts: getCounts(userId)]
            case ListType.NEXT:
                return [forms: findNextList(userId, cmd.args), counts: getCounts(userId)]
            default:
                throw new BadRequestException()
        }
    }

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
join form.paperApprover paperApprover
where paperApprover.id = :teacherId
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
join form.paperApprover paperApprover
where paperApprover.id = :teacherId
and form.datePaperApproved is not null
and form.status = :status
order by form.datePaperApproved desc
''',[teacherId: teacherId, status: State.FINISHED], args
    }

    def countTobeList(String teacherId) {
        dataAccessService.getLong '''
select count(*)
from DegreeApplication form join form.award award join form.paperApprover paperApprover
where current_date between award.requestBegin and award.approvalEnd
and form.status = :status
and paperApprover.id = :teacherId
''', [teacherId: teacherId, status: State.PROGRESS]
    }

    def countNextList(String teacherId) {
        dataAccessService.getLong '''
select count(*)
from DegreeApplication form join form.award award join form.paperApprover paperApprover
where current_date between award.requestBegin and award.approvalEnd
and form.status = :status
and paperApprover.id = :teacherId
''', [teacherId: teacherId, status: State.FINISHED]
    }

    def getCounts(String teacherId) {
        [
            (ListType.TOBE): countTobeList(teacherId),
            (ListType.NEXT): countNextList(teacherId),
        ]
    }

    def getFormForReview(String teacherId, Long id, ListType type) {
        def form = applicationFormService.getFormInfo(id)

        def workitem = Workitem.findByInstanceAndActivityAndToAndDateProcessedIsNull(
                WorkflowInstance.load(form.workflowInstanceId),
                WorkflowActivity.load("${DegreeApplication.WORKFLOW_ID}.process"),
                User.load(teacherId),
        )
        if (form.paperApproverId != teacherId) {
            throw new BadRequestException()
        }

        return [
                form               : form,
                counts             : getCounts(teacherId),
                workitemId         : workitem ? workitem.id : null,
                settings           : Award.get(form.awardId),
                fileNames          : applicationFormService.findFiles(form.studentId, form.awardId),
                prevId             : getPrevReviewId(teacherId, id, type),
                nextId             : getNextReviewId(teacherId, id, type),
        ]
    }

    def getFormForReview(String teacherId, Long id, ListType type, UUID workitemId) {
        def form = applicationFormService.getFormInfo(id)

        if (form.paperApproverId != teacherId) {
            throw new BadRequestException()
        }

        return [
                form               : form,
                counts             : getCounts(teacherId),
                workitemId         : workitemId,
                settings           : Award.get(form.awardId),
                fileNames          : applicationFormService.findFiles(form.studentId, form.awardId),
                prevId             : getPrevReviewId(teacherId, id, type),
                nextId             : getNextReviewId(teacherId, id, type),
        ]
    }

    private Long getPrevReviewId(String teacherId, Long id, ListType type) {
        switch (type) {
            case ListType.TOBE:
                return dataAccessService.getLong('''
select form.id
from DegreeApplication form join form.award award
where current_date between award.requestBegin and award.approvalEnd
and form.status = :status
and form.paperApprover.id = :teacherId
and form.datePaperSubmitted < (select datePaperSubmitted from DegreeApplication where id = :id)
order by form.datePaperSubmitted desc
''', [teacherId: teacherId, id: id, status: State.PROGRESS])
            case ListType.NEXT:
                return dataAccessService.getLong('''
select form.id
from DegreeApplication form
where form.paperApprover.id = :teacherId
and form.datePaperApproved is not null
and form.status <> :status
and form.datePaperSubmitted < (select datePaperSubmitted from DegreeApplication where id = :id)
order by form.datePaperSubmitted desc
''', [teacherId: teacherId, id: id, status: State.PROGRESS])
        }
    }

    private Long getNextReviewId(String teacherId, Long id, ListType type) {
        switch (type) {
            case ListType.TOBE:
                return dataAccessService.getLong('''
select form.id
from DegreeApplication form join form.award award
where current_date between award.requestBegin and award.approvalEnd
and form.status = :status
and form.paperApprover.id = :teacherId
and form.datePaperApproved > (select datePaperApproved from DegreeApplication where id = :id)
order by form.datePaperApproved asc
''', [teacherId: teacherId, id: id, status: State.PROGRESS])
            case ListType.NEXT:
                return dataAccessService.getLong('''
select form.id
from DegreeApplication form
where form.paperApprover.id = :teacherId
and form.datePaperApproved is not null
and form.status <> :status
and form.datePaperApproved < (select datePaperApproved from DegreeApplication where id = :id)
order by form.datePaperApproved desc
''', [teacherId: teacherId, id: id, status: State.PROGRESS])
        }
    }

    void accept(String userId, AcceptCommand cmd, UUID workitemId) {
        DegreeApplication form = DegreeApplication.get(cmd.id)
        if (form.paperApprover != userId) {
            throw new BadRequestException()
        }
        domainStateMachineHandler.accept(form, userId, 'process', cmd.comment, workitemId, form.student.id)
        form.datePaperApproved = new Date()
        form.save()
    }

    void reject(String userId, RejectCommand cmd, UUID workitemId) {
        DegreeApplication form = DegreeApplication.get(cmd.id)
        if (form.paperApprover != userId) {
            throw new BadRequestException()
        }
        domainStateMachineHandler.reject(form, userId, 'process', cmd.comment, workitemId)
        form.datePaperApproved = new Date()
        form.save()
    }
}
