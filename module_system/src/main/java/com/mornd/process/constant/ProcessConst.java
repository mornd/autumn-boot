package com.mornd.process.constant;

import java.io.File;

/**
 * @author: mornd
 * @dateTime: 2023/3/28 - 22:15
 * 流程常量类
 */
public abstract class ProcessConst {
    /**
     * 流程定义文件存放的文件夹名称
     */
    public static final String PROCESS_DIR_NAME = "process";
    /**
     * 流程定义文件存放的文件夹路径 /process
     */
    public static final String PROCESS_PATH = File.separator + PROCESS_DIR_NAME;


    /**
     * 流程定义文件后缀
     */
    public static final String  PROCESS_FILE_SUFFIX = ".bpmn20.zip";
}
