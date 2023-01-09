package starlingtechchallenge.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
  private String feedItemUid;
  private String categoryUid;
  private Amount amount;
  private SourceAmount sourceAmount;
  private String direction;
  private String updatedAt;
  private String settlementTime;
  private String source;
  private String status;
  private String transactingApplicationUserUid;
  private String counterPartyType;
  private String counterPartyUid;
  private String counterPartyName;
  private String counterPartySubEntityUid;
  private String counterPartySubEntityName;
  private String counterPartySubEntityIdentifier;
  private String counterPartySubEntitySubIdentifier;
  private String reference;
  private String country;
  private String spendingCategory;
  private boolean hasAttachment;
  private boolean hasReceipt;
  private String batchPaymentDetails;
}
