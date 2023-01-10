package starlingtechchallenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import starlingtechchallenge.domain.response.AddToSavingsGoalResponse;
import starlingtechchallenge.service.RoundUpService;

@RequestMapping("/round-up")
@RestController
public class RoundUpController {

  private final RoundUpService roundUpService;

  @Autowired
  public RoundUpController(RoundUpService roundUpService) {
    this.roundUpService = roundUpService;
  }

  @GetMapping(value = "/account/{accountUid}/goal-id/{savingsGoalUid}")
  public ResponseEntity<AddToSavingsGoalResponse> roundUp(@PathVariable final String accountUid, @PathVariable final String savingsGoalUid, @RequestParam final String changesSince) {
    AddToSavingsGoalResponse response = roundUpService.calculateRoundUp(accountUid, savingsGoalUid, changesSince);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
