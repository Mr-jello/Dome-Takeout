package top.mrjello.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.mrjello.constant.MessageConstant;
import top.mrjello.result.Result;
import top.mrjello.utils.AliOssUtil;

import java.util.UUID;

/**
 * @author jason@mrjello.top
 * @date 2023/8/6 20:50
 */
@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * alioss中上传文件
     * @return Result
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws Exception {
        log.info("Upload file: {}", file);
        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        // 获取文件后缀
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = UUID.randomUUID().toString() + substring;
        try {
            String url = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(url);
        } catch (Exception e) {
            log.error("Upload file to alioss failed: {}", e.getMessage());
            throw new Exception(MessageConstant.UPLOAD_FAILED);
        }


    }


}
