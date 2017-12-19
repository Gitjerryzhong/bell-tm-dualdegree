package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 协议查看
 */
@PreAuthorize('hasAuthority("PERM_AGREEMENT_READ")')
class AgreementViewController {
    AgreementService agreementService
    SecurityService securityService

    def index() {
        def agreements = agreementService.findAgreementsByDepartment(securityService.departmentId)
        renderJson(agreements)
    }
}
