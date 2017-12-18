package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.organization.Student
import cn.edu.bnuz.bell.security.SecurityService
import grails.gorm.transactions.Transactional

@Transactional
class StudentValidateService {
    SecurityService securityService
    def messageSource

    def validate(StudentAbroadCommand cmd) {
        def error = new ArrayList<String>()
        def ids = cmd.studentIds.split("\n")
        def students = findStudents(ids)

//      检查是否存在越权导入
        if (ids.length != students.size()) {
            def ids_invalid = (ids as String[]) - students
            error.add("${messageSource.getMessage('error.department_check_failed', null, Locale.CHINA)}: ${ids_invalid.toArrayString()}")
            return [error: error]
        }

//      检查是否存在重复导入
        def duplicates = hasDuplicates(ids)
        if (duplicates) {
            error.add("${messageSource.getMessage('error.exist_duplicates', null, Locale.CHINA)}: ${duplicates.toListString()}")
            return [error: error]
        }

//      检查学生参加的项目是否有协议支持
        def groupMatchIds = groupMatch(ids, cmd.groupId)
        if (ids.length != groupMatchIds.size()) {
            def ids_invalid = (ids as String[]) - groupMatchIds
            error.add("${messageSource.getMessage('error.group_check_failed', null, Locale.CHINA)}: ${ids_invalid.toArrayString()}")
            return [error: error]
        }

//      去除已经导入成绩自助打印系统的学生
        def duplicatesInPrint = hasDuplicatesInPrint(ids)
        def studentsInPrint = students
        if (duplicatesInPrint) {
            studentsInPrint = students.grep{
                !(it in duplicatesInPrint)
            }
        }
        return [error: null, students: students, studentsPrint: studentsInPrint]
    }
    def getDeptAdmins() {
        DeptAdmin.executeQuery'''
select d.id from DeptAdmin da join da.department d 
where da.teacher.id = :userId
''',[userId: securityService.userId]
    }

    def findStudents(String[] ids) {
        Student.executeQuery'''
select st.id
from Student st join st.department d join st.major mj join mj.subject sj
where d.id in (:departments) and st.id in (:ids) and sj.isDualDegree is true
''',[departments: deptAdmins, ids: ids]
    }

    def hasDuplicates(String[] ids) {
        StudentAbroad.executeQuery'''
select st.student.id
from StudentAbroad st 
where st.student.id in (:ids)
''',[ids: ids]
    }

    def groupMatch(String[] ids, Long groupId) {
        Student.executeQuery'''
select distinct st.id
from Student st, Agreement agreement join agreement.item item 
where st.id in (:ids) and st.major.id = item.major.id and agreement.group.id = :groupId
''',[ids: ids, groupId: groupId]
    }

    def hasDuplicatesInPrint(String[] ids) {
        StudentPrint.executeQuery'''
select st.studentId
from StudentPrint st 
where st.studentId in (:ids)
''',[ids: ids]
    }
}
