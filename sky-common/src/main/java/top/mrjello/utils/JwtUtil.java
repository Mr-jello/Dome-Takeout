package top.mrjello.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * @author jason@mrjello.top
 * @date 2023/8/2 23:59
 */

public class JwtUtil {

    // token时效：24小时
    public static final long EXPIRE = 1000 * 60 * 60 * 24;
    // 签名哈希的密钥，对于不同的加密算法来说含义不同
    public static final String APP_SECRET = "JasonZhai@top.mrjelloJasonZhai@top.mrjelloJasonZhai@top.mrjelloJasonZhai@top.mrjello";
    /**
     * 根据用户id和昵称生成token
     * @param claims 设置的信息
     * @return JWT规则生成的token
     */
    public static String generateJwtToken(Map<String, Object> claims){
        return Jwts.builder()
                .setSubject("mrjello")
                .setIssuedAt(new Date())
                .setClaims(claims)
                // 传入Key对象
                .signWith(Keys.hmacShaKeyFor(APP_SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .compact();
    }

    /**
     * 判断token是否存在与有效
     * @param jwtToken token字符串
     * @return 如果token有效返回true，否则返回false
     */
    public static boolean checkToken(String jwtToken) {
        if (jwtToken == null) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(APP_SECRET.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取token中的信息
     * @param jwtToken token字符串
     * @return token中的信息
     */
    public static Claims parseJwtToken(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(APP_SECRET.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }


    /**
     * 根据用户id生成token
     * @param secretKet 秘钥
     * @param claims 设置的信息
     * @return JWT规则生成的token
     */
    public static String generateUserToken(String secretKet, Map<String, Object> claims){
        return Jwts.builder()
                .setSubject("mrjello")
                .setIssuedAt(new Date())
                .setClaims(claims)
                // 传入Key对象
                .signWith(Keys.hmacShaKeyFor(secretKet.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .compact();
    }

    /**
     * 判断用户token是否存在与有效
     * @param secretKey 秘钥
     * @param token token字符串
     * @return 如果token有效返回true，否则返回false
     */
    public static boolean checkUserToken(String secretKey, String token) {
        if (token == null) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取用户token中的信息
     * @param secretKey 秘钥
     * @param token token字符串
     */
    public static Claims parseUserToken(String secretKey, String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
