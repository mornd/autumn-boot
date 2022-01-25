package com.mornd.system.config.security.components;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author mornd
 * @dateTime 2021/9/6 - 10:35
 * jwt工具类
 */
@Slf4j
@Component
public class JwtTokenUtil {
    //claim用户名
    private static final String CLAIM_USERNAME = "sub";
    //claim创建时间
    private static final String CLAIM_CREATED = "created";
    //jwt的密钥
    @Value("${jwt.secret}")
    private String secret;
    //jwt的失效时间
    @Value("${jwt.expiration}")
    private long expiration;
    //JWT存储的请求头
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    //JWT负载拿到开头
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    /**
     * 传入用户登录信息，生成token
     * @param userDetails
     * @return
     */
    public String generateToken(UserDetails userDetails) {
        Map<String,Object> claim = new HashMap<>();
        claim.put(CLAIM_USERNAME,userDetails.getUsername());
        claim.put(CLAIM_CREATED,new Date());
        return generateToken(claim);
    }

    public String generateToken(Map<String,Object> claim) {
        return Jwts.builder()
                .setClaims(claim)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }

    /**
     * 生成token失效时间
     * @return
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    /**
     * 从token中获取荷载
     * @param token
     * @return
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.warn("token无效或已过期：{}",e.getMessage());
            return null;
        }
    }

    /**
     * 从token中获取登陆用户名
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token){
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.warn("从token荷载获取用户名异常：{}",e.getMessage());
            return null;
        }
    }

    /**
     * 验证token是否有效
     * @param token
     * @param userDetails
     * @return
     */
    public boolean validateToken(String token,UserDetails userDetails){
        String username = getUsernameFromToken(token);
        return Objects.equals(username, userDetails.getUsername())
                && !isTokenExpired(token);
    }

    /**
     * 判断token是否失效
     * @param token
     * @return
     */
    private boolean isTokenExpired(String token) {
        Date expireDate = getExpiredDateFromToken(token);
        return expireDate.before(new Date());
    }

    /**
     * 从token中获取过期时间
     * @param token
     * @return
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 判断token是否可以被刷新
     * @param token
     * @return
     */
    public boolean canRefresh(String token){
        return !isTokenExpired(token);
    }

    /**
     * 刷新token
     * @param token
     * @return
     */
    public String refreshToken(String token){
        Claims claims = getClaimsFromToken(token);
        claims.put(CLAIM_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 从request域中获取token
     * @param request
     * @return
     */
    public String getToken(HttpServletRequest request){
        String token = null;
        String header = request.getHeader(tokenHeader);
        if(header != null && header.startsWith(tokenHead)){
            token = header.substring(tokenHead.length());
        }
        return token;
    }
}
