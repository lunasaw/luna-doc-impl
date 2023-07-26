package io.github.lunasaw;

import io.github.lunasaw.bitmap.RedisBigMapService;
import org.checkerframework.checker.units.qual.K;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author weidian
 * @date 2023/7/26
 */
public class BitmapTest extends BaseTest {

    private static final String KEY = "app_20_10_13";
    @Autowired
    private RedisBigMapService  redisBigMapService;

    @Test
    public void countCard() {
        redisBigMapService.setBit(KEY, 1L, true);
        redisBigMapService.setBit(KEY, 3L, true);
        redisBigMapService.setBit(KEY, 4L, true);

        Long aLong = redisBigMapService.bitCount(KEY);
        System.out.println(aLong);

        Boolean bit = redisBigMapService.getBit(KEY, 2L);
        System.out.println(bit);
    }
}
