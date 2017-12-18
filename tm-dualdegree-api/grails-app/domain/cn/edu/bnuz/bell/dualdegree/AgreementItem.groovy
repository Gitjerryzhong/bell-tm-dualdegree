package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.master.Major


class AgreementItem implements Serializable {
    /**
     * 年级专业
     */
    Major major

    /**
     * 可衔接国外专业
     */
    String majorOptions

    /**
     * 协议
     */
    static belongsTo = [agreement: Agreement]

    static mapping = {
        comment                 '协议适用年级专业'
        table                   'dual_degree_agreement_item'
        id                      composite: ['agreement', 'major']
    }
}
