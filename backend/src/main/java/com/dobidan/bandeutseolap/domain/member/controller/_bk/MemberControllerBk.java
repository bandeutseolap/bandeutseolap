package com.dobidan.bandeutseolap.domain.member.controller._bk;

// 1. 화면 조회 완료 후 내 오라클 db 로 데이터 저장하기
// 2. JPA 사용

// 스프링 컨테이너에 등록
//@RestController
/*
public class MemberControllerBk {


    private final MemberServiceImpl memberService;
    private MemberServiceImpl userService;


    // 서비스와 연결하기
    // @Autowired( 의존관계 주입 : DI)
    @Autowired
    public MemberControllerBk(MemberServiceImpl userService, MemberServiceImpl memberService) {
        this.userService = userService;
        this.memberService = memberService;
    }




    // axios : name(필드) -> controller (consumes, request) -> controller return ( produces, ResponseEnity )


    //리액트는 MemberForm를 전혀 모릅니다.
   // 리액트는 단지 axios.post('/member/login', { name })로 JSON을 보낼 뿐이고,
    // 스프링이 그 JSON의 키(name)를 MemberForm의 프로퍼티(name)에 매핑해 줍니다.
    @PostMapping(value ="/member/login", consumes = "application/json")
    public Long createNewMember(@RequestBody MemberForm memberForm) {
        // 로그인 버튼 누르면 로그아웃으로 버튼 변경

        // dto 로 변경 예정
        Member member = new Member();

        member.setName(memberForm.getName());

        System.out.println("member : " + memberForm.getName());

        return memberService.join(member);
    }


    // 회원 목록 조회
    @GetMapping(value = "/members")
    public  List<Member> selectMembers() {
        return memberService.findAllMembers();
    }

    // 로그인 후 마이페이지
//    @GetMapping("/mypage")
//    public void selectMypage(){
//
//    }
}
 */