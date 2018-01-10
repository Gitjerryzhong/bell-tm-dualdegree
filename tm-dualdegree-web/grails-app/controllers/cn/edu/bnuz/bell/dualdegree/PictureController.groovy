package cn.edu.bnuz.bell.dualdegree

import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus

class PictureController {
    SecurityService securityService

    @Value('${bell.student.filesPath}')
    String filesPath

    def show(String awardId, String fileName) {
        def picturePath = filesPath
        def desFileName = fileName

        if (fileName) {
            def fileType = fileName.substring(fileName.lastIndexOf('.'))
            if (fileType == ".pdf") {
                desFileName = "pdf.jpg"
            } else if (awardId) {
                picturePath = "${filesPath}/${awardId}/${securityService.userId}"
            }
        } else {
            desFileName = 'none.jpg'
        }
        File file = new File(picturePath, "${desFileName}")
        output(file)
    }

    def fileView(String awardId, String fileName) {
//        output(awardId, fileName)
    }

    def fileSource(String awardId, String fileName) {
        def picturePath = "${filesPath}/${awardId}/${securityService.userId}"
        File file = new File(picturePath, fileName)
        output(file)
    }

    private output(File file) {
        if (!file.exists()) {
            render status: HttpStatus.NOT_FOUND
        } else {
            response.contentType = URLConnection.guessContentTypeFromName(file.getName())
            response.outputStream << file.bytes
            response.outputStream.flush()
        }
    }
}
