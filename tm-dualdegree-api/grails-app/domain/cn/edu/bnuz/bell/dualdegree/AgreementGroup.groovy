package cn.edu.bnuz.bell.dualdegree

class AgreementGroup {
    /**
     *项目名称
     */
    String        name

    static mapping = {
        comment                 '项目'
        table                   'dual_degree_agreement_group'
        id                      generator: 'identity', comment: '项目ID'
        name                    length: 50, comment: '项目名称'
    }

}
