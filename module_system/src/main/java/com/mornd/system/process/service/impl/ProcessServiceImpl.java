package com.mornd.system.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mornd.system.process.constant.ProcessConst;
import com.mornd.system.process.entity.Process;
import com.mornd.system.process.entity.vo.ProcessVo;
import com.mornd.system.process.mapper.ProcessMapper;
import com.mornd.system.process.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * @author: mornd
 * @dateTime: 2023/3/29 - 11:28
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ProcessServiceImpl
        extends ServiceImpl<ProcessMapper, Process>
        implements ProcessService {

    private final RepositoryService repositoryService;

    @Override
    public IPage<ProcessVo> pageList(ProcessVo vo) {
        IPage<ProcessVo> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        baseMapper.pageList(page, vo);
        return page;
    }

    /**
     * 流程部署
     * @param filename 文件名
     * @return
     */
    @Override
    public Deployment deployByZip(String filename) {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(ProcessConst.PROCESS_DIR_NAME + File.separator + filename);

        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deploy = repositoryService
                .createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();

        log.info("流程部署成功");
        log.info("流程部署id：{}", deploy.getId());
        log.info("流程部署名称：{}", deploy.getName());
        return deploy;
    }

}
