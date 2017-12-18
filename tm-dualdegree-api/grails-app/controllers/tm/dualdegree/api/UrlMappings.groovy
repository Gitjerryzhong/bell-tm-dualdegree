package tm.dualdegree.api

class UrlMappings {

    static mappings = {

        "/agreements"(resources:'agreement')
        "/settings"(resources:'setting')
        "/students"(resources: 'studentAbroad')

        "/"(controller: 'application', action:'index')
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
