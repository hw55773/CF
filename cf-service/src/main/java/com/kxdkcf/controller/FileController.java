package com.kxdkcf.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpStatus;
import com.kxdkcf.Result.Result;
import com.kxdkcf.context.ThreadLocalValueUser;
import com.kxdkcf.dto.HealthDataDTO;
import com.kxdkcf.service.IFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@Tag(name = "文件操作接口")
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final IFileService fileService;
    @Value("${kxdkcf.file.upload-dir}")
    private String uploadDir;

    @Operation(summary = "上传图片")
    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file, HttpServletResponse response) {

        log.info("上传目录绝对路径：{}", Paths.get(uploadDir).toAbsolutePath());

        try {
            // 生成唯一文件名
            String originalFileName = file.getOriginalFilename();
            String fileExt = FileUtil.extName(originalFileName);
            String fileName = IdUtil.fastUUID() + "." + fileExt;

           /* // 构建保存路径
            Path uploadPath = Paths.get(uploadDir);
            FileUtil.mkdir(uploadPath.toFile()); // 自动创建目录
            // 保存文件（Hutool工具）
            FileUtil.writeFromStream(file.getInputStream(), uploadPath.resolve(fileName).toFile());
*/
            // 保存文件（使用Hutool）
            FileUtil.writeFromStream(file.getInputStream(), new File(uploadDir, fileName));

            // 生成访问URL
            String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/images/")
                    .path(fileName)
                    .toUriString();

            return Result.success(url);
        } catch (IOException e) {
            response.setStatus(HttpStatus.HTTP_INTERNAL_ERROR);
            return Result.error("文件上传失败！");
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filename) throws IOException {
        // 构造文件路径并防止路径遍历攻击
        Path filePath = Paths.get(uploadDir).resolve(filename).normalize();

        validatePath(filePath);
        // 检查文件是否存在
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        // 创建文件资源流（不会一次性加载到内存）
        Resource resource = new InputStreamResource(Files.newInputStream(filePath));

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filePath.getFileName() + "\"");

        // 自动检测Content-Type
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    private void validatePath(Path filePath) throws SecurityException {
        // 确保文件在指定目录下
        if (!filePath.startsWith(Paths.get(uploadDir).normalize())) {
            throw new SecurityException("非法文件路径访问!");
        }
    }

    @PostMapping("/health-data")
    public ResponseEntity<?> uploadHealthData(HealthDataDTO healthDataDTO) {

        log.info("健康数据:{}", healthDataDTO.toString());
        fileService.processingOfHealthData(healthDataDTO, ThreadLocalValueUser.getThreadLocalValue(Long.class));

        return ResponseEntity.ok(Result.success());
    }
}