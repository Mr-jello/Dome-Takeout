package top.mrjello;

import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * @author jason@mrjello.top
 * @date 2023/8/3 18:24
 */
public class AppTest {


    @Test
    public void uuidTest() {
        for(int i = 0; i < 5; i++) {
            System.out.println(UUID.randomUUID().toString());
        }
    }
}
