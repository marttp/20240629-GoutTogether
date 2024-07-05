package dev.tpcoder.goutbackend.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainerConfig.class)
public class AbstractIntegrationTest {
    
}
