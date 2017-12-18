package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.master.Major
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import grails.gorm.transactions.Transactional

@Transactional
class StudentAbroadService {
    SecurityService securityService
    StudentValidateService studentValidateService

    /**
     * 出国学生列表
     */
    def list(StudentOptionsCommand cmd) {
        def result = StudentAbroad.executeQuery'''
select new map(
sa.id as id,
g.name as groupName,
g.id as groupId,
st.id as studentId,
st.name as studentName,
st.sex as sex,
st.atSchool as atSchool,
d.name as departmentName,
mj.grade as grade,
sj.name as subjectName,
sj.id as subjectId,
ac.name as adminClassName
)
from StudentAbroad sa 
join sa.student st 
join sa.agreementGroup g
join st.department d 
join st.major mj 
join mj.subject sj 
join st.adminClass ac
where d.id in (:departments) and sa.enabled is true
''',[departments: studentValidateService.deptAdmins]
        return result.grep{
            (cmd.sujectId ? cmd.sujectId == it.subjectId : true) &&
                    (cmd.grade ? cmd.grade == it.grade : true) &&
                    (cmd.groupId ? cmd.groupId == it.groupId : true) &&
                    (cmd.studentName ? cmd.studentName == it.studentName : true) &&
                    (cmd.studentId ? cmd.studentId == it.studentId : true)
        }
    }

    /**
     * 保存
     */
    def create(StudentAbroadCommand cmd) {
        def validate = studentValidateService.validate(cmd)
        if (validate.error) {
            return validate.error
        }
        def students = validate.students
        def studentsPrint = validate.studentsPrint
        def me = Teacher.load(securityService.userId)
        def group = AgreementGroup.load(cmd.groupId)
        StudentAbroad.executeUpdate'''
    insert into StudentAbroad (student, operator, addedDate, agreementGroup, enabled)
    select st, :user, now(), :agreementGroup, true from Student st where st.id in (:ids) 
''',[user: me, agreementGroup: group, ids: students]
//      写入自助打印系统
        if (studentsPrint && studentsPrint.size()) {
            StudentPrint.executeUpdate'''
    insert into StudentPrint (studentId, studentName, dateAdded, operatorAdded, inProject, type)
    select st.id, st.name, now(), :userId, 1, :agreementGroup from Student st where st.id in (:ids) 
''',[userId: me.id, agreementGroup: group.name, ids: studentsPrint]
        }
        return null
    }

    /**
     * 删除
     * @param id
     * @return
     */
    def delete(Long id) {
        def form = StudentAbroad.get(id)
        if (form) {
            form.delete()
        }
    }

    def getAgreementGroups() {
        AgreementGroup.executeQuery'''
select new map(g.id as id, g.name as name) 
from AgreementGroup g
'''
    }

    def getSubjects() {
        Major.executeQuery'''
select distinct new map(
sj.id as id,
sj.name as name
)
from Major mj join mj.subject sj
where mj.department.id in (:departments) and sj.isDualDegree is true
order by sj.name
''',[departments: studentValidateService.deptAdmins]
    }

    def getGrades() {
        Major.executeQuery'''
select distinct mj.grade
from Major mj join mj.subject sj
where mj.department.id in (:departments) and sj.isDualDegree is true
order by mj.grade
''',[departments: studentValidateService.deptAdmins]
    }
}
