package com.mornd.system.utils;

import com.mornd.system.entity.po.SysPermission;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @author mornd
 * @dateTime 2021/8/11 - 17:32
 * 生成TreeSet （用于菜单的去重、定制排序）
 * 菜单实体类需重写hashCode()和equals()方法 （使用lombok的@Data注解可忽略）
 */
public class MenuUtil {

    /**
     * 定制排序规则
     * @return
     */
    private static Set<SysPermission> generateTreeSet(){
        return new TreeSet<>((o1, o2) -> {
            /**
             * 排序根目录：定制排序，首先根据sort升序
             * return 负数表示放在红黑树的左边,即逆序输出
             *        正数表示放在红黑树的右边，即顺序输出
             *        0表示元素相同，treeSet这里仅存放第一个元素
             *        o1 比较 02 表示升序
             */
            int compare = Double.compare(o1.getSort(), o2.getSort());
            // 如果是 TreeSet 或 TreeMap 结构， 这里返回0不处理则后者不会添加进集合
            if(compare == 0) {
                // 将 title 进行字符串比较
                return o1.getTitle().compareTo(o2.getTitle());
            } else {
                return compare;
            }
            /*if(o1.getSort() < o2.getSort()){
                return -1;
            }else if(o1.getSort() > o2.getSort()){
                return 1;
            }else {
                //如果两个比较的sort值相同，再比较id，如果也id相同，则过滤其中一个值
                if(o1.getId().equals(o2.getId())) return 0;
                //再根据id的hashCode排序
                if(o1.getId().hashCode() < o2.getId().hashCode()) return -1;
                else return 1;
            }*/
        });
    }

    /**
     * 排序节点
     * @param rootId
     * @param collection
     * @return
     */
    public static Set<SysPermission> toTree(String rootId, Collection<SysPermission> collection){
        if(rootId == null || ObjectUtils.isEmpty(collection)) return null;
        HashSet<SysPermission> hashSet = new HashSet<>(collection);
        Set<SysPermission> result = generateTreeSet();
        for (SysPermission item : hashSet) {
            if (rootId.equals(item.getParentId())) {
                result.add(findChildren(item, hashSet));
            }
        }
        return result;
    }

    /**
     * 递归匹配子集
     * @param permission
     * @param set
     * @return
     */
    private static SysPermission findChildren(SysPermission permission, Set<SysPermission> set){
        set.forEach(item -> {
            if (permission.getId().equals(item.getParentId())) {
                if (permission.getChildren() == null) {
                    permission.setChildren(generateTreeSet());
                }
                permission.getChildren().add(findChildren(item, set));
            }
        });
        return permission;
    }

    /**
     * 将子集进行去重、排序
     * @param collection
     * @return
     */
    public static Set<SysPermission> toTreeSet(Collection<SysPermission> collection){
        if(ObjectUtils.isEmpty(collection)) return null;
        HashSet<SysPermission> hashSet = new HashSet<>(collection);
        Set<SysPermission> permissions = generateTreeSet();
        permissions.addAll(hashSet);
        return permissions;
    };


    /**
     * 根据条件过滤菜单
     * @param rootId
     * @param all
     * @param filterSet
     * @return
     */
    public static Set<SysPermission> filterTree(String rootId, Set<SysPermission> all, Set<SysPermission> filterSet) {
        if(rootId == null || ObjectUtils.isEmpty(all) || ObjectUtils.isEmpty(filterSet)) return null;
        Set<SysPermission> result = new HashSet<>();
        for (SysPermission filter : filterSet) {
            findParent(rootId, all, result, filter);
        }
        return toTree(rootId, result);
    }

    /**
     * 找到其父节点并加入集合中
     * @param rootId
     * @param all
     * @param result
     * @param current
     */
    private static void findParent(String rootId, Set<SysPermission> all, Set<SysPermission> result, SysPermission current) {
        result.add(current);
        if(!rootId.equals(current.getParentId())) {
            for (SysPermission sysPermission : all) {
                if(Objects.equals(sysPermission.getId(), current.getParentId())) {
                    findParent(rootId, all, result, sysPermission);
                }
            }
        }
    }


}
