package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.organization.Department
import cn.edu.bnuz.bell.organization.Teacher

import java.time.LocalDate

/**
 * 双学位授予工作
 */
class DualDegreeAward {
    String      title
    String      content
    LocalDate   requestBegin
    LocalDate   requestEnd
    LocalDate   paperEnd
    LocalDate   approvalEnd
    Teacher     creator
    Date        dateCreated
    Department  department

    static mapping = {
        comment                 '双学位授予工作'
        id                      generator: 'identity', comment: '工作ID'
        title                   length: 100, comment: '标题'
        content                 length: 1000,comment: '内容'
        requestBegin            comment: '申请起始'
        requestEnd              comment: '申请截止'
        paperEnd                comment: '论文提交截止'
        approvalEnd             comment: '审批工作结束'
        creator                 comment: '创建人'
        dateCreated             comment: '创建日期'
        department              comment: '适用学院'
    }
}
