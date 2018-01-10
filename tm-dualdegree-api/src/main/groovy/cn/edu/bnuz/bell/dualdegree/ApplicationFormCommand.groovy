package cn.edu.bnuz.bell.dualdegree

class ApplicationFormCommand {
    Long   id
    String universityCooperative
    String majorCooperative
    String email
    String linkman
    String phone

    def getValue() {
        return "${universityCooperative}-${majorCooperative}-${email}-${linkman}-${phone}"
    }
}
