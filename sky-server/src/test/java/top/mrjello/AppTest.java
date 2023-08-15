package top.mrjello;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;
import java.util.UUID;

/**
 * @author jason@mrjello.top
 * @date 2023/8/3 18:24
 */
//@SpringBootTest
public class AppTest {

//    @Autowired
    private JavaMailSender javaMailSender;


    @Test
    public void uuidTest() {
        for(int i = 0; i < 5; i++) {
            System.out.println(UUID.randomUUID().toString());
        }
    }

    @Test
    public void simpleMailTest() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("quintonloventiy96@gmail.com");
        simpleMailMessage.setTo("z749047172@gmail.com");
        simpleMailMessage.setSubject("测试邮件");
        simpleMailMessage.setText("测试邮件内容: 你好，这是一封测试邮件3");
        javaMailSender.send(simpleMailMessage);
    }

    @Test
    public void mailTest() throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setFrom("quintonloventiy96@gmail.com");
        mimeMessageHelper.setTo("z749047172@gmail.com");
        mimeMessageHelper.setSubject("测试邮件");
        mimeMessageHelper.setText("<h1>测试邮件内容: 你好，这是一封测试邮件2<h1>", true);
        mimeMessageHelper.addAttachment("1.jpg", new File("E:\\FagXc6waAAEccnf.jpg"));
        javaMailSender.send(mimeMessage);

    }
}
