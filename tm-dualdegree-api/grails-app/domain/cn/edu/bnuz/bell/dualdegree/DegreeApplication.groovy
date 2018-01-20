package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.organization.Student
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.StateObject
import cn.edu.bnuz.bell.workflow.StateUserType
import cn.edu.bnuz.bell.workflow.WorkflowInstance

import java.time.LocalDate

/**
 * 学位申请
 */
class DegreeApplication implements StateObject {
    /**
     * 学位授予工作
     */
    Award award

    /**
     * 申请人
     */
    Student student

    /**
     * 联系人
     */
    String linkman

    /**
     * 联系人电话
     */
    String phone

    /**
     * 邮件
     */
    String email

    /**
     * 合作大学
     */
    String universityCooperative

    /**
     * 国外专业
     */
    String majorCooperative

    /**
     * 创建日期
     */
    LocalDate dateCreated

    /**
     * 状态
     */
    State status

    /**
     * 修改时间
     */
    Date dateModified

    /**
     * 提交时间
     */
    Date dateSubmitted

    /**
     * 审核人
     */
    Teacher checker

    /**
     * 审核时间
     */
    Date dateChecked

    /**
     * 论文导师，审批人
     */
    Teacher approver

    /**
     * 审批时间
     */
    Date dateApproved

    /**
     * 工作流实例
     */
    WorkflowInstance workflowInstance

    static mapping = {
        comment '出国学生学位申请'
        table                            schema: 'tm_dual'
        id                               generator: 'identity', comment: 'ID'
        award                            comment: '学位授予工作'
        student                          comment: '学生'
        linkman                          length: 20, comment: '导入操作的老师'
        phone                            length: 30, comment: '联系电话'
        email                            length: 50, comment: '邮件'
        universityCooperative            length: 100, comment: '合作大学'
        majorCooperative                 length: 100, comment: '国外专业'
        dateCreated                      comment: '填表的日期'
        status                           sqlType: 'tm.state', type: StateUserType, comment: '状态'
        dateCreated                      comment: '创建时间'
        dateModified                     comment: '修改时间'
        dateSubmitted                    comment: '提交时间'
        checker                          comment: '审核人'
        dateChecked                      comment: '审核时间'
        approver                         comment: '审批人，论文导师'
        dateApproved                     comment: '审批时间'
        workflowInstance                 comment: '工作流实例'
    }
    static constraints = {
        dateSubmitted    nullable: true
        checker          nullable: true
        dateChecked      nullable: true
        approver         nullable: true
        dateApproved     nullable: true
        workflowInstance nullable: true
    }

    String getWorkflowId() {
        WORKFLOW_ID
    }

    static final WORKFLOW_ID = 'degree.application'
}
