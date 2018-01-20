package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.security.User
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.ReviewerProvider
import grails.gorm.transactions.Transactional

@Transactional(readOnly = true)
class ApplicationReviewerService implements ReviewerProvider{
    List<Map> getReviewers(Object id, String activity) {
        switch (activity) {
            case Activities.CHECK:
                return getCheckers(id as Long)
            case Activities.APPROVE:
                return getApprovers()
            default:
                throw new BadRequestException()
        }
    }

    List<Map> getCheckers(Long id) {
        DegreeApplication.executeQuery'''
select new map(c.id as id, c.name as name)
from DegreeApplication da 
join da.checker c
where da.id = :id
''', [id: id]
    }

    List<Map> getApprovers() {

    }

}
