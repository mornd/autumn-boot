package com.mornd.system.process.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mornd.system.annotation.LogStar;
import com.mornd.system.constant.enums.LogType;
import com.mornd.system.controller.base.BaseController;
import com.mornd.system.entity.result.JsonResult;
import com.mornd.system.exception.BadRequestException;
import com.mornd.system.process.entity.ProcessTemplate;
import com.mornd.system.process.service.ProcessTemplateService;
import com.mornd.system.validation.UpdateValidGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.mornd.system.process.constant.ProcessConst.PROCESS_FILE_SUFFIX;
import static com.mornd.system.process.constant.ProcessConst.PROCESS_PATH;

/**
 * @author: mornd
 * @dateTime: 2023/3/26 - 16:27
 */

@Slf4j
@Validated // @Valid 不生效
@RestController
@RequestMapping("/processTemplate")
@RequiredArgsConstructor
public class ProcessTemplateController extends BaseController {
    private final ProcessTemplateService processTemplateService;

    @GetMapping
    @LogStar(title = "查询审批模块列表", businessType = LogType.SELECT)
    public JsonResult pageList(ProcessTemplate template) {
        IPage<ProcessTemplate> page = processTemplateService.pageList(template);
        return JsonResult.successData(page);
    }

    @GetMapping("get/{id}")
    @LogStar(title = "通过id查询审批模板数据", businessType = LogType.SELECT)
    public JsonResult getById(@PathVariable Long id) {
        ProcessTemplate processTemplate = processTemplateService.getById(id);
        return JsonResult.successData(processTemplate);
    }

    @LogStar(title = "上传流程模板数据和流程定义文件", businessType = LogType.UPLOAD)
    @PostMapping("/insertAndUploadProcessDefinition")
    /**
     * @RequestBody 不支持 multipart/form-data 文件上传
     * @RequestParam(name = "file", required = true) 可加可不加
     */
    public JsonResult insertAndUploadProcessDefinition(
                                            @Validated ProcessTemplate processTemplate,
                                            MultipartFile file) throws IOException {
        checkFile(file);
        checkFileName(file.getOriginalFilename());

        processTemplateService
                .insertAndUploadProcessDefinition(processTemplate, file);
        return success();
    }

    @LogStar(title = "修改流程模板数据和流程定义文件", businessType = LogType.UPLOAD)
    @PutMapping("/updateAndUploadProcessDefinition")
    public JsonResult updateAndUploadProcessDefinition(
            @Validated(UpdateValidGroup.class) ProcessTemplate processTemplate,
            MultipartFile file) throws IOException {
        if(processTemplate.getUpdateFile()) {
            checkFile(file);
            checkFileName(file.getOriginalFilename());
        }

        processTemplateService
                .updateAndUploadProcessDefinition(processTemplate, file);
        return success();
    }

    /**
     * 检测名称是否重复
     * @param name
     * @param id
     * @return true重复，false不重复
     */
    @GetMapping("/checkName")
    public JsonResult checkName(@NotBlank(message = "名称不能为空") String name, Long id) {
        return JsonResult.successData(processTemplateService.checkName(name, id));
    }

    @LogStar(title = "删除流程模板", businessType = LogType.DELETE)
    @DeleteMapping("{id}")
    public JsonResult delete(@PathVariable Integer id) {
        boolean row = processTemplateService.removeById(id);
        return row ? success() : failure();
    }

    @LogStar(title = "下载流程定义文件", businessType = LogType.DOWNLOAD)
    @PostMapping("/downloadProcessDefinition/{filename}")
    public void downloadProcessDefinition(@PathVariable String filename, HttpServletResponse response) {
        // 获取 resources 目录下的输入流方式一
//        InputStream is = this.getClass().getClassLoader()
//                .getResourceAsStream("process/" + filename); // process前面不能加斜杠

        // 方式二          /process/xxx.bpmn20.zip
        ClassPathResource classPathResource =
                new ClassPathResource(PROCESS_PATH + File.separator + filename);
        try (
            InputStream is = classPathResource.getInputStream();
             OutputStream os = response.getOutputStream();) {
            byte[] bytes = StreamUtils.copyToByteArray(is);
            response.reset();
            //下面这两行是为了解决跨域，如果没有跨域可删除
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            response.setContentType("application/octet-stream;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            response.addHeader("Content-Length", "" + bytes.length);
            os.write(bytes);
            os.flush();
        } catch (IOException e) {
            log.error("下载流程定义文件出错", e);
        }
    }

    @LogStar(title = "发布流程模板", businessType = LogType.PUBLISH)
    @PutMapping("/publish/{id}")
    public JsonResult publish(@PathVariable Long id) {
        boolean result = processTemplateService.publish(id);
        return result ? JsonResult.success("发布成功") : JsonResult.failure("发布失败");
    }

    /**
     * 检测文件类型是否合法
     * @param filename
     * @return
     */
    private void checkFileName(String filename) {
        if(filename.endsWith(PROCESS_FILE_SUFFIX)) {
            if("".equals(filename.replace(PROCESS_FILE_SUFFIX, ""))) {
                throw new BadRequestException("流程文件名称不能为空");
            }
        } else {
            throw new BadRequestException("文件格式不正确，必须以" + processTemplateService + "结尾");
        }
    }
}
