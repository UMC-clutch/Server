package clutch.clutchserver.report.entity;

import clutch.clutchserver.contract.entity.Contract;
import clutch.clutchserver.global.common.BaseDateEntity;
import clutch.clutchserver.global.common.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.security.cert.CertPathBuilder;

@Entity
@Table(name = "report")
@Getter @Setter
@RequiredArgsConstructor
public class Report extends BaseDateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

// 연관관계 매핑
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "contract_id")
//    private Contract contract;

    // 신고 상태 (완료, 심사 중)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private int repayment; // 최우선 변제금
    private boolean hasLowIncome; // 소액임차인 여부
    private boolean hasSubmittedContract; // 계약서 제출 여부
    private boolean hasResistance; // 대항력 여부
    private boolean hasRepayment; // 최우선 변제금 가능여부

    @Builder
    public Report(Long id, Contract contract, ReportStatus status, int repayment, boolean hasLowIncome, boolean hasSubmittedContract, boolean hasResistance, boolean hasRepayment) {
        this.id = id;
        this.status = status;
        this.repayment = repayment;
        this.hasLowIncome = hasLowIncome;
        this.hasSubmittedContract = hasSubmittedContract;
        this.hasResistance = hasResistance;
        this.hasRepayment = hasRepayment;
    }
}
