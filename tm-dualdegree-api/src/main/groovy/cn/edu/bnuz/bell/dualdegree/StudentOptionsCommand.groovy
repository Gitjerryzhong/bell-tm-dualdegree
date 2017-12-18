package cn.edu.bnuz.bell.dualdegree

class StudentOptionsCommand {
    String studentId
    String studentName
    Integer grade
    String sujectId
    Integer groupId

    def checkValue() {
        return "${studentId}-${studentName}-${grade}-${sujectId}-${groupId}"
    }
}
