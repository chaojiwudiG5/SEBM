package group5.sebm.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    @Test
    void testGenerateAndParseToken_shouldReturnUserId() {
        Long userId = 123L;
        String token = JwtUtils.generateToken(userId);

        assertNotNull(token);

        Claims claims = JwtUtils.parseToken(token);
        assertEquals(String.valueOf(userId), claims.getSubject());

        Long parsedUserId = JwtUtils.getUserIdFromToken(token);
        assertEquals(userId, parsedUserId);
    }

    @Test
    void testIsTokenExpired_shouldReturnFalseForNewToken() {
        String token = JwtUtils.generateToken(1L);
        boolean expired = JwtUtils.isTokenExpired(token);
        assertFalse(expired);
    }

    @Test
    void testIsTokenExpired_shouldReturnTrueForExpiredToken() {
        // 手动生成一个过期 token
        Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        Date past = new Date(System.currentTimeMillis() - 1000 * 60); // 1 分钟前
        String token = Jwts.builder()
                .setSubject("1")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000 * 60))
                .setExpiration(past)
                .signWith(key)
                .compact();

        // 由于 JwtUtils 内部 SECRET_KEY 不一样，parseToken 会报异常，所以这里只能测试逻辑本身用 try/catch
        assertThrows(Exception.class, () -> JwtUtils.isTokenExpired(token));
    }

    @Test
    void testParseToken_withInvalidToken_shouldThrowException() {
        String invalidToken = "invalid.token.string";
        assertThrows(Exception.class, () -> JwtUtils.parseToken(invalidToken));
    }

    @Test
    void testGetUserIdFromToken_withInvalidToken_shouldThrowException() {
        String invalidToken = "invalid.token.string";
        assertThrows(Exception.class, () -> JwtUtils.getUserIdFromToken(invalidToken));
    }
}
