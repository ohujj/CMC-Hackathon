package com.cmchackathon.domain.user.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class NicknameGenerator {

    private static final List<String> ADJECTIVES = List.of(
            "다정한", "조용한", "씩씩한", "느긋한", "포근한", "은은한", "고요한", "수줍은",
            "단단한", "따뜻한", "산뜻한", "보드라운", "맑은", "깊은", "잔잔한", "발랄한",
            "차분한", "엉뚱한", "사뿐한", "묵묵한"
    );

    private static final List<String> NOUNS = List.of(
            "관객", "영사기", "필름", "스크린", "엔딩크레딧", "프롤로그", "장면", "컷",
            "테이크", "시네필", "포스터", "트레일러", "스틸컷", "오프닝", "리허설",
            "감독", "주연", "조연", "엑스트라", "팝콘"
    );

    public String generate() {
        String adj = ADJECTIVES.get(ThreadLocalRandom.current().nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(ThreadLocalRandom.current().nextInt(NOUNS.size()));
        int num = ThreadLocalRandom.current().nextInt(10, 1000);
        return adj + noun + num;
    }
}
