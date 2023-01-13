package starlingtechchallenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import starlingtechchallenge.domain.response.AllSavingsGoalDetails;
import starlingtechchallenge.service.RoundUpService;

@RequestMapping("/round-up")
@RestController
public class RoundUpController {

  private final RoundUpService roundUpService;

  @Autowired
  public RoundUpController(RoundUpService roundUpService) {
    this.roundUpService = roundUpService;
  }


  /**
   * This API performs the round up for out going transactions made and transfer
   * @param accountUid
   * @param changesSince
   * @return AllSavingsGoalDetails A list of total out going transactions within a given time frame.
   */
  @GetMapping(value = "/account/{accountUid}")
  public ResponseEntity<AllSavingsGoalDetails> roundUp(@PathVariable final String accountUid, @RequestParam final String changesSince) {
    AllSavingsGoalDetails response = roundUpService.calculateRoundUp(accountUid, changesSince);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
