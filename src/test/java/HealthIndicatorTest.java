import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.boot.jdbc.health.DataSourceHealthIndicator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HealthIndicatorTest {
    @Mock
    DataSource dataSource;

    @Mock
    Connection connection;

    @Mock
    DatabaseMetaData databaseMetaData;

    @Test
    void whenDbIsUpHealthIsUp() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(true);

        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");

        DataSourceHealthIndicator indicator = new DataSourceHealthIndicator(dataSource);
        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    void whenDbThrowsHealthIsDown() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection refused"));

        DataSourceHealthIndicator indicator = new DataSourceHealthIndicator(dataSource);
        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}
