package clutch.clutchserver.building.dto;

import clutch.clutchserver.global.common.enums.LogicType;
import clutch.clutchserver.global.common.enums.Type;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class BuildingRequestDto {

    //건물 id
    private Long buildingId;

    //건물명
    private String buildingName;

    //지번 주소
    private String address;

    //동
    private String dong;

    //호
    private String ho;

    //근저당 설정 기준일
    private LocalDateTime collateralDate;

    // 접수 유형
    private LogicType logicType;

    // 건물 유형
    private Type type;

    // 평형 수
    private String area;

}
