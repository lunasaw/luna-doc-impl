package io.github.lunasaw.geo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <h1>城市信息</h1>
 * Created by Qinyi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CityInfo {

    /** 城市 */
    private String city;

    /** 经度 */
    private Double longitude;

    /** 纬度 */
    private Double latitude;
}
