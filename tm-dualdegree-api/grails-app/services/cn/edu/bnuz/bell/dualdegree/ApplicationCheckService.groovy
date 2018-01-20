package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.User
import cn.edu.bnuz.bell.service.DataAccessService
import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import cn.edu.bnuz.bell.workflow.ListCommand
import cn.edu.bnuz.bell.workflow.ListType
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.WorkflowActivity
import cn.edu.bnuz.bell.workflow.WorkflowInstance
import cn.edu.bnuz.bell.workflow.Workitem
import grails.gorm.transactions.Transactional

@Transactional
class ApplicationCheckService {
    DataAccessService dataAccessService
    DomainStateMachineHandler domainStateMachineHandler
    ApplicationFormService applicationFormService

    def list(String userId, ListCommand cmd) {
        switch (cmd.type) {
            case ListType.TODO:
                return findTodoList(userId, cmd.args)
            case ListType.EXPR:
                return findExprList(userId, cmd.args)
            case ListType.DONE:
                return findDoneList(userId, cmd.args)
            default:
                throw new BadRequestException()
        }
    }

    def findTodoList(String teacherId, Map args) {
        def forms = DegreeApplication.executeQuery '''
select new map(
  form.id as id,
  student.id as studentId,
  student.name as studentName,
  student.sex as sex,
  adminClass.name as className,
  form.dateSubmitted as date,
  form.status as status
)
from DegreeApplication form
join form.student student
join student.adminClass adminClass
join form.award award
where form.checker.id = :teacherId
and current_date between award.requestBegin and award.approvalEnd
and form.status = :status
order by form.dateSubmitted
''',[teacherId: teacherId, status: State.SUBMITTED], args

        return [forms: forms, counts: getCounts(teacherId)]
    }

    def findExprList(String teacherId, Map args) {
        def forms = DegreeApplication.executeQuery '''
select new map(
  form.id as id,
  student.id as studentId,
  student.name as studentName,
  student.sex as sex,
  adminClass.name as className,
  form.dateSubmitted as date,
  form.status as status
)
from DegreeApplication form
join form.student student
join student.adminClass adminClass
join form.award award
where form.checker.id = :teacherId
and current_date not between award.requestBegin and award.approvalEnd
and form.status = :status
order by form.dateSubmitted
''',[teacherId: teacherId, status: State.SUBMITTED], args

        return [forms: forms, counts: getCounts(teacherId)]
    }

    def findDoneList(String teacherId, Map args) {
        def forms = DegreeApplication.executeQuery '''
select new map(
  form.id as id,
  student.id as studentId,
  student.name as studentName,
  student.sex as sex,
  adminClass.name as className,
  form.dateSubmitted as date,
  form.status as status
)
from DegreeApplication form
join form.student student
join student.adminClass adminClass
where form.checker.id = :teacherId
and form.dateChecked is not null
and form.status <> :status
order by form.dateChecked desc
''',[teacherId: teacherId, status: State.SUBMITTED], args

        return [forms: forms, counts: getCounts(teacherId)]
    }

    def countTodoList(String teacherId) {
        dataAccessService.getLong '''
select count(*)
from DegreeApplication form join form.award award
where current_date between award.requestBegin and award.approvalEnd
and form.status = :status
and form.checker.id = :teacherId
''', [teacherId: teacherId, status: State.SUBMITTED]
    }

    def countExprList(String teacherId) {
        dataAccessService.getLong '''
select count(*)
from DegreeApplication form join form.award award
where current_date not between award.requestBegin and award.approvalEnd
and form.status = :status
and form.checker.id = :teacherId
''', [teacherId: teacherId, status: State.SUBMITTED]
    }

    def getCounts(String teacherId) {
        def teacher = Teacher.load(teacherId)
        def todo = countTodoList(teacherId)
        def expr = countExprList(teacherId)
        def done = DegreeApplication.countByCheckerAndStatusNotEqualAndDateCheckedIsNotNull(teacher, State.SUBMITTED)

        [
                (ListType.TODO): todo,
                (ListType.EXPR): expr,
                (ListType.DONE): done,
        ]
    }

    def getFormForReview(String teacherId, Long id, ListType type, String activity) {
        def form = applicationFormService.getFormInfo(id)

        def workitem = Workitem.findByInstanceAndActivityAndToAndDateProcessedIsNull(
                WorkflowInstance.load(form.workflowInstanceId),
                WorkflowActivity.load("${DegreeApplication.WORKFLOW_ID}.${activity}"),
                User.load(teacherId),
        )
        domainStateMachineHandler.checkReviewer(id, teacherId, activity)

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

        def activity = Workitem.get(workitemId).activitySuffix
        domainStateMachineHandler.checkReviewer(id, teacherId, activity)

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
            case ListType.TODO:
                return dataAccessService.getLong('''
select form.id
from DegreeApplication form join form.award award
where current_date between award.requestBegin and award.approvalEnd
and form.status = :status
and form.checker.id = :teacherId
and form.dateSubmitted < (select dateSubmitted from DegreeApplication where id = :id)
order by form.dateSubmitted desc
''', [teacherId: teacherId, id: id, status: State.SUBMITTED])
            case ListType.EXPR:
                return dataAccessService.getLong('''
select form.id
from DegreeApplication form join form.award award
where current_date not between award.requestBegin and award.approvalEnd
and form.checker.id = :teacherId
and form.status = :status
and form.dateSubmitted < (select dateSubmitted from DegreeApplication where id = :id)
order by form.dateSubmitted desc
''', [teacherId: teacherId, id: id, status: State.SUBMITTED])
            case ListType.DONE:
                return dataAccessService.getLong('''
select form.id
from DegreeApplication form
where form.checker.id = :teacherId
and form.dateChecked is not null
and form.status <> :status
and form.dateChecked > (select dateChecked from DegreeApplication where id = :id)
order by form.dateChecked asc
''', [teacherId: teacherId, id: id, status: State.SUBMITTED])
        }
    }

    private Long getNextReviewId(String teacherId, Long id, ListType type) {
        switch (type) {
            case ListType.TODO:
                return dataAccessService.getLong('''
select form.id
from DegreeApplication form join form.award award
where current_date between award.requestBegin and award.approvalEnd
and form.status = :status
and form.checker.id = :teacherId
and form.dateSubmitted > (select dateSubmitted from DegreeApplication where id = :id)
order by form.dateSubmitted asc
''', [teacherId: teacherId, id: id, status: State.SUBMITTED])
            case ListType.EXPR:
                return dataAccessService.getLong('''
select form.id
from DegreeApplication form join form.award award
where current_date not between award.requestBegin and award.approvalEnd
and form.checker.id = :teacherId
and form.status = :status
and form.dateSubmitted > (select dateSubmitted from DegreeApplication where id = :id)
order by form.dateSubmitted asc
''', [teacherId: teacherId, id: id, status: State.SUBMITTED])
            case ListType.DONE:
                return dataAccessService.getLong('''
select form.id
from DegreeApplication form
where form.checker.id = :teacherId
and form.dateChecked is not null
and form.status <> :status
and form.dateChecked < (select dateChecked from DegreeApplication where id = :id)
order by form.dateChecked desc
''', [teacherId: teacherId, id: id, status: State.SUBMITTED])
        }
    }
}
