package com.dobidan.bandeutseolap.domain.board.service;

import org.springframework.stereotype.Service;

/**
 * BoardServiceImpl
 *
 * BoardService 인터페이스를 구현한 서비스 계층 클래스.
 *
 * - @Service: 스프링이 서비스 컴포넌트로 인식하도록 지정
 * - 실제 비즈니스 로직 구현 (게시글 작성, 조회, 수정, 삭제 등)
 * - Controller에서 호출되어 Repository와 상호작용하며 데이터 처리
 *
 * 현재는 뼈대만 정의되어 있으며,
 * 추후 BoardRepository를 주입받아 CRUD 기능을 구현할 예정.
 */
@Service
public class BoardServiceImpl implements BoardService {
}