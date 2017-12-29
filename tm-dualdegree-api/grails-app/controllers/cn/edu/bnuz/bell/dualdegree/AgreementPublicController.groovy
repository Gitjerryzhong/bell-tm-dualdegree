package cn.edu.bnuz.bell.dualdegree

import org.springframework.security.access.prepost.PreAuthorize

/**
 * 协议查看--教务处
 */
@PreAuthorize('hasAuthority("PERM_DUALDEGREE_AGREEMENT_READ")')
class AgreementPublicController {
    AgreementService agreementService

    def index() {
        renderJson(agreementService.list())
    }

    def show(Long id) {
        renderJson(agreementService.getFormForShow(id))
    }

}
