package com.mornd.system.config.security.components;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author mornd
 * @dateTime 2022/5/2 - 17:49
 * 生成、校验、解析 token
 */
@Slf4j
@Component
public class TokenProvider {
    @Resource
    private TokenProperties tokenProperties;

    /**
     * 通过当前登录的用户信息生成 token
     * 注意：以下生成的token中头部和荷载是可以通过base64解析出来的，而签名中包含了盐，所以无法解析出来
     * @param userDetails
     * @return
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                // 声明标识 => jti
                .setId(UUID.randomUUID().toString())
                // 设置主题 => sub
                .setSubject(userDetails.getUsername())
                // 签发人 => iss
                .setIssuer("mornd")
                // 签发时间 => iat
                .setIssuedAt(new Date())
                // 过期时间 => exp
                .setExpiration(generateExpirationDate())
                // 加密算法 => alg 和 盐值
                .signWith(SignatureAlgorithm.HS512, tokenProperties.getSecret())
                .compact();
    }

    /**
     * 生成 token 失效时间
     * 单位：分钟
     * @return
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + tokenProperties.getExpiration());
    }

    /**
     * 从 token 中获取负载
     * @param token
     * @return
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(tokenProperties.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 获取 token 荷载中的主题信息
     * @param token
     * @return
     */
    public String getSubject(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    /**
     * 从 request 域中解析出 token
     * @param request
     * @return
     */
    public String searchToken(HttpServletRequest request) {
        String bearerToken = null;
        String header = request.getHeader(tokenProperties.getTokenHeader());
        if(StringUtils.hasText(header) && header.startsWith(tokenProperties.getTokenHead())) {
            bearerToken = header.replace(tokenProperties.getTokenHead(), "");
        }
        return bearerToken;
    }

    /**
     * 添加自定义荷载信息生成 token
     * @param claim
     * @return
     */
    public String generateToken(Map<String,Object> claim) {
        return Jwts.builder()
                //注意！！！ 使用 setClaims() 会替换掉之前的荷载信息， addClaims() 则是追加荷载信息
                .addClaims(claim)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, tokenProperties.getSecret())
                .compact();
    }

    /**
     * 判断token是否过期
     * @param token
     * @return
     */
    public boolean isTokenExpired(String token) {
        Date expireDate = getExpiredDateFromToken(token);
        return expireDate.before(new Date());
    }

    /**
     * 从token中获取过期时间
     * @param token
     * @return
     */
    public Date getExpiredDateFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration();
    }

    /**
     * 获取 token 终止续期时间
     * @param token 令牌
     * @return 终止续期时间 ms
     */
    public long getTerminateRenewalTime(String token) {
        Claims claims = getClaims(token);
        // 获取 token 生成时间
        long tokenIssued = claims.getIssuedAt().getTime();
        // 续期过期时间
        long renewalExpiration = tokenProperties.getRenewalExpiration();
        // 系统当前时间 < (token生成时间 + 续期过期时间 - token过期时间) = 是否继续续期token
        //终止续期时间
        return  (tokenIssued + renewalExpiration - tokenProperties.getExpiration());
    }
}
