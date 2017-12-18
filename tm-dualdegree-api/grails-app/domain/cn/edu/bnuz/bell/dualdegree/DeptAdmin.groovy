package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.organization.Department
import cn.edu.bnuz.bell.organization.Teacher

class DeptAdmin {
    Department department
    Teacher teacher

    static mapping = {
        comment '学院管理员'
        table 'dual_degree_dept_admin'
        id generator: 'identity', comment: '无意义ID'
        department comment: '可管理部门'
        teacher comment: '教师'
    }
}
