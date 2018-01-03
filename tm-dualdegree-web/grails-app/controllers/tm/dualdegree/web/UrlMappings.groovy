package tm.dualdegree.web

class UrlMappings {

    static mappings = {

        "/agreements"(resources: 'agreementForm', includes: ['index'])
        "/agreement-publics"(resources: 'agreementPublic', includes: ['index'])
        "/settings"(resources: 'setting', includes: ['index'])

        "/departments"(resources: 'department', includes: []) {
            "/students"(resources: 'studentAbroad', includes: ['index'])
            "/awards"(resources: 'award', includes: ['index'])
            "/agreements"(resources: 'agreementPublicDept', includes: ['index'])
        }

        "/students"(resource: 'student', includes: []) {
            "/degree-applications"(resource: 'applicationForm')
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
