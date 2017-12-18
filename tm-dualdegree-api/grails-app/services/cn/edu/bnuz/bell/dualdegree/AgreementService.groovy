package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.master.Major
import cn.edu.bnuz.bell.utils.CollectionUtils
import cn.edu.bnuz.bell.utils.GroupCondition
import grails.gorm.transactions.Transactional

@Transactional
class AgreementService {
    /**
     * 协议列表
     */

    def list() {
        Agreement.executeQuery '''
select new map(
    agreement.id                as      id,
    agreement.name              as      name,
    gr.name                  as      groupName,
    agreement.universityEn      as      universityEn,
    agreement.universityCn      as      universityCn,
    agreement.memo              as      memo
)
from Agreement agreement join agreement.group gr
order by agreement.id
'''
    }

    /**
     * 保存协议
     */
    def create(AgreementCommand cmd) {
        Agreement form = new Agreement(
                name:               cmd.agreementName,
                group:              AgreementGroup.load(cmd.group),
                universityCn:       cmd.universityCn,
                universityEn:       cmd.universityEn,
                memo:               cmd.memo
        )
        cmd.addedItems.each { item ->
            form.addToItem(new AgreementItem(
                    major: Major.load(item.id),
                    majorOptions: item.majorOptions
            ))
        }
        form.save()
        return form
    }

    /**
     * 项目列表
     */
    def getGroups() {
        AgreementGroup.executeQuery'''
select new map(
    gr.id   as id,
    gr.name as name
)
from AgreementGroup gr
'''
    }

    /**
     * 2+2年级专业列表
     */
    def getMajors() {
        Major.executeQuery'''
select new map(
    major.id        as id,
    major.grade     as grade,
    subject.name    as subjectName,
    department.id   as departmentId,
    department.name as departmentName    
)
from Major major join major.subject subject join major.department department
where subject.isDualDegree is true and department.enabled is true
order by department.name, subject.name, major.grade
'''
    }

    /**
     * 编辑
     */
    def getFormForEdit(Long id) {
        def result = Agreement.executeQuery '''
select new map(
    agreement.id                as      id,
    agreement.name              as      agreementName,
    gr.id                       as      group,
    agreement.universityEn      as      universityEn,
    agreement.universityCn      as      universityCn,
    agreement.memo              as      memo
)
from Agreement agreement join agreement.group gr
where agreement.id = :id
''',[id: id]
        if (result) {
            def form = result[0]
            form['items'] = findAgreementItems(id)
            return form
        } else {
            return []
        }
    }

    /**
     * 浏览
     */
    def getFormForShow(Long id) {
        def result = Agreement.executeQuery '''
select new map(
    agreement.id                as      id,
    agreement.name              as      agreementName,
    gr.name                     as      groupName,
    agreement.universityEn      as      universityEn,
    agreement.universityCn      as      universityCn,
    agreement.memo              as      memo
)
from Agreement agreement join agreement.group gr
where agreement.id = :id
''',[id: id]
        if (result) {
            def form = result[0]
            def items = findAgreementItems(id)
            List<GroupCondition> conditions = [
                    new GroupCondition(
                            groupBy: 'departmentId',
                            into: 'subjects',
                            mappings: [
                                    departmentId  : 'id',
                                    departmentName: 'name'
                            ]
                    ),
                    new GroupCondition(
                            groupBy: 'subjectName',
                            into: 'options',
                            mappings: [
                                    subjectName: 'name'
                            ]
                    ),
                    new GroupCondition(
                            groupBy: 'majorOptions',
                            into: 'grades',
                            mappings: [
                                    majorOptions: 'name'
                            ]
                    ),
            ]
            form['items'] = CollectionUtils.groupBy(items, conditions)
            return form
        } else {
            return []
        }
    }

    /**
     * 更新
     */
    def update(AgreementCommand cmd) {
        Agreement form = Agreement.load(cmd.id)
        if (form) {
            form.name = cmd.agreementName
            form.universityCn = cmd.universityCn
            form.universityEn = cmd.universityEn
            form.group = AgreementGroup.load(cmd.group)
            form.memo = cmd.memo
            println cmd.memo

            cmd.addedItems.each { item ->
                def major = Major.load(item.id)
                def agreementItem = AgreementItem.get(new AgreementItem(agreement: form, major: major))
                if (!agreementItem) {
                    form.addToItem(new AgreementItem(
                            major: major,
                            majorOptions: item.majorOptions
                    ))
                }
            }

            cmd.removedItems.each {
                def agreementItem = AgreementItem.load(new AgreementItem(agreement: form, major: Major.load(it)))
                form.removeFromItem(agreementItem)
                agreementItem.delete()
            }
            form.save(flush: true)
            return form
        }
    }

    private findAgreementItems(Long agreementId) {
        AgreementItem.executeQuery'''
select new map(
    major.id            as id,
    item.majorOptions   as majorOptions,
    major.grade         as grade,
    subject.name        as subjectName,
    department.id       as departmentId,
    department.name     as departmentName
)
from AgreementItem item join item.major major join major.subject subject join major.department department
where item.agreement.id = :id
order by department.name, subject.name, major.grade, item.majorOptions
''',[id: agreementId]
    }
}
