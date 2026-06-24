package com.cloudoptimizer.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class CostSnapshot {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  private Instant observedAt; private String period; private String currency; private String source; private BigDecimal amount;
  public CostSnapshot() {}
  public CostSnapshot(Instant observedAt,String period,String currency,String source,BigDecimal amount){this.observedAt=observedAt;this.period=period;this.currency=currency;this.source=source;this.amount=amount;}
  public Long getId(){return id;} public Instant getObservedAt(){return observedAt;} public String getPeriod(){return period;} public String getCurrency(){return currency;} public String getSource(){return source;} public BigDecimal getAmount(){return amount;}
}
