package com.dobidan.bandeutseolap.domain.member.repository._bk;

// HashMap => ConcurrentHashMap 사용 권장

// 1. 일단 메모리 저장 = > *12강 내 로직이 맞게 설계되었는지 테스트 케이스 작성하기
// 2. JDBC 저장(X)
// 3. Mysql & JPA 저장 = > 테이블 설계 안됨
//@Repository
/*
public class MemoryMemberRepository implements MemberRepository {

    private static Map<Long, Member> keyValueStore = new HashMap<>();
    private static long seq = 0L;

    @Override
    public Member save(Member user) {
        user.setId(++seq);
        keyValueStore.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(keyValueStore.get(id));
    }

    @Override
    public Optional<Member> findByName(String name) {
        for(Member value : keyValueStore.values()){
            if(value.getName().equals(name)){
                return Optional.of(value);
            }

        }

        return Optional.empty();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(keyValueStore.values());
    }


    public void clearStore(){
        keyValueStore.clear();
    }

}
*/