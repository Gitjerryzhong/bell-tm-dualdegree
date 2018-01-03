package cn.edu.bnuz.bell.dualdegree

import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasRole("ROLE_DUALDEGREE_STUDENT")')
class AwardPublicController {
    AwardService awardService

    def index(String studentId) {
        renderJson(awardService.list(studentId))
    }
}
