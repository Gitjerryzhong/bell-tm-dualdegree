package tm.dualdegree.web

class UrlMappings {

    static mappings = {

        "/agreements"(resources: 'agreementForm', includes: ['index'])
        "/agreement-views"(resources: 'agreementView', includes: ['index'])

        "/settings"(resources: 'setting', includes: ['index'])

        "/studentAbroads"(resources: 'studentAbroadAdmin', includes: ['index'])

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
