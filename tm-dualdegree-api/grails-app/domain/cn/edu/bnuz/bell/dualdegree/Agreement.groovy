package cn.edu.bnuz.bell.dualdegree

class Agreement {
    /**
     * 协议名称
     */
    String          name

    /**
     * 项目分类 由前台定义好map
     */
    AgreementGroup  group

    /**
     * 国外大学名称（英文）
     */
    String          universityEn

    /**
     * 国外大学名称（中文）
     */
    String          universityCn

    /**
     * 备注
     */
    String          memo

    /**
     * 协议明细
     */
    static hasMany = [item: AgreementItem]

    static mapping = {
        comment                 '协议'
        table                   'dual_degree_agreement'
        id                      generator: 'identity', comment: '协议ID'
        name                    length: 500, comment: '协议名称'
        memo                    length: 1000,comment: '备注'
        group                   comment: '项目分类'
        universityEn            length: 200, comment: '国外大学英文名'
        universityCn            length: 100, comment: '国外大学英文名'
    }

    static constraints = {
        memo         nullable: true
    }
}
