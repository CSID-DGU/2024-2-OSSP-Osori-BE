package dongguk.osori.domain.user;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private static final long CODE_EXPIRE_MINUTES = 5; // 인증 코드 유효 시간 (5분)

    // 인증 번호 생성
    public static int createNumber() {
        return (int)(Math.random() * 900000) + 100000; // 6자리 랜덤 숫자
    }

    // 이메일 작성
    public MimeMessage createMail(String recipientEmail, int authCode) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, recipientEmail);
            message.setSubject("[Osori] 회원가입을 위한 이메일 인증");

            // 이메일 본문
            String body = "<h1>안녕하세요. Osori입니다.</h1>"
                    + "<h3>회원가입을 위한 요청하신 인증 번호입니다.</h3>"
                    + "<div align='center' style='border:1px solid black; font-family:verdana;'>"
                    + "<h2>회원가입 인증 코드입니다.</h2>"
                    + "<h1 style='color:blue'>" + authCode + "</h1>"
                    + "</div>"
                    + "<br><h3>감사합니다.</h3>";

            message.setText(body, "UTF-8", "html");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    // 이메일 전송 및 Redis 저장
    public void sendEmail(String recipientEmail) {
        int authCode = createNumber();
        MimeMessage message = createMail(recipientEmail, authCode);

        // Redis에 인증 코드 저장, 5분 후 만료
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(recipientEmail, String.valueOf(authCode), CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 이메일 전송
        javaMailSender.send(message);
    }

    // 인증 코드 확인
    public boolean verifyAuthCode(String email, String inputCode) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String storedCode = valueOperations.get(email);

        return storedCode != null && storedCode.equals(inputCode);
    }
}
