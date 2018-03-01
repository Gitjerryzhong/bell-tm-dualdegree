package tm.dualdegree.api

class UrlMappings {

    static mappings = {

        "/agreements"(resources: 'agreement')
        "/agreement-publics"(resources: 'agreementPublic', includes: ['index', 'show'])
        "/settings"(resources: 'setting')

        "/departments"(resources: 'department', includes: []) {
            "/students"(resources: 'studentAbroad')
            "/awards"(resources: 'award')
            "/mentors"(resources: 'mentor')
            "/agreements"(controller: 'agreementPublic', action: 'agreementsOfDept', method: 'GET')
        }

        "/students"(resources: 'student', includes: []) {
            "/awards"(resources: 'awardPublic', ['show'])
            "/applications"(resources: 'applicationForm') {
                "/approvers"(controller: 'applicationForm', action: 'approvers', method: 'GET')
                "/papers"(resources: 'paperForm')
            }
        }

        "/approvers"(resources: 'approver', includes: []) {
            "/applications"(resources: 'applicationApproval', includes: ['index', 'show', 'update']) {
                "/workitems"(resources: 'applicationApproval', includes: ['show', 'patch'])
                collection {
                    "/mentors"(controller: 'mentor', action: 'index', method: 'GET')
                }
            }
            "/papers"(resources: 'paperApproval', includes: ['index', 'show', 'update']) {
                "/workitems"(resources: 'paperApproval', includes: ['show', 'patch'])
            }
        }

        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
