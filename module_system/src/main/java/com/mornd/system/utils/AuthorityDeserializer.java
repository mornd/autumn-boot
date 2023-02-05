package com.mornd.system.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: mornd
 * @dateTime: 2023/2/1 - 22:52
 * 自定义 UserDetails 的 Authority 反序列化器
 * 用于解决 UserDetails 作为实体类接收前端参数时，getAuthorities 无法序列号问题
 */
@Deprecated
public class AuthorityDeserializer extends JsonDeserializer<Collection<? extends GrantedAuthority>> {

    @Override
    public Collection<? extends GrantedAuthority> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        List<GrantedAuthority> grantedAuthorities = new LinkedList<>();

        ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode jsonNode = objectMapper.readTree(jsonParser);

        Iterator<JsonNode> elements = jsonNode.elements();

        while (elements.hasNext()) {
            JsonNode next = elements.next();
            // authority 是页面传进来的参数名
            JsonNode authority = next.get("authority");

            grantedAuthorities.add(new SimpleGrantedAuthority(authority.asText()));
        }
        return grantedAuthorities;
    }
}
