package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.report.ReportClientService
import cn.edu.bnuz.bell.report.ReportRequest
import cn.edu.bnuz.bell.report.ReportResponse
import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_DUALDEGREE_DEPT_ADMIN")')
class ApplicationReportController {
    ReportClientService reportClientService
    SecurityService securityService

    def index() { }

    def show(Integer awardId, Integer applicationId) {
        def parameters = [department_id: securityService.departmentId, award_id: awardId, myid: applicationId]
        def reportName = 'dualdegree-paper-approval'
        if (!applicationId) {
            parameters = [department_id: securityService.departmentId, award_id: awardId]
            reportName = 'dualdegree-paper-approval-all'
        }
        report(new ReportRequest(
                reportService: 'tm-report',
                reportName: reportName,
                format: 'pdf',
                parameters: parameters
        ))

    }

    private report(ReportRequest reportRequest) {
        ReportResponse reportResponse = reportClientService.runAndRender(reportRequest)

        if (reportResponse.statusCode == HttpStatus.OK) {
            response.setHeader('Content-Disposition', reportResponse.contentDisposition)
            response.outputStream << reportResponse.content
        } else {
            response.setStatus(reportResponse.statusCode.value())
        }
    }
}
