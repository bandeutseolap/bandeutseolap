package com.dobidan.bandeutseolap.domain.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PatchBoardRequestDto {
    @NotBlank
    String boardTitle;
    @NotBlank
    String boardContent; // 댓글 수, 조회 수는 안가져와도 되나??

    @NotBlank
    String userId;
}
