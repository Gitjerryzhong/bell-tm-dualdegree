package cn.edu.bnuz.bell.dualdegree

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 协议查看
 */
@PreAuthorize('hasAuthority("PERM_DUALDEGREE_DEPT_ADMIN")')
class AgreementPublicDeptController {

    def index() { }
}
