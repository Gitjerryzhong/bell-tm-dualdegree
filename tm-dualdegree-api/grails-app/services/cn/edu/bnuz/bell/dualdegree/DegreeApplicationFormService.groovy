package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.organization.Student
import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import cn.edu.bnuz.bell.workflow.commands.SubmitCommand
import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Value

import java.time.LocalDate

@Transactional
class DegreeApplicationFormService {
    @Value('${bell.student.filesPath}')
    String filesPath
    DomainStateMachineHandler domainStateMachineHandler

    /**
     * 保存本人在指定学位授予批次的申请单
     * @param userId 申请人ID
     * @param awardId 学位授予工作ID
     * @return 持久化的表单
     */
    def create(String userId, Long awardId, ApplicationFormCommand cmd) {
        if (getFormInfo(userId, awardId)) {
            //同一批每人只能一个申请
            throw new ForbiddenException()
        }
        def now = new Date()

        DegreeApplication form = new DegreeApplication(
                award: Award.load(awardId),
                student: Student.load(userId),
                dateCreated: LocalDate.now(),
                universityCooperative: cmd.universityCooperative,
                majorCooperative: cmd.majorCooperative,
                email: cmd.email,
                linkman: cmd.linkman,
                phone: cmd.phone,
                dateModified: now,
                status: domainStateMachineHandler.initialState
        )

        if (!form.save()) {
            form.errors.each {
                println it
            }
        }
        domainStateMachineHandler.create(form, userId)
        return form
    }

    /**
     * 保存本人在指定学位授予批次的申请单
     * @param userId 申请人ID
     * @param awardId 学位授予工作ID
     * @return 持久化的表单
     */
    def update(String userId, Long awardId, ApplicationFormCommand cmd) {
        DegreeApplication form = DegreeApplication.load(cmd.id)
        Award award = Award.get(awardId)
        Student student = Student.get(userId)
        if (form.award != award || form.student != student) {
            //无权更新
            throw new ForbiddenException()
        }
        def now = new Date()

        form.setUniversityCooperative(cmd.universityCooperative)
        form.setMajorCooperative(cmd.majorCooperative)
        form.setEmail(cmd.email)
        form.setLinkman(cmd.linkman)
        form.setPhone(cmd.phone)
        form.setDateModified(now)

        if (!form.save()) {
            form.errors.each {
                println it
            }
        }
        domainStateMachineHandler.create(form, userId)
        return form
    }

    /**
     * 获取本人在指定学位授予批次的申请单用于显示
     * @param userId 申请人ID
     * @param awardId 学位授予工作ID
     * @return 申请信息
     */
    Map getFormInfo(String userId, Long awardId) {
        def results = DegreeApplication.executeQuery'''
select new map(
  form.id as id,
  award.id as awardId,
  award.requestEnd as requestEnd,
  award.paperEnd as paperEnd,
  award.approvalEnd as approvalEnd,
  student.id as studentId,
  student.name as studentName,
  form.phone as phone,
  form.linkman as linkman,
  form.email as email,
  form.universityCooperative as universityCooperative,
  form.majorCooperative as majorCooperative,
  form.dateCreated as dateCreated,
  form.dateModified as dateModified,
  form.dateSubmitted as dateSubmitted,
  checker.name as checker,
  form.dateChecked as dateChecked,
  approver.name as approver,
  form.dateApproved as dateApproved,
  form.status as status,
  form.workflowInstance.id as workflowInstanceId
)
from DegreeApplication form
join form.award award
join form.student student
left join form.checker checker
left join form.approver approver
where award.id = :awardId and student.id = :userId
''', [awardId: awardId, userId: userId]
        if (!results) {
            return null
        }

        return results[0]
    }

    /**
     * 获取申请单信息，用于显示
     * @param userId 申请人ID
     * @param awardId 学位授予工作ID
     * @return 申请信息
     */
    def getFormForShow (String userId, Long awardId) {
        def form = getFormInfo(userId, awardId)
        if (!form) {
            throw new NotFoundException()
        }
        // 防止超期提交
        form.editable = form.requestEnd >= LocalDate.now() &&
                domainStateMachineHandler.canUpdate(form)

        return form
    }

    Map getFormForCreate(String userId, Long awardId) {
        Award award = Award.get(awardId)
        Student student = Student.get(userId)
        //15级是分水岭，以前的采用CooperativeUniversity，后面的采用协议中的合作大学
        def universities
        if (student.major.grade <= 2015) {
            universities = getCooperativeUniversity(student.department.id)
        } else {
            universities = getCooperativeUniversity(student)
        }
        return [
                form: [],
                timeNode: [
                        requestBegin: award.requestBegin,
                        requestEnd: award.requestEnd,
                        paperEnd: award.paperEnd,
                        approvalEnd: award.approvalEnd
                ],
                universities: universities,
                fileNames: findFiles(awardId, userId)
        ]
    }

    def getFormForEdit(String userId, Long awardId) {
        Map vm = getFormForCreate(userId, awardId)
        vm.form = getFormForShow(userId, awardId)

        return vm
    }

    def submit(String userId, SubmitCommand cmd) {
        DegreeApplication form = DegreeApplication.get(cmd.id)

        if (!form) {
            throw new NotFoundException()
        }

        if (form.student.id != userId) {
            throw new ForbiddenException()
        }

        if (!domainStateMachineHandler.canSubmit(form)) {
            throw new BadRequestException()
        }

        domainStateMachineHandler.submit(form, userId, cmd.to, cmd.comment, cmd.title)

        form.dateSubmitted = new Date()
        form.save()
    }

    private List<String> getCooperativeUniversity(String departmentId) {
        CooperativeUniversity.executeQuery'''
select new map(c.name as universityEn)
from CooperativeUniversity c
where c.department.id = :departmentId
''', [departmentId: departmentId]
    }

    private List<Map<String, String>> getCooperativeUniversity(Student student) {
        AgreementMajor.executeQuery'''
select new map(
    ag.universityEn as universityEn,
    ag.universityCn as universityCn
)
from AgreementMajor agmj 
join agmj.agreement ag
join ag.region agRegion,
StudentAbroad sa join sa.agreementRegion saRegion join sa.student student
where agRegion = saRegion and student.id = :studentId and student.major = agmj.major
''', [studentId: student.id]
    }

    private Map<String, String> findFiles(Long awardId, String studentId) {
        File dir = new File("${filesPath}/${awardId}/${studentId}")
        if (!dir.exists()) {
            return []
        }
        Map<String, String> fileNames = [:]
        for (File file: dir.listFiles()) {
            def index = file.name.indexOf('_')
            if (index == -1) {
                continue
            }
            String key = file.name.substring(0, index)
            fileNames[key] = file.name
        }
        return fileNames
    }
}
