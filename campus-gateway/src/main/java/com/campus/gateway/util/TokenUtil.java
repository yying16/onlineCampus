package com.campus.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {

    private static final String SECRET_KEY = "(aud9h9.a!!)";

    // 生成token
    public static String generateToken(String id, JSONObject user, long expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", id);
        claims.put("user", user);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + expiration*3600000))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // 获取token中的信息
    public static String getSubjectFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }


    // 获取token中的信息
    public static JSONObject getClaimsFromToken(String token) {
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            JSONObject user = JSONObject.parseObject(JSONObject.toJSONString(claims.get("user"))); //将hash转换为user类型
            return user;
        }catch (MalformedJwtException e){
            return null;
        }
    }

    // 根据token获取uid
    public static String getUidFromToken(String token) {
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return String.valueOf(claims.get("uid"));
        }catch (MalformedJwtException e){
            return null;
        }
    }

    // 验证token是否有效
    public static boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}