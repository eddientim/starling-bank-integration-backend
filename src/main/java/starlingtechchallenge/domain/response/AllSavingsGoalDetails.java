package starlingtechchallenge.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import starlingtechchallenge.domain.AllSavingsGoalDetail;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllSavingsGoalDetails {
    private List<AllSavingsGoalDetail> savingsGoalList;
}
