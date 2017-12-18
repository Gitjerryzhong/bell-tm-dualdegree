package cn.edu.bnuz.bell.dualdegree

class StudentPrint {
    String studentId
    String studentName
    Date   dateAdded
    Date   dateDeleted
    String operatorAdded
    String operatorDeleted
    Integer inProject
    String type
    static mapping = {
        table name: 'et_tms_xsmd'
        id generator: 'sequence',column:'mdid',params: [sequence:'student_print_id_seq']
        studentId       column: 'xh', sqlType: 'varchar', length: 20, comment: '学号'
        studentName     column: 'xm', sqlType: 'varchar', length: 50, comment: '姓名'
        dateAdded       column: 'adddate', sqlType: 'timestamp', comment: '添加日期'
        dateDeleted     column: 'deldate', sqlType: 'timestamp', comment: '删除日期'
        operatorAdded   column: 'addauthor', sqlType: 'varchar', length: 20, comment: '添加人'
        operatorDeleted column: 'delauthor', sqlType: 'varchar', length: 20, comment: '删除人'
        inProject       column: 'inproject', sqlType: 'integer', length: 20, comment: '参加项目'
        type            column: 'kind', sqlType: 'varchar', length: 20, comment: '项目名称'
    }

    static constraints = {
        dateDeleted     nullable: true
        operatorDeleted nullable: true
    }
}
