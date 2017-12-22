package cn.edu.bnuz.bell.dualdegree

import org.grails.datastore.mapping.query.Query

import java.text.SimpleDateFormat

class AwardCommand {
    Long   id
    String title
    String content
    String requestBegin
    String requestEnd
    String paperEnd
    String approvalEnd
    String departmentId

    Date toDate(String dateStr) {
        SimpleDateFormat spdf = new SimpleDateFormat("yyyy-MM-dd");
        if(dateStr!=null){
            Date date=spdf.parse(dateStr)
            return date
        }else{
            return null
        }
    }

    def getRequestBeginToDate() {
        return toDate(requestBegin)
    }

    def getRequestEndToDate() {
        return toDate(requestEnd)
    }

    def getPaperEndToDate() {
        return toDate(paperEnd)
    }

    def getApprovalEndToDate() {
        return toDate(approvalEnd)
    }
}
