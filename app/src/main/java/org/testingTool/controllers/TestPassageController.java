package org.testingTool.controllers;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.testingTool.dto.UserAnswerFormDto;
import org.testingTool.model.Role;
import org.testingTool.model.TestEntity;
import org.testingTool.model.UserAnswerEntity;
import org.testingTool.model.UserEntity;
import org.testingTool.repository.UserRepository;
import org.testingTool.services.EmailService;
import org.testingTool.services.TestService;
import org.testingTool.services.UserAnswerService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tests")
public class TestPassageController {

  private final TestService testService;
  private final UserAnswerService userAnswerService;
  private final UserRepository userRepository;
  private final EmailService emailService;

  @PreAuthorize("@accessChecker.canPassTest(principal.username, #id)")
  @GetMapping("/{id}")
  public String testPassage(@PathVariable Long id, Model model) {
    TestEntity test = testService.getTestById(id);
    model.addAttribute("test", test);
    return "test";
  }

  @PreAuthorize("@accessChecker.canPassTest(principal.username, #id)")
  @PostMapping("/{id}")
  public String submitPassage(
      @PathVariable Long id,
      @ModelAttribute UserAnswerFormDto formDto,
      @AuthenticationPrincipal UserDetails userDetails) {
    UserEntity user =
        userRepository
            .findByUid(userDetails.getUsername())
            .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    List<UserAnswerEntity> userAnswers = userAnswerService.saveAnswers(formDto, user.getId());

    UserEntity admin =
        userRepository.findByRole(Role.ADMIN).stream()
            .findFirst()
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "Администратор не найден")); // Будем считать, что админ у нас пока один

    String testName = testService.getTestById(id).getName();
    emailService.sendUserResultsToMail(admin.getEmail(), user.getUid(), testName, userAnswers);

    return "redirect:/tests/success";
  }

  @GetMapping("/success")
  public String testPassedSuccess() {
    return "test-success";
  }
}
