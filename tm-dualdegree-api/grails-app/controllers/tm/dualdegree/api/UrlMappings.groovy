package tm.dualdegree.api

class UrlMappings {

    static mappings = {

        "/agreements"(resources: 'agreement')
        "/agreement-public-depts"(resources: 'agreementPublicDept', includes: ['index'])
        "/settings"(resources: 'setting')
        "/students"(resources: 'studentAbroad')
        "/awards"(resources: 'dualDegreeAward')

        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
