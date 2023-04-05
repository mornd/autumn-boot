# 获取左侧菜单树
SELECT
        id,
        parent_id,
        title,
        `NAME`,
        path,
        component,
        icon,
        sort,
        keep_alive,
        require_auth,
        is_route,
        enabled,
        menu_type
        FROM
        sys_permission sp
        WHERE
        sp.hidden = #{hidden}
        AND sp.id IN(
        SELECT
        per_id
        FROM
        sys_role_permission srp
        WHERE
        srp.role_id IN (
        SELECT
        id
        FROM
        sys_role sr
        WHERE
        sr.enabled = #{enableState}
        AND sr.id IN (
        SELECT
        role_id
        FROM
        sys_user_role sur
        WHERE
        user_id IN(
        SELECT
        id
        FROM
        sys_user su
        WHERE
        su.status = #{enableState}
        <if test="userId != null and userId != ''">
            AND su.id = #{userId}
        </if>
        )
        )
        ))
