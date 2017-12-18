package cn.edu.bnuz.bell.dualdegree


class AgreementCommand {

    Long          id
    String        agreementName
    Integer       group
    String        universityEn
    String        universityCn
    String        memo

    List<Item>      addedItems
    List<Integer>   removedItems

    def checkValue() {
        removedItems.each{
            println "here: ${it}"
        }
    }

    class Item {
        /**
         * 年级专业
         */
        Integer id
        String majorOptions
    }

}
