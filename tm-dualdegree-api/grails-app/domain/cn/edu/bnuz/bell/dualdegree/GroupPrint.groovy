package cn.edu.bnuz.bell.dualdegree

class GroupPrint implements Serializable{
    String majorId
    String name

    static mapping = {
        table   name: 'et_tms_project'
        majorId column: 'id', sqlType: 'varchar', length: 20, comment: '教学计划号'
        name    length: 50, comment: '项目名称'
        id      composite: ['majorId', 'name']
    }
}
