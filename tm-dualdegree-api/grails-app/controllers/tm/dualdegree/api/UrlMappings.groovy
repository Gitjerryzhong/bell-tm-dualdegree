package tm.dualdegree.api

class UrlMappings {

    static mappings = {

        "/agreements"(resources: 'agreement')
        "/agreement-views"(resources: 'agreementView', includes: ['index'])
        "/settings"(resources: 'setting')
        "/students"(resources: 'studentAbroad')

        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
