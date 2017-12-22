package tm.dualdegree.web

class UrlMappings {

    static mappings = {

        "/agreements"(resources: 'agreementForm', includes: ['index'])
        "/agreement-public-depts"(resources: 'agreementPublicDept', includes: ['index'])

        "/settings"(resources: 'setting', includes: ['index'])

        "/studentAbroads"(resources: 'studentAbroadAdmin', includes: ['index'])
        "/awards"(resources: 'dualDegreeAward', includes: ['index'])

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
