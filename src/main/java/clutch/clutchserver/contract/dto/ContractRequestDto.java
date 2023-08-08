package clutch.clutchserver.contract.dto;

import clutch.clutchserver.image.entity.Image;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class ContractRequestDto{

    @Schema(description = "거주 여부")
    private Boolean has_lived;

    @Schema(description = "운송 보고 날짜")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate transport_report_date;

    @Schema(description = "확인 날짜")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate confirmation_date;

    @Schema(description = "집주인 개입 여부")
    private Boolean has_landlord_intervene;

    @Schema(description = "배당 신청 여부")
    private Boolean has_applied_dividend;

    @Schema(description = "보증금")
    private BigInteger deposit;
}
