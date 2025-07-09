package com.dong.bible.common.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Aspect to save transaction log
 * </BR> Exception 발생 시 거래그룹명세 만 저장. 거래명세 데이터 생성 불가.
 *
 * @author Won Gilho
 * @since Version 1.0
 * <pre>
 * ===================== Change history ======================
 * DATE          AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2023-08-24    Won Gilho     최초 생성
 * </pre>
 */
@Slf4j
@Order(1)
@Aspect
@Component
@RequiredArgsConstructor
public class TransactionLogAspect {
//    final DealingGroupService dealingGroupService;
//    final DealingService dealingService;
//    final
//    @Around("@annotation(kmaas.kpg.common.annotation.TransactionLog) && args(.., @RequestBody body)")
//    public Object proceed(ProceedingJoinPoint joinPoint, final Object body) throws Throwable {
//
//        try {
//            // NOTE @TransactionLog 사용 시 해당 body와 payload의 type에 따라 거래그룹번호와 거래번호를 설정해 주어야 한다
//            if(body instanceof ApprovalReqDTO){
//                ApprovalReqDTO approvalReqDTO = (ApprovalReqDTO) body;
//                approvalReqDTO.getHeader().setDlngGroupNo(dealingGroupService.getNextDlngGroupNo());
//                if(!ObjectUtils.isEmpty(approvalReqDTO.getPmInfos())){
//                    approvalReqDTO.getPmInfos().forEach(dto -> dto.setDlngNo(dealingService.getNextDlngNo()));
//                }
//                if(!ObjectUtils.isEmpty(approvalReqDTO.getRailroadInfo())){
//                    approvalReqDTO.getRailroadInfo().setDlngNo(dealingService.getNextDlngNo());
//                }
//                if(!ObjectUtils.isEmpty(approvalReqDTO.getKobusInfo())){
//                    approvalReqDTO.getKobusInfo().setDlngNo(dealingService.getNextDlngNo());
//                }
//                if(!ObjectUtils.isEmpty(approvalReqDTO.getAirInfo())){
//                    approvalReqDTO.getAirInfo().setDlngNo(dealingService.getNextDlngNo());
//                }
//                if(!ObjectUtils.isEmpty(approvalReqDTO.getIntercitybusInfo())){
//                    approvalReqDTO.getIntercitybusInfo().setDlngNo(dealingService.getNextDlngNo());
//                }
//            } else if(body instanceof CancelReqDTO){
//                CancelReqDTO cancelReqDTO = (CancelReqDTO) body;
//                cancelReqDTO.getHeader().setDlngGroupNo(dealingGroupService.getNextDlngGroupNo());
//                if(!ObjectUtils.isEmpty(cancelReqDTO.getPmCancelInfos())){
//                    cancelReqDTO.getPmCancelInfos().forEach(dto -> dto.setDlngNo(dealingService.getNextDlngNo()));
//                }
//                if(!ObjectUtils.isEmpty(cancelReqDTO.getRailroadCancelInfo())){
//                    cancelReqDTO.getRailroadCancelInfo().setDlngNo(dealingService.getNextDlngNo());
//                }
//                if(!ObjectUtils.isEmpty(cancelReqDTO.getKobusCancelInfo())){
//                    cancelReqDTO.getKobusCancelInfo().setDlngNo(dealingService.getNextDlngNo());
//                }
//                if(!ObjectUtils.isEmpty(cancelReqDTO.getAirCancelInfo())){
//                    cancelReqDTO.getAirCancelInfo().setDlngNo(dealingService.getNextDlngNo());
//                }
//                if(!ObjectUtils.isEmpty(cancelReqDTO.getIntercitybusCancelInfo())){
//                    cancelReqDTO.getIntercitybusCancelInfo().setDlngNo(dealingService.getNextDlngNo());
//                }
//            } else if (body instanceof PartialCancelReqDTO) {
//                PartialCancelReqDTO partialCancelReqDTO = (PartialCancelReqDTO) body;
//                partialCancelReqDTO.getHeader().setDlngGroupNo(dealingGroupService.getNextDlngGroupNo());
//                partialCancelReqDTO.setDlngNo(dealingService.getNextDlngNo());
//            } else if (body instanceof TransactionReqDTO) {
//                // 선민투어 취소 이벤트 배치
//                TransactionReqDTO transactionReqDTO = (TransactionReqDTO) body;
//                transactionReqDTO.getHeader().setDlngGroupNo(dealingGroupService.getNextDlngGroupNo());
//                transactionReqDTO.setDlngNo(dealingService.getNextDlngNo());
//            } else if (body instanceof CancelICBusReqDTO) {
//                // 시외버스 취소 callback
//                CancelICBusReqDTO icBusReqDTO = (CancelICBusReqDTO) body;
//                icBusReqDTO.getHeader().setDlngGroupNo(dealingGroupService.getNextDlngGroupNo());
//                icBusReqDTO.setDlngNo(dealingService.getNextDlngNo());
//            } else if (body instanceof AdminCancelReqDTO) {
//                // Admin 승인취소
//                AdminCancelReqDTO adminCancelReqDTO = (AdminCancelReqDTO) body;
//                adminCancelReqDTO.getHeader().setDlngGroupNo(dealingGroupService.getNextDlngGroupNo());
//                adminCancelReqDTO.setDlngNo(dealingService.getNextDlngNo());
//            } else if (body instanceof AdminRailroadCancelReqDTO) {
//                // Admin 철도 승인취소
//                AdminRailroadCancelReqDTO adminCancelReqDTO = (AdminRailroadCancelReqDTO) body;
//                adminCancelReqDTO.getHeader().setDlngGroupNo(dealingGroupService.getNextDlngGroupNo());
//                adminCancelReqDTO.setDlngNo(dealingService.getNextDlngNo());
//            } else {
//                throw new BizException(ResponseCode.SYS_SERVER_ERROR, "@TransactionLog target class가 구현되지 않았습니다. - " + body.getClass().getName());
//            }
//            Object result = joinPoint.proceed();
//
//            // 거래내역 저장처리
//            dealingService.save(body, result);
//            dealingGroupService.save(body, result);
//            return result;
//        } catch (RuntimeException e){
//            // 거래명세 데이터 생성 불가로 저장하지 않음.
//            dealingGroupService.save(body, e);
//            throw e;
//        }
//    }
}
