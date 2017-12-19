package cn.edu.bnuz.bell.dualdegree

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 协议查看
 */
@PreAuthorize('hasAuthority("PERM_AGREEMENT_READ")')
class AgreementViewController {

    def index() { }
}
