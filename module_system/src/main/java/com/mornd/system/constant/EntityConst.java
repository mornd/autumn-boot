package com.mornd.system.constant;

/**
 * @author mornd
 * @dateTime 2022/1/2 - 14:57
 */
public interface EntityConst {
    //全局常量：启用
    Integer ENABLED = 1;
    //全局常量：停用
    Integer DISABLED = 0;
    
    //逻辑删除
    Integer DELETED = 0; //已删除
    Integer NORMAL = 1; //正常
}
