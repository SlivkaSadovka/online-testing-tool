package org.testingTool.dto;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserAnswerFormDto {
  private Long testId;
  private Long userId;
  private List<UserAnswerDto> answers;
}
