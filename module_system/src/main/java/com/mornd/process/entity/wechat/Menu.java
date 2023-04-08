package com.mornd.process.entity.wechat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mornd.process.entity.ProcessBaseEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author: mornd
 * @dateTime: 2023/4/6 - 20:12
 * 微信公众号菜单
 */

@Data
@TableName("oa_wechat_menu")
public class Menu extends ProcessBaseEntity {

    /**
     * 上级菜单id
     */
    private Long parentId;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    @Size(max = 16, message = "名称过长")
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * 网页 链接，用户点击菜单可打开链接
     */
    private String url;

    /**
     * 菜单KEY值，用于消息接口推送
     */
    @Size(max = 128, message = "菜单key过长")
    private String menuKey;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 子集
     */
    @TableField(exist = false)
    private List<Menu> children;

}
