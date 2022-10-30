package com.mornd.system.constant;

/**
 * @author mornd
 * @dateTime 2021/11/12 - 22:00
 * 返回的消息
 */
public interface ResultMessage {
    String CRUD_SUPERADMIN = "超级管理员不可操作";

    String INSERT_MSG = "添加成功";
    String UPDATE_MSG = "修改成功";
    String DELETE_MSG = "删除成功";

    String INSERT_FAILURE_MSG = "添加失败，请重试";
    String UPDATE_FAILURE_MSG = "修改失败，请重试";
    String DELETE_FAILURE_MSG = "删除删除，请重试";

    String NOT_LOGGED = "尚未登录，请先登录";
    String NOT_AUTH = "当前权限不足以访问";
}
