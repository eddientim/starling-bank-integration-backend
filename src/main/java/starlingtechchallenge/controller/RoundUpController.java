package starlingtechchallenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import starlingtechchallenge.domain.response.AllSavingsGoalDetails;
import starlingtechchallenge.service.RoundUpService;

import java.time.OffsetDateTime;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

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
     *
     * @param accountUid account holder id
     * @param dateTimeFrom query date start date
     * @param dateTimeTo query of end date
     * @return AllSavingsGoalDetails A list of total out going transactions within a given time frame.
     */
    @GetMapping("/account/{accountUid}")
    public ResponseEntity<AllSavingsGoalDetails> roundUp(@PathVariable final String accountUid,
                                                         @RequestParam("dateTimeFrom") @DateTimeFormat(iso = DATE_TIME) OffsetDateTime dateTimeFrom,
                                                         @RequestParam("dateTimeTo") @DateTimeFormat(iso = DATE_TIME) OffsetDateTime dateTimeTo) {
        AllSavingsGoalDetails response = roundUpService.calculateRoundUp(accountUid, dateTimeFrom, dateTimeTo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
