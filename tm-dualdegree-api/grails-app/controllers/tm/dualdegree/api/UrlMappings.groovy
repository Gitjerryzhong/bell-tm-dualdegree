package tm.dualdegree.api

class UrlMappings {

    static mappings = {

        "/agreements"(resources: 'agreement')
        "/agreement-publics"(resources: 'agreementPublic', includes: ['index', 'show'])
        "/settings"(resources: 'setting')

        "/departments"(resources: 'department', includes: []) {
            "/students"(resources: 'studentAbroad')
            "/awards"(resources: 'award')
            "/agreements"(controller: 'agreementPublic', action: 'agreementsOfDept', method: 'GET')
        }

        "/students"(resources: 'student', includes: []) {
            "/awards"(resources: 'awardPublic', ['show'])
            "/applications"(resources: 'applicationForm') {
                "/checkers"(controller: 'applicationForm', action: 'checkers', method: 'GET')
            }
        }

        "/teachers"(resources: 'teacher', includes: []) {
            "/applications"(resources: 'applicationCheck', includes: ['index', 'show']) {
                "/workitems"(resources: 'applicationCheck', includes: ['show', 'patch'])
            }
        }

        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
