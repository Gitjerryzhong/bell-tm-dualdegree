menuGroup 'main', {
    dualDegreeAdmin 60,{
        agreementAdmin      10, 'PERM_DUALDEGREE_AGREEMENT_WRITE',  '/web/dualdegree/agreements'
        setting             20, 'PERM_DUALDEGREE_ADMIN', '/web/dualdegree/settings'
        studentAbroad       30, 'PERM_DUALDEGREE_DEPT_ADMIN', '/web/dualdegree/departments/${departmentId}/students'
        agreementPublicDept 31, 'PERM_DUALDEGREE_DEPT_ADMIN', '/web/dualdegree/departments/${departmentId}/agreements'
        award               32, 'PERM_DUALDEGREE_DEPT_ADMIN', '/web/dualdegree/departments/${departmentId}/awards'
        mentor              33, 'PERM_DUALDEGREE_DEPT_ADMIN', '/web/dualdegree/departments/${departmentId}/mentors'
        paperApproval       40, 'PERM_DUALDEGREE_PAPER_APPROVE', '/web/dualdegree/approvers/${userId}/papers'
        agreementPublic     60, 'PERM_DUALDEGREE_AGREEMENT_READ', '/web/dualdegree/agreement-publics'
        applicationApproval 70, 'PERM_DUALDEGREE_DEPT_ADMIN', '/web/dualdegree/approvers/${userId}/applications'
    }
    dualDegree 61,{
        application     10, 'PERM_DUALDEGREE_WRITE', '/web/dualdegree/students/${userId}/applications'
    }
}
