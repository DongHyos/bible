package com.dong.bible.common;

/**
 * 공통코드 Constants
 *
 * @author Won Gilho
 * @since Version 1.0
 * <pre>
 * ===================== Change history ======================
 * DATE          AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2023-09-14    Won Gilho     최초 생성
 * </pre>
 */
public class CommCodeConstants {
    // 결제처리코드 - 01:결제완료/02:결제오류/03:결제미처리/04:결제취소완료/05:결제취소오류/06:결제취소미처리
    public static final String STLM_PRCS_PAY_SUCCESS = "01";
    public static final String STLM_PRCS_PAY_ERROR = "02";
//    public static final String STLM_PRCS_PAY_FAIL = "03";
    public static final String STLM_PRCS_CANCEL_SUCCESS = "04";
    public static final String STLM_PRCS_CANCEL_ERROR = "05";
//    public static final String STLM_PRCS_CANCEL_FAIL = "06";

    // 운송수단코드 - 11:항공/12:항만/13:철도/16:고속/시외버스/17:PM/18:공유차/19:택시/20:DRT
    public static final String TRNP_MEAN_AIR = "11";
    public static final String TRNP_MEAN_TRAIN = "13";
    public static final String TRNP_MEAN_BUS = "16";
    public static final String TRNP_MEAN_PM = "17";
    public static final String TRNP_MEAN_CAR = "19";

    // 거래상태 - 01:접수/02:정상/03:오류
    public static final String DLNG_STTS_SUCCESS = "02";
    public static final String DLNG_STTS_ERROR = "03";

    // 결제상태코드 - 01:승인/02:취소/03:부분취소/04:입금/05:환불/06:부분환불/07:적립/08:사용
    public static final String STLM_STTS_APPROVAL = "01";
    public static final String STLM_STTS_CANCEL = "02";
    public static final String STLM_STTS_PART_CANCEL = "03";
    public static final String STLM_STTS_DEPOSIT = "04";
    public static final String STLM_STTS_REFUND = "05";
    public static final String STLM_STTS_PART_REFUND = "06";
    public static final String STLM_STTS_ADD = "07";
    public static final String STLM_STTS_USE = "08";

    // 결제결과코드 - 01:정상/02:실패/03:미응답
    public static final String STLT_RSLT_SUCCESS = "01";
    public static final String STLT_RSLT_FAIL = "02";
    public static final String STLT_RSLT_ERROR = "03";

    // 결제수단코드 - 01:신용카드:/02:계좌이체:/03:가상계좌:/04:휴대폰:/05:포인트:/06:상품권:/07:제휴간편결제
    public static final String STLM_MEAN_CARD = "01";

    // 할부코드
    public static final String IPLN_CARD = "C";
    public static final String IPLN_SHOP = "S";

    // 항공 상태값 - Ready:예약접수/Cancel:취소/TicketEnd:발권완료/Refund:환불
    public static final String AIR_STATUS_TICKET_END = "TicketEnd";
    public static final String AIR_STATUS_REFUND = "Refund";

    // 발권구분코드 - 01:발권정상/02:발권오류/03:발권취소정상/04:발권취소오류
    public static final String TKIS_SE_ISSUE_SUCCESS = "01";
    public static final String TKIS_SE_ISSUE_FAIL = "02";
    public static final String TKIS_SE_CANCEL_SUCCESS = "03";
//    public static final String TKIS_SE_CANCEL_FAIL = "04";

    // 운송수단 - 00:전체, 01:PM, 02:항공, 03:고속버스, 04:기차 05:시외버스
    public static final String TRAN_CODE_ALL = "00";
    public static final String TRAN_CODE_PM = "01";
    public static final String TRAN_CODE_AIR = "02";
    public static final String TRAN_CODE_EXPRESS_BUS = "03";
    public static final String TRAN_CODE_TRAIN = "04";
    public static final String TRAN_CODE_IC_BUS = "05";

    // 카드구분코드 - 01:신용/02:체크
    public static final String CARD_SE_CD_CREDIT = "01";
    public static final String CARD_SE_CD_CHECK = "02";

    // 회사유형코드 - 01:플랫폼사용자/02:운송사업자/03:제휴사업자/04:PG사업자/05:운송사PG사업자/06:한국도로공사/07:기타
    public static final String CARD_CO_TYPE_PL = "01";
    public static final String CARD_CO_TYPE_OP = "02";
//    public static final String CARD_CO_TYPE_PG = "04";
//    public static final String CARD_CO_TYPE_OP_PG = "05";

    // 운송사마일리지코드 - 01:미사용/02:적립/03:적립취소/04:사용/05:사용취소/06:사용&적립/07:사용&적립취소
    public static final String TRCO_MLGE_NONE = "01";
    public static final String TRCO_MLGE_ADD = "02";
    public static final String TRCO_MLGE_ADD_CANCEL = "03";
    public static final String TRCO_MLGE_USE = "04";
    public static final String TRCO_MLGE_USE_CANCEL = "05";
    public static final String TRCO_MLGE_UNA = "06";
    public static final String TRCO_MLGE_UNA_CANCEL = "07";

    // 고속버스 운송사예약발권코드 - 01:발권/02:발권취소
    public static final String TRCO_RSVT_TKIS_ISSUE = "01";
    public static final String TRCO_RSVT_TKIS_CANCEL = "02";
    public static final String TRCO_RSVT_TKIS_CANCEL_PARTIAL = "03";

    // 미처리상태코드 - 01:미처리접수/02:미처리완료/03:미처리오류
    public static  final String UNTN_STTS_01 = "01";
    public static  final String UNTN_STTS_SUCCESS = "02";
    public static  final String UNTN_STTS_ERROR = "03";

    // 미처리결과코드값 - 00:이상없음/01:담당자확인필요/02:PG확인필요/09:기타
    public static final String UNTN_RSLT_CHECK_OK = "00";
    public static final String UNTN_RSLT_CHECK_PIC = "01";
//    public static final String UNTN_RSLT_CHECK_PG = "02";


    // 미처리코드(API) - TREM:거래내역없음/TCEM:거래취소내역없음/TRER:거래내역상이/TCER:거래취소내역상이/STEM:정산내역없음/STER:정산내역상이/ETER:기타
    public static  final String UNTN_TREM = "TREM";
    public static  final String UNTN_TCEM = "TCEM";
    public static  final String UNTN_TRER = "TRER";
    public static  final String UNTN_TCER = "TCER";
    public static  final String UNTN_STEM = "STEM";
    public static  final String UNTN_STER = "STER";
    public static  final String UNTN_ETER = "ETER";


    // 미처리코드(DB) - 01:무응답/02:응답오류/03:네트워크장애/04:대사오류/05:거래내역없음/06:거래취소내역없음/07:거래내역상이/08:거래취소내역상이/09:정산내역없음/10:정산내역상이/11:기타
//    public static final String UNTN_01 = "01";
    public static final String UNTN_02 = "02";
    public static final String UNTN_05 = "05";
    public static final String UNTN_06 = "06";
    public static final String UNTN_07 = "07";
    public static final String UNTN_08 = "08";
    public static final String UNTN_09 = "09";
    public static final String UNTN_10 = "10";
    public static final String UNTN_11 = "11";

    // 미처리등록채널코드 - 01:KMaaS시스템/02:플랫폼사업자/03:운송사업자
    public static final String UNTN_REG_CHNL_KM = "01";
    public static final String UNTN_REG_CHNL_PL = "02";
//    public static final String UNTN_REG_CHNL_OP = "03";

    // 결제업무구분코드 - 01:거래/02:결제/03:거래취소/04:결제취소/05:거래대사/06:정산대사/07:마일리지/08:마일리지취소/09:마일리지대사/10:예약발권/11:기타
    public static final String STLM_TASK_SE_APPROVAL = "02";
    public static final String STLM_TASK_SE_CANCEL = "04";
    public static final String STLM_TASK_SE_05 = "05";
    public static final String STLM_TASK_SE_06 = "06";

    // 거래대사상태코드 - 01:거래대사전/02:거래대사정상/03:거래대사오류
    public static final String DLNG_CPRS_STTS_BEFORE = "01";
    public static final String DLNG_CPRS_STTS_SUCCESS = "02";
    public static final String DLNG_CPRS_STTS_ERROR = "03";

    // 정산상태코드 - 01:정산대사전/02:미입금/03:입금완료
    public static final String CLCLN_STTS_DEPOSIT = "03";

    // 거래대사상세코드 00:정상/01:결제내역없음/02:결제취소내역없음/03:결제내역상이/04:결제취소내역상이/05:기타오류
    public static final String DLNG_CPRS_DTL_SUCCESS = "00";
    public static final String DLNG_CPRS_DTL_NO_APPROVAL = "01";
    public static final String DLNG_CPRS_DTL_NO_CANCEL = "02";
    public static final String DLNG_CPRS_DTL_NOT_SAME_APPROVAL = "03";
    public static final String DLNG_CPRS_DTL_NOT_SAME_CANCEL = "04";
    public static final String DLNG_CPRS_DTL_ETC = "05";

    // 대사구분코드 - 01:KCP 거래대사/02:KCP 정산대사/03:항공권 거래대사/04:고속버스 거래대사/05:철도거래대사/06:마일리지거래대사/07:시외버스거래대사/08:PG매입대사
    public static final String CPRS_SE_KCP_TRADE = "01";
    public static final String CPRS_SE_KCP_SALE = "02";
    public static final String CPRS_SE_AIR = "03";
    public static final String CPRS_SE_KOBUS = "04";
    public static final String CPRS_SE_TRAIN = "05";
    public static final String CPRS_SE_MILEAGE = "06";
    public static final String CPRS_SE_ICBUS = "07";
    public static final String CPRS_SE_KCP_PURCHASE = "08";

    // PG거래상태코드 - PSAP:승인/PSCN:취소/PSPC:부분취소/PSDP:입금/PSRF:환불/PSPR:부분환불/PSSV:적립/PSUS:사용
    public static final String PG_DLNG_STTS_APPROVAL = "PSAP";
    public static final String PG_DLNG_STTS_CANCEL = "PSCN";
    public static final String PG_DLNG_STTS_PART_CANCEL = "PSPC";
    public static final String PG_DLNG_STTS_DEPOSIT = "PSDP";
    public static final String PG_DLNG_STTS_REFUND = "PSRF";
    public static final String PG_DLNG_STTS_PART_REFUND = "PSPR";
    public static final String PG_DLNG_STTS_ADD = "PSSV";
    public static final String PG_DLNG_STTS_USE = "PSUS";

    // 발권취소구분코드값 - Cancel:취소/환불/PartialCancel:부분취소/NoShowCancel:노쇼취소/ExceptCancel:결항취소
//    public static final String TKIS_RTRCN_SE_PARCAL_CANCEL = "PartialCancel";

    // 집계구분코드 - 01:거래/02:정산/03:마일리지/04:예약발권
    public static final String TOT_SE_TRADE = "01";
    public static final String TOT_SE_SALE = "02";
    public static final String TOT_SE_MILEAGE = "03";
    public static final String TOT_SE_TICKET = "04";

    // PG회사 구분 01:NHN KCP/02:운송사업자PG/99:기타
    public static final String PG_CO_SE_KCP = "01";

    // PG 결제구분 - 01:일반결제/02:자동결제
    public static final String PG_STLM_SE_NORMAL = "01";
    public static final String PG_STLM_SE_BATCH = "02";

    // 발권취소경로코드 - 00: 정보없음/01:고객취소/02:관리자페이지취소/03:운송사PG취소
    public static final String TKIS_RTRCN_PATH_CLIENT = "01";
    public static final String TKIS_RTRCN_PATH_ADMIN = "02";
    public static final String TKIS_RTRCN_PATH_OPERATOR = "03";

    // 발권상태코드 - 01:승인완료/02:발권완료
    public static final String TKIS_STTS_APPROVAL = "01";

    // 시외버스 집계코드 구분 - 01:승인일자/02:매입일자
    public static final String IC_BUS_TOT_APPROVAL = "01";
    public static final String IC_BUS_TOT_PURCHASE = "02";
}
