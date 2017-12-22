package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.organization.Department
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import grails.gorm.transactions.Transactional

@Transactional
class DualDegreeAwardService {
    SecurityService securityService

    def list() {
        DualDegreeAward.executeQuery'''
select new map(
    ba.id   as id,
    ba.title   as title,
    ba.requestBegin as requestBegin,
    ba.requestEnd as requestEnd,
    ba.paperEnd as paperEnd,
    ba.approvalEnd as approvalEnd,
    ba.creator as creator,
    ba.dateCreated as dateCreated,
    ba.department.region as departmentName
)
from DualDegreeAward ba, DeptAdministrator da join da.teacher t
where ba.department = da.department and da.teacher.id = :userId
''', [userId: securityService.userId]
    }

    /**
     * 保存
     */
    def create(AwardCommand cmd) {
        DualDegreeAward form = new DualDegreeAward(
                title: cmd.title,
                content: cmd.content,
                requestBegin: cmd.requestBeginToDate,
                requestEnd: cmd.requestEndToDate,
                paperEnd: cmd.paperEndToDate,
                approvalEnd: cmd.approvalEndToDate,
                creator: Teacher.load(securityService.userId),
                dateCreated: new Date(),
                department: Department.load(cmd.departmentId)
        )

        form.save()
        return form
    }
}
