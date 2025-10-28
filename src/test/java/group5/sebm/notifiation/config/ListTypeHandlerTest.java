package group5.sebm.notifiation.config;

import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * List类型处理器测试
 */
@ExtendWith(MockitoExtension.class)
class ListTypeHandlerTest {

    @Mock
    private PreparedStatement ps;

    @Mock
    private ResultSet rs;

    @Mock
    private CallableStatement cs;

    private ListTypeHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ListTypeHandler();
    }

    @Test
    void testSetNonNullParameter_WithValidList() throws SQLException {
        // Given
        List<Integer> parameter = Arrays.asList(1, 2, 3);

        // When
        handler.setNonNullParameter(ps, 1, parameter, JdbcType.VARCHAR);

        // Then
        verify(ps).setObject(eq(1), anyString(), eq(Types.VARCHAR));
    }

    @Test
    void testSetNonNullParameter_WithEmptyList() throws SQLException {
        // Given
        List<Integer> parameter = Collections.emptyList();

        // When
        handler.setNonNullParameter(ps, 1, parameter, JdbcType.VARCHAR);

        // Then
        verify(ps).setNull(1, Types.VARCHAR);
    }

    @Test
    void testSetNonNullParameter_WithNull() throws SQLException {
        // When
        handler.setNonNullParameter(ps, 1, null, JdbcType.VARCHAR);

        // Then
        verify(ps).setNull(1, Types.VARCHAR);
    }

    @Test
    void testGetNullableResult_ByColumnName_WithValidJson() throws SQLException {
        // Given
        when(rs.getString("testColumn")).thenReturn("[1,2,3]");

        // When
        List<Integer> result = handler.getNullableResult(rs, "testColumn");

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(Arrays.asList(1, 2, 3), result);
        verify(rs).getString("testColumn");
    }

    @Test
    void testGetNullableResult_ByColumnName_WithNull() throws SQLException {
        // Given
        when(rs.getString("testColumn")).thenReturn(null);

        // When
        List<Integer> result = handler.getNullableResult(rs, "testColumn");

        // Then
        assertNull(result);
        verify(rs).getString("testColumn");
    }

    @Test
    void testGetNullableResult_ByColumnName_WithEmptyString() throws SQLException {
        // Given
        when(rs.getString("testColumn")).thenReturn("");

        // When
        List<Integer> result = handler.getNullableResult(rs, "testColumn");

        // Then
        assertNull(result);
        verify(rs).getString("testColumn");
    }

    @Test
    void testGetNullableResult_ByColumnName_WithInvalidJson() throws SQLException {
        // Given
        when(rs.getString("testColumn")).thenReturn("invalid json");

        // When
        List<Integer> result = handler.getNullableResult(rs, "testColumn");

        // Then
        assertNull(result);
        verify(rs).getString("testColumn");
    }

    @Test
    void testGetNullableResult_ByColumnIndex_WithValidJson() throws SQLException {
        // Given
        when(rs.getString(1)).thenReturn("[4,5,6]");

        // When
        List<Integer> result = handler.getNullableResult(rs, 1);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(Arrays.asList(4, 5, 6), result);
        verify(rs).getString(1);
    }

    @Test
    void testGetNullableResult_ByColumnIndex_WithNull() throws SQLException {
        // Given
        when(rs.getString(1)).thenReturn(null);

        // When
        List<Integer> result = handler.getNullableResult(rs, 1);

        // Then
        assertNull(result);
        verify(rs).getString(1);
    }

    @Test
    void testGetNullableResult_CallableStatement_WithValidJson() throws SQLException {
        // Given
        when(cs.getString(1)).thenReturn("[7,8,9]");

        // When
        List<Integer> result = handler.getNullableResult(cs, 1);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(Arrays.asList(7, 8, 9), result);
        verify(cs).getString(1);
    }

    @Test
    void testGetNullableResult_CallableStatement_WithNull() throws SQLException {
        // Given
        when(cs.getString(1)).thenReturn(null);

        // When
        List<Integer> result = handler.getNullableResult(cs, 1);

        // Then
        assertNull(result);
        verify(cs).getString(1);
    }

    @Test
    void testGetNullableResult_WithWhitespace() throws SQLException {
        // Given
        when(rs.getString("testColumn")).thenReturn("   ");

        // When
        List<Integer> result = handler.getNullableResult(rs, "testColumn");

        // Then
        assertNull(result);
        verify(rs).getString("testColumn");
    }

    @Test
    void testGetNullableResult_WithSingleElement() throws SQLException {
        // Given
        when(rs.getString(1)).thenReturn("[10]");

        // When
        List<Integer> result = handler.getNullableResult(rs, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Collections.singletonList(10), result);
    }
}

