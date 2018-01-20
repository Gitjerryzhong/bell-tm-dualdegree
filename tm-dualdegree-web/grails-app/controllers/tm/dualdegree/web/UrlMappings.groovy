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

        "/students"(resources: 'student', includes: []) {
            "/applications"(resources: 'applicationForm', includes: ['index'])
        }

        "/teachers"(resources: 'teacher', includes: []) {
            "/applications"(resources: 'applicationCheck', includes: ['index'])
        }

        "/picture"(resource: 'picture', includes: ['show']) {
            collection {
                "/fileview"(action: 'fileView', method: 'GET')
                "/filesrc"(action: 'fileSource', method: 'GET')
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
