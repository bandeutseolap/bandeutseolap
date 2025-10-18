package com.dobidan.bandeutseolap.domain.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeleteBoardRequestDto {
    @NotBlank
    String userId;
}
