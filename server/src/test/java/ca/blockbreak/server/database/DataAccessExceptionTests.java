package ca.blockbreak.server.database;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DataAccessExceptionTests {

	/**
	* Creates DataAccessException with message, it should not be null
     */
    @Test
    public void shouldCreateDataAccessException() {
        var e = new DataAccessException("TestException");
        assertNotNull(e);
    }

    /**
    * Creates DataAccessException with message and throwable, it should not be null
    */
    @Test
    public void shouldCreateDataAccess2Exception() {
        var e = new DataAccessException("TestException", new Exception("Exception"));

        assertNotNull(e);
    }
}
