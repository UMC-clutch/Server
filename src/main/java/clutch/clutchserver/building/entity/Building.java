package clutch.clutchserver.building.entity;

import clutch.clutchserver.address.entity.Address;
import clutch.clutchserver.global.common.enums.LogicType;
import clutch.clutchserver.global.common.enums.Type;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "building")
public class Building {

    //건물 id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "building_id")
    private Long buildingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    //건물명
    private String buildingName;

    //건물 시세
    private int price;

    //건물 유형
    @Enumerated(EnumType.STRING)
    private Type type;

    //근저당 설정 기준일
    private String collateralDate;

    //접수 유형(속성)
    @Enumerated(EnumType.STRING)
    private LogicType logicType;

    //근저당액
    private int collateralMoney;

    //평형 수
    private String area;
}
