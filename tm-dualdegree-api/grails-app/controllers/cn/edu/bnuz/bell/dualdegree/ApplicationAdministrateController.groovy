package cn.edu.bnuz.bell.dualdegree

import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasAuthority("PERM_DUALDEGREE_DEPT_ADMIN")')
class ApplicationAdministrateController {
    ApplicationAdministrateService applicationAdministrateService

    def index(String departmentId, Long awardId) {
        renderJson applicationAdministrateService.list(departmentId, awardId)
    }

    def show(String departmentId, Long awardId, Long id) {
        renderJson applicationAdministrateService.getFormForReview(departmentId, awardId, id)
    }
}
