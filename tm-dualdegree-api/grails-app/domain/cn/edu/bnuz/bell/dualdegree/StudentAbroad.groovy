package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.organization.Student
import cn.edu.bnuz.bell.organization.Teacher

/**
 * 出国学生
 */
class StudentAbroad {
    Student student
    Teacher operator
    Date addedDate
    AgreementGroup agreementGroup
    Boolean enabled

    static mapping = {
        comment '出国学生'
        table 'dual_degree_student_abroad'
        id generator: 'identity', comment: '无意义ID'
        student          comment: '学生'
        operator         comment: '导入操作的老师'
        addedDate        comment: '导入操作的日期'
        agreementGroup   comment: '参加的项目'
        enabled          comment: '是否有效'
    }

    static constraints = {
        group nullable: true
    }
}
