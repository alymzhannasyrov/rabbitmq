package kg.alymzhan.petchatgpt.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.buf.HexUtils;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class UserUtils {

    public static String generateUserSecret() {
        String number = String.valueOf(RandomUtils.nextInt(0, 1000000));
        String hexString = HexUtils.toHexString(number.getBytes(StandardCharsets.UTF_8));
        return StringUtils.leftPad(hexString, 16, "0");
    }
}
