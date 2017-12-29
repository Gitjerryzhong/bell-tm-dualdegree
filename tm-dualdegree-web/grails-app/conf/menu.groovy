menuGroup 'main', {
    dualDegreeAdmin 60,{
        agreementAdmin      10, 'PERM_DUALDEGREE_AGREEMENT_WRITE',  '/web/dualdegree/agreements'
        setting             20, 'PERM_DUALDEGREE_ADMIN', '/web/dualdegree/settings'
        studentAbroadAdmin  30, 'PERM_DUALDEGREE_DEPT_ADMIN', '/web/dualdegree/studentAbroads'
        agreementPublicDept 40, 'PERM_DUALDEGREE_DEPT_ADMIN', '/web/dualdegree/agreement-public-depts'
        award               50, 'PERM_DUALDEGREE_DEPT_ADMIN', '/web/dualdegree/awards'
        agreementPublicDept 60, 'PERM_DUALDEGREE_AGREEMENT_READ', '/web/dualdegree/agreement-publics'
    }
}
