package clutch.clutchserver.report.service;

import clutch.clutchserver.building.dto.BuildingRequestDto;
import clutch.clutchserver.building.dto.BuildingResponseDto;
import clutch.clutchserver.building.entity.Building;
import clutch.clutchserver.building.repository.BuildingRepository;
import clutch.clutchserver.building.service.BuildingService;
import clutch.clutchserver.contract.entity.Contract;
import clutch.clutchserver.contract.repository.ContractRepository;
import clutch.clutchserver.global.common.CalculateDeposit;
import clutch.clutchserver.global.common.enums.ReportStatus;
import clutch.clutchserver.global.payload.ApiResponse;
import clutch.clutchserver.image.entity.Image;
import clutch.clutchserver.image.repository.ImageRepository;
import clutch.clutchserver.report.dto.ReportResponseDto;
import clutch.clutchserver.report.entity.Report;
import clutch.clutchserver.report.repository.ReportRepository;
import clutch.clutchserver.user.entity.User;
import clutch.clutchserver.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final BuildingService buildingService;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final ImageRepository imageRepository;
    private final BuildingRepository buildingRepository;
    private final CalculateDeposit calculateDeposit;
    private final ContractRepository contractRepository;

    //건물 정보 입력 반환
    public ResponseEntity<?> getBuildingResDto(BuildingRequestDto buildingRequestDto) throws UnsupportedEncodingException, ParseException, JsonProcessingException, InterruptedException {


        // db 에 저장되어 있는 건물들과 주소, 동, 호수, 평형 비교해서 가져오기.
        Optional<Building> buildingOptional = buildingRepository.findByAddressAndDongAndHoAndArea(
                buildingRequestDto.getAddress(),
                buildingRequestDto.getDong(),
                buildingRequestDto.getHo(),
                buildingRequestDto.getArea()
        );
        // 만약 db 에 같은 건물이 저장 되어 있어서 buildingOptional 에 담겨있을 경우..
        if(buildingOptional.isPresent()){

            Building building = buildingOptional.get();
            building.setCollateralDate(buildingRequestDto.getCollateralDate());

            BuildingResponseDto buildingResponseDto = BuildingResponseDto.builder()
                    .buildingId(building.getBuildingId())
                    .buildingName(building.getBuildingName())
                    .address(building.getAddress())
                    .dong(building.getDong())
                    .ho(building.getHo())
                    .collateralDate(building.getCollateralDate())
                    .type(building.getType())
                    .build();

            // 근저당 설정 기준일 추가적으로 입력 받았으므로 db 에 update 해줌.
            buildingRepository.save(building);

            ApiResponse apiResponse = ApiResponse.builder()
                    .check(true)
                    .information(buildingResponseDto)
                    .build();

            return ResponseEntity.ok(apiResponse);

        }
        // 같은 건물이 db 에 저장되어 있지 않아서 buildingOptional 에 아무것도 담기지 않았을 경우..
        else{
            Building building = new Building();

            // codef api 에서 가져온 정확한 건물 이름으로 저장.
            building.setBuildingName(buildingService.getBuildingName(buildingRequestDto.getAddress()));

            building.setType(buildingRequestDto.getType()); //건물 유형
            building.setArea(buildingRequestDto.getArea()); //건물 평형 수

            //주소 정보 set
            building.setAddress(buildingRequestDto.getAddress());
            building.setDong(buildingRequestDto.getDong());
            building.setHo(buildingRequestDto.getHo());

            building.setPrice(buildingService.getPrice(building)); // 시세 가져와서 저장.

            //새로운 건물이므로 db 에 저장.
            buildingRepository.save(building);

            BuildingResponseDto buildingResponseDto = BuildingResponseDto.builder()
                    .buildingId(building.getBuildingId())
                    .buildingName(building.getBuildingName())
                    .address(building.getAddress())
                    .dong(building.getDong())
                    .ho(building.getHo())
                    .collateralDate(buildingRequestDto.getCollateralDate())
                    .type(building.getType())
                    .build();

            ApiResponse apiResponse = ApiResponse.builder()
                    .check(true)
                    .information(buildingResponseDto)
                    .build();

            return ResponseEntity.ok(apiResponse);

        }


    }


    public ResponseEntity<?> getCompReport(String useremail) {
        User findUser = userRepository.findByEmail(useremail).get();

        Optional<Contract> findContractOptional = contractRepository.findByUserId(findUser.getId());
        Contract findContract;
        if (findContractOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            findContract = findContractOptional.get();
        }

        Optional<Report> findReportOptional = reportRepository.findByContractId(findContract.getId());
        Report findReport;
        if (findReportOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            findReport = findReportOptional.get();
        }

        Building findBuilding = findContract.getBuilding();

        ReportResponseDto reportResponseDto = ReportResponseDto.builder()
                .reportStatus(findReport.getStatus())
                .reportedAt(findReport.getCreatedAt())
                .buildingName(findBuilding.getBuildingName())
                .collateralDate(findBuilding.getCollateralDate())
                .address(findBuilding.getAddress())
                .dong(findBuilding.getDong())
                .ho(findBuilding.getHo())
                .buildingType(findBuilding.getType())
                .has_lived(findContract.getHas_lived())
                .transport_report_date(findContract.getTransport_report_date())
                .confirmation_date(findContract.getConfirmation_date())
                .has_landlord_intervene(findContract.getHas_landlord_intervene())
                .has_applied_dividend(findContract.getHas_applied_dividend())
                .deposit(findContract.getDeposit())
                .build();

        ApiResponse apiResponse = ApiResponse.builder()
                .check(true)
                .information(reportResponseDto)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Transactional
    public void reportDelete(String useremail) {

        // user의 email로 유저 찾기
        User findUser = userRepository.findByEmail(useremail).get();

        // userId로 연관된 계약 찾기
        Contract findContract = contractRepository.findByUserId(findUser.getId()).get();

        // 계약id로 신고 내역 찾기
        Report findReport = reportRepository.findByContractId(findContract.getId()).get();

        // report와 연관된 contract 삭제
        contractRepository.delete(findContract);
        reportRepository.delete(findReport);
    }

    public void saveReport(Long userId,Contract contract,Building building) {

        Boolean resist = false;
        Boolean has_image = false;
        Boolean has_low_income =false;
        int repayment = 0;
        ReportStatus report_status = ReportStatus.DECISIONING;
        //대항력 여부
        if(contract.getHas_applied_dividend() && contract.getHas_lived() && !contract.getHas_landlord_intervene()){
            if((contract.getTransport_report_date()!=null)&&(contract.getConfirmation_date()!=null)){
                resist = true;
            }else{
                resist =false;
            }
        }else {
            resist =false;
        }
        //소액임차인 여부
        int calculateResult = calculateDeposit.calculate(contract.getDeposit(), building.getAddress(),contract.getConfirmation_date());
        if(calculateResult!=0){
            has_low_income =true;
        }


        //user에 image존재?
        Image imageEntity = null;
        imageEntity = imageRepository.findById(userId)
                .orElse(imageEntity = null);
        if(imageEntity!= null){
            has_image = true;
        }else{
            has_image = false;
        }
        //최우선 변제금
        if(resist && has_image && has_low_income){
            repayment = calculateResult;
        }





        //STATUS 부여해주기
        if(resist && has_image && has_low_income&&(repayment!=0)){
            report_status = ReportStatus.COMP;
        }else{
            report_status = ReportStatus.DECISIONING;
        }

        //report객체 생성 후 repository에 저장하기
        Report report= Report.builder()
                .hasLowIncome(has_low_income)
                .hasRepayment(has_low_income)
                .hasResistance(resist)
                .repayment(repayment)
                .contract(contract)
                .status(report_status)
                .hasSubmittedContract(has_image)
                .build();

        reportRepository.save(report);
    }



}

